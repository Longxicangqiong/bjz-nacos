package com.bjz.portal.spring.springDataRedis.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bjz.common.util.Res;
import com.bjz.portal.common.pojo.SystemMessage;
import com.bjz.portal.common.service.IPortalService;
import com.bjz.portal.spring.springDataRedis.config.RedisCacheUtil;
import com.bjz.portal.spring.springDataRedis.service.IRedisCacheService;
import com.bjz.portal.spring.springDataRedis.vo.RedisCacheData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.bjz.common.constants.RedisConstants.*;

/**
 * @Classname RedisCacheServiceImpl
 * @Description TODO
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */

@Service
@Primary
public class RedisCacheServiceImpl implements IRedisCacheService {

    @Autowired
    public StringRedisTemplate stringRedisTemplate;
    @Autowired
    public IPortalService portalService;
    @Autowired
    public RedisCacheUtil redisCacheUtil;


    private static final ExecutorService cacheExecutor = Executors.newFixedThreadPool(10);



    @Override
    public Res<SystemMessage> cachePenetration(Long id) {
        // 先从redis缓存中查
        String messageKey = CACHE_MESSAGE_KEY + id;
        String messageJson = stringRedisTemplate.opsForValue().get(messageKey);

        // 判断缓存是否是防止缓存穿透的空值
        if("".equals(messageJson)){
            return Res.error();
        }

        if(StringUtils.isNotBlank(messageJson)){
            return Res.ok(JSONUtil.toBean(messageJson, SystemMessage.class));
        }

        // 没有再从数据库查
        SystemMessage message = portalService.getById(id);
        if(null == message){
            // 防止缓存穿透，redis和数据库都没有查询到数据
            stringRedisTemplate.opsForValue().set(messageKey, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return Res.error();
        }
        // 详情时查询数据，并写入Redis缓存中
        stringRedisTemplate.opsForValue().set(messageKey, JSONUtil.toJsonStr(message), CACHE_MESSAGE_TTL, TimeUnit.MINUTES);
        return Res.ok(message);
    }

    @Override
    public Res updateCache(SystemMessage systemMessage) {
        if(null == systemMessage.getId()){
            return Res.error("消息主键Id不存在");
        }

        portalService.updateById(systemMessage);
        // 修改数据并删除缓存
        stringRedisTemplate.delete(CACHE_MESSAGE_KEY + systemMessage.getId());

        return Res.ok();
    }

    @Override
    public Res<SystemMessage> cacheWithMutex(Long id) {
        // 先从redis缓存中查
        String messageKey = CACHE_MESSAGE_KEY + id;
        String messageJson = stringRedisTemplate.opsForValue().get(messageKey);

        // 判断缓存是否是防止缓存穿透的空值
        if("".equals(messageJson)){
            return Res.error();
        }

        if(StringUtils.isNotBlank(messageJson)){
            return Res.ok(JSONUtil.toBean(messageJson, SystemMessage.class));
        }

        String lockKey = CACHE_LOCK_KEY + id;
        SystemMessage message = null;
        try {
            // 缓存中没有数据，先获取锁
            boolean lock = redisCacheUtil.tryLock(lockKey);
            // 没有获取到锁时，休眠50ms，再重试
            if(!lock){
                Thread.sleep(50);
                // 递归
                return cacheWithMutex(id);
            }

            // todo 获取到锁时，应该再次检查redis有无缓存数据 DoubleCheck

            // 模拟查询数据库时间较长
            Thread.sleep(200);

            // 获取到锁再从数据库查
            message = portalService.getById(id);
            if(null == message){
                // 防止缓存穿透，redis和数据库都没有查询到数据
                stringRedisTemplate.opsForValue().set(messageKey, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                return Res.error();
            }
            // 详情时查询到数据，并写入Redis缓存中
            stringRedisTemplate.opsForValue().set(messageKey, JSONUtil.toJsonStr(message), CACHE_MESSAGE_TTL, TimeUnit.MINUTES);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            redisCacheUtil.unlock(lockKey);
        }

        return Res.ok(message);
    }

    @Override
    public Res<SystemMessage> cacheWithLogicExpire(Long id) {
        String dataKey = CACHE_MESSAGE_KEY + id;
        String redisDataJson = stringRedisTemplate.opsForValue().get(dataKey);
        // 实际场景中，双11，缓存击穿的热点key肯定是提前插入的，数据预热，所以这里不考虑缓存穿透问题
        if(StringUtils.isBlank(redisDataJson)){
            return Res.ok();
        }

        // 反序列化
        RedisCacheData redisCacheData = JSONUtil.toBean(redisDataJson, RedisCacheData.class);
        SystemMessage message = JSONUtil.toBean((JSONObject) redisCacheData.getData(), SystemMessage.class);
        // 没过期，直接返回
        if(LocalDateTime.now().isBefore(redisCacheData.getExpireTime())){
            return Res.ok(message);
        }
        // 过期重置缓存，先获取锁
        String lockKey = CACHE_LOCK_KEY + id;
        boolean lock = redisCacheUtil.tryLock(lockKey);
        // 获得锁，用线程池开启新线程去重置缓存
        if(lock){
            // todo 获得锁应该再次判断热点key数据的逻辑过期时间 DoubleCheck

            cacheExecutor.execute(()->{
                try {
                    saveRedisData(id, CACHE_LOGIC_TTL);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    redisCacheUtil.unlock(lockKey);
                }
            });
        }

        // 获没获得锁都返回旧数据
        return Res.ok(message);
    }

    @Override
    public Res saveRedisData(Long id, Long expireSeconds){
        SystemMessage message = portalService.getById(id);
        try {
            // 模拟实际数据查询慢
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 过期时间应该设为30分钟，这里为了方便测试设为20秒
        redisCacheUtil.setCacheWithLogicExpire(CACHE_MESSAGE_KEY, message, expireSeconds, TimeUnit.SECONDS);
        return Res.ok();
    }

    @Override
    public Res<SystemMessage> testRedisCacheUtil(Long id) {
        SystemMessage systemMessage = redisCacheUtil.getCachePenetration(CACHE_MESSAGE_KEY, id, SystemMessage.class, portalService::getById, CACHE_MESSAGE_TTL, TimeUnit.MINUTES);
        if (null == systemMessage) systemMessage = new SystemMessage();
        return Res.ok(systemMessage);
    }


}
