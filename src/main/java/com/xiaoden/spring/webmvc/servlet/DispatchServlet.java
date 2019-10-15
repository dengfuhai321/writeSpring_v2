package com.xiaoden.spring.webmvc.servlet;

import com.xiaoden.spring.annotation.Controller;
import com.xiaoden.spring.annotation.RequestMapping;
import com.xiaoden.spring.annotation.RequestParam;
import com.xiaoden.spring.context.FHApplicationContext;
import com.xiaoden.spring.webmvc.FHHandlerAdapter;
import com.xiaoden.spring.webmvc.FHHandlerMapping;
import com.xiaoden.spring.webmvc.FHModelAndView;
import com.xiaoden.spring.webmvc.FHViewResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dengfuhai
 * @description
 * @date 2019/9/25 0025
 */
//servlet只是作为一个mvc的启动入口
public class DispatchServlet extends HttpServlet {

    private final String LOCATION="contextConfigLocation";

//保存方法和url的关系，一个Controller中的一个RequestParam注解的方法就代表一个handlerMapping
    private List<FHHandlerMapping> handlerMappings=new ArrayList<FHHandlerMapping>();
//保存每一个handler中method方法对应的形参和下标
    private Map<FHHandlerMapping,FHHandlerAdapter> handlerAdapters=new HashMap<FHHandlerMapping,FHHandlerAdapter>();

