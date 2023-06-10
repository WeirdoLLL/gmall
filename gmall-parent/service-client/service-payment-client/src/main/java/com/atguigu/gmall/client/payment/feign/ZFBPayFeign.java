package com.atguigu.gmall.client.payment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Weirdo
 * 日期2023/4/13 13:51
 * 支付宝支付微服务feign调用接口
 */
@FeignClient(name ="service-payment" ,path = "/zfb/pay",contextId = "ZFBPayFeign")
public interface ZFBPayFeign {

    /**
     * 获取支付宝支付的页面信息
     * @param body
     * @param orderId
     * @param money
     * @return
     */
    @GetMapping(value = "/getPayInfo")
    public String getPaymentInfo(@RequestParam("body") String body,
                                 @RequestParam("orderId") String orderId,
                                 @RequestParam("money") String money);
}
