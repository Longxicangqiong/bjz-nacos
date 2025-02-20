package com.bjz.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import java.util.Objects;
import java.util.Optional;

/**
 * @Classname Res
 * @Description 前后端分离的数据交互标准
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Res<T> {

    /**
     * 响应状态码
     **/
    private String code;

    /**
     * 响应信息
     **/
    private String message;

    /**
     * 响应数据
     **/
    private T data;



    /**
     * 状态码枚举
     **/
    @Getter
    @AllArgsConstructor
    enum StatusCode{

        SUCCESS("200", "请求成功"),
        ERROR("400", "请求失败"),
        UNAUTHORIZED("401", "用户名或密码错误"),
        SERVICE_UNAVALIABLE("500", "服务器不可用");

        /**
         * 响应状态码
         **/
        private String key;

        private String value;

    }


    @Override
    public String toString() {
        return "Res{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }



    public static Res ok(){
        return ok(null,null,null);
    }

    public static <C> Res<C> ok(C data){
        return ok(null, null, data);
    }

    public static <C> Res ok(String code, String message, C data){
        return Res.builder().code(Optional.ofNullable(code).orElse(StatusCode.SUCCESS.getKey()))
                .message(Optional.ofNullable(message).orElse(StatusCode.SUCCESS.getValue()))
                .data(Optional.ofNullable(data).orElse(null)).build();
    }

    public static Res error(){
        return error(null,null,null);
    }

    public static <C> Res<C> error(C data){
        return error(null, null, data);
    }

    public static <C> Res error(String code, String message, C data){
        return Res.builder().code(Optional.ofNullable(code).orElse(StatusCode.ERROR.getKey()))
                .message(Optional.ofNullable(message).orElse(StatusCode.ERROR.getValue()))
                .data(Optional.ofNullable(data).orElse(null)).build();
    }


    /**
     * @MethodName isOk
     * @Description 判断请求是否成功
     * @Param: res
     * @Return: java.lang.Boolean
     * @Author BJZ
     * @Date
     **/
    public Boolean isOk(Res res){
        return StatusCode.SUCCESS.getKey().equals(getCode());
    }


    /**
     * @MethodName isDataEmpty
     * @Description  判断响应数据是否为空
     * @Return: java.lang.Boolean
     * @Author BJZ
     * @Date
     **/
    public Boolean isDataEmpty(){
        return Objects.isNull(this) || Objects.isNull(getData());
    }


    /**
     * @MethodName transferData
     * @Description data响应数据转换为对应的实体类
     *                解决feignClient调用接口，不指定泛型数据导致responseContent变为了LinkedHashMap类型的情况，
     * @Param: clazz
     * @Return: T
     * @Author BJZ
     * @Date
     **/
    public static <T> T transferData(Res res, Class<T> clazz){
        T t = null;
        if(!res.isDataEmpty()){
            ObjectMapper objectMapper = new ObjectMapper();
            t = objectMapper.convertValue(res.getData(), clazz);
        }
        return t ;
    }

    public static <T> T transferData(Res res, TypeReference<T> typeReference){
        T t = null;
        if(!res.isDataEmpty()){
            ObjectMapper objectMapper = new ObjectMapper();
            t = objectMapper.convertValue(res.getData(), typeReference);
        }
        return t;
    }







}

