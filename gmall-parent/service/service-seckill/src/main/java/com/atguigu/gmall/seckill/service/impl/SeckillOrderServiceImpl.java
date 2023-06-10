package com.atguigu.gmall.seckill.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.util.GmallThreadLocalUtil;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.seckill.mapper.SeckillOrderMapper;
import com.atguigu.gmall.seckill.pojo.SeckillOrder;
import com.atguigu.gmall.seckill.pojo.UserRecode;
import com.atguigu.gmall.seckill.service.SeckillOrderService;
import com.atguigu.gmall.seckill.util.DateUtil;
import com.atguigu.gmall.seckill.util.SeckillUtils;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Weirdo
 * 日期2023/4/14 9:18
 * 秒杀订单接口实现类
 */
@Service
@Log4j2
public class SeckillOrderServiceImpl implements SeckillOrderService {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private SeckillOrderMapper seckillOrderMapper;

    /**
     * 秒杀下单 其实是排队
     *
     * @param time
     * @param goodsId
     * @param num
     * @return
     */
    @Override
    public UserRecode addSeckillOrder(String time, String goodsId, Integer num) {
        String username = GmallThreadLocalUtil.get();
       // String username = "ldx";
        //包装用户秒杀信息
        UserRecode userRecode = new UserRecode();
        userRecode.setUsername(username);
        userRecode.setCreateTime(new Date());
        userRecode.setGoodsId(goodsId);
        userRecode.setTime(time);
        userRecode.setNum(num);
        //使用redis进行控制 防止重复排队 排队计数器
        Long increment = redisTemplate.opsForValue().increment("User_Recode_Queue_Count_" + username, 1);
        if (increment > 1) {
            userRecode.setStatus(3);
            userRecode.setMsg("重复排队");
            return userRecode;
        }
        CompletableFuture.runAsync(() -> {
            //设置秒杀订单的有效期  防止一直存在影响用户购买商品 排队计数器
            redisTemplate.expire("User_Recode_Queue_Count_" + username, 900, TimeUnit.SECONDS);
            //排队 将用户秒杀信息发布至消息队列
            rabbitTemplate.convertAndSend("seckill_order_exchange",
                    "seckill.order.add",
                    JSONObject.toJSONString(userRecode));
            //将用户秒杀信息存入redis
            redisTemplate.opsForValue().set("User_Recode_Info_" + username, userRecode);
        }, threadPoolExecutor);
        //结束 返回排队中信息
        return userRecode;
    }

    /**
     * 查询排队状态
     *
     * @return
     */
    @Override
    public UserRecode getUserRecode() {
        String username = GmallThreadLocalUtil.get();
        //String username = "ldx";
        return (UserRecode) redisTemplate.opsForValue().get("User_Recode_Info_" + username);

    }

