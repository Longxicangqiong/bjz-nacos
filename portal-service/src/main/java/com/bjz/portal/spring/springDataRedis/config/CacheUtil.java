package com.bjz.portal.spring.springDataRedis.config;

import com.bjz.portal.spring.springDataRedis.service.ICacheService;
import com.bjz.portal.spring.springDataRedis.service.impl.CacheServiceC;
import com.bjz.portal.spring.springDataRedis.service.impl.CacheServiceS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import static org.springframework.context.ApplicationContext.*;

/**
 * @Classname CacheUtil
 * @Description TODO
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@Slf4j
public class CacheUtil {

    private static ICacheService cacheService;

    public static ICacheService getCache(){
        if(null == cacheService){
            try {
                if("single".equals("")){
                    cacheService = ApplicationContext.getBean(CacheServiceS.class);
                }else{
                    cacheService = ApplicationContext.getBean(CacheServiceC.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("初始化缓存实例失败");
            }
        }
        return cacheService;
    }

}
