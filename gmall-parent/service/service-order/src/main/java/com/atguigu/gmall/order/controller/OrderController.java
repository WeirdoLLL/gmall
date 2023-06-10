package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单相关的控制层
 */
@RestController
@RequestMapping(value = "/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 新增订单
     * @param orderInfo
     * @return
     */
    @PostMapping(value = "/addOrder")
    public Result addOrder(@RequestBody OrderInfo orderInfo){
        orderService.addOrder(orderInfo);
        return Result.ok();
    }

    /**
     * 取消订单
     * @param orderId
     * @return
     */
    @GetMapping(value = "/cancelOrder")
    public Result cancelOrder(@RequestParam Long orderId){
        orderService.cancelOrder(orderId);
        return Result.ok();
    }

    /**
     * 获取订单支付信息
     * @param orderId
     * @return
     */
    @GetMapping(value = "/getOrderPayInfo")
    public Result getOrderPayInfo(@RequestParam Long orderId,String paymentChannel){
        return Result.ok(orderService.getOrderPayInfo(orderId,paymentChannel));
    }

}
