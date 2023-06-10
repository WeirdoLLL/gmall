package com.atguigu.gmall.oauth.service.impl;

import com.atguigu.gmall.oauth.service.LoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Base64;
import java.util.Map;

/**
 * 包名:com.atguigu.gmall.oauth.service.impl
 *
 * @author Weirdo
 * 日期2023/4/6 19:50
 * 自定义登录接口实现类
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private LoadBalancerClient loadBalancerClient;
    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;
    /**
     * 自定义登录
     *  @param username
     * @param password
     * @return
     */
    @Override
    public AuthToken login(String username, String password) {
        //参数校验
        if (StringUtils.isEmpty(username) ||
                StringUtils.isEmpty(password)){
            throw new RuntimeException("用户名和密码不能为空");
        }
        //请求头声明 2个参数 username password
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.set("Authorization",getHeadersParam());
        //请求体声明 3个参数 username password grant_type
        MultiValueMap<String, String> body = new HttpHeaders();
        body.set("username",username);
        body.set("password",password);
        body.set("grant_type","password");
        //包装请求参数
        HttpEntity httpEntity = new HttpEntity(body,headers);
//        //像固定地址发送post请求 硬编码 ↓
//        String url = "http://localhost:9001/oauth/token";
        //像固定地址发送post请求
        ServiceInstance choose = loadBalancerClient.choose("service-oauth");
        String url = choose.getUri().toString()+"/oauth/token";

        /**
         * 1 请求地址 2 请求类型 3 请求实体 4 响应结果集类型
         */
        ResponseEntity<Map> exchange =
                restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);

        //获取返回结果
        Map<String,String> result = exchange.getBody();
        //初始化一个token
        AuthToken authToken = new AuthToken();
        //放入需要的token信息
        authToken.setAccessToken(result.get("access_token"));
        authToken.setRefreshToken(result.get("refresh_token"));
        authToken.setJti(result.get("jti"));
        //返回token
        return authToken;
    }

    /**
     * 获取拼接Headers
     * @return
     */
    private String getHeadersParam() {
        //拼接客户端id和秘钥
        String str = clientId +":"+ clientSecret;
        //使用Base64加密
        byte[] encode = Base64.getEncoder().encode(str.getBytes());
        //返回Base64加密后 字节转字符串header
        return "Basic " + new String(encode);

    }

    public static void main(String[] args) {
        byte[] decode = Base64.getDecoder().decode("$2a$10$KM6vHlq//BrgNat8zZRzXOKPln3uJPOke3XU4bWqM5qPsjz0hwLhO");
        System.out.println(new String(decode));
    }
}
