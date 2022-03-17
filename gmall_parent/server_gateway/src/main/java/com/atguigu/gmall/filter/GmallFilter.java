package com.atguigu.gmall.filter;

import com.atguigu.gmall.utils.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * GmallFilter 全部过滤器类
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/12 0:02
 **/
@Component
public class GmallFilter implements GlobalFilter, Ordered {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 过滤器的自定义逻辑
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取用户的请求体 响应体
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //检查url是否有token
        String token = request.getQueryParams().getFirst("token");
        //如果没有token,继续检查header里面是否有token
        if(StringUtils.isEmpty(token)){
            token=request.getHeaders().getFirst("token");
            if(StringUtils.isEmpty(token)){
                //检查cookies是否有token
                HttpCookie cookie = request.getCookies().getFirst("token");
                if(cookie!=null){
                    token=cookie.getValue();
                    String name = cookie.getName();
                }
            }
        }
        //都无,拒绝请求
        if(StringUtils.isEmpty(token)){
            response.setStatusCode(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE);
            return response.setComplete();
        }
        //比较redis中存储的token,相同则放行,不同或者redis中的token为空都拒绝访问
        String gatewayIpAddress = IpUtil.getGatewayIpAddress(request);
        String redisToken = stringRedisTemplate.opsForValue().get(gatewayIpAddress);
        if(redisToken==null||!token.equals(redisToken)){
            response.setStatusCode(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE);
            return response.setComplete();
        }
        //转发微服务之前,完善token的格式
        request.mutate().header("Authorization","bearer "+token);
        //放行
        return chain.filter(exchange);
    }

    /**
     * 过滤器的执行属性
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
