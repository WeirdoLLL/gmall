package com.atguigu.gmall.cart.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * @author Weirdo
 * 日期2023/4/9 18:26
 * 购物车feign调用接口
 */
@FeignClient(name = "service-cart",path = "/order/cart",contextId = "CartFeign")
public interface CartFeign {
    /**
     * 下单时使用的内部接口
     *
     * @return
     */
    @GetMapping("/getOrderAddInfo")
    Map<String, Object> getOrderAddInfo();

    /**
     * 删除购物车
     * @return
     */
    @GetMapping(value = "/deleteCart")
    public Boolean deleteCart();
}