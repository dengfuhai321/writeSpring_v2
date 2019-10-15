package com.xiaoden.spring.beans;

import com.xiaoden.spring.core.FactoryBean;

/**
 * @author dengfuhai
 * @description
 * @date 2019/9/26 0026
 */
public class BeanWrapper extends FactoryBean {
    //还会用到观察者模式
    //1.支持事件响应,会有一个监听
    private BeanPostProcessor postProcessor;

    public BeanPostProcessor getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(BeanPostProcessor postProcessor) {
        this.postProcessor = postProcessor;
    }

    //原始的通过反射new出来，然后包装保存
    private Object originalinstance;//代理对象

    private Object wrapperinstance;//包装过的对象

    public BeanWrapper(Object instance){
        //这里先把不改变，弄成一样的
        this.originalinstance=instance;
        this.wrapperinstance=instance;

    }

    //返回代理类
    public Object getWrappedInstance(){
        return this.wrapperinstance;
    }
    //返回代理以后的class
    public Class<?> getWrapperClass(){
        return this.wrapperinstance.getClass();
    }




}
