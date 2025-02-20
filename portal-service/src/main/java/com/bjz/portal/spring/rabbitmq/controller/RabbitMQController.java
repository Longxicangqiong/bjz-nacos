package com.bjz.portal.spring.rabbitmq.controller;

import com.bjz.portal.spring.rabbitmq.pojo.RabbitMQPojo;
import com.bjz.portal.spring.rabbitmq.service.IRabbitMQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rabbitmqTest")
public class RabbitMQController {

    @Autowired
    @Qualifier(value = "rabbitMQServiceImpl")
    private IRabbitMQService rabbitMQService;



    @PostMapping("/testHelloWorldQueue")
    public void testHelloWorldQueue(@RequestBody RabbitMQPojo rabbitMQPojo){
        rabbitMQService.testHelloWorldQueue(rabbitMQPojo);
    }

    @PostMapping("/testWorkQueue")
    public void testWorkQueue(@RequestBody RabbitMQPojo rabbitMQPojo){
        rabbitMQService.testWorkQueue(rabbitMQPojo);
    }

    @PostMapping("/testExchange")
    public void testExchange(@RequestBody RabbitMQPojo rabbitMQPojo){
        rabbitMQService.testExchange(rabbitMQPojo);
    }

}
