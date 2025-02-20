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
 * @Classname LoginInterceptor
 * @Description 刷新token拦截器： 拦截一切路径/请求/页面
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */

@Slf4j
public class RefreshTokenInterceptor implements HandlerInterceptor {

    public StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("刷新token拦截器");

        // 从请求头中获取token
        String token = request.getHeader("token");
        if(StringUtils.isEmpty(token)){
            // 为空时直接放行
            return true;
        }

        // 根据token从redis中获取用户信息
        String tokenKey = LOGIN_TOKEN_KEY + token;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(tokenKey);
        if(userMap.isEmpty()){
            return true;
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
