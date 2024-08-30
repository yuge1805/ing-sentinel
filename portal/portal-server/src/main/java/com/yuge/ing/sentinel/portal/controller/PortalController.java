package com.yuge.ing.sentinel.portal.controller;

import com.yuge.ing.sentinel.portal.config.PortalProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: yuge
 * @date: 2023/11/23
 **/
@Slf4j
@RestController
@RequestMapping("/")
public class PortalController {

    @Value("${portal.version}")
    private String portalVersion;

    @Resource
    private PortalProperties portalProperties;

    @GetMapping("/hello")
    public String hello() throws InterruptedException {
        log.info("Hello portal!");
        log.info("value version: {}, properties version: {}", portalVersion, portalProperties.getVersion());
        return "Hello portal!";
    }

}
