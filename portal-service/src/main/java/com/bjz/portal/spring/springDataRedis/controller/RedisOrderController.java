package com.bjz.portal.spring.springDataRedis.controller;

import com.bjz.common.util.Res;
import com.bjz.portal.spring.springDataRedis.service.IRedisOrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname RedisOrderController
 * @Description 利用redis实现商品抢购订单相关功能
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@RestController
@RequestMapping("/portal/redis/order")
public class RedisOrderController {

    @Autowired
    private IRedisOrderService redisOrderService;



    @ApiOperation("商品抢购，并实现一人一单需求")
    @PostMapping("/addRedisOrder/{id}")
    public Res addRedisOrder(@PathVariable("id") Long id){
        return redisOrderService.addRedisOrder(id);
    }

    @ApiOperation("异步实现商品抢购")
    @PostMapping("/asynRedisOrder/{id}")
    public Res asynRedisOrder(@PathVariable("id") Long id){
        return redisOrderService.asynRedisOrder(id);
    }




}
