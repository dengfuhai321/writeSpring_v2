package com.xiaoden.spring.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

/**
 * @author dengfuhai
 * @description
 * @date 2019/10/8 0008
 */
public class FHHandlerAdapter {
    //这个是记录了一个方法的所有参数的形参和下标，比如<request,1><response,2><name,3>
    //是和url真正对应的RequestParam的String name和 Integer 下标
    private Map<String,Integer> paramMapping;

    public FHHandlerAdapter(Map<String, Integer> paramMapping) {
        this.paramMapping = paramMapping;
    }

    /*
     * 功能描述:
     * @author deng_fuhai
     * @date 2019/10/8 0008
     * @param request
     * @param response
     * @param hander 这个hander中包含了controller对象，url,和method
     * @return com.xiaoden.spring.webmvc.FHModelAndView
     */
    public FHModelAndView handle(HttpServletRequest request, HttpServletResponse response, FHHandlerMapping hander)throws Exception {
        //根据用户请求的参数信息，和Method 中的参数信息进行动态匹配

        //当用户穿过来的ModelAndView为空时，才会默认new一个新的ModelAndView
        //1.准备好这个方法的形参列表的参数类型
        //getgetParameterTypes返回方法参数类型
        Class<?>[] parameterTypes = hander.getMethod().getParameterTypes();
        //2.拿到自定义命名参数所在的位置
        //用户通过URL传递过来参数列表
        //比如t1=1&t1=2&t2=3
        // key=t1;value[0]=1,value[1]=2   key=t2;value[0]=3
        Map<String, String[]> reqParametMap = request.getParameterMap();

        //3.构造实参列表（这里只保存了有RequestParam注解的url实参值）
         Object [] paramValues=   new Object[parameterTypes.length];
        for (Map.Entry<String,String[]> param:reqParametMap.entrySet()) {
            //把实参数组转化成字符串,正则\\s代表空格
            String ceshi=Arrays.toString(param.getValue());
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll("\\s", "");
            if(!this.paramMapping.containsKey(param.getKey())){
                //查看访问该方法中的参数的形参中有没有url传过来的key
                continue;
            }
            Integer index = this.paramMapping.get(param.getKey());
            //因为url传递过来的值都是string类型的，而方法中的参数类型各种各样
            //这里返回url中传递的值，并且转换成方法中的参数类型
            paramValues[index]= caseStringValue(value,parameterTypes[index]);
        }
        if (this.paramMapping.containsKey(HttpServletRequest.class.getName())) {
            Integer integer = this.paramMapping.get(HttpServletRequest.class.getName());
            paramValues[integer] = request;
        }
        if (this.paramMapping.containsKey(HttpServletResponse.class.getName())) {
            Integer integer = this.paramMapping.get(HttpServletResponse.class.getName());
            paramValues[integer] = response;
        }

            //最终这里就集齐了对应方法类型的参数值paramValues
            //4.从handler中取出controller和Method，反射调用
            Object result = hander.getMethod().invoke(hander.getController(), paramValues);
            //这里查看该方法是否返回ModelAndView
            if(result==null){
                return null;
            }
            boolean isModelAndView=hander.getMethod().getReturnType()==FHModelAndView.class;
            if(isModelAndView){
                return (FHModelAndView) result;
            }else{
                return null;
            }

    }

    /*
     * 功能描述: //这是把url传过来的String类型转换成方法对象的类型
     * @param value  需要转换的值
     * @param clazz  转换的目标类型
     * @return java.lang.Object
     */
    private Object caseStringValue(String value,Class<?> clazz){
        if(clazz==String.class){
            return value;
        }else if(clazz==Integer.class){
            return Integer.valueOf(value);//把String类型转换成Integer
        }else if(clazz==int.class){//转换成int类型可以使用 Integer.parseInt()
            return Integer.valueOf(value).intValue();
        }else{
            return null;
        }

    }
}
