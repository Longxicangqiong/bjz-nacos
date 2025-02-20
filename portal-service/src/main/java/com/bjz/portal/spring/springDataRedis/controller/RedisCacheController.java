package com.bjz.portal.spring.springDataRedis.controller;

import com.bjz.common.util.Res;
import com.bjz.portal.common.pojo.SystemMessage;
import com.bjz.portal.spring.springDataRedis.service.IRedisCacheService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Classname RedisCacheController
 * @Description Redis缓存相关方面调试
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@RestController
@Api("Redis缓存")
@RequestMapping("/portal/redis/cache")
public class RedisCacheController {

    @Autowired
    public IRedisCacheService redisCacheService;



    @ApiOperation("缓存穿透：缓存空对象并设置短暂有效期")
    @GetMapping("/cachePenetration/{id}")
    public Res<SystemMessage> cachePenetration(@PathVariable("id") Long id) {
        // 根据id查询消息并返回
        return redisCacheService.cachePenetration(id);
    }

    @ApiOperation("修改缓存")
    @PostMapping("/updateCache")
    public Res updateMessage(@RequestBody SystemMessage systemMessage){
        return redisCacheService.updateCache(systemMessage);
    }

    @ApiOperation("缓存击穿解决方案：互斥锁(使用setnx实现)")
    @GetMapping("/cacheWithMutex/{id}")
    public Res<SystemMessage> cacheWithMutex(@PathVariable("id") Long id) {
        // 根据id查询消息并返回
        return redisCacheService.cacheWithMutex(id);
    }

    @ApiOperation("缓存击穿解决方案：逻辑过期")
    @GetMapping("/cacheWithLogicExpire/{id}")
    public Res<SystemMessage> cacheWithLogicExpire(@PathVariable("id") Long id){
        return redisCacheService.cacheWithLogicExpire(id);
    }

    @ApiOperation("数据预热：提前存入缓存击穿的热点数据")
    @GetMapping("/saveRedisData")
    public Res saveRedisData(@RequestParam("id") Long id, @RequestParam("expireSeconds") Long expireSeconds){
        return redisCacheService.saveRedisData(id,expireSeconds);
    }

    @ApiOperation("缓存工具类测试")
    @GetMapping("/testRedisCacheUtil/{id}")
    public Res<SystemMessage> testRedisCacheUtil(@PathVariable("id") Long id){
        return redisCacheService.testRedisCacheUtil(id);
    }

}
