package com.bjz.portal.spring.springDataRedis.controller;

import com.bjz.common.util.Res;
import com.bjz.portal.spring.springDataRedis.service.IRedisLoginService;
import com.bjz.portal.spring.springDataRedis.vo.LoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Classname LoginController
 * @Description 基于Redis实现短信登录
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@RestController
@RequestMapping("/login")
public class RedisLoginController {

    @Autowired
    private IRedisLoginService redisLoginService;



    @ApiOperation("发送短信验证码")
    @GetMapping("/sendMessageCode")
    public Res sendMessageCode(@RequestParam String phone){
        return redisLoginService.sendMessageCode(phone);
    }

    @ApiOperation("短信验证码登录/注册")
    @PostMapping("/loginMessageCode")
    public Res loginMessageCode(@RequestBody LoginVO loginVO){
        return redisLoginService.loginMessageCode(loginVO);
    }


}
