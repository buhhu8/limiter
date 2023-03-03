package com.buhhu8.limiter.controller;

import com.buhhu8.limiter.annotation.Intercept;
import com.buhhu8.limiter.service.ServiceTest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class LimiterController {

    private final ServiceTest serviceTest;

    @GetMapping("getAll")
    public ResponseEntity getAll() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("controller")
    @Intercept
    public ResponseEntity get() {
        return ResponseEntity.ok().build();
    }

}