    /**
     * 秒杀订单真实处理下单   异步抢单
     *
     * @param userRecodeString
     */
    @Override
    public void seckillOrderRealAddOrder(String userRecodeString) {
        //System.out.println(userRecodeString+"-----------------------------------------");
        //反序列化
        UserRecode userRecode = JSONObject.parseObject(userRecodeString, UserRecode.class);
        //参数校验
        //获取用户名时间段
        String username = userRecode.getUsername();
        //获取时间段
        String time = userRecode.getTime();
        //判断商品是否在当前秒杀时段
        if (StringUtils.isEmpty(time) ||
                !DateUtil.data2str(DateUtil.getDateMenus().get(0), DateUtil.PATTERN_YYYYMMDDHH).equals(time)) {
            //传入商品参数时间不在当前时间段
            seckillOrderAddFailDeleteRedisInfo(userRecode, "购买商品不在当前活动的时间段");
            return;
        }
        //判断商品
        String goodsId = userRecode.getGoodsId();
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.opsForHash().get(time, goodsId);
        //判断商品是否存在
        if (null == seckillGoods) {
            //商品不存在
            seckillOrderAddFailDeleteRedisInfo(userRecode, "购买商品不存在");
            return;
        }
        //商品存在判断购买数量
        Integer seckillLimit = seckillGoods.getSeckillLimit();
        Integer num = userRecode.getNum();
        if (null == num || num < 0) {
            //购买数量为空
            seckillOrderAddFailDeleteRedisInfo(userRecode, "购买商品数量为空");

            return;
        } else if (num > seckillLimit) {
            //超出限制购买数量
            seckillOrderAddFailDeleteRedisInfo(userRecode, "购买商品超出限制购买数量");
            return;
        }
        /**
         * 限购1个的方案
         */
//        //如果限购一个这样写 不用加for循环  pop弹出一次只能弹出一个
//        //用来控制库存
//        Object o = redisTemplate.opsForList().rightPop("Seckill_Goods_Stock_Count_Queue_" + goodsId);
//        if (null == o) {
//            //商品售罄
//            seckillOrderAddFailDeleteRedisInfo(userRecode, "商品售罄");
//            return;
//        }
        //用来控制库存
        //控制库存
        /**
         * 该方案缺点
         * 最后几个库存对这个队列操作会很频繁
         * 最后几个库存大概率出现非公平情况
         * 比如 只有1个库存 有人拍2个3个1个  但是第一个人占用了库存后面的人哪怕拍1个也不行 当第一个人的处理完回滚如果刚好有人拍1个就能成功
         */
        for (int i = 0; i < num; i++) {
            Object o = redisTemplate.opsForList().rightPop("Seckill_Goods_Stock_Count_Queue_" + goodsId);
            if (null == o) {
                if (i > 0) {
                    //计算要回滚的数量
                    String[] idStock = SeckillUtils.getIdStock(goodsId, i);
                    //回滚
                    redisTemplate.opsForList().leftPushAll("Seckill_Goods_Stock_Count_Queue_" + goodsId, idStock);
                    //商品库存不足
                    seckillOrderAddFailDeleteRedisInfo(userRecode, "商品库存不足");
                    return;
                }
            }
        }
        //更新库存
        Long increment = redisTemplate.opsForHash().increment("Seckill_Goods_Stock_Count_Increment_" + time,
                goodsId, -num);
        /**
         * 方案2 使用redis自增
         * 优点:
         * 快 一次出结果
         * 缺点
         * 有可能导致有几个永远卖不出去 出现库存极大负数再回滚会最初剩余库存
         * 比如库存剩1个 这时所有并发购买请求都在减库存 所以后面所有请求都只会是负数 直到没有减库存的购买请求开始回滚 回滚回第一个触发为负数的购买请求前库存
         * 这时如果并发不高刚好有一个人买了1个才卖的出去
         */
//        //更新库存
//        Long increment = redisTemplate.opsForHash().increment("Seckill_Goods_Stock_Count_Increment_" + time,
//                goodsId, -num);
//        //如果库存不够会<0 回滚
//        if(increment < 0){
//            //回滚
//            redisTemplate.opsForHash().increment("Seckill_Goods_Stock_Count_Increment_" + time,
//                    goodsId, num);
//            //商品售罄
//            seckillOrderAddFailDeleteRedisInfo(userRecode,"商品库存不足");
//            return;
//        }
        //更新商品展示页面的库存
        seckillGoods.setStockCount(increment.intValue());
        redisTemplate.opsForHash().put(time, goodsId, seckillGoods);
        //没有问题 生成订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        seckillOrder.setGoodsId(goodsId);
        seckillOrder.setMoney(seckillGoods.getCostPrice().multiply(new BigDecimal(num)));
        seckillOrder.setUserId(username);
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setStatus(OrderStatus.UNPAID.getComment());
        seckillOrder.setNum(num);
        //存储订单
        redisTemplate.opsForHash().put("Seckill_Order_Time_" + time, seckillOrder.getId(), seckillOrder);
        //更新用户排队状态 秒杀成功 等待支付
        userRecode.setStatus(2);
        userRecode.setMsg("秒杀成功,等待支付!");
        userRecode.setOrderId(seckillOrder.getId());
        userRecode.setMoney(seckillOrder.getMoney().toString());
        redisTemplate.opsForValue().set("User_Recode_Info_" + userRecode.getUsername(), userRecode);
        //将等待支付状态发送至消息队列 设置过期时间 处理超时订单
        rabbitTemplate.convertAndSend("seckill_order_normal_exchange",
                "seckill.order.dead",
                username, message -> {
                    //设置消息属性
                    MessageProperties messageProperties = message.getMessageProperties();
                    //15分钟超时  测试设置30秒
                    messageProperties.setExpiration(30000 + "");
                    //消息内容作为返回值发送
                    return message;
                });

    }

