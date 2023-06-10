package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 包名:com.atguigu.gmall.product.service
 *
 * @author Weirdo
 * 日期2023/3/28 16:28
 * 供服务内部调用的接口类
 */
public interface ItemService {

    /**
     * 内部调用查询商品详情
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfo(Long skuId);

    /**
     * 内部调用查询商品详情优化
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfoFromDBORRedis(Long skuId);

    /**
     * 内部调用根据三级分类id获取完整分类
     * @param category3Id
     * @return
     */
    BaseCategoryView getCategoryByCategory3Id(Long category3Id);

    /**
     * 内部调用查询sku图片信息
     * @param skuId
     * @return
     */
    List<SkuImage> getSkuImage(Long skuId);

    /**
     * 查询价格
     * @param skuId
     * @return
     */
    BigDecimal getPrice(Long skuId);

    /**
     * 查询销售属性
     * @param skuId
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> selectSpuSaleAttrBySpuAndSkuId(Long skuId,Long spuId);

    /**
     *获取键值对
     * @return
     */
    Map getKeyAndValue(Long spuId);

//    /**
//     * 内部调用获取全部分类信息 供用户页面查询
//     * @return
//     */
//    List<JSONObject> getBaseCategoryList();

    /**
     * 查询品牌信息
     * @param id
     * @return
     */
    BaseTrademark getBaseTrademark(Long id);

    /**
     * 查询指定sku的平台属性
     *
     * @param skuId
     * @return
     */
    List<BaseAttrInfo> getBaseAttrInfo(Long skuId);

    /**
     * 扣减库存
     *
     * @param decountMapJsonString
     * @return
     */
    Boolean decountSkuStock(String decountMapJsonString);

    /**
     * 回滚库存
     * @param rollbackMapJsonString
     * @return
     */
    Boolean rollbackStock(String rollbackMapJsonString);

}
