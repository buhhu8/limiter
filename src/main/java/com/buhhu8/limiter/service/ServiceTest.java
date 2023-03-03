package com.buhhu8.limiter.service;

import com.buhhu8.limiter.annotation.Intercept;
import org.springframework.stereotype.Service;


@Service
public class ServiceTest {

    @Intercept
    public void get(){
    }
}
