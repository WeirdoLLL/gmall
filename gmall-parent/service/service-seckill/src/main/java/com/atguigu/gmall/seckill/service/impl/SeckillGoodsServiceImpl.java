package com.atguigu.gmall.seckill.service.impl;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author Weirdo
 * 日期2023/4/14 8:33
 * 秒杀商品接口实现类
 */
@Service
@Log4j2
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private SeckillGoodsMapper seckillGoodsMapper;
    /**
     * 获取秒杀时间端商品列表
     *
     * @param time
     * @return
     */
    @Override
    public List<SeckillGoods> getSeckillGoodsList(String time) {
        return redisTemplate.opsForHash().values(time);
    }

    /**
     * 获取指定时段指定商品
     *  @param time
     * @param goodsId
     * @return
     */
    @Override
    public SeckillGoods getsecKillGoods(String time, String goodsId) {
        return (SeckillGoods) redisTemplate.opsForHash().get(time,goodsId);
    }

    /**
     * 同步数据库中的秒杀商品库存信息  (***正常处理方案***)下面注释里还写了个失败兜底方案思路
     *
     * @param time
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateSeckillStock(String time) {
        //从redis使用 hkey: time 取出对应时间段的商品的 id: stock的key
        Set<String> keys = redisTemplate.opsForHash().keys("Seckill_Goods_Stock_Count_Increment_" + time);
        //遍历获取每个商品的id再从redis 根据 hkey time: key id: stock 拿到库存数量 存入数据库
        if (null != keys && keys.size()>0) {
            keys.stream().forEach(goodsId -> {
                Integer stockCount = (Integer) redisTemplate.opsForHash().get("Seckill_Goods_Stock_Count_Increment_" + time, goodsId);
                //修改数据库中商品库存
                int i = seckillGoodsMapper.SeckillGoodsStock(goodsId, stockCount);
                //更新结果小于0 说明出现异常
                if (i <0){
                    //失败记录到日志不抛出异常  这样不影响其他的已经成功或者回滚的
                    //除了这种方案 还可以失败的redis不清理 还可以失败内容写入数据库
                    log.error(time+"的商品库存同步失败 失败的商品id为"+goodsId+"应该同步的库存为"+ stockCount);
                    //(***失败兜底方案***)拓展--可以使用定时任务 每天固定0点处理T-1同步失败的全部商品数据--若同步依然失败 可以同步短信/邮件通知人工处理
                }
                //同步完成后清理redis中这个商品的key
                redisTemplate.opsForHash().delete("Seckill_Goods_Stock_Count_Increment_" + time, goodsId);
            });
            //删除 防止给商品重复设置过期时间 限制时间段进入一次的自增 清理已经完成的时间段不影响加入还没完成的 保持只有5个时间段
            redisTemplate.opsForHash().delete("Seckill_Goods_Expire_Times", time);
        }
    }
}
