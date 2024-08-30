package com.yuge.ing.sentinel.portal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: yuge
 * @date: 2024/8/23
 **/
@Data
@Configuration
@ConfigurationProperties("portal")
public class PortalProperties {

    private String name;

    private String desc;

    private String version;

}
