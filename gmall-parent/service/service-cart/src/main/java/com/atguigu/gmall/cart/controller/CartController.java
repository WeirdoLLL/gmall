package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Weirdo
 * 日期2023/4/9 18:56
 * 供内部调用购物车控制层
 */
@RestController
@RequestMapping("/order/cart")
public class CartController {

    @Resource
    private CartInfoService cartInfoService;

    /**
     * 下单时使用的内部接口
     * @return
     */
    @GetMapping("/getOrderAddInfo")
    public Map<String, Object> getOrderAddInfo(){
        return cartInfoService.getOrderConfirmCartInfo();
    }

    /**
     * 删除购物车
     * @return
     */
    @GetMapping(value = "/deleteCart")
    public Boolean deleteCart(){
        try {
            return cartInfoService.deleteCart();
        }catch (Exception e){
            return false;
        }
    }
}
