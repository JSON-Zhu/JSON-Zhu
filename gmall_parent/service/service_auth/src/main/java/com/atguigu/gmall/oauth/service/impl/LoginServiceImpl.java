package com.atguigu.gmall.oauth.service.impl;

import com.atguigu.gmall.oauth.service.LoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/**
 * LoginServiceImpl
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/9 19:41
 **/
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return : AuthToken
     */
    @Override
    public AuthToken login(String username, String password) {
        //校验登录名和密码
        if(username==null||password==null){
            throw new RuntimeException("用户名或密码不能为空");
        }
        //包装中的三个参数 用户名 密码 模式
        MultiValueMap params = new HttpHeaders();
        params.add("grant_type","password");
        params.add("username",username);
        params.add("password",password);
        //包装客户端id和密钥
        MultiValueMap headers = new HttpHeaders();
        headers.add("Authorization",getHeader());
        //对localhost:9001发起请求
        //String url="http://localhost:9001/oauth/token"; 更新为从负载均衡动态获取
        //以负载均衡的方式获取服务的实例
        ServiceInstance serviceInstance = loadBalancerClient.choose("service-oauth");
        //通过实例获取服务信息
        String url = serviceInstance.getUri().toString()+"/oauth/token";
        //获取请求的结果 解析令牌的数据
        ResponseEntity<Map> exchange =
                restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(params, headers), Map.class);
        //获取请求的结果解析令牌的数据
        Map<String,String> body = exchange.getBody();
        //返回结果
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(body.get("access_token"));
        authToken.setRefreshToken(body.get("refresh_token"));
        authToken.setJti(body.get("jti"));
        //返回
        return authToken;
    }

    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.clientSecret}")
    private String clientSecret;

    /**
     *构建请求头中的参数
     * @return : java.lang.String
     */
    private String getHeader(){
        //客户端id+:+客户端密钥
        String base64ClientCode=clientId+":"+clientSecret;
        //进行base64编码
        byte[] encode = Base64.getEncoder().encode(base64ClientCode.getBytes());
        //使用basic + 空格 +加密结果
        return "Basic "+ new String(encode);
    }
}
