package com.yuge.ing.sentinel.portal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: yuge
 * @date: 2023/11/23
 **/
@RestController
@RequestMapping("/portal")
public class PortalController {

    @GetMapping("/hello")
    public String hello() throws InterruptedException {
        return "Hello portal!";
    }

}
