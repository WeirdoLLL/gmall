package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.util.GmallThreadLocalUtil;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.service.UserAddressService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Weirdo
 * 日期2023/4/8 14:16
 * 用户收货地址服务接口实现类
 */
@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Resource
    private UserAddressMapper userAddressMapper;
    /**
     * 查询用户收货地址列表
     * @return
     */
    @Override
    public List<UserAddress> getUserAddress() {
    return userAddressMapper.selectList(
            new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, GmallThreadLocalUtil.get()));
    }
}
