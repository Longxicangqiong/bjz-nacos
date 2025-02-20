package com.bjz.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "pattern")  // 约定大于配置：前缀+类成员变量名=配置属性
public class NacosConfig {

    public String dateFormat;

    public String commonValue;


}
