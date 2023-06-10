package com.atguigu.gmall.oauth.controller;

import com.atguigu.gmall.common.result.Result;

import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.oauth.service.LoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 包名:com.atguigu.gmall.oauth.controller
 *
 * @author Weirdo
 * 日期2023/4/6 19:43
 * 自定义登录控制层
 */
@RestController
@RequestMapping("/user/login")
public class LoginController {
    @Resource
    private LoginService loginService;
    @Resource
    private HttpServletRequest request;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @GetMapping
    public Result<AuthToken> login(String username, String password){
        AuthToken login = loginService.login(username, password);
        //将用户ip地址与用户的令牌进行绑定存储用于后续令牌校验防止盗用
        String ipAddress = IpUtil.getIpAddress(request);
        stringRedisTemplate.opsForValue().set(ipAddress, login.getAccessToken());
        return Result.ok(login);


    }
}
