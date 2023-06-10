package com.atguigu.gmall.payment.service;

import java.util.Map;

/**
 * @author Weirdo
 * 日期2023/4/11 20:17
 * 支付相关接口类
 */
public interface PaymentService {

    /**
     * 获取第三方支付的信息
     * @param payMap
     * @return
     */
    public String getPayInfo(Map<String,String> payMap);

    /**
     * 获取订单的支付结果
     * @param orderId
     * @return
     */
    public String getPayResult(String orderId);
}
