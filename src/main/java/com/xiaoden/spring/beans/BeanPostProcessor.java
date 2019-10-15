package com.xiaoden.spring.beans;

//用作事件监听的
public class BeanPostProcessor {
    public Object postProcessBeforeInitialization(Object bean, String beanName)  {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName)  {
        return bean;
    }


}
