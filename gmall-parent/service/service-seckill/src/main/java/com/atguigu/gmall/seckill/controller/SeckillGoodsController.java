package com.atguigu.gmall.seckill.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.atguigu.gmall.seckill.util.DateUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author Weirdo
 * 日期2023/4/13 20:32
 */
@RestController
@RequestMapping("/goods/seckill")
public class SeckillGoodsController {

    @Resource
    private SeckillGoodsService seckillGoodsService;
    /**
     * 获取秒杀的5个时间段
     * @return
     */
    @GetMapping("/getSeckillTimes")
    public Result<List<Date>> getSeckillTimes(){
        return Result.ok(DateUtil.getDateMenus());
    }
    /**
     * 查询指定时间段的商品列表
     * @param time
     * @return
     */
    @GetMapping(value = "/getSeckillGoodsList")
    public Result<List> getSeckillGoodsList(String time){
        return Result.ok(seckillGoodsService.getSeckillGoodsList(time));
    }

    /**
     * 查询指定的秒杀商品
     * @param time
     * @param goodsId
     * @return
     */
    @GetMapping(value = "/getSeckillGoods")
    public Result<Object> getSeckillGoods(String time, String goodsId){
        return Result.ok(seckillGoodsService.getsecKillGoods(time, goodsId));
    }
}
