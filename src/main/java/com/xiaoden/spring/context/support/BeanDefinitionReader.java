package com.xiaoden.spring.context.support;

import com.xiaoden.spring.beans.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author dengfuhai
 * @description
 * @date 2019/9/26 0026
 */
//对配置文件的查找，读取，和解析
public class BeanDefinitionReader {

    private Properties config=new Properties();
    //存放要封装的BeanDefinition的全类名
    private List<String> registyBeanClasses=new ArrayList<>();
    //在配置文件中，用来获取自动扫描包名的key
    private final String SCAN_PACKAGE="scanPackage";

    //构造方法传入配置文件路径
    public BeanDefinitionReader(String ...locaitons){
        //在spring中是通过Reader查找和定位的
        //对application.properties定位
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locaitons[0].replace("classpath:",""));
        try {
            config.load(is);//读取到config中
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //解析配置文件,得到扫描包下的所有的类名
        doScanner(config.getProperty(SCAN_PACKAGE));
        //properties.getProperty(key)是得到properties配置文件中的键值为key的value

    }
    public List<String> loadBeanDefinitions(){
        return this.registyBeanClasses;
    }
    //每注册一个className，就返回一个BeanDefinition,
    //这里只是对配置信息进行简单的分装
    public BeanDefinition registerBean(String className){
        if(this.registyBeanClasses.contains(className)){
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClassName(className);
            //设置在ioc容器中的beanDefinition的名字,substring（起始位置，结束位置）截取字符串，这里+1是得到全包名最后一个点的位置+1
            //默认首字母小写处理
            beanDefinition.setFactoryBeanName(lowerFirstCase(className.substring(className.lastIndexOf(".")+1)));
            return beanDefinition;
        }
        return null;
    }

    //返回读取的配置信息
    public Properties getConfig(){
        return this.config;
    }
    //解析配置文件,得到扫描包下的所有的类名
    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());//得到一个目录文件
        for (File file:classDir.listFiles()) {//遍历目录中的文件
            if (file.isDirectory()){//如果是目录就遍历目录中的文件再加载一下
                doScanner(packageName+"."+file.getName());
            }else{
                registyBeanClasses.add(packageName+"."+file.getName().replace(".class",""));
            }

        }
    }
    //把字符串首字母小写
    private String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0]+=32;
        return String.valueOf(chars);
    }
}
