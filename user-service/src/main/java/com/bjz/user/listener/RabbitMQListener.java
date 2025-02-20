package com.bjz.user.listener;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

//@Component
public class RabbitMQListener {

    @RabbitListener(queues = "helloWorld.queue")
    public void listenHelloWorldQueue(String message){
        System.out.println("helloWorld.queue = " + message);
    }

    @RabbitListener(queues = "work.queue")
    public void listenWorkQueue1(String message){
        System.out.println("work.queue = " + message + "  dateTime: " +  LocalTime.now());
        try {
            // 一秒处理50个
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "work.queue")
    public void listenWorkQueue2(String message){
        System.err.println("work.queue ----- " + message + "  dateTime: " +  LocalTime.now());
        try {
            // 一秒处理5个
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    监听的还是队列
    @RabbitListener(queues = "fanout.queue1")
    public void listenFanoutExchange1(String message){
        System.out.println("fanout.queue1 = " + message);
    }

    @RabbitListener(queues = "fanout.queue2")
    public void listenFanoutExchange2(String message){
        System.out.println("fanout.queue2 = " + message);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("direct.queue1"),
            exchange = @Exchange(name = "direct.exchange",  type = ExchangeTypes.DIRECT),
            key = {"shenzhen","guangzhou"}
    ))
    public void listenDirectExchange1(String message){
        System.out.println("direct.queue1 = " + message);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("direct.queue2"),
            exchange = @Exchange(name = "direct.exchange", type = ExchangeTypes.DIRECT),
            key = {"shenzhen","dongguan"}
    ))
    public void listenDirectExchange(String message){
        System.out.println("direct.queue2 = " + message);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("topic.queue1"),
            exchange = @Exchange(value = "topic.exchange", type = ExchangeTypes.TOPIC),
            key = {"shenzhen.#"}
    ))
    public void listenTopicExchange1(String message){
        System.out.println("topic.shenzhen.# = " + message);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("topic.queue2"),
            exchange = @Exchange(value = "topic.exchange", type = ExchangeTypes.TOPIC),
            key = {"#.cluster"}
    ))
    public void listenTopicExchange2(String message){
        System.out.println("topic.#.cluster = " + message);
    }


}
