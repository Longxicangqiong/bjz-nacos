package com.bjz.portal.spring.springDataRedis.service;

import com.bjz.common.util.Res;
import com.bjz.portal.common.pojo.SystemMessage;

/**
 * @Classname IRedisCacheService
 * @Description TODO
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
public interface IRedisCacheService {

    /**
     * @MethodName cachePenetration
     * @Description 缓存穿透
     * @Param: id
     * @Return: com.bjz.common.util.Res<com.bjz.portal.common.pojo.SystemMessage>
     * @Author BJZ
     * @Date
     **/
    Res<SystemMessage> cachePenetration(Long id);

    /**
     * @MethodName updateCache
     * @Description 修改缓存
     * @Param: systemMessage
     * @Return: com.bjz.common.util.Res
     * @Author BJZ
     * @Date
     **/
    Res updateCache(SystemMessage systemMessage);

    /**
     * @MethodName cacheWithMutex
     * @Description 缓存击穿解决方案：互斥锁(使用setnx实现)
     * @Param: id
     * @Return: com.bjz.common.util.Res<com.bjz.portal.common.pojo.SystemMessage>
     * @Author BJZ
     * @Date
     **/
    Res<SystemMessage> cacheWithMutex(Long id);

    /**
     * @MethodName cacheWithLogicExpire
     * @Description 缓存击穿解决方案：逻辑过期
     * @Param: id
     * @Return: com.bjz.common.util.Res<com.bjz.portal.common.pojo.SystemMessage>
     * @Author BJZ
     * @Date
     **/
    Res<SystemMessage> cacheWithLogicExpire(Long id);

    /**
     * @MethodName saveRedisData
     * @Description 数据预热：提前存入缓存击穿的热点数据
     * @param: id
     * @Param: expireSeconds
     * @Return: com.bjz.common.util.Res
     * @Author BJZ
     * @Date
     **/
    Res saveRedisData(Long id, Long expireSeconds);

    /**
     * @MethodName testRedisCacheUtil
     * @Description 缓存工具类测试
     * @Param: id
     * @Return: com.bjz.common.util.Res<com.bjz.portal.common.pojo.SystemMessage>
     * @Author BJZ
     * @Date
     **/
    Res<SystemMessage> testRedisCacheUtil(Long id);
}
