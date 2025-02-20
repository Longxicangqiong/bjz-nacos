package com.bjz.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoginFilter implements GlobalFilter, Ordered {

    /**
     * 自定义业务的过滤逻辑
     * @param exchange  请求上下文，可以获取request/response等信息
     * @param chain      链
     * @return Mono      WebFlux中的API：响应式编程
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求参数
        ServerHttpRequest request = exchange.getRequest();
        MultiValueMap<String, String> params = request.getQueryParams();
        String username = params.getFirst("username");
        // 通过放行
        if("admin".equals(username)){
            return chain.filter(exchange);
        } else{
            // 设置状态码：未登录401
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            // 请求拦截
            return response.setComplete();
        }

    }

    @Override
    public int getOrder() {
        return -1;
    }

}
