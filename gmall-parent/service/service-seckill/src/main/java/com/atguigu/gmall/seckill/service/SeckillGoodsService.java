package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.model.activity.SeckillGoods;

import java.util.List;

/**
 * @author Weirdo
 * 日期2023/4/13 20:36
 * 秒杀商品服务接口
 */
public interface SeckillGoodsService {

    /**
     * 获取秒杀时间列表
     * @param time
     * @return
     */
    List getSeckillGoodsList(String time);

    /**
     * 获取指定时段指定商品
     * @param time
     * @param goodsId
     * @return
     */
    SeckillGoods getsecKillGoods(String time, String goodsId);

    /**
     * 同步数据库中的秒杀商品库存信息
     * @param time
     */
    void updateSeckillStock(String time);
}
