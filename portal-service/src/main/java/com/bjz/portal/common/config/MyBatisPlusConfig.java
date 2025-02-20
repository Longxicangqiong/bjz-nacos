package com.bjz.portal.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname MyBatisPlusConfig
 * @Description MyBatis-Plus配置类
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */

//@MapperScan("com.bjz.portal")
@Configuration
public class MyBatisPlusConfig {

    /**
     * @MethodName mybatisPlusInterceptor
     * @Description  重写MyBatis-Plus拦截器
     * @Return: com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
     * @Author BJZ
     * @Date
     **/
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        // 初始化核心插件
        MybatisPlusInterceptor myBatisPlusInterceptor = new MybatisPlusInterceptor();

        // 分页插件：说明数据库类型(不同数据库的底层的分页原理不一样)
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setMaxLimit(1000l);  // 设置分页上线
        myBatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor);

        // MyBatis-Plus乐观锁插件
        myBatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        return myBatisPlusInterceptor;
    }



    /**
     * @MethodName paginationInterceptor
     * @Description  配置分页插件
     * @Return: PaginationInterceptor
     * @Author BJZ
     * @Date
     **/
/*    @Bean
    public PaginationInnerInterceptor paginationInterceptor(){
        return new PaginationInnerInterceptor();
    }*/




}
