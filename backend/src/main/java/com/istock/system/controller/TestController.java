package com.istock.system.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tess")
public class TestController {

    @GetMapping("/test")
    public String test(
            Authentication authentication
    ) {
        return String.format("test %s", authentication.getName());
    }

}
