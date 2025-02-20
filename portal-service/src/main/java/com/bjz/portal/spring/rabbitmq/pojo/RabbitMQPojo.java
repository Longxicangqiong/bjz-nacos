package com.bjz.portal.spring.rabbitmq.pojo;

import lombok.Data;

/**
 * @Classname RabbitMQPojo
 * @Description
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@Data
public class RabbitMQPojo {

    // 交换机
    private String exchange;

    //
    private String routingKey;

    // 队列
    private String queue;

    // 消息：jackson序列化
    private Object message;

}
