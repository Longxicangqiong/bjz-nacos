package com.bjz.portal.spring.springDataRedis.config;

import cn.hutool.core.util.BooleanUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static com.bjz.common.constants.RedisConstants.*;

/**
 * @Classname  RedisCustomLock
 * @Description Redis的分布式锁实现：基于setnx实现
 *              利用nginx的配置（反向代理、负载均衡）来模拟集群模式（后台同一微服务起多个端口）
 *              采用的是string类型，存在问题：不可重入、不可重试、超时释放、主从一致性
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
public class RedisCustomLock {

    /**
     *  业务模块编码
     */
    private String key;

    private StringRedisTemplate stringRedisTemplate;

    public RedisCustomLock(String key, StringRedisTemplate stringRedisTemplate) {
        this.key = key;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     *  lua脚本：Script
     *  DefaultRedisScript<Long>：Long为返回值类型
     */
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        // 设置脚本的位置
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("/redis/redisUnLock.lua"));
        // 返回值类型
        UNLOCK_SCRIPT.setResultType(Long.class);
    }


    /**
     * @MethodName tryLock
     * @Description 尝试获取分布式锁：基于setnx实现，key为模块编码，value为UUID+当前线程id。因为不同jvm/进程/微服务中的线程id可能相同
     *              缓存击穿时尝试获取锁：使用方案：setnx不存在才插入
     * @Param: expireTime 过期时间：单位秒
     * @Return: boolean
     * @Author BJZ
     * @Date
     **/
    public boolean tryLock(Long expireTime){
        // 分布式下，key为模块编码，value为UUID+当前线程id。
        String threadId = REDIS_LOCK_UUID_VALUE + Thread.currentThread().getId();
        Boolean result = stringRedisTemplate.opsForValue()
                .setIfAbsent(REDIS_LOCK_KEY + key, threadId, expireTime, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(result);
    }

    /**
     * @MethodName unLock
     * @Description  释放锁：直接删除锁容易出现误删情况(当持有锁的时间大于过期时间)
     *                     当前线程id 与 锁的值value 一致时，才释放锁。不加UUID很容易相同
     *                     但，判断锁和释放锁要保证原子性，不然还是会误删，通过lua脚本来实现
     * @Author BJZ
     * @Date
     **/
    public void unLock(){
/*        String threadId = REDIS_LOCK_VALUE + Thread.currentThread().getId();
        String value = stringRedisTemplate.opsForValue().get(REDIS_LOCK_KEY + key);
        // 当前线程id 与 锁的值 一致时，才释放锁。不加UUID很容易相同
        if(threadId.equals(value)){
            stringRedisTemplate.delete(REDIS_LOCK_KEY + key);
        }*/

        // 查询、判断、删除三行代码不能保证原子性，还是会有误删可能，执行lua脚本一行命令来实现
        stringRedisTemplate.execute(
                UNLOCK_SCRIPT,  // 脚本
                Collections.singletonList(REDIS_LOCK_KEY + key),
                REDIS_LOCK_UUID_VALUE + Thread.currentThread().getId());
    }

}
