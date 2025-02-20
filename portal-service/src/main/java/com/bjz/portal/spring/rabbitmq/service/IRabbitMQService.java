package com.bjz.portal.spring.rabbitmq.service;

import com.bjz.portal.spring.rabbitmq.pojo.RabbitMQPojo;

public interface IRabbitMQService {

    void testHelloWorldQueue(RabbitMQPojo rabbitMQPojo);

    void testWorkQueue(RabbitMQPojo rabbitMQPojo);

    void testExchange(RabbitMQPojo rabbitMQPojo);
}
