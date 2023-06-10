package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Weirdo
 * 日期2023/4/13 10:30
 * 支付宝支付接口实现类
 */
@Service("zFBPaymentServiceImpl")
public class ZFBPaymentServiceImpl implements PaymentService {

    @Value("${alipay_url}")
    private String payUrl;

    @Value("${app_id}")
    private String appId;

    @Value("${app_private_key}")
    private String appPrivateKey;

    @Value("${alipay_public_key}")
    private String alipayPublicKey;

    @Value("${return_payment_url}")
    private String returnPaymentUrl;

    @Value("${notify_payment_url}")
    private String notifyPaymentUrl;

    /**
     * 获取第三方支付的信息
     *
     * @param payMap
     * @return
     */
    @Override
    public String getPayInfo(Map<String,String> payMap) {

        //支付宝支付客户端对象初始化
        AlipayClient alipayClient = new DefaultAlipayClient(
                payUrl,
                appId,
                appPrivateKey,
                "json",
                "UTF-8",
                alipayPublicKey,
                "RSA2");
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //异步接收地址，仅支持http/https，公网可访问
        request.setNotifyUrl(notifyPaymentUrl);
        //同步跳转地址，仅支持http/https
        request.setReturnUrl(returnPaymentUrl);
        /******必传参数******/
        JSONObject bizContent = new JSONObject();
        //商户订单号，商家自定义，保持唯一性
        bizContent.put("out_trade_no", "java0926000000" + payMap.get("orderId"));
        //支付金额，最小值0.01元
        bizContent.put("total_amount", payMap.get("money"));
        //订单标题，不可使用特殊符号
        bizContent.put("subject", payMap.get("body"));
        //电脑网站支付场景固定传值FAST_INSTANT_TRADE_PAY
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        //附加参数
        bizContent.put("passback_params", JSONObject.toJSONString(payMap));
        //保存请求参数
        request.setBizContent(bizContent.toString());
        try {
            //发起请求
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if (response.isSuccess()) {
                return response.getBody();
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    /**
     * 获取订单的支付结果
     *
     * @param orderId
     * @return
     */
    @Override
    public String getPayResult(String orderId) {
        //支付宝支付客户端对象初始化
        AlipayClient alipayClient = new DefaultAlipayClient(
                payUrl,
                appId,
                appPrivateKey,
                "json",
                "UTF-8",
                alipayPublicKey,
                "RSA2");
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        request.setBizContent(bizContent.toString());
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                return response.getBody();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

