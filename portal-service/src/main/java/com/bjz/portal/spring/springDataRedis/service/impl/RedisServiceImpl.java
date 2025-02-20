package com.bjz.portal.spring.springDataRedis.service.impl;

import com.bjz.common.pojo.User;
import com.bjz.common.util.Res;
import com.bjz.portal.spring.springDataRedis.service.IRedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jsqlparser.util.validation.metadata.NamedObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

/**
 * @Classname RedisServiceImpl
 * @Description TODO
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@Service
public class RedisServiceImpl implements IRedisService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    // ObjectMapper
    public static final ObjectMapper objectMapper = new ObjectMapper();



    @Override
    public Res<String> stringTest(String key, String value) {
        Object obj = redisTemplate.opsForValue().get(key);
        // 有就返回
        if(null != obj){
            return Res.ok(obj.toString());
        }

        // 没有插入再返回
        redisTemplate.opsForValue().set(key, value);
        return Res.ok(redisTemplate.opsForValue().get(key).toString());
    }

    @Override
    public Res<User> stringTestPojo(String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        if(null != obj){
            return Res.ok((User) obj);
        }

        User user = User.builder().username("zhangfei").address("taoyuan").build();

        redisTemplate.opsForValue().set(key, user);
        return Res.ok((User) redisTemplate.opsForValue().get(key));
    }

    @Override
    public Res<User> stringRedisTemplateTest(String key) throws JsonProcessingException {
        if(stringRedisTemplate.hasKey(key)){
            String value = stringRedisTemplate.opsForValue().get(key);
            return Res.ok(objectMapper.readValue(value,User.class));
        }

        // 在代码里手动做pojo的序列化和反序列化，而不是让redisTemplate来做
        String data = objectMapper.writeValueAsString(User.builder().username("关羽").address("樊城").build());
        stringRedisTemplate.opsForValue().set(key,data);
        return Res.ok(objectMapper.readValue(stringRedisTemplate.opsForValue().get(key), User.class));
    }

    @Override
    public Res<Map<Object, Object>> hashTest(String key) {
        if(stringRedisTemplate.hasKey(key)){
            Set<Object> keys = stringRedisTemplate.opsForHash().keys(key);
            Object name = stringRedisTemplate.opsForHash().get(key, "name");
            Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(key);
            List<Object> values = stringRedisTemplate.opsForHash().values(key);
            return Res.ok(map);
        }

        stringRedisTemplate.opsForHash().put(key,"name","赵云");
        stringRedisTemplate.opsForHash().put(key, "address", "常山");
        return Res.ok(stringRedisTemplate.opsForHash().entries(key));
    }

}
