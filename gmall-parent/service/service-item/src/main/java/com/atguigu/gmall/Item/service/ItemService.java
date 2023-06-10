package com.atguigu.gmall.Item.service;

import com.atguigu.gmall.model.product.BaseCategoryView;

import java.util.Map;

/**
 * 包名:com.atguigu.gmall.Item.service
 *
 * @author Weirdo
 * 日期2023/3/28 15:51
 * 获取商品详情页面信息的接口类
 */
public interface ItemService {

    /**
     * 获取商品商品详情页需要的所有信息
     * @param skuId
     * @return
     */
    Map<String,Object> getItemPageInfo(Long skuId);
}
