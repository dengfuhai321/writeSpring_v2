package com.xiaoden.spring.webmvc;

import java.util.Map;

/**
 * @author dengfuhai
 * @description
 * @date 2019/10/8 0008
 */
public class FHModelAndView {
    private String viewName;
    private Map<String,?> model;

    public FHModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