    private List<FHViewResolver> viewResolvers=new ArrayList<FHViewResolver>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            dodispatch(req,resp);
        } catch (Exception e) {
            //Arrays.toString()打印数组中的内容，如果直接arr[].toString打印的是地址
            resp.getWriter().write("500 Exception,Details:\r\n"+ Arrays.toString(e.getStackTrace()).replaceAll("\\s","\r\n"));
            e.printStackTrace();
        }
    }
    private void dodispatch(HttpServletRequest request,HttpServletResponse response)throws Exception{
        FHHandlerMapping hander = getHander(request);
        if(hander==null){
            response.getWriter().write("404 ");
        }
        FHHandlerAdapter ha= getHandlerAdapter(hander);

        if(ha==null){
            response.getWriter().write("ha为空 ");
        }
        //这里实现了url对应的方法调用，分两步走，1.把url数据转换成方法的参数，2.反射执行方法
        FHModelAndView mv = ha.handle(request, response, hander);
        //这一步把处理好的数据和ModelAndView输出
        processDispatchResult(response,mv);

    }

    private void processDispatchResult(HttpServletResponse response, FHModelAndView mv)throws Exception {
        //调用viewResolver的resolverView方法
        if(mv==null){//如果返回值为void也就没有返回的意义了
            return ;
        }
        if(this.viewResolvers.isEmpty()){
            return ;
        }
        for (FHViewResolver viewResolver:this.viewResolvers) {
            if(!mv.getViewName().equals(viewResolver.getViewName())){
                continue;//不是该viewResolver就跳过该次，继续循环
            }
            String out = viewResolver.viewResolver(mv);
            if(out!=null){
                response.getWriter().write(out);
                break;
            }

        }



    }


    private FHHandlerMapping getHander(HttpServletRequest request) {
        if(this.handlerMappings.isEmpty()){
            return null;
        }
        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        //把项目的前缀去掉，把多个//变成一个/
        url=url.replace(contextPath,"").replaceAll("/+","/");
        for (FHHandlerMapping handler:this.handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if(!matcher.matches()){//如果不能匹配上正则表达式，继续循环
                continue;
            }
            //匹配上正则直接返回handlerMapping
            return handler;
        }
        return null;
    }
    private FHHandlerAdapter getHandlerAdapter(FHHandlerMapping hander) {
        if(this.handlerAdapters.isEmpty()){
            return null;
        }
        return this.handlerAdapters.get(hander);
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("Spring容器初始化");
        FHApplicationContext context = new FHApplicationContext(config.getInitParameter(LOCATION));

        initStrategies(context);//springMVC初始化
    }
    protected void initStrategies(FHApplicationContext context) {
        //有九种策略
        //针对每个用户的请求，都会经过一些处理的策略之后，最终才有结果输出
        //每种策略都可以自定义干预，但输出的结果是一致的。
        //下面就是传说中的======九大组件======

        initMultipartResolver(context);//文件上传解析
        initLocaleResolver(context);//本地化解析
        initThemeResolver(context);//主题解析
        /*我们会自己实现*/
        initHandlerMappings(context);//通过HandlerMapping将请求映射到处理器Handler中。
        /*我们会自己实现*/
        initHandlerAdapters(context);//HandlerAdapter进行多类型的参数动态匹配

        initHandlerExceptionResolvers(context);//如果执行遇到异常交给它来处理
        initRequestToViewNameTranslator(context);//直接解析请求到视图名
        /*我们会自己实现*/
        //通过ViewResolver将逻辑视图ModelAndView解析成具体的视图View
        initViewResolvers(context);

        initFlashMapManager(context);//flash映射管理器
    }

    private void initLocaleResolver(FHApplicationContext context) {}
    private void initMultipartResolver(FHApplicationContext context) {}
    private void initThemeResolver(FHApplicationContext context) {}
    private void initHandlerExceptionResolvers(FHApplicationContext context) {}
    private void initRequestToViewNameTranslator(FHApplicationContext context) {}
    private void initFlashMapManager(FHApplicationContext context) {}

    //HandlerMapping用来保存Controller中配置的RequestMapping和Method的关系
    private void initHandlerMappings(FHApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        //首先从容器中取到所有的实例
        for (String beanName:beanNames) {
            Object controller = context.getBean(beanName);
            Class<?> clazz = controller.getClass();
            if(!clazz.isAnnotationPresent(Controller.class)){
                continue;
            }
            //获得Controller类上的RequestMapping的value
            String baseUrl="";
            //如果存在RequestMapping的value就执行
            if(clazz.isAnnotationPresent(RequestMapping.class)){
                RequestMapping annotation = clazz.getAnnotation(RequestMapping.class);
                baseUrl=annotation.value();
            }
            //扫描所有的method方法
            Method[] methods = clazz.getMethods();
            for (Method method:methods) {
                //如果没有RequestMapping的注解是不会和url对应的
               if(!method.isAnnotationPresent(RequestMapping.class)){
                   continue;
               }
               //否则
                RequestMapping annotation = method.getAnnotation(RequestMapping.class);
               //获得的value是//的，所以/+就是把多个/变成一个/，把*变成.*
               String regex=(("/"+baseUrl+annotation.value().replaceAll("\\*",".*")).replaceAll("/+","/"));
               //这里把字符串变成正则表达式
                Pattern patten = Pattern.compile(regex);
                this.handlerMappings.add(new FHHandlerMapping(patten,controller,method));
                System.out.println("Mapping:"+regex+","+method);
            }
        }


    }
    //HandlerAdapter用来动态匹配Method的参数，包括类型转换和动态赋值
    private void initHandlerAdapters(FHApplicationContext context) {

        for (FHHandlerMapping handlerMapping:this.handlerMappings) {

            //每一个方法有一个参数列表，这里保存的是形参列表
            Map<String,Integer> paramMapping=new HashMap<String,Integer>();



            //这里处理命名的参数列表RequestParam("name")
            Annotation[][] pa =handlerMapping.getMethod().getParameterAnnotations();
            for(int i=0;i<pa.length;i++){
                for(Annotation a :pa[i]){
                    if(a instanceof RequestParam){
                        String paramName = ((RequestParam) a).value();
                        if(!"".equals(paramName.trim())){
                            //放入注解参数名称和当前参数位置
                            paramMapping.put(paramName,i);
                        }else{
             //没有注解参数也应该放入参数名称和下标位置，

                        }

                    }
                }

            }
            //这里处理非命名参数
            //只处理request和response，普通的没有RequestParam注解的不处理
            Class<?>[] paramTypes= handlerMapping.getMethod().getParameterTypes();
            for(int i=0;i<paramTypes.length;i++){
                Class<?> type=paramTypes[i];
                if(type==HttpServletRequest.class
                        ||type==HttpServletResponse.class){
                    paramMapping.put(type.getName(),i);
                }
            }

            this.handlerAdapters.put(handlerMapping,new FHHandlerAdapter(paramMapping));

        }


    }
    //自己解析的模板语言
    private void initViewResolvers(FHApplicationContext context) {
        //在页面访问http://localhost/first.html
        //解决页面名字和和模板关联问题
        String templateRoot = context.getConfig().getProperty("templateRoot");
        //如果加了getClassLoader获得的文件默认就是在根目录下，否则路径前得写/代表根目录
        //src下或者resource下是根目录

        //获得路径
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for (File template:templateRootDir.listFiles()) {
            this.viewResolvers.add(new FHViewResolver(template.getName(),template));
            
        }

    }

}