    /**
     * 取消秒杀订单
     *
     * @param username
     */
    @Override
    public void cancelSeckillOrder(String username) {
        //默认主动取消
        String msg = OrderStatus.CANCEL.getComment();
        //判断是主动取消还是超时取消
        if (StringUtils.isEmpty(username)) {
            //用户名自己在控制层写调用传入null 到这里自己从线程获取用户名
            //这样来判断是用户主动取消
            username = GmallThreadLocalUtil.get();
//            username = "ldx";
        } else {
            //消息队列发过来的用户名  设置为超时取消
            msg = OrderStatus.TIMEOUT.getComment();
        }
        //从redis获取用户排队状态
        UserRecode userRecode = (UserRecode) redisTemplate.opsForValue().get("User_Recode_Info_" + username);
        if (userRecode == null) {
            return;
        }
        //用订单号做锁  防止订单重复取消幂等性问题  防止订单同时支付和取消 用订单号做锁粒度不影响性能 限制支付取消用同一把锁
        String orderId = userRecode.getOrderId();
        RLock lock = redissonClient.getLock("Update_Cancle_Seckill_Order_Lock_" + orderId);
        try {
            //尝试加锁 失败就算了 不加了 取消订单不像支付 必须等到成功
            if (lock.tryLock()) {
                try {
                    //从redis获取秒杀订单信息  秒杀订单 存储的key是 time orderId 时间还没取出来先从排队信息取一下
                    String time = userRecode.getTime();
                    SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForHash().get("Seckill_Order_Time_" + time, orderId);
                    //参数校验 判断订单是否存在
                    if (null == seckillOrder) {
                        return;
                    }
                    //动态修改订单状态  主动取消和超时取消
                    seckillOrder.setStatus(msg);
                    //将订单信息写入数据库
                    int insert = seckillOrderMapper.insert(seckillOrder);
                    if (insert <= 0) {
                        return;
                    }
                    //取消成功并写入数据库 回滚redis库存数据 两种情况  活动结束  活动没结束
                    rollbackSeckillGoodsStock(userRecode);
                    //清理标识位
                    clearSeckillFlag(userRecode);
                } catch (Exception e) {
                    log.error("取消订单加锁成功 业务逻辑出现问题 异常原因: " + e.getMessage());
                } finally {
                    //最终释放锁
                    lock.unlock();
                }
            }
        } catch (Exception e1) {
            log.error("取消订单加锁出现异常 异常原因为" + e1.getMessage());
        }

    }

    /**
     * 修改订单状态
     * 修改订单的支付结果
     *
     * @param resultString
     */
    @Override
    public void updateSeckillOrder(String resultString) {
        //反序列化
        Map<String, String> result = JSONObject.parseObject(resultString, Map.class);
        //获取订单号
        String orderId = result.get("out_trade_no");
        //获取锁 使用订单id做为锁 修改支付状态要反复尝试加锁至成功
        RLock lock = redissonClient.getLock("Update_Cancle_Seckill_Order_Lock_" + orderId);
        try{
            //支付加锁成功为止
            lock.lock();
            try{
                System.out.println(Thread.currentThread().getName()+orderId+"------------------");
                //获取支付渠道 因为支付渠道不同  附加值key的名字不一样所以先获取支付渠道做判断
                //先初始化一下附加数据属性
                String attachString = null;
                String payChannel = result.get("payChannel");
                if (payChannel.equals("WX")) {
                    //获取微信的附加数据
                    attachString = result.get("attach");
                } else if (payChannel.equals("ZFB")) {
                    attachString = result.get("passback_params");
                }
                //附加数据是个map 反序列化
                Map<String, String> attach = JSONObject.parseObject(attachString, Map.class);
                //获取附加数据中的用户名
                String username = attach.get("username");
                //从redis中获取用户的排队状态
                UserRecode userRecode =
                        (UserRecode) redisTemplate.opsForValue().get("User_Recode_Info_" + username);
                if (userRecode == null) {
                    //排队状态不存在 可能支付完了 可能取消了
                    SeckillOrder seckillOrder = seckillOrderMapper.selectById(orderId);
                    //如果取消了需要退款
                    if (seckillOrder.getStatus().equals(OrderStatus.CANCEL.getComment()) ||
                            seckillOrder.getStatus().equals(OrderStatus.TIMEOUT.getComment())) {
                        System.out.println("原路退款");
                    } else if (seckillOrder.getStatus().equals(OrderStatus.PAID.getComment())) {
                        //已支付 检验用户是否多渠道支付 获取上次支付的报文
                        String transactionId = seckillOrder.getTransactionId();
                        //从报文获取支付渠道 如果不一样就是多渠道重复支付 退款   相同渠道不会重复支付 微信支付宝会控制
                        Map<String, String> lastPayMap = JSONObject.parseObject(transactionId, Map.class);
                        if (!lastPayMap.get("payChannel").equals(payChannel)) {
                            System.out.println("原路退款");
                        }
                    }
                    return;
                }
                //订单没有被处理过  没有取消没有超时 没有修改过支付状态
                //获取时间段
                String time = userRecode.getTime();
                //从redis获取秒杀订单
                SeckillOrder seckillOrder =
                        (SeckillOrder) redisTemplate.opsForHash().get("Seckill_Order_Time_" + time, orderId);
                if (seckillOrder == null) {
                    return;
                }
                //将订单修改为已支付
                seckillOrder.setStatus(OrderStatus.PAID.getComment());
                seckillOrder.setTransactionId(resultString);
                seckillOrder.setPayTime(new Date());
                //将秒杀订单就可以写入数据库(insert)
                int insert = seckillOrderMapper.insert(seckillOrder);
                if (insert <= 0) {
                    return;
                }
                //清理标识位
                clearSeckillFlag(userRecode);
            }catch(Exception e){
                log.error("修改秒杀订单的支付状态出现逻辑异常,异常的原因为:" + e.getMessage() + ",报文为:" + resultString);
            }finally {
                //最终释放锁
                lock.unlock();
            }
        }catch(Exception e1){
            log.error("修改秒杀订单的支付状态加锁失败,原因为:" + e1.getMessage() + ", 订单支付的报文为:" + resultString);
        }

    }

