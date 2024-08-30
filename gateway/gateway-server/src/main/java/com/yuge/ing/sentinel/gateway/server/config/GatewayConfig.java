package com.yuge.ing.sentinel.gateway.server.config;

import com.yuge.ing.sentinel.gateway.server.exception.JsonErrorExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.List;

/**
 * @author: yuge
 * @date: 2024/8/22
 **/
@Configuration
public class GatewayConfig {

    /**
     * 自定义json异常处理
     * 须注意优先级@Order顺序，否则会影响 spring.cloud.sentinel.scg.fallback的生效
     * sentinel的异常处理Bean声明: com.alibaba.cloud.sentinel.gateway.scg.SentinelSCGAutoConfiguration#sentinelGatewayBlockExceptionHandler()
     *
     * @return
     */
    @Bean
    @Order(-1)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes,
                                                             ServerProperties serverProperties,
                                                             WebProperties webProperties,
                                                             List<ViewResolver> viewResolvers,
                                                             ServerCodecConfigurer serverCodecConfigurer,
                                                             ApplicationContext applicationContext) {
        JsonErrorExceptionHandler exceptionHandler = new JsonErrorExceptionHandler(
                errorAttributes,
                webProperties.getResources(),
                serverProperties.getError(),
                applicationContext);
        exceptionHandler.setViewResolvers(viewResolvers);
        exceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
        return exceptionHandler;
    }

}
