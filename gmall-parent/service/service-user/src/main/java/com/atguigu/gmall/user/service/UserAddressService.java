package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;

import java.util.List;

/**
 * @author Weirdo
 * 日期2023/4/8 14:14
 * 用户收货地址服务接口
 */
public interface UserAddressService {
    /**
     * 查询用户收货地址列表
     * @return
     */
    List<UserAddress> getUserAddress();
}
