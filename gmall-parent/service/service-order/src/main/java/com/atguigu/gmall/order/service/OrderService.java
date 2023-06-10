package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

import java.io.Serializable;

/**
 * @author Weirdo
 * 日期2023/4/9 18:13
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 新增订单
     * @param orderInfo
     */
    void addOrder(OrderInfo orderInfo);

    /**
     * 取消订单
     * @param orderId
     */
    void cancelOrder(Long orderId);

    /**
     * 获取订单支付状态
     * @return
     */
    String getOrderPayInfo(Long OrderId, String paymentChannel);

    /**
     * 修改订单支付状态
     * @param resultString
     */
    void updateOrder(String resultString);
}
