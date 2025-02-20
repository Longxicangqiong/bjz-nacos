package com.bjz.portal.spring.springDataRedis.interceptor;

import com.bjz.common.pojo.User;
import com.bjz.portal.spring.springDataRedis.vo.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Classname LoginInterceptor
 * @Description 登录拦截器： 拦截那些需要登录才能访问的请求/页面
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("登录状态校验");

        // ThreadLocal中获取用户
        User user = UserHolder.getUser();
        if(null == user){
            // 拦截，返回状态码401
            response.setStatus(401);
            return false;
        }

        return true;
    }

}
