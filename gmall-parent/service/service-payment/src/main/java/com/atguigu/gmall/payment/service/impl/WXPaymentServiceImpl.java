package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.util.HttpClient;
import com.atguigu.gmall.payment.service.PaymentService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Weirdo
 * 日期2023/4/11 20:21
 */
@Service("wXPaymentServiceImpl")
public class WXPaymentServiceImpl implements PaymentService {

    @Value("${weixin.pay.appid}")
    private String appId;

    @Value("${weixin.pay.partner}")
    private String partner;

    @Value("${weixin.pay.partnerkey}")
    private String partnerkey;

    @Value("${weixin.pay.notifyUrl}")
    private String notifyUrl;

    /**
     * 获取第三方支付的信息
     * @param payMap
     * @return
     */
    @Override
    public String getPayInfo(Map<String,String> payMap) {
        //参数校验
        if (StringUtils.isEmpty(payMap.get("body")) ||
                StringUtils.isEmpty(payMap.get("orderId")) ||
                StringUtils.isEmpty(payMap.get("money"))) {
            return null;
        }
        try {
            Thread.sleep(10000);
            System.out.println("--------------------------"+Thread.currentThread().getName()+payMap.get("orderId"));
            //校验传入参数后 拼接参数
            //拼接参数
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("appid", appId);
            paramsMap.put("mch_id", partner);
            paramsMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paramsMap.put("body", payMap.get("body"));
            paramsMap.put("out_trade_no", "java0926000000" + payMap.get("orderId"));
            paramsMap.put("total_fee", payMap.get("money"));
            paramsMap.put("spbill_create_ip", "192.168.200.1");
            paramsMap.put("notify_url", notifyUrl);
            paramsMap.put("trade_type", "NATIVE");
            //包装附加数据
            Map<String,String> attachMap = new HashMap<>();
            attachMap.put("exchange",payMap.get("exchange"));
            attachMap.put("routingKey",payMap.get("routingKey"));
            if(!StringUtils.isEmpty(payMap.get("username"))){
                attachMap.put("username", payMap.get("username"));
            }
            //存储附加数据,在通知的时候会原样返回
            paramsMap.put("attchMap",JSONObject.toJSONString(attachMap));
            //将参数转换为xml
            String xmlParamsString = WXPayUtil.generateSignedXml(paramsMap, partnerkey);
            //像固定的api地址发送post请求
            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            //初始化客户端 发送post请求
            HttpClient httpClient = new HttpClient(url);
            httpClient.setXmlParam(xmlParamsString);
            httpClient.setHttps(true);
            httpClient.post();
            //获取响应的结果为xml数据
            String resultXmlString = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXmlString);
            //判断协议字段 判断业务字段 都为success 获取二维码地址
            if (resultMap.get("result_code").equals("SUCCESS") &&
                    resultMap.get("return_code").equals("SUCCESS")){
                return resultMap.get("code_url");
            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
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
        //参数校验
        if (StringUtils.isEmpty(orderId)){
            return null;
        }
        try{
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("appid", appId);
        paramsMap.put("mch_id", partner);
        paramsMap.put("out_trade_no", "java0926000000" + orderId);
        paramsMap.put("nonce_str", WXPayUtil.generateNonceStr());
        //将参数转换为xml
        String xmlParamsString = WXPayUtil.generateSignedXml(paramsMap, partnerkey);
        //像固定的api地址发送post请求
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
        //引入httpclient客户端
        HttpClient httpClient = new HttpClient(url);
        httpClient.setXmlParam(xmlParamsString);
        httpClient.setHttps(true);
        httpClient.post();
        //获取响应的结果为xml数据
        String resultXmlString = httpClient.getContent();
        //解析xml数据
        Map<String, String> resultMap =
                WXPayUtil.xmlToMap(resultXmlString);
        //判断协议字段 判断业务字段,都为success,获取二维码地址
        return JSONObject.toJSONString(resultMap);
    }catch (Exception e){
        e.printStackTrace();
    }
        return null;
}

}
