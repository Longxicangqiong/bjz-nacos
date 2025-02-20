package com.bjz.portal.spring.springDataRedis.vo;

import com.bjz.portal.common.pojo.SystemMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;

/**
 * @Classname RedisData
 * @Description 缓存击穿的热点数据：带逻辑过期时间
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@Data
@AllArgsConstructor
public class RedisCacheData {

    @ApiModelProperty("逻辑过期时间")
    public LocalDateTime expireTime;

    @ApiModelProperty("热点数据")
    public Object data;

}
