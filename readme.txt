这是模仿Spring的第二个版本，
    一.在v1版本上更加模仿了Spring的IOC,DI的步骤，
    二.以及模仿实现了SpringMVC的功能
实现一的步骤：
    1.Spring起始点BeanFactory，代表着Spring容器的管理和诞生，这里FHApplication
implements BeanFactory。
    2.通过实例化Application的构造方法，传入配置文件application.properties,
然后refresh().
    3.refresh()方法就开始初始化Spring容器了，
     3.1定位，new BeanDefinitionReader(configLocation)；
     即对配置文件的查找，读取，和解析，都是交给BeanDefinitionReader来
处理的，其中包括
    doScanner(String packageName)解析配置文件，得到扫描包下的所有的类名。
    registerBean(String className),解析bean的配置信息，返回bean配置信息的
    简单封装对象BeanDefinition。
     3.2加载 reader.loadBeanDefinitions();
   其实就是通过BeanDefinitionReader对象获得的className的集合，为下一步作
准备。
     3.3注册   doRegisty(beanClassNames)
     通过全包名beanClassNames将BeanDefinition注册到beanDefinitionMap中，
也就是ioc容器中。
     3.4依赖注入    doAutorited()
     Spring中默认是不会自动依赖注入的，除非lazy-init=false,或者
getBean()得到该对象，这里我是自动依赖注入的
    其中getBean()得到对象，instantionBean(BeanDefinition)返回实例对象，但
是不会直接返回该对象，而是把该实例对象重新包装，返回BeanWrapper对象
    getBean()时不管是实例对象还是包装对象都会在返回的同时保存在相应的容器中，便于
自动化全部依赖注入
    populateBean()//给原始的实例对象进行依赖注入，反射
@Test:在DispatchServlet这个servlet的init中context=new Application();完
成Spring容器的初始化,然后initStrategies(context)实现SpringMVC

    二.SpringMVC说白了，就是像Spring一样提前准备好bean,这个是准备把用户访问
的url和要访问的Method关联起来
    initStrategies()它会初始化九大组件，我们实现了三个
initHandlerMappings(context);把每个Method和url关联起来
    HandlerMapping记录url,controller,method
initHandlerAdapters(context);用来动态匹配Method的参数，包括类型转换和动态赋值
    HandlerAdapter记录每一个Handler的method对应起来的形参名字和下标记录下来。
initViewResolvers(),就是把所有的模板model.html，index.jsp啥的都记录起来
以上就SpringMVC就初始化好了，然后开始实现访问的过程。
url访问的时候都会经过servlet的doPost()和doGet()方法
    1.进入dodispatch(req,resp)方法
    2.getHander()根据访问url找到对应的记录方法的HanderMapping。
    3.getHandlerAdapter()根据handler找到HandlerAdapter
    4.HandlerAdapter.handle()这个方法真正实现了方法的调用。
        4.1.把url数据转换成方法的参数，
        4.2.反射执行方法
    5.processDispatchResult()实现对返回的ModelAndView进行渲染。（没有实现）


