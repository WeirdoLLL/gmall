package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.gateway.util.IpUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * 包名:com.atguigu.gmall.gateway.filter
 *
 * @author Weirdo
 * 日期2023/4/7 14:37
 * 全局过滤器
 */
@Component
public class GmallFilter implements GlobalFilter, Ordered {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 自定义过滤器逻辑
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取用户请求的request对象
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (path.contains("/user/login")){
            //登陆 直接放行
            return chain.filter(exchange);
        }
        //获取一个空的响应对象
        ServerHttpResponse response = exchange.getResponse();
        //从url中获取token
        String token = request.getQueryParams().getFirst("token");
        //若url中没有 从head中获取
        if (StringUtils.isEmpty(token)){
            token = request.getHeaders().getFirst("token");
            //若head也没有 从cookie获取
            if (StringUtils.isEmpty(token)){
                HttpCookie httpCookie = request.getCookies().getFirst("token");
                if (httpCookie != null){
                    token = httpCookie.getValue();
                }
            }
        }
        //cookie中也没有token则拒绝请求
        if (StringUtils.isEmpty(token)){
            //设置响应的状态码 正常开发应该是响应 UNAUTHORIZED(401, "Unauthorized") 方便测试选择NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required")
            response.setStatusCode(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
            //拒绝请求 response.setComplete()-->到此结束 返回一个Model 空白的
            return response.setComplete();
        }
        //获取用户ip地址
        String gatwayIpAddress = IpUtil.getGatwayIpAddress(request);
        //携带了令牌 需要效验用户ip地址和令牌是否和登录的时候一致
        String redisToken = stringRedisTemplate.opsForValue().get(gatwayIpAddress);
        //用户请求到这里 说明本次请求是携带token的 那么redis应该存了ip对应的token
        // redis中这个ip对应的token为空 说明没登陆过 说明这个token是假的
        if (StringUtils.isEmpty(redisToken)){
            //设置响应的状态码 正常开发应该是响应 UNAUTHORIZED(401, "Unauthorized") 方便测试选择NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required")
            response.setStatusCode(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
            //拒绝请求 response.setComplete()-->到此结束 返回一个Model 空白的
            return response.setComplete();
        }
        //用户携带的token和redis中存储的不一致
        if (!token.equals(redisToken)){
            //设置响应的状态码 正常开发应该是响应 UNAUTHORIZED(401, "Unauthorized") 方便测试选择NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required")
            response.setStatusCode(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
            //拒绝请求 response.setComplete()-->到此结束 返回一个Model 空白的
            return response.setComplete();
        }
        //将令牌以固定的key和固定的格式存储到请求头
        request.mutate().header("Authorization","bearer "+redisToken);
        //将请求放行
        return chain.filter(exchange);
    }

    /**
     * 自定义过滤器的顺序
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
