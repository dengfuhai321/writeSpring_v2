package com.xiaoden.demo.mvc.action;

import com.xiaoden.demo.service.IDemoService;
import com.xiaoden.demo.service.IModifyService;
import com.xiaoden.demo.service.IQueryService;
import com.xiaoden.demo.service.ModifyService;
import com.xiaoden.spring.annotation.Autowried;
import com.xiaoden.spring.annotation.Controller;
import com.xiaoden.spring.annotation.RequestMapping;
import com.xiaoden.spring.annotation.RequestParam;
import com.xiaoden.spring.webmvc.FHModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author dengfuhai
 * @description
 * @date 2019/9/26 0026
 */
@Controller
@RequestMapping("/web")
public class MyAction {
    @Autowried
    IDemoService demoService;
    @Autowried
    IQueryService queryService;
    @Autowried
    IModifyService modifyService;

    @RequestMapping("/query.json")
    public FHModelAndView query(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam("name")String name){
        String query = queryService.query(name);
        System.out.println(query);
        return out(response,name);

    }
//http://localhost:8080/writeSpring_v2_war/web/addTeacher.json?name=xiaodeng&addr=wuan
    @RequestMapping("/add*.json")
    public FHModelAndView add(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam("name")String name,@RequestParam("addr")String addr){
        String query = modifyService.add(name,addr);
        System.out.println(query);
        return out(response,query);

    }
    private FHModelAndView out(HttpServletResponse response,String str){
        try {
            response.getWriter().write(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
