package com.yuge.ing.sentinel.portal.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.SentinelWebInterceptor;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.config.SentinelWebMvcConfig;
import com.yuge.ing.sentinel.portal.interceptor.EnhanceSentinelInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

/**
 * @author: yuge
 * @date: 2024/9/13
 **/
@Deprecated
//@Configuration
public class SentinelConfigurationV1 {

    @Bean
    @Primary
    public SentinelWebInterceptor enhanceSentinelInterceptor(
            SentinelWebMvcConfig sentinelWebMvcConfig) {
        return new EnhanceSentinelInterceptor(sentinelWebMvcConfig);
    }

}
