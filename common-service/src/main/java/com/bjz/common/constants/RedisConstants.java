package com.bjz.common.constants;


import java.util.UUID;

/**
 * @Classname RedisConstants
 * @Description redis的静态常量
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
public class RedisConstants {

    // 短信登录的验证码key前缀
    public static final String LOGIN_CODE_KEY = "login:code:";
    // 短信登录的验证码有效期：单位分钟
    public static final Long LOGIN_CODE_TTL = 5l;
    // token的key前缀
    public static final String LOGIN_TOKEN_KEY = "login:token:";
    // token的有效期：单位分钟
    public static final Long LOGIN_TOKEN_TTL = 30l;
    // 系统消息缓存key前缀
    public static final String CACHE_MESSAGE_KEY = "cache:message:";
    // 系统消息的有效期：单位分钟
    public static final Long CACHE_MESSAGE_TTL = 30L;
    // 防止缓存穿透，设置空置有效期：单位分钟
    public static final Long CACHE_NULL_TTL = 1L;
    // 缓存击穿时，互斥锁setnx的key前缀
    public static final String CACHE_LOCK_KEY = "cache:lock:";
    // 缓存击穿时，互斥锁setnx的有效期，一般为业务正常执行时间的十倍：单位秒
    public static final Long CACHE_LOCK_TTL = 10L;
    // 缓存击穿时，逻辑过期时间应该设为30分钟，这里为了方便测试设为20秒
    public static final Long CACHE_LOGIC_TTL = 20L;
    // redis分布式锁的key前缀
    public static final String REDIS_LOCK_KEY = "redis:lock:";
    // redis分布式锁的value前缀：UUID+当前线程id。因为不同jvm中的线程id可能相同
    public static final String REDIS_LOCK_UUID_VALUE = UUID.randomUUID().toString() + "-";

}
