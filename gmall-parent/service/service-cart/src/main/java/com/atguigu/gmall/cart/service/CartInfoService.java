package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;
import java.util.Map;

/**
 * @author Weirdo
 * 日期2023/4/7 18:50
 * 购物车服务接口
 */
public interface CartInfoService {

    /**
     * 购物车新增
     * @param skuId
     * @param num
     */
    void addCart(Long skuId,Integer num);

    /**
     * 查询购物车信息
     * @return
     */
    List<CartInfo> getCartInfo();

    /**
     * 删除购物车数据
     * @param id
     */
    void removeCart(Long id);

    /**
     * 更新选中和取消选中的状态
     * @param id
     * @param status
     */
    void updateCheckStatus(Long id,Short status);

    /**
     * 合并购物车 批量新增
     * @param cartInfoList
     */
    void mergeCartInfo(List<CartInfo> cartInfoList);

    /**
     * 提交订单 获取确认购物车数据
     * @return
     */
    Map<String, Object> getOrderConfirmCartInfo();

    /**
     * 下单完成以后,清理本次购买的购物车数据
     *
     * @return
     */
    Boolean deleteCart();
}
