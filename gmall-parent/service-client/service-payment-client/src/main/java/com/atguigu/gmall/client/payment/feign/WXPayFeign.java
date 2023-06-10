package com.atguigu.gmall.client.payment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Weirdo
 * 日期2023/4/12 20:05
 */
@FeignClient(name ="service-payment" ,path ="/wx/pay" ,contextId = "WXPayFeign")
public interface WXPayFeign {

    /**
     * 获取微信支付的二维码信息
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/getPayInfo")
    public String getPayInfo(@RequestParam("body") String body,
                             @RequestParam("orderId") String orderId,
                             @RequestParam("money") String money);
}
