package com.bjz.portal.spring.springDataRedis.service.impl;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.bjz.common.feign.UserFeign;
import com.bjz.common.pojo.User;
import com.bjz.common.util.Res;
import com.bjz.portal.spring.springDataRedis.service.IRedisLoginService;
import com.bjz.portal.spring.springDataRedis.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.bjz.common.constants.RedisConstants.*;

/**
 * @Classname RedisLoginService
 * @Description TODO
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@Service
@Slf4j
@Primary
public class RedisLoginServiceImpl implements IRedisLoginService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserFeign userFeign;


    @Override
    public Res sendMessageCode(String phone) {
        log.info("校验手机号");

        // 生成六位随机验证码
        String code = RandomUtil.randomNumbers(6);

        // 验证码存入redis中
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);

        log.info("给手机号发送验证码:{}", code);

        return Res.ok(code);
    }

    @Override
    public Res loginMessageCode(LoginVO loginVO) {
        log.info("校验手机号");

        // 从redis中获取验证码
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + loginVO.getPhone());
        // 判断验证码是否正确
        if(StringUtils.isEmpty(cacheCode) || !cacheCode.equals(loginVO.getCode())){
            return Res.error("验证码错误");
        }

        // TODO
        log.info("根据手机号查询用户并判断用户状态，不存在就创建新用户/注册");
        User user = new User(123L, "曹操", "陈留");
        Map<String, Object> data = BeanUtil.beanToMap(user,
                new HashMap<>(),
                // 自定义：忽略null值，并将value做处理
                CopyOptions.create().ignoreNullValue().setFieldValueEditor((key,value)-> value.toString()));

        // 生成token作为登录令牌：以UUID为token，因为手机号敏感信息不能回显暴露
        String token = UUID.randomUUID().toString(true);
        String tokenKey = LOGIN_TOKEN_KEY + token;
        // 将用户以Hash形式存入redis中
        stringRedisTemplate.opsForHash().putAll(tokenKey, data);
        stringRedisTemplate.expire(tokenKey, LOGIN_TOKEN_TTL, TimeUnit.MINUTES);

        // 返回token
        return Res.ok(token);
    }

}
