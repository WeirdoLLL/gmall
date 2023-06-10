package com.atguigu.gmall.Item.service.impl;

import com.atguigu.gmall.Item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.feign.ItemFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 包名:com.atguigu.gmall.Item.service.impl
 *
 * @author Weirdo
 * 日期2023/3/28 15:55
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Resource
    private ItemFeign itemFeign;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    /**
     * 获取商品商品详情页需要的所有信息
     *
     * @param skuId
     * @return
     */
    @Override
    public Map<String, Object> getItemPageInfo(Long skuId) {
        //参数校验
        if (null == skuId){
            return null;
        }
        //返回结果初始化
        Map<String,Object> result = new ConcurrentHashMap<>();
        //根据skuId查出skuInfo信息 参数校验 判断skuInfo是否为null skuInfo.getId是否为null
        CompletableFuture<SkuInfo> future1 = CompletableFuture.supplyAsync(()->{
            SkuInfo skuInfo = itemFeign.getSkuInfo(skuId);
            if (null == skuInfo || null == skuInfo.getId()){
                return null;
            }
            result.put("skuInfo",skuInfo);
            return skuInfo;
        },threadPoolExecutor);

        //根据skuInfo查询完整分类列表
        CompletableFuture<Void> future2 = future1.thenAcceptAsync((skuInfo -> {
            if (null != skuInfo && null != skuInfo.getId()){
                Long category3Id = skuInfo.getCategory3Id();
                BaseCategoryView categoryView = itemFeign.getCategory(category3Id);
                result.put("categoryView",categoryView);
            }
        }),threadPoolExecutor);

        //查询默认图片和图片列表
        CompletableFuture<Void> future3 = future1.thenAcceptAsync((skuInfo -> {

            if (null != skuInfo && null != skuInfo.getId()){
                List<SkuImage> skuImageList = itemFeign.getSkuImage(skuId);
                result.put("skuImageList",skuImageList);
            }
        }),threadPoolExecutor);

        //查询价格
        CompletableFuture<Void> future4 = future1.thenAcceptAsync((skuInfo -> {
            if (null != skuInfo && null != skuInfo.getId()) {
                BigDecimal price = itemFeign.getPrice(skuId);
                result.put("price", price);
            }
        }),threadPoolExecutor);

        //查询销售属性 标出当前需要选中哪几个值
        CompletableFuture<Void> future5 = future1.thenAcceptAsync((skuInfo -> {
            if (null != skuInfo && null != skuInfo.getId()) {
                List<SpuSaleAttr> spuSaleAttrList =
                        itemFeign.getSpuSaleAttr(skuId, skuInfo.getSpuId());
                result.put("spuSaleAttrList", spuSaleAttrList);
            }
        }),threadPoolExecutor);

        //查询键值对
        CompletableFuture<Void> future6 = future1.thenAcceptAsync((skuInfo -> {
            if (null != skuInfo && null != skuInfo.getId()) {
                Map keyAndValues = itemFeign.getKeyAndValue(skuInfo.getSpuId());
                result.put("keyAndValues", keyAndValues);
            }
        }),threadPoolExecutor);

        //以上数据整合  必须任务全部执行完 返回
        CompletableFuture.allOf(future2, future3, future4, future5, future6).join();
        //返回
        return result;
    }
}
