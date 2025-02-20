package com.bjz.portal.spring.springDataRedis.config;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bjz.common.util.Res;
import com.bjz.portal.common.pojo.SystemMessage;
import com.bjz.portal.common.service.IPortalService;
import com.bjz.portal.spring.springDataRedis.vo.RedisCacheData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.bjz.common.constants.RedisConstants.*;

/**
 * @Classname RedisCacheUtil
 * @Description redis缓存
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@Component
public class RedisCacheUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    private static final ExecutorService cacheExecutor = Executors.newFixedThreadPool(10);



    /**
     * @MethodName setCache
     * @Description 设置缓存
     * @param: key
     * @param: data
     * @param: expireTime
     * @Param: timeUnit
     * @Author BJZ
     * @Date
     **/
    public void setCache(String key, Object data, Long expireTime, TimeUnit timeUnit){
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(data), expireTime, timeUnit);
    }

        /**
     * @MethodName setCache
     * @Description 设置缓存：数据带逻辑过期时间
     * @param: key
     * @param: data
     * @param: expireTime
     * @Param: timeUnit
     * @Author BJZ
     * @Date
     **/
    public void setCacheWithLogicExpire(String key, Object data, Long expireTime, TimeUnit timeUnit){
        RedisCacheData cache = new RedisCacheData(LocalDateTime.now().plusSeconds(timeUnit.toSeconds(expireTime)), data);
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(cache));
    }

    /**
     * @MethodName getCachePenetration
     * @Description          获取缓存：解决缓存穿透问题
     * @param: keyPrefix
     * @param: id
     * @param: objectType    返回的数据类型 R
     * @param: sqlSentence
     * @param: expireTime
     * @Param: timeUnit
     * @Return: R            返回的数据类型
     * @Author BJZ
     * @Date
     * <R,ID>                先定义泛型，再使用泛型
     * Function<T, R>        有入参T 出参R的函数
     **/
    public <R,ID> R getCachePenetration(String keyPrefix, ID id, Class<R> objectType, Function<ID,R> sqlSentence, Long expireTime, TimeUnit timeUnit){
        // 先从redis缓存中查
        String key = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(key);

        // 判断缓存是否是防止缓存穿透的空值
        if("".equals(json)){
            return null;
        }

        if(StringUtils.isNotBlank(json)){
            return JSONUtil.toBean(json, objectType);
        }

        // 没有再从数据库查
        R data = sqlSentence.apply(id);
        if(null == data){
            // 防止缓存穿透，redis和数据库都没有查询到数据
            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        // 详情时查询数据，并写入Redis缓存中
        setCache(key,data,expireTime,timeUnit);
        return data;
    }

    /**
     * @MethodName getCacheWithLogicExpire
     * @Description        获取缓存：缓存数据带逻辑过期时间，解决缓存击穿问题
     * @param: keyPrefix
     * @param: id
     * @param: sqlSentence
     * @param: expireTimr
     * @Param: timeUnit
     * @Return: R
     * @Author BJZ
     * @Date
     **/
    public <R,ID> R getCacheWithLogicExpire(String keyPrefix, ID id, Class<R> objectType, Function<ID,R> sqlSentence, Long expireTime, TimeUnit timeUnit){
        String key = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(key);
        // 实际场景中，双11，缓存击穿的热点key肯定是提前插入的，数据预热，所以这里不考虑缓存穿透问题
        if(StringUtils.isBlank(json)){
            return null;
        }

        // 反序列化依赖于字节码的类型
        RedisCacheData redisCacheData = JSONUtil.toBean(json, RedisCacheData.class);
        R data = JSONUtil.toBean((JSONObject) redisCacheData.getData(), objectType);
        // 没过期，直接返回
        if(LocalDateTime.now().isBefore(redisCacheData.getExpireTime())){
            return data;
        }
        // 过期重置缓存，先获取锁
        String lockKey = CACHE_LOCK_KEY + id;
        boolean lock = tryLock(lockKey);
        // 获得锁，用线程池开启新线程去重置缓存
        if(lock){
            // todo 获得锁应该再次判断热点key数据的逻辑过期时间 DoubleCheck

            cacheExecutor.execute(()->{
                try {
                    // 查询数据库
                    R r = sqlSentence.apply(id);
                    // 写入缓存
                    setCacheWithLogicExpire(lockKey, r, expireTime,timeUnit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
//                    e.printStackTrace();
                } finally {
                    // 加锁后都要在try/finally中释放锁
                    unlock(lockKey);
                }
            });
        }

        // 获没获得锁都返回旧数据
        return data;
    }

    /**
     * @MethodName tryLock
     * @Description  缓存击穿时尝试获取锁：使用方案：setnx不存在才插入
     *               redis的分布式锁：基于setnx实现，key为模块编码，value为当前线程ID
     * @Return: boolean 基本类型
     * @Author BJZ
     * @Date
     **/
    public boolean tryLock(String key){
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent( key, key, CACHE_LOCK_TTL, TimeUnit.SECONDS);
        // 直接返回result会拆箱，可能会引起空指针异常
        return BooleanUtil.isTrue(result);
    }

    /**
     * @MethodName unlock
     * @Description 缓存击穿时释放锁
     * @Param: id
     * @Author BJZ
     * @Date
     **/
    public void unlock(String key){
        stringRedisTemplate.delete(key);
    }

}
