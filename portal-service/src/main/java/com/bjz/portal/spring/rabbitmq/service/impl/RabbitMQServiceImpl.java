package com.bjz.portal.spring.rabbitmq.service.impl;

import com.bjz.portal.spring.rabbitmq.pojo.RabbitMQPojo;
import com.bjz.portal.spring.rabbitmq.service.IRabbitMQService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
//@Primary
public class RabbitMQServiceImpl implements IRabbitMQService {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public void testHelloWorldQueue(RabbitMQPojo rabbitMQPojo) {
        rabbitTemplate.convertAndSend(rabbitMQPojo.getQueue(), rabbitMQPojo.getMessage());
    }

    @Override
    public void testWorkQueue(RabbitMQPojo rabbitMQPojo) {
        // 一秒发50个
        for (int i = 0; i < 50; i++) {
            rabbitTemplate.convertAndSend(rabbitMQPojo.getQueue(), rabbitMQPojo.getMessage() + "______"  + i);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 发送给交换机
    @Override
    public void testExchange(RabbitMQPojo rabbitMQPojo) {
        rabbitTemplate.convertAndSend(
                rabbitMQPojo.getExchange(), rabbitMQPojo.getRoutingKey(), rabbitMQPojo.getMessage());
    }
}
