package com.bjz.common.config;


import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * 此配置类加在启动类的@EnableFeignClient里时，为全局配置。
 * 加在具体的@FeignClient里时，针对某个微服务局部配置
 * */
public class FeignConfig {

    @Bean
    public Logger.Level logLevel(){
        return Logger.Level.FULL;
    }

}
