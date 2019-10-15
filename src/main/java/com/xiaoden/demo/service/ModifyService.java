package com.xiaoden.demo.service;

import com.xiaoden.spring.annotation.Service;

/**
 * @author dengfuhai
 * @description
 * @date 2019/10/9 0009
 */
@Service
public class ModifyService implements IModifyService {

    @Override
    public String add(String name, String addr) {
        return "my name is"+name+"addr is"+addr;
    }
}
