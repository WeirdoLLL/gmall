package com.atguigu.gmall.oauth.service;

import com.atguigu.gmall.oauth.util.AuthToken;

/**
 * 包名:com.atguigu.gmall.oauth.service
 *
 * @author Weirdo
 * 日期2023/4/6 19:48
 * 自动以登录接口
 */
public interface LoginService {

    /**
     * 自定义登录
     * @param userName
     * @param password
     * @return
     */
    AuthToken login(String username, String password);
}
