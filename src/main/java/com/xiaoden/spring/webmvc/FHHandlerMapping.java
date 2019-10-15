package com.xiaoden.spring.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author dengfuhai
 * @description
 * @date 2019/10/8 0008
 */
public class FHHandlerMapping {
    //正则表达式
    private Pattern pattern;//封装的url，可以用来匹配url
    private Object controller;
    private Method method;

    public FHHandlerMapping(Pattern pattern, Object controller, Method method) {
        this.pattern = pattern;
        this.controller = controller;
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
