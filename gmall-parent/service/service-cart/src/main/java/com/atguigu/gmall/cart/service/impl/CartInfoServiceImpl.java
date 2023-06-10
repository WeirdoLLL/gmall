package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.common.constant.CartConst;
import com.atguigu.gmall.common.util.GmallThreadLocalUtil;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ItemFeign;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.util.concurrent.AtomicDouble;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Weirdo
 * 日期2023/4/7 18:56
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CartInfoServiceImpl implements CartInfoService {
    @Resource
    private CartInfoMapper cartInfoMapper;
    @Resource
    private ItemFeign itemFeign;

    /**
     * 购物车新增
     *
     * @param skuId
     * @param num      加入数量
     */
    @Override
    public void addCart(Long skuId, Integer num) {
        //参数校验
        if (null == skuId || null == num) {
            return;
        }
        //新增数量控制
        if (num > 200) {
            num = 200;
        }
        //判断sku是否存在
        SkuInfo skuInfo = itemFeign.getSkuInfo(skuId);
        if (null == skuInfo || null == skuInfo.getId()) {
            throw new RuntimeException("商品不存在");
        }
        //从本地线程获取用户名
        String username = GmallThreadLocalUtil.get();
        //查询用户购物车中是否有该商品
        CartInfo cartInfo = cartInfoMapper.selectOne(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getSkuId, skuId)
                .eq(CartInfo::getUserId, username));
        if (null == cartInfo || null == cartInfo.getId()) {
            if (num <= 0) {
                return;
            }
            //用户购物车没有这个商品  新增商品
            //包装cartInfo对象
            cartInfo = new CartInfo();
            //补全属性
            cartInfo.setUserId(username);
            cartInfo.setSkuId(skuId);
            //获取价格  价格模拟动态查询 feign调用查询
            cartInfo.setCartPrice(itemFeign.getPrice(skuId));
            cartInfo.setSkuNum(num);
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setSkuName(skuInfo.getSkuName());
            //新增数据到数据库
            int insert = cartInfoMapper.insert(cartInfo);
            if (insert <= 0) {
                throw new RuntimeException("新增购物车失败");
            }
        } else {
            //已存在商品  合并商品
            num = cartInfo.getSkuNum() + num;
            if (num > 200) {
                num = 200;
            }
            if (num <= 0) {
                //传入数量<0 做删除操作
                int delete = cartInfoMapper.deleteById(cartInfo.getId());
                if (delete < 0){
                    throw new RuntimeException("新增购物车失败");
                }
            } else {
                cartInfo.setSkuNum(num);
                int i = cartInfoMapper.updateById(cartInfo);
                if (i < 0) {
                    throw new RuntimeException("新增购物车失败");
                }
            }
        }

    }

    /**
     * 查询购物车信息
     *
     * @return
     */
    @Override
    public List<CartInfo> getCartInfo() {
        //从本地线程获取用户名
        String username = GmallThreadLocalUtil.get();
        return cartInfoMapper.selectList(
                new LambdaQueryWrapper<CartInfo>()
                        .eq(CartInfo::getUserId,username));
    }

    /**
     * 删除购物车数据
     *
     * @param id
     */
    @Override
    public void removeCart(Long id) {
        //从本地线程获取用户名
        String username = GmallThreadLocalUtil.get();
        int delete = cartInfoMapper.delete(
                new LambdaQueryWrapper<CartInfo>()
                        .eq(CartInfo::getId, id)
                        .eq(CartInfo::getUserId, username));
        if(delete < 0){
            throw new RuntimeException("删除购物车数据失败");
        }
    }

    /**
     * 更新选中和取消选中的状态
     *
     * @param id
     * @param status
     */
    @Override
    public void updateCheckStatus(Long id, Short status) {
        //从本地线程获取用户名
        String username = GmallThreadLocalUtil.get();
        int i = -1;
        //参数校验
        if (id == null){
            //全部选中和取消选中
            i = cartInfoMapper.updateAll(status, username);
        }else{
            //根据id选择修改一个
            i = cartInfoMapper.updateOne(status, username, id);
        }
        if (i < 0){
            throw new RuntimeException("修改选中状态失败");
        }
    }

    /**
     * 合并购物车 批量新增
     *
     * @param cartInfoList
     */
    @Override
    public void mergeCartInfo(List<CartInfo> cartInfoList) {
        cartInfoList.stream().forEach(cartInfo -> {
            this.addCart(cartInfo.getId(), cartInfo.getSkuNum());
        });
    }

    /**
     * 获取订单确认页面的购物车数据
     * @return
     */
    @Override
    public Map<String, Object> getOrderConfirmCartInfo() {
        //根据查询条件查询购物车数据
        List<CartInfo> cartInfoList = cartInfoMapper.selectList(
                new LambdaQueryWrapper<CartInfo>()
                        .eq(CartInfo::getUserId, GmallThreadLocalUtil.get())
                        .eq(CartInfo::getIsChecked, CartConst.CART_CHECK));
        if(cartInfoList == null || cartInfoList.isEmpty()){
            return null;
        }
        Map<String, Object> result = new HashMap<>();
        //初始化订单商品总数量
        AtomicInteger totalNum = new AtomicInteger(0);
        //初始化订单商品总价格
        AtomicDouble totalPrice = new AtomicDouble(0);
        //价格实时查询放入 还需要订单商品总数量 总价格
        List<CartInfo> cartInfoNewList = cartInfoList.stream().map(cartInfo -> {
            //获取订单商品实时价格
            BigDecimal skuPrice = itemFeign.getPrice(cartInfo.getSkuId());
            //存储实时价格
            cartInfo.setSkuPrice(skuPrice);
            //计算商品总数量
            totalNum.getAndAdd(cartInfo.getSkuNum());
            //计算商品总价格
            totalPrice.getAndAdd(skuPrice.multiply(new BigDecimal(cartInfo.getSkuNum().toString())).doubleValue());
            //返回购物车信息
            return cartInfo;
        }).collect(Collectors.toList());
        result.put("cartInfoList",cartInfoNewList);
        result.put("totalNum",totalNum);
        result.put("totalPrice",totalPrice);
        return result;
    }

    /**
     * 下单完成以后,清理本次购买的购物车数据
     *
     * @return
     */
    @Override
    public Boolean deleteCart() {
        int delete = cartInfoMapper.delete(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, GmallThreadLocalUtil.get())
                .eq(CartInfo::getIsChecked, CartConst.CART_CHECK));

        return delete > 0;
    }
}
