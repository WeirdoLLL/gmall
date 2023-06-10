package com.atguigu.gmall.list.service;

/**
 * 包名:com.atguigu.gmall.list.service
 *
 * @author Weirdo
 * 日期2023/4/2 21:12
 * 商品相关接口类
 */
public interface GoodsService {

    /**
     * 将数据库商品写入es
     */
    void addSkuIntoEs(Long skuId);

    /**
     * 将数据从Es移除Sku
     */
    void removeGoods(Long goodsId);
}
