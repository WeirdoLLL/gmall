package com.atguigu.gmall.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.cart.feign.CartFeign;
import com.atguigu.gmall.client.payment.feign.WXPayFeign;
import com.atguigu.gmall.client.payment.feign.ZFBPayFeign;
import com.atguigu.gmall.common.util.GmallThreadLocalUtil;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.mapper.PaymentInfoMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.product.feign.ItemFeign;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Weirdo
 * 日期2023/4/9 18:14
 * 订单服务接口实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Resource
    private CartFeign cartFeign;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private RedissonClient redissonClient;
    @Autowired
    private RedisTemplate redisTemplate;
    @Resource
    private ItemFeign itemFeign;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private WXPayFeign wxPayFeign;
    @Resource
    private PaymentInfoMapper paymentInfoMapper;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private ZFBPayFeign zfbPayFeign;
    /**
     * 新增订单
     *
     * @param orderInfo
     */
    @Override
    public void addOrder(OrderInfo orderInfo) {
        //参数校验
        if (null == orderInfo) {
            throw new RuntimeException("新增订单信息为空");
        }
        //获取用户名
        String username = GmallThreadLocalUtil.get();
        //加锁,保证每个用户同时只有一个线程在进行下单,这个有用户其他的线程直接拒绝失败
        RLock lock = redissonClient.getLock("User_Add_Order_" + username);
        try {
            if (lock.tryLock()) {
                try {
                    //只有加锁成功的线程有资格设置过期时间,防止死锁
                    redisTemplate.expire("User_Add_Order_" + username, 10, TimeUnit.SECONDS);
                    //查询购物车中本次购买的购物车数据和最新的总数量和总金额
                    Map<String, Object> result = cartFeign.getOrderAddInfo();
                    if (result == null) {
                        return;
                    }
                    //获取购物车列表
                    //获取总金额
                    BigDecimal totalMoney = new BigDecimal(result.get("totalPrice").toString());
                    //新增订单表
                    orderInfo.setTotalAmount(totalMoney);
                    orderInfo.setOrderStatus(OrderStatus.UNPAID.getComment());
                    orderInfo.setUserId(username);
                    orderInfo.setCreateTime(new Date());
                    orderInfo.setExpireTime(new Date(System.currentTimeMillis() + 1800000));
                    orderInfo.setProcessStatus(ProcessStatus.UNPAID.getComment());
                    int insert = orderInfoMapper.insert(orderInfo);
                    if (insert <= 0) {
                        throw new RuntimeException("新增订单失败");
                    }
                    //订单表新增成功获取订单号
                    Long orderId = orderInfo.getId();
                    //补全订单详情中的订单号信息 以购物车数据为依据新增订单详情表
                    List cartInfoList = (List) result.get("cartInfoList");
                    Map<String, Object> decountMap = saveOrderDetail(cartInfoList, orderId);
                    //删除购物车--事务-购物车微服务
//                    if(!cartFeign.deleteCart()){
//                        throw new RuntimeException("删除购物车失败!");
//                    }
                    //扣减库存
                    if (!itemFeign.decountSkuStock(JSONObject.toJSONString(decountMap))) {
                        throw new RuntimeException("库存不足,下单失败!");
                    }
                    //发送延迟消息 30分钟以后 若订单没有支付 则取消订单 超时取消
                    //把这个消息放进死信队列 时间到了 再进入正常队列 判断一下这个订单还是不是未支付 是就改成超时取消 不是可能就是主动取消 已支付等
                    rabbitTemplate.convertAndSend("order_normal_exchange",
                            "order.dead",
                            orderId + "",
                            (message) -> {
                                //获取消息的属性
                                MessageProperties messageProperties = message.getMessageProperties();
                                //设置消息的过期时间 ttl=毫秒
                                messageProperties.setExpiration(120000 + "");
                                //返回message
                                return message;
                            });
                    System.out.println("发送消息的时间为" + System.currentTimeMillis());
                } catch (Exception e) {
                    log.error("下单时候加锁成功,但是新增订单出现异常,异常的原因为:" + e.getMessage());
                    throw e;
                } finally {
                    //释放锁
                    lock.unlock();
                }
            } else {
                //重复下单
                throw new RuntimeException("重复下单!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("下单时候加锁失败出现异常,内容为:" + e.getMessage());
            throw e;
        }
    }

    /**
     * 取消订单
     *
     * @param orderId
     */
    @Override
    public void cancelOrder(Long orderId) {
        //参数校验
        if (null == orderId) {
            return;
        }
        RLock lock = redissonClient.getLock("payment_cancel_order" + orderId);
        try {
            if (lock.tryLock()) {
                try {
                    redisTemplate.expire("payment_cancel_order" + orderId, 10, TimeUnit.SECONDS);
                    //获取用户名
                    String username = GmallThreadLocalUtil.get();
                    //默认状态
                    String msg = OrderStatus.CANCEL.getComment();
                    //构造查询条件
                    LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<OrderInfo>()
                            .eq(OrderInfo::getId, orderId)
                            .eq(OrderInfo::getOrderStatus, OrderStatus.UNPAID.getComment());
                    if (StringUtils.isEmpty(username)) {
                        //没有username 说明没有request 说明不是用户请求
                        //超时取消
                        msg = OrderStatus.TIMEOUT.getComment();
                    } else {
                        //有username 有request
                        //主动取消 拼接查询条件
                        wrapper.eq(OrderInfo::getUserId, username);
                    }
                    //查询订单信息
                    OrderInfo orderInfo = orderInfoMapper.selectOne(wrapper);
                    if (null == orderInfo || null == orderInfo.getId()) {
                        return;
                    }
                    //修改订单状态 主动取消订单
                    orderInfo.setOrderStatus(msg);
                    orderInfo.setProcessStatus(msg);
                    int i = orderInfoMapper.updateById(orderInfo);
                    if (i <= 0) {
                        throw new RuntimeException("取消订单失败");
                    }

                    //回滚库存
                    rollbackStock(orderId);
                } catch (Exception e) {
                    log.error("取消订单加锁成功 但是逻辑出现问题 异常信息为" + e.getMessage());
                    throw e;
                } finally {
                    //出现异常 最终释放锁
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("重复取消订单");
            }
        } catch (Exception e) {
            log.error("取消订单加锁出现异常 异常信息为" + e.getMessage());
            throw e;
        }
    }

    /**
     * 获取订单支付状态
     *  @param orderId
     * @param paymentChannel
     * @return
     */
    @Override
    public String getOrderPayInfo(Long orderId, String paymentChannel) {
        //参数校验
        if (null == orderId){
            return null;
        }
        //控制用户不要频繁申请支付信息
        //获取锁 使用订单id加锁
        RLock lock = redissonClient.getLock("order_payment_info" + orderId);
        //捕获加锁出现的异常
        try{
            //抢锁
            if (lock.tryLock()){
                try{
                    //抢到锁 设置过期时间 防止死锁
                    redisTemplate.expire("order_payment_info" + orderId,10,TimeUnit.SECONDS);
                    //从redis中查询获取用户之前是否申请过支付地址
                    String result =
                            (String)redisTemplate.opsForValue().get("Order_Payment_Info_Url_" + orderId);
                    if (result != null){
                        return result;
                    }
                    //查询订单信息
                    OrderInfo orderInfo = orderInfoMapper.selectOne(
                            new LambdaQueryWrapper<OrderInfo>()
                                    .eq(OrderInfo::getOrderStatus, OrderStatus.UNPAID.getComment())
                                    .eq(OrderInfo::getId, orderId));
                    if (null == orderInfo || null == orderInfo.getId()){
                        return null;
                    }

                    //初始化支付信息
                    PaymentInfo paymentInfo = new PaymentInfo();
                    paymentInfo.setOrderId(orderId);
                    paymentInfo.setPaymentType(paymentChannel);
                    //远程调用支付渠道 根据用户选择的支付渠道
                    if (paymentChannel.equals("WX")){
                        //微信支付
                        String wxPayUrl = wxPayFeign.getPayInfo("wx_pay_body",
                                orderId + "",
                                orderInfo.getTotalAmount().multiply(new BigDecimal(100)).intValue() + "");
                        if (!StringUtils.isEmpty(wxPayUrl)){
                            paymentInfo.setPayUrl(wxPayUrl);
                        }
                    }else if (paymentChannel.equals("ZFB")){
                        //支付宝支付
                        String zfbPayUrl = zfbPayFeign.getPaymentInfo("zfb_pay_body",
                                orderId + "",
                                orderInfo.getTotalAmount() + "");
                        if (!StringUtils.isEmpty(zfbPayUrl)){
                            paymentInfo.setPayUrl(zfbPayUrl);
                        }
                    }
                    int insert = paymentInfoMapper.insert(paymentInfo);
                    if (insert <= 0){
                        throw new RuntimeException("新增付款信息失败");
                    }
                    //将支付信息序列化
                    String paymentInfoJsonString = JSONObject.toJSONString(paymentInfo);
                    //异步双写
                    CompletableFuture.runAsync(()->{
                        //将付款信息写入redis 设置过期时间
                        redisTemplate.opsForValue().set(
                                "Order_Payment_Info_Url_"+orderId,
                                paymentInfoJsonString,
                                300, TimeUnit.SECONDS);
                    },threadPoolExecutor);
                    //返回付款信息
                    return paymentInfoJsonString;
                }catch (Exception e){
                    //抢到锁但出现异常
                }finally {
                    //最终释放锁
                    lock.unlock();
                }
            }
        }catch (Exception e){
            //没抢到锁
        }

        return null;
    }

    /**
     * 修改订单支付状态
     *
     * @param resultString
     */
    @Override
    public void updateOrder(String resultString) {
        //支付结果JSON转Map
        Map<String, String> result = JSONObject.parseObject(resultString, Map.class);
        String orderId = result.get("out_trade_no");
        //使用订单号做为key获取锁
        RLock lock = redissonClient.getLock("payment_cancel_order" + orderId);
        //尝试加锁
        try {
            //加锁成功
            if (lock.tryLock()) {
                try {
                    //抢到锁 设置过期时间 防止死锁
                    redisTemplate.expire("order_payment_info" + orderId,10,TimeUnit.SECONDS);
                    //参数校验 查询订单是否存在
                    OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
                    //判断订单是否存在 不存在结束方法
                    if (null == orderInfo || null == orderInfo.getId()) {
                        return;
                    }
                    //获取本次支付的渠道信息
                    String payChannel = result.get("payChannel");
                    //判断订单状态
                    if (orderInfo.getOrderStatus().equals(OrderStatus.CANCEL.getComment()) ||
                            orderInfo.getOrderStatus().equals(OrderStatus.TIMEOUT.getComment())) {
                        //订单状态为 已取消或超超时取消  退款需要证书 模拟退款
                        System.out.println("调用退款接口 完成退款");
                    }else if (orderInfo.getOrderStatus().equals(OrderStatus.UNPAID.getComment())){
                        //订单状态为未支付 则修改订单状态
                        //交易流水号
                        if(payChannel.equals("WX")){
                            orderInfo.setOutTradeNo(result.get("transaction_id"));
                        }else if(payChannel.equals("ZFB")){
                            orderInfo.setOutTradeNo(result.get("trade_no"));
                        }
                        orderInfo.setOrderStatus(OrderStatus.PAID.getComment());
                        orderInfo.setProcessStatus(OrderStatus.PAID.getComment());
                        orderInfo.setTradeBody(resultString);
                        int update = orderInfoMapper.updateById(orderInfo);
                        if (update < 0){
                            throw new RuntimeException("修改订单支付状态异常");
                        }
                    }else if(orderInfo.getOrderStatus().equals(OrderStatus.PAID.getComment())){
                        //判断已经支付的渠道和现在支付的渠道是否一致 若不一致 需要退款
                        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(
                                new LambdaQueryWrapper<PaymentInfo>()
                                        .eq(PaymentInfo::getOrderId, orderId));
                        //判断当前支付渠道和之前支付渠道是否一致
                        if (!paymentInfo.getPaymentType().equals(result.get("paymentChannel"))){
                            //支付渠道不一致则确实付了两次要退款 同一笔订单不同付款渠道重复付款
                            //模拟退款
                            System.out.println("----------调用退款接口 进行退款-----------");
                        }
                    }
                }catch(Exception e1){
                    //加锁成功 但修改订单出现异常
                    log.error("修改订单加锁成功 改订单出现异常" + e1);
                }finally{
                    //最终释放锁
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            //没抢到锁
            log.error("修改订单加锁失败 失败原因为" + e);
        }
    }

    /**
     * 回滚库存
     *
     * @param orderId
     */
    private void rollbackStock(Long orderId) {
        //根据订单号查询该笔订单的订单详情skuId和num
        List<OrderDetail> orderDetailList =
                orderDetailMapper.selectList(
                        new LambdaQueryWrapper<OrderDetail>()
                                .eq(OrderDetail::getOrderId, orderId));
        //定义回滚的集合 使用map封装回滚的数据 一次feign调用回滚
        Map<String, Object> rollbackMap = new ConcurrentHashMap<>();
        //遍历统计回滚库存的商品id和数量
        orderDetailList.stream().forEach(orderDetail -> {
            //遍历存入
            rollbackMap.put(orderDetail.getSkuId() + "", orderDetail.getSkuNum());
        });
        //存好数据 一次feign调用 商品管理微服务进行库存回滚 先将回滚传入的map序列化
        if (!itemFeign.rollbackStock(JSONObject.toJSONString(rollbackMap))) {
            throw new RuntimeException("回滚失败");
        }
    }

    /**
     * 保存订单详情
     *
     * @param cartInfoList
     * @param orderId
     * @return
     */
    private Map<String, Object> saveOrderDetail(List cartInfoList, Long orderId) {
        Map<String, Object> decountMap = new ConcurrentHashMap<>();
        //遍历保存
        cartInfoList.stream().forEach(o -> {
            //序列化
            String cartInfoJsonString = JSONObject.toJSONString(o);
            //反序列化
            CartInfo cartInfo = JSONObject.parseObject(cartInfoJsonString, CartInfo.class);

            //初始化订单详情对象
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setOrderPrice(cartInfo.getSkuPrice());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            int insert = orderDetailMapper.insert(orderDetail);
            if (insert <= 0) {
                throw new RuntimeException("新增订单详情失败");
            }
            decountMap.put(cartInfo.getSkuId() + "", cartInfo.getSkuNum());
        });
        //返回
        return decountMap;
    }
}