    /**
     * 清理标识位
     *
     * @param userRecode
     */
    private void clearSeckillFlag(UserRecode userRecode) {
        //删除订单数据
        redisTemplate.opsForHash().delete("Seckill_Order_Time_" + userRecode.getTime(), userRecode.getOrderId());
        //删除排队计数器
        redisTemplate.delete("User_Recode_Queue_Count_" + userRecode.getUsername());
        //删除排队状态
        redisTemplate.delete("User_Recode_Info_" + userRecode.getUsername());

    }


    /**
     * 回滚库存 两种情况 活动结束 活动没结束
     * 活动结束了就只回滚increment因为这个最后还要用来做写入数据库的数据
     * 活动没结束 查一下redis的 商品详情展示的数据存不存在 不存在说明清空了就不用处理了
     * 不为null 说明活动没结束 回滚商品详情数据的库存(回滚的是总剩余库存)和队列pop(回滚的是购买数量)
     *
     * @param userRecode
     */
    private void rollbackSeckillGoodsStock(UserRecode userRecode) {
        //锁的key用到了商品id和时间 先拿出来
        String time = userRecode.getTime();
        String goodsId = userRecode.getGoodsId();
        //商品库存自增值是一定要回滚的 最后写数据库的时候还要用这个库存做依据
        Long stockCount = redisTemplate.opsForHash().increment("Seckill_Goods_Stock_Count_Increment_" + time, goodsId, userRecode.getNum());
        //商品如果还没结束还需要回滚商品详情页面的库存数据 还有商品库存pop队列的库存
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.opsForHash().get(time, goodsId);
        if (seckillGoods != null) {
            //更新商品数据
            seckillGoods.setStockCount(stockCount.intValue());
            redisTemplate.opsForHash().put(time, goodsId, seckillGoods);
            //回滚队列pop
            redisTemplate.opsForList().leftPushAll("Seckill_Goods_Stock_Count_Queue_" + goodsId,
                    SeckillUtils.getIdStock(goodsId, userRecode.getNum()));
        }


    }

    /**
     * 秒杀订单  处理订单  订单有问题的时候  更新排队状态  清楚排队计数器
     *
     * @param userRecode
     * @param msg
     */
    private void seckillOrderAddFailDeleteRedisInfo(UserRecode userRecode, String msg) {
        //更新排队状态
        userRecode.setStatus(3);
        //根据问题动态传入返回用户的 秒杀失败原因
        userRecode.setMsg(msg + "秒杀失败");
        //操作redis秒杀订单信息
        redisTemplate.opsForValue().set("User_Recode_Info_" + userRecode.getUsername(), userRecode);
        //删除排队计数器
        redisTemplate.delete("User_Recode_Queue_Count_" + userRecode.getUsername());

    }
}
