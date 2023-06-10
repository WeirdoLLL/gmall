package com.atguigu.gmall.payment.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.payment.service.PaymentService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Weirdo
 * 日期2023/4/11 21:25
 */
@RestController
@RequestMapping("/wx/pay")
public class WxPayController {

    @Resource
    private PaymentService wXPaymentServiceImpl;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 获取微信支付的二维码信息
     *
     * @param payMap
     * @return
     */
    @GetMapping(value = "/getPayInfo")
    public String getPayInfo(@RequestParam("payMap") Map<String,String> payMap) {
        return wXPaymentServiceImpl.getPayInfo(payMap);
    }

    /**
     * 查询订单的支付结果
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/getPayResult")
    public String getPayResult(@RequestParam("orderId") String orderId) {
        return wXPaymentServiceImpl.getPayResult(orderId);
    }

    /**
     * 微信回调通知地址
     *
     * @param request
     * @param orderId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/notify/address")
    public String wxNotifyAddress(HttpServletRequest request, String orderId) throws Exception {
//        //获取微信回调通知数据流
//        ServletInputStream inputStream = request.getInputStream();
//        //定义输出流
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        //定义缓冲区
//        byte[] buffer = new byte[1024];
//        //定义读取长度
//        int len = 0;
//        //从输入流读取到缓冲区再写入输出流
//        while((len = inputStream.read(buffer)) != -1){
//            outputStream.write(buffer,0,len);
//        }
//        //获取输出流字节码
//        byte[] bytes = outputStream.toByteArray();
//        //转成字符串
//        String xmlPayResult = new String(bytes);
//        //xml转map
//        Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlPayResult);
        String result = "{\"transaction_id\":\"4200001821202304120303576367\",\"nonce_str\":\"1e417d6d858a455ab86b49f21afab577\",\"bank_type\":\"OTHERS\",\"openid\":\"oHwsHuDnttIJ7ZimbZzd38ML8k4Y\",\"sign\":\"77AFA36BF3233CF534E6BF1BC815A6AA\",\"fee_type\":\"CNY\",\"mch_id\":\"1558950191\",\"cash_fee\":\"1\",\"out_trade_no\":\"java092600000023412\",\"appid\":\"wx74862e0dfcf69954\",\"total_fee\":\"1\",\"trade_type\":\"NATIVE\",\"result_code\":\"SUCCESS\",\"attach\":\"{\\\"exchange\\\":\\\"payment_exchange\\\",\\\"routingKey\\\":\\\"pay.seckill.order\\\",\\\"username\\\":\\\"ldx\\\"}\",\"time_end\":\"20230412111303\",\"is_subscribe\":\"N\",\"return_code\":\"SUCCESS\"}";
        Map<String, String> map = JSONObject.parseObject(result, Map.class);
        map.put("out_trade_no",orderId);
        //记录支付渠道为WX渠道
        map.put("payChannel","WX");
        //获取附加数据
        String attachString = map.get("attach");
        //附加数据反序列化
        Map<String, String> attach = JSONObject.parseObject(attachString, Map.class);
        //发送支付结果消息
        rabbitTemplate.convertAndSend(attach.get("exchange"),
                attach.get("routingKey"),
                JSONObject.toJSONString(map));
        System.out.println(JSONObject.toJSONString(map));
//        //关闭流
//        inputStream.close();
//        outputStream.close();;
        //定义响应结果
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("return_code", "SUCCESS");
        responseMap.put("return_msg", "OK");
        //响应微信
        return WXPayUtil.mapToXml(responseMap);
    }
}
