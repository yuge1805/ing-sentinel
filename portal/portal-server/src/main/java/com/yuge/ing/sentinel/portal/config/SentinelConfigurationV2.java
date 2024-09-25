package com.yuge.ing.sentinel.portal.config;

import com.alibaba.cloud.sentinel.SentinelProperties;
import com.alibaba.cloud.sentinel.SentinelWebAutoConfiguration;
import com.alibaba.cloud.sentinel.SentinelWebMvcConfigurer;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.SentinelWebInterceptor;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.DefaultBlockExceptionHandler;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.UrlCleaner;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.config.SentinelWebMvcConfig;
import com.yuge.ing.sentinel.portal.interceptor.EnhanceSentinelInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * SentinelWebAutoConfiguration中定义的Bean无法覆盖；
 * 故：手动排除SentinelWebAutoConfiguration类的加载；
 * 暂未发现@EnableAutoConfiguration(exclude = SentinelWebAutoConfiguration.class) \ @SpringBootApplication(exclude = SentinelWebAutoConfiguration.class) 两者区别；
 *
 * 不建议V1，Spring容器中存在重复的sentinelWebInterceptor；虽然不会使用，但是就是有洁癖；
 * 基于V2，可以更好封装通用组件；
 * 至于在组件中屏蔽SentinelWebAutoConfiguration加载，可以参考原生Sentinel Gateway，在环境变量后置处理器中，强行spring.cloud.sentinel.enabled属性置为false;
 * 这个配置spring.autoconfigure.exclude应该也可以，未验证；
 *
 * @author: yuge
 * @date: 2024/9/13
 **/
@Configuration
@EnableAutoConfiguration(exclude = SentinelWebAutoConfiguration.class)
public class SentinelConfigurationV2 {

    @Autowired
    private SentinelProperties properties;

    @Autowired
    private Optional<UrlCleaner> urlCleanerOptional;

    @Autowired
    private Optional<BlockExceptionHandler> blockExceptionHandlerOptional;

    @Autowired
    private Optional<RequestOriginParser> requestOriginParserOptional;

    @Bean
    public SentinelWebInterceptor sentinelWebInterceptor(
            SentinelWebMvcConfig sentinelWebMvcConfig) {
        return new EnhanceSentinelInterceptor(sentinelWebMvcConfig);
    }

    @Bean
    public SentinelWebMvcConfig sentinelWebMvcConfig() {
        SentinelWebMvcConfig sentinelWebMvcConfig = new SentinelWebMvcConfig();
        sentinelWebMvcConfig.setHttpMethodSpecify(properties.getHttpMethodSpecify());
        sentinelWebMvcConfig.setWebContextUnify(properties.getWebContextUnify());

        if (blockExceptionHandlerOptional.isPresent()) {
            blockExceptionHandlerOptional
                    .ifPresent(sentinelWebMvcConfig::setBlockExceptionHandler);
        }
        else {
            if (StringUtils.hasText(properties.getBlockPage())) {
                sentinelWebMvcConfig.setBlockExceptionHandler(((request, response,
                                                                e) -> response.sendRedirect(properties.getBlockPage())));
            }
            else {
                sentinelWebMvcConfig
                        .setBlockExceptionHandler(new DefaultBlockExceptionHandler());
            }
        }

        urlCleanerOptional.ifPresent(sentinelWebMvcConfig::setUrlCleaner);
        requestOriginParserOptional.ifPresent(sentinelWebMvcConfig::setOriginParser);
        return sentinelWebMvcConfig;
    }

    @Bean
    public SentinelWebMvcConfigurer sentinelWebMvcConfigurer() {
        return new SentinelWebMvcConfigurer();
    }


}
