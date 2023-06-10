package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.service.UserAddressService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Weirdo
 * 日期2023/4/8 14:20
 * 用户收货地址控制层
 */
@RestController
@RequestMapping("/api/user")
public class UserAddressController {

    @Resource
    private UserAddressService userAddressService;

    /**
     * 查询用户收货地址列表
     * @return
     */
    @GetMapping("/getUserAddress")
    public Result<List<UserAddress>> getUserAddress(){
        return Result.ok(userAddressService.getUserAddress());
    }
}
