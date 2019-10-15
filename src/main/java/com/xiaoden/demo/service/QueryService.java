package com.xiaoden.demo.service;

import com.xiaoden.spring.annotation.Service;

/**
 * @author dengfuhai
 * @description
 * @date 2019/10/8 0008
 */
@Service
public class QueryService implements IQueryService {

    @Override
    public String query(String name) {
        return "your name is"+name;
    }
}
