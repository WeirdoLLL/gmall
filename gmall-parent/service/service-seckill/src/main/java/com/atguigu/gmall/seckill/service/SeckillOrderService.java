package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.seckill.pojo.UserRecode;

/**
 * @author Weirdo
 * 日期2023/4/14 9:16
 * 秒杀订单接口
 */
public interface SeckillOrderService {

    /**
     * 秒杀下单 其实是排队
     * @param time
     * @param goodsId
     * @param num
     * @return
     */
    UserRecode addSeckillOrder(String time, String goodsId, Integer num);

    /**
     * 查询排队状态
     * @return
     */
    UserRecode getUserRecode();

    /**
     * 秒杀订单真实处理下单
     * @param userRecodeString
     */
    void seckillOrderRealAddOrder(String userRecodeString);

    /**
     * 超时取消秒杀订单
     * @param username
     */
    void cancelSeckillOrder(String username);

    /**
     * 修改订单状态
     * 修改订单的支付结果
     * @param resultString
     */
    void updateSeckillOrder(String resultString);
}
