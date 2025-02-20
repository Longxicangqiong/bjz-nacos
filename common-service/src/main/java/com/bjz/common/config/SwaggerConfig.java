package com.bjz.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Classname SwaggerConfig
 * @Description TODO
 * @Author BJZ
 * @Version 1.0.0
 * @Date
 */
@Configuration
@EnableSwagger2  // 访问地址 http://localhost:8081/swagger-ui.html
public class SwaggerConfig {


    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.bjz"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * @MethodName apiInfo
     * @Description
     * @Return: springfox.documentation.service.ApiInfo
     * @Author BJZ
     * @Date
     **/
    private ApiInfo apiInfo() {  //这个方法也可以不要
        return new ApiInfoBuilder()
                .title("Your API Docs") // API文档的标题
                .description("Your API description") // API文档的描述
                .version("1.0") // API的版本
                .build();
    }




}
