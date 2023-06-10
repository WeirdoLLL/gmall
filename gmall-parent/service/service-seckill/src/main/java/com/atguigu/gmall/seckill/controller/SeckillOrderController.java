package com.atguigu.gmall.seckill.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.seckill.service.SeckillOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Weirdo
 * 日期2023/4/14 10:05
 * 秒杀订单控制层
 */
@RestController
@RequestMapping(value = "/order/seckill")
public class SeckillOrderController {

    @Resource
    private SeckillOrderService seckillOrderService;

    /**
     * 秒杀下单: 假下单真排队
     * @param time
     * @param goodsId
     * @param num
     * @return
     */
    @GetMapping(value = "/addSeckillOrder")
    public Result addSeckillOrder(String time, String goodsId, Integer num){
        return Result.ok(seckillOrderService.addSeckillOrder(time, goodsId, num));
    }

    /**
     * 查询用户的排队状态
     * @return
     */
    @GetMapping(value = "/getUserRecode")
    public Result getUserRecode(){
        return Result.ok(seckillOrderService.getUserRecode());
    }



}
