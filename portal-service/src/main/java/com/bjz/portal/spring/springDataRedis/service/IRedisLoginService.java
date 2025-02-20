package com.bjz.portal.spring.springDataRedis.service;

import com.bjz.common.util.Res;
import com.bjz.portal.spring.springDataRedis.vo.LoginVO;

/**
 * @Classname IRedisLoginService
 * @Description TODO
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
public interface IRedisLoginService {

    /**
     * @MethodName sendMessageCode
     * @Description 发送短信
     * @Param: phone
     * @Return: com.bjz.common.util.Res
     * @Author BJZ
     * @Date
     **/
    Res sendMessageCode(String phone);

    /**
     * @MethodName loginMessageCode
     * @Description 短信验证码登录/注册
     * @Param: loginVO 
     * @Return: com.bjz.common.util.Res
     * @Author BJZ
     * @Date
     **/
    Res loginMessageCode(LoginVO loginVO);
}
