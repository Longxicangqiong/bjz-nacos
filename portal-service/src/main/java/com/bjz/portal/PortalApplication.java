package com.bjz.portal;

import com.bjz.common.feign.UserFeign;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @MethodName
 * @Description
 * @Author BJZ
 * @Date
 **/

@MapperScan("com.bjz.portal")
@SpringBootApplication
//@EnableFeignClients(defaultConfiguration = FeignConfig.class)
//@EnableFeignClients(basePackages = {"com.bjz.common.feign"})
@EnableFeignClients(clients = {UserFeign.class})               // 推荐：具体的某个提供者：数组形式
@EnableSwagger2
public class PortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortalApplication.class, args);
    }







    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

/*    @Bean
    public IRule ribbonRule(){
        // 轮询
//        return new RoundRobinRule();
        // 随机
        return new RandomRule();
    }*/

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}