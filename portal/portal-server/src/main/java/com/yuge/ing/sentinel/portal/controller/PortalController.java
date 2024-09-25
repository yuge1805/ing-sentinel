package com.yuge.ing.sentinel.portal.controller;

import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.yuge.ing.sentinel.portal.config.PortalProperties;
import com.yuge.ing.sentinel.portal.dto.HelloDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/hello/{object}")
    public String helloObject(@PathVariable String object) throws InterruptedException {
        return "Hello " + object;
    }

    @GetMapping("/helloGetParam")
//    @SentinelResource(value = "/helloGetParam", entryType = EntryType.IN)
    public String helloGetParam(@RequestParam Long id, @RequestParam String name) {
        return "Hello Get Param!";
    }

    @PostMapping("/helloPostParam")
    public String helloPostParam(@RequestBody HelloDTO dto) {
        return "Hello Get Param!";
    }

}
