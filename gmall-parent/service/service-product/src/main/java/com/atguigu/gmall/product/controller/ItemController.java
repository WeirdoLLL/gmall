package com.atguigu.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.cache.JavaGmallCache;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ItemService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 包名:com.atguigu.gmall.product.controller
 *
 * @author Weirdo
 * 日期2023/3/28 16:33
 *
 * 供内部调用使用的控制层
 */
@RestController
@RequestMapping("/api/item")
public class ItemController {

    @Resource
    private ItemService itemService;

    /**
     * 内部调用查询商品详情信息控制层
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuInfo/{skuId}")
    @JavaGmallCache(prefix = "getSkuInfo:")
    public SkuInfo getSkuInfo(@PathVariable(value = "skuId") Long skuId){
        return itemService.getSkuInfo(skuId);
    }

    /**
     * 内部调用根据category3Id获取完整category
     * @param category3Id
     * @return
     */
    @GetMapping("/getCategory/{category3Id}")
    @JavaGmallCache(prefix = "getCategory:")
    public BaseCategoryView getCategory(@PathVariable(value = "category3Id") Long category3Id){
        return itemService.getCategoryByCategory3Id(category3Id);
    }

    /**
     * 内部查询图片列表
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuImage/{skuId}")
    @JavaGmallCache(prefix = "getSkuImage:")
    public List<SkuImage> getSkuImage(@PathVariable(value = "skuId") Long skuId){
    return itemService.getSkuImage(skuId);
    }

    /**
     * 内部查询价格
     * @param skuId
     * @return
     */
    @GetMapping("/getPrice/{skuId}")
    @JavaGmallCache(prefix = "getPrice:")
    public BigDecimal getPrice(@PathVariable(value = "skuId") Long skuId){
        return itemService.getPrice(skuId);
    }

    /**
     * 查询销售属性
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/getSpuSaleAttr/{skuId}/{spuId}")
    @JavaGmallCache(prefix = "getSpuSaleAttr:")
    public List<SpuSaleAttr> getSpuSaleAttr(@PathVariable(value = "skuId") Long skuId,
                                            @PathVariable(value = "spuId") Long spuId){

        return itemService.selectSpuSaleAttrBySpuAndSkuId(skuId, spuId);
    }

    /**
     * 查询键值对
     * @param spuId
     * @return
     */
    @GetMapping("/getKeyAndValue/{spuId}")
    @JavaGmallCache(prefix = "getKeyAndValue:")
    public Map getKeyAndValue(@PathVariable(value = "spuId") Long spuId){
        return itemService.getKeyAndValue(spuId);

    }

//    /**
//     * 内部调用获取全部分类信息 供用户页面查询
//     * @return
//     */
//    @GetMapping("/getBaseCategory")
//    public List<JSONObject> getBaseCategory(){
//        return itemService.getBaseCategoryList();
//    }

    /**
     * 查询品牌的实现
     * @param id
     * @return
     */
    @GetMapping("/getBaseTradeMark/{id}")
    public BaseTrademark getBaseTrademark(@PathVariable Long id){
        return itemService.getBaseTrademark(id);
    }

    /**
     * 查询指定sku的平台属性和值
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getBaseAttrInfo/{skuId}")
    public List<BaseAttrInfo> getBaseAttrInfo(@PathVariable(value = "skuId") Long skuId){
        return itemService.getBaseAttrInfo(skuId);
    }

    /**
     * 扣减库存
     * @param decountMapJsonString
     * @return
     */
    @GetMapping("/decountSkuStock")
    public Boolean decountSkuStock(@RequestParam String decountMapJsonString){

        try{
            return itemService.decountSkuStock(decountMapJsonString);
        }catch(Exception e){
            return false;
        }
    }

    /**
     * 回滚库存
     * @param rollbackMapJsonString
     * @return
     */
    @GetMapping("/rollbackStock")
    public Boolean rollbackStock(@RequestParam String rollbackMapJsonString){
        try {
            return itemService.rollbackStock(rollbackMapJsonString);
        } catch (Exception e) {
            return false;
        }
    }

}
