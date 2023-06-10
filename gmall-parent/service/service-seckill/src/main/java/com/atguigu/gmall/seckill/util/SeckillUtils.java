package com.atguigu.gmall.seckill.util;

/**
 * @author Weirdo
 * 日期2023/4/14 21:11
 * 通用方法工具类
 */
public class SeckillUtils {
    /**
     * 构建一个长度为商品库存总数的数组  里面存的都是一样的商品id
     * 有多少库存 存多少次
     * @param goodsId
     * @param stockCount
     * @return
     */
    public static String[] getIdStock(String goodsId,Integer stockCount) {
        //初始化一个商品库存总量的数组
        String[] idStock = new String[stockCount];
        //遍历赋值
        for (int i = 0; i < stockCount; i++) {
            idStock[i] = goodsId;
        }
        //返回
        return idStock;
    }
}
