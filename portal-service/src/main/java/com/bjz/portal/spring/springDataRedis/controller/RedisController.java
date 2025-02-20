package com.bjz.portal.spring.springDataRedis.controller;

import com.bjz.common.pojo.User;
import com.bjz.common.util.Res;
import com.bjz.portal.spring.springDataRedis.service.IRedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Classname springDataRedisController
 * @Description TODO
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@RestController
@RequestMapping("/redisTest")
public class RedisController {

    @Autowired
    @Qualifier("redisServiceImpl")
    private IRedisService redisService;



    @ApiOperation("Redis的String类型测试")
    @GetMapping("/stringTest")
    public Res<String> stringTest(@RequestParam String key, @RequestParam String value){
        return redisService.stringTest(key,value);
    }

    @ApiOperation("Redis的String类型存储的value为对象时测试")
    @GetMapping("/stringTestPojo")
    public Res<User> stringTestPojo(@RequestParam String key){
        return redisService.stringTestPojo(key);
    }

    @ApiOperation("stringRedisTemplate测试")
    @GetMapping("/stringRedisTemplateTest")
    public Res<User> stringRedisTemplateTest(@RequestParam String key) throws JsonProcessingException {
        return redisService.stringRedisTemplateTest(key);
    }

    @ApiOperation("Redis的Hash类型测试")
    @GetMapping("/hashTest")
    public Res<Map<Object, Object>> hashTest(@RequestParam String key){
        return redisService.hashTest(key);
    }

}
