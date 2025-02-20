package com.bjz.portal.spring.springDataRedis.service;

import com.bjz.common.pojo.User;
import com.bjz.common.util.Res;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

/**
 * @Classname IRedisService
 * @Description TODO
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
public interface IRedisService {

    /**
     * @MethodName stringTest
     * @Description  Redis的String类型测试
     * @param: key
     * @Param: value
     * @Return: com.bjz.common.util.Res<java.lang.String>
     * @Author BJZ
     * @Date
     **/
    Res<String> stringTest(String key, String value);

    /**
     * @MethodName stringTestPojo
     * @Description Redis的String类型存储的value为对象时测试
     * @Param: key
     * @Return: com.bjz.common.util.Res<com.bjz.common.pojo.User>
     * @Author BJZ
     * @Date
     **/
    Res<User> stringTestPojo(String key);

    /**
     * @MethodName stringRedisTemplateTest
     * @Description stringRedisTemplate测试
     * @Param: key
     * @Return: com.bjz.common.util.Res<com.bjz.common.pojo.User>
     * @Author BJZ
     * @Date
     **/
    Res<User> stringRedisTemplateTest(String key) throws JsonProcessingException;

    /**
     * @MethodName hashTest
     * @Description Redis的Hash类型测试
     * @Param: key
     * @Return: com.bjz.common.util.Res<java.lang.String>
     * @Author BJZ
     * @Date
     **/
    Res<Map<Object, Object>> hashTest(String key);
}
