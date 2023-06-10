package com.atguigu.gmall.seckill.task;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.seckill.util.DateUtil;
import com.atguigu.gmall.seckill.util.SeckillUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Weirdo
 * 日期2023/4/13 15:56.
 * 秒杀商品 从数据库写入redis定时任务
 */
@Component
public class SeckillGoodsFromDbToRedisTask {

    /**
     * 定时任务
     * cron: 秒 分 时 日 月 周
     *  *: 任意时间
     *  指定时间执行: 10
     *  逗号: 指定哪几个时间执行
     *  区间: 哪个区间内执行
     *  /: 间隔
     *  ?
     *  W: 工作日
     *  L: 最后
     *
     *  fixedDelay: 每5秒执行一次,受方法的执行时间影响,上一次任务执行完以后开始倒计时--绝对不会叠加
     *  fixedRate: 每5秒执行一次,不受方法的执行时间影响,上一次任务执行开始后就倒计时--叠加运行
     *
     */

    @Resource
    private SeckillGoodsMapper seckillGoodsMapper;

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 定时的将数据库中秒杀商品的数据预热到redis中去: 提前准备好
     */
    @Scheduled(cron = "10/20 * * * * *")
    public void seckillGoodsFromDbToRedisTask(){
        //计算当前系统所在的时间段和后续4个时间段  共5个时间段
        List<Date> dateMenus = DateUtil.getDateMenus();
        //遍历 查询每个时间段的活动商品的数据
        dateMenus.stream().forEach(date -> {
            //获取活动的开始时间 2023-04-12 08:00
            String startTime =
                    DateUtil.data2str(date, DateUtil.PATTERN_YYYY_MM_DDHHMM);
            //获取活动的截止时间 2023-04-12 10:00
//            String endTime =
//                    DateUtil.data2str(DateUtil.addDateHour(date, 2), DateUtil.PATTERN_YYYY_MM_DDHHMM);
            Date endTime = DateUtil.addDateHour(date, 2);
            //计算数据存活时间
            long liveTime = endTime.getTime() - System.currentTimeMillis();
            //redis中存储商品的时间段的key  key= 2023041208
            String goodsKey = DateUtil.data2str(date, DateUtil.PATTERN_YYYYMMDDHH);
            //条件构造器
            LambdaQueryWrapper<SeckillGoods> wrapper = new LambdaQueryWrapper<>();
            //审核必须通过的商品
            wrapper.eq(SeckillGoods::getStatus, "1");
            //活动时间以内
            wrapper.ge(SeckillGoods::getStartTime, startTime);
            wrapper.le(SeckillGoods::getEndTime, endTime);
            //库存大于0
            wrapper.gt(SeckillGoods::getStockCount, 0);
            Set keys = redisTemplate.opsForHash().keys(goodsKey);
            if (null != keys && keys.size() >0){
                wrapper.notIn(SeckillGoods::getId,keys);
            }
            //查询数据
            List<SeckillGoods> seckillGoodsList =
                    seckillGoodsMapper.selectList(wrapper);
            //遍历将每个商品存储到redis中 Hash数据类型
            seckillGoodsList.stream().forEach(seckillGoods -> {
                //存储到redis中
                redisTemplate.opsForHash().put(goodsKey,seckillGoods.getId()+"",seckillGoods);
                //构建一个商品剩余库存个数长度和元素个数的队列 下单的依据 限制商品库存数量 防止超卖 并设置过期时间到结束时间过期
                redisTemplate.opsForList().leftPushAll("Seckill_Goods_Stock_Count_Queue_" + seckillGoods.getId(),
                        SeckillUtils.getIdStock(seckillGoods.getId()+"", seckillGoods.getStockCount()));
                redisTemplate.expire("Seckill_Goods_Stock_Count_Queue_" + seckillGoods.getId(),liveTime, TimeUnit.MILLISECONDS);
                //商品自增值的的作用 1记录商品的真实剩余库存 2同步数据到数据库
                redisTemplate.opsForHash().increment("Seckill_Goods_Stock_Count_Increment_" + goodsKey,
                        seckillGoods.getId()+"", seckillGoods.getStockCount());
            });
            //设置每个时间段的商品数据过期时间
            setExpire(goodsKey,liveTime);
        });
    }

    /**
     * 设置过期时间
     * @param time
     * @param liveTime
     */
    private void setExpire(String time,Long liveTime) {
        //控制每个时间段的商品数据过期时间只设置一次
        Long increment = redisTemplate.opsForHash().increment("Seckill_Goods_Expire_Times", time, 1);
        if (increment >1){
            //设置过了 跳过
            return;
        }
        //设置每个时间段的商品数据过期时间
        redisTemplate.expire(time,liveTime,TimeUnit.MILLISECONDS);
        //发送商品库存同步的延迟消息
        rabbitTemplate.convertAndSend("seckill_goods_normal_exchange",
                "seckill.goods.dead",
                time+"",
                message -> {
                    //在消息属性设置过期时间 秒杀时间段到期后再多加一些时间 更稳妥一点
                    MessageProperties messageProperties = message.getMessageProperties();
                    messageProperties.setExpiration(liveTime+180000+"");
                    //测试先用20秒
//                    messageProperties.setExpiration(20000+"");
                    //返回
                    return message;
                });
    }
}
