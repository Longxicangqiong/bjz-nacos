package com.bjz.portal.spring.springDataRedis.service;

import com.bjz.common.util.Res;
import com.bjz.portal.common.pojo.SystemOrder;

/**
 * @Classname IRedisOrderService
 * @Description 利用redis实现商品抢购订单相关功能
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
public interface IRedisOrderService {

    /**
     * @MethodName addRedisOrder
     * @Description 商品抢购，并实现一人一单需求：先查询，再判断，高并发时都会出现判断错误情况
     * @Param: id
     * @Return: com.bjz.common.util.Res
     * @Author BJZ
     * @Date
     **/
    Res addRedisOrder(Long id);

    /**
     * @MethodName addOrder
     * @Description 一人一单、扣减库存、创建订单
     * @Param: id
     * @Return: com.bjz.common.util.Res
     * @Author BJZ
     * @Date
     **/
    Res addOrder(Long id);

    /**
     * @MethodName asynRedisOrder
     * @Description 异步实现商品抢购，并实现一人一单需求：先查询，再判断，高并发时都会出现判断错误情况
     * @Param: id
     * @Return: com.bjz.common.util.Res
     * @Author BJZ
     * @Date
     **/
    Res asynRedisOrder(Long id);

    /**
     * @MethodName asynOrder
     * @Description 异步下单：一人一单、扣减库存、创建订单
     * @Param: id
     * @Return: com.bjz.common.util.Res
     * @Author BJZ
     * @Date
     **/
    void asynOrder(SystemOrder order) ;
}
