package com.bjz.portal.spring.springDataRedis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Classname RedisIdWorker
 * @Description 使用Redis的实现ID自增：基于Reids的String类型的自增长
 *              在单元测试类中测试
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@Component
public class RedisIdWorker {

    // 开始时间2008-01-01时间戳：31位
    private static final long BEGIN_TIMESTAMP = 1199145600l;
    // 左移32位
    private static final long LEFT_BITS = 32;

    @Autowired
    public StringRedisTemplate stringRedisTemplate;


    /**
     * @MethodName getId
     * @Description
     * @Param: key
     * @Return: long：id是8位共64bit位（符号位1bit(0正数)+时间戳31bit+序列号32bit）
     * @Author BJZ
     * @Date
     **/
    public long getId(String key){
        // 获取时间戳
        long nowSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond - BEGIN_TIMESTAMP;

        // 序列号：基于Reids的String类型的自增长(最大值2^64，有上线)，且我们这里用32位，很容易满，
        //        所以以天为单位作为key，也好统计每年/月/日的订单量。
        //        redis的Key的层级命名格式：多个单词之间用冒号:隔开
        String localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        long count = stringRedisTemplate.opsForValue().increment("icr:" + key + ":" + localDate);

        // 拼接
        long id = timestamp << LEFT_BITS | count;
        return id;
    }



    public static void main(String[] args) {
        LocalDateTime time = LocalDateTime.of(2008, 01, 01, 0, 0, 0);
        System.out.println(time.toEpochSecond(ZoneOffset.UTC));
    }

}
