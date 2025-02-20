package com.bjz.portal.spring.springDataRedis.service.impl;

import cn.hutool.json.JSONUtil;
import com.bjz.common.pojo.User;
import com.bjz.portal.spring.springDataRedis.service.ICacheService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Classname CacheServiceC
 * @Description 集群缓存
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@Service
public class CacheServiceC implements ICacheService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String getUserNameById(String userId) {
        if(StringUtils.isBlank(userId)){
            return null;
        }

        String user = stringRedisTemplate.opsForValue().get(userId);
        if(null == user){
            initUser();
            user = stringRedisTemplate.opsForValue().get(userId);
        }

        if(null == user){
            // 缓存穿透
            User userCache = User.builder().id(Long.parseLong(userId)).build();
            stringRedisTemplate.opsForValue().set(userId, JSONUtil.toJsonStr(userCache));
        }

        return user;
    }

    private void initUser() {
        // 查数据库全部用户，都写入缓存
    }

}
