package com.bjz.portal.spring.springDataRedis.config;

import io.lettuce.core.ReadFrom;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @Classname RedisConfig
 * @Description TODO
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@Configuration
public class RedisConfig {

    /**
     * @MethodName redisTemplate
     * @Description   重置key和value的序列化工具
     * @Param: redisConnectionFactory
     * @Return: org.springframework.data.redis.core.RedisTemplate<java.lang.String,java.lang.Object>
     * @Author BJZ
     * @Date
     **/
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        // 创建RedisTemplate对象
        RedisTemplate<String, Object> newRedisTemplate = new RedisTemplate<>();

        // 设置连接工厂：
        newRedisTemplate.setConnectionFactory(redisConnectionFactory);

        // 创建JSON序列化工具
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

        // key和hashKey采用Strin序列化：StringRedisSerializer
        newRedisTemplate.setKeySerializer(RedisSerializer.string());
        newRedisTemplate.setHashKeySerializer(RedisSerializer.string());

        // value和hashValue采用JSON序列化：GenericJackson2JsonRedisSerializer
        newRedisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        newRedisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);

        // 返回新的RedisTemplate
        return newRedisTemplate;
    }

    /**
     * @MethodName redissonClient
     * @Description  配置Redisson：不建议使用springboot的yml配置，因为会替换spring的redis一些配置
     * @Return: org.redisson.api.RedissonClient ： redisson的工厂类，代码里使用就是这个类
     * @Author BJZ
     * @Date
     **/
    @Bean
    public RedissonClient redissonClient(){
        // 配置redis信息
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.186.224:6379").setPassword("12345678");
        // 创建RedissonClient对象
        return Redisson.create(config);
    }

    /**
     * @MethodName lettuceClientConfigurationBuilderCustomizer
     * @Description  sentinel时 对Lettuce的自定义配置.来实现读写分离
     * @Return: org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer
     * @Author BJZ
     * @Date
     **/
    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer(){
        // 配置redis的读取策略,是一个枚举: ReadFrom.REPLICA_PREFERRED( 先从从节点读取数据，若读不到，再从主节点读取)
        return clientConfigurationBuilder -> clientConfigurationBuilder.readFrom(ReadFrom.REPLICA_PREFERRED);
    }


}
