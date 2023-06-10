package com.atguigu.gmall.payment.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.payment.service.PaymentService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Weirdo
 * 日期2023/4/13 10:47
 * 支付宝支付微服务控制层
 */
@RestController
@RequestMapping("/zfb/pay")
public class ZFBPayController {

    @Resource
    private PaymentService zFBPaymentServiceImpl;
    @Resource
    private RabbitTemplate rabbitTemplate;
    /**
     * 获取支付宝支付的页面信息
     * @param payMap
     * @return
     */
    @GetMapping(value = "/getPayInfo")
    public String getPaymentInfo(@RequestParam("payMap") Map<String,String> payMap){
        return zFBPaymentServiceImpl.getPayInfo(payMap);
    }

    /**
     * 兜底方法 主动查询支付结果
     * @param orderId
     * @return
     */
    @GetMapping(value = "/getPayResult")
    public String getPayResult(@RequestParam("orderId")String orderId){
        return zFBPaymentServiceImpl.getPayResult(orderId);
    }

    /**
     * 异步通知: 接受支付宝支付的通知结果的接口
     * @param notifyMap
     * @return
     */
    @RequestMapping(value = "/notify/address")
    public String zfbNotifyAddress(@RequestParam Map<String, String> notifyMap,String orderId){
        //支付宝这个url还没有附加数
        String zfbNotifyUrl = "{\"gmt_create\":\"2023-04-13 11:32:25\",\"charset\":\"UTF-8\",\"gmt_payment\":\"2023-04-13 11:32:49\",\"notify_time\":\"2023-04-13 11:32:50\",\"subject\":\"zfb_body\",\"sign\":\"hkcba3zRq48H/Eof6uLpbyJvp47G0+SNxRL0kQ4tfAorRIj6cvyKD1mpxbzoj9zL1uDHppE41ZW+lN0g5NtlqS0U9+TJ7YH9g4CNPYScHtGNEdU4TgjZyyPAnmEB3VQH/PCOcLtnULKAGBca7ryRYjxt2LgTZBdIbJh59S4sD897/GiZLkLxj15xDE9yLck478SE9Cdfe+f4de0hODJ7J+ojwxi4UVkNKFZR00DjquhJdf7hlnkPgS4p7fwzm39gmtSaYdbD2QyPthBMgBtXysnnT8Sn+qESG/C082sDSfKuG4RxQMuP5GDMYZUy5ZHHms8ECxad7yeekexMjL1CuA==\",\"buyer_id\":\"2088022855662424\",\"invoice_amount\":\"0.01\",\"version\":\"1.0\",\"notify_id\":\"2023041301222113250062421436213187\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"out_trade_no\":\"java092600000023413\",\"total_amount\":\"0.01\",\"trade_status\":\"TRADE_SUCCESS\",\"trade_no\":\"2023041322001462421411612385\",\"auth_app_id\":\"2021001163617452\",\"receipt_amount\":\"0.01\",\"point_amount\":\"0.00\",\"buyer_pay_amount\":\"0.01\",\"app_id\":\"2021001163617452\",\"sign_type\":\"RSA2\",\"seller_id\":\"2088831489324244\"}";
        Map<String,String> zfbNotifyMap = JSONObject.parseObject(zfbNotifyUrl, Map.class);
        zfbNotifyMap.put("out_trade_no", orderId);
        //记录支付渠道
        zfbNotifyMap.put("payChannel","ZFB");
        //获取回传参数
        String passbackParamsString = zfbNotifyMap.get("passback_params");
        //反序列化
        Map<String, String> map = JSONObject.parseObject(passbackParamsString, Map.class);
        //支付成功 将直接结果发送至消息队列
        rabbitTemplate.convertAndSend(map.get("exchange"),
                map.get("routingKey"),
                JSONObject.toJSONString(zfbNotifyMap));
        return "success";
    }

    /**
     * 同步回调: 将用户带回商城
     * @param notifyMap
     * @return
     */
    @RequestMapping(value = "/return/address")
    public String zfbReturnAddress(@RequestParam Map<String, String> notifyMap){
        System.out.println("同步通知的内容为: " + JSONObject.toJSONString(notifyMap));
        return "开打支付成功页面!";
    }
}
