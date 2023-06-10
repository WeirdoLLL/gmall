package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 包名:com.atguigu.gmall.product.service.impl
 *
 * @author Weirdo
 * 日期2023/3/24 13:55
 * 后台管理服务接口类
 */
public interface ManageService {
    /**
     * 一级分类 查询所有
     * @return
     */
    List<BaseCategory1> getCategory1();

    /**
     * 获取二级分类
     * @param category1Id
     * @return
     */
    List<BaseCategory2> getCategory2(Long category1Id);

    /**
     * 根据二级分类id 查询三级分类
     * @param category2Id
     * @return
     */
    List<BaseCategory3> getCategory3(Long category2Id);

    /**
     * 保存平台属性
     * @param baseAttrInfo
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据分类id查询平台属性的信息
     * @param category3Id
     * @return
     */
    List<BaseAttrInfo> getBaseAttrInfoList(Long category3Id);

    /**
     * 删除属性同时删除该属性所有值
     * @param attrId
     */
    void deleteAttrInfo(Long attrId);

    /**
     * 查询品牌 因为没有做 三级分类与品牌关联 查询所有
     * @return
     */
    List<BaseTrademark> getTradeMarkList();

    /**
     * 获取销售属性名称
     * @return
     */
    List<BaseSaleAttr> getBaseSaleAttrList();

    /**
     * 获取分页显示品牌列表
     * @return
     */
    IPage<BaseTrademark> getPageTradeMark(Integer page, Integer size);

    /**
     * 保存品牌信息
     * @param baseTrademark
     */
    void saveTradeMark(BaseTrademark baseTrademark);

    /**
     * 保存SPU信息
     * @param spuInfo
     */
    void saveSpuInfo(SpuInfo spuInfo);

    /**
     * 查询SPU图片列表
     * @param spuId
     * @return
     */
    List<SpuImage> getSpuImageList(Long spuId);

    /**
     * 分页条件查询spu
     * @param category3Id
     * @param page
     * @param size
     * @return
     */
    IPage<SpuInfo> getSpuInfo(Long category3Id, Integer page, Integer size);

    /**
     * 分页查询SKUInfo表
     * @param page
     * @param size
     * @return
     */
    IPage<SkuInfo> getSkuList(Integer page, Integer size);

    /**
     * 根据SPUId查询SPU销售属性的名称和值
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuAttrAndValueList(Long spuId);

    /**
     * 保存SKU信息
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 根据SKUId修改is_sale字段上下架
     * @param skuId
     * @param status
     */
    void updateSaleStatusBySkuId(Long skuId,Short status);
}
