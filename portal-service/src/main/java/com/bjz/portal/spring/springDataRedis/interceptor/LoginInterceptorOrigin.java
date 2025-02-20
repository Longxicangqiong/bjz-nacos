package com.bjz.portal.spring.springDataRedis.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.bjz.common.pojo.User;
import com.bjz.portal.spring.springDataRedis.vo.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.bjz.common.constants.RedisConstants.LOGIN_TOKEN_KEY;
import static com.bjz.common.constants.RedisConstants.LOGIN_TOKEN_TTL;

/**
 * @Classname LoginInterceptorOrigin
 * @Description 登录拦截器： 会拦截那些需要登录才能访问的请求/页面，但有的请求/页面不需要登录也能浏览访问。
 *                        当用户访问这些页面时，token就不会自动刷新
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */

@Slf4j
public class LoginInterceptorOrigin implements HandlerInterceptor {

    public StringRedisTemplate stringRedisTemplate;

    public LoginInterceptorOrigin(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("登录状态校验");

        // 从请求头中获取token
        String token = request.getHeader("token");
        if(StringUtils.isEmpty(token)){
            // token不存在，拦截，返回状态码401
            response.setStatus(401);
            return false;
        }

        // 根据token从redis中获取用户信息
        String tokenKey = LOGIN_TOKEN_KEY + token;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(tokenKey);
        if(userMap.isEmpty()){
            response.setStatus(401);
            return false;
        }
        // 转换时有异常错误就抛出
        User user = BeanUtil.fillBeanWithMap(userMap, new User(), false);

        // 将用户存到Threadlocal中
        UserHolder.saveUser(user);

        // 刷新token有效期
        stringRedisTemplate.expire(tokenKey, LOGIN_TOKEN_TTL, TimeUnit.MINUTES);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
