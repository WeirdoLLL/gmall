package com.atguigu.gmall.product.feign;

import com.atguigu.gmall.model.product.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 包名:com.atguigu.gmall.product.feign
 *
 * @author Weirdo
 * 日期2023/3/28 16:20
 * 商品详情页使用的feign接口
 */
@FeignClient(name = "service-product",path = "/api/item",contextId = "ItemFeign")
public interface ItemFeign {

    /**
     * 内部调用查询商品详情信息控制层
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable Long skuId);

    /**
     * 根据三级分类id获取完整分类
     * @param category3Id
     * @return
     */
    @GetMapping("/getCategory/{category3Id}")
    BaseCategoryView getCategory(@PathVariable(value = "category3Id") Long category3Id);

    /**
     * 内部查询图片列表
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuImage/{skuId}")
    List<SkuImage> getSkuImage(@PathVariable(value = "skuId") Long skuId);

    /**
     * 内部查询价格
     * @param skuId
     * @return
     */
    @GetMapping("/getPrice/{skuId}")
    BigDecimal getPrice(@PathVariable(value = "skuId") Long skuId);

    /**
     * 查询销售属性
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/getSpuSaleAttr/{skuId}/{spuId}")
    List<SpuSaleAttr> getSpuSaleAttr(@PathVariable(value = "skuId") Long skuId,
                                            @PathVariable(value = "spuId") Long spuId);


    /**
     * 查询键值对
     * @param spuId
     * @return
     */
    @GetMapping("/getKeyAndValue/{spuId}")
    Map getKeyAndValue(@PathVariable(value = "spuId") Long spuId);

    /**
     * 查询品牌的实现
     * @param id
     * @return
     */
    @GetMapping("/getBaseTradeMark/{id}")
    public BaseTrademark getBaseTrademark(@PathVariable Long id);

    /**
     * 查询指定sku的平台属性和值
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getBaseAttrInfo/{skuId}")
    public List<BaseAttrInfo> getBaseAttrInfo(@PathVariable(value = "skuId") Long skuId);

    /**
     * 扣减库存
     * @param decountMapJsonString
     * @return
     */
    @GetMapping("/decountSkuStock")
    public Boolean decountSkuStock(@RequestParam String decountMapJsonString);

    /**
     * 回滚库存
     * @param rollbackMapJsonString
     * @return
     */
    @GetMapping("/rollbackStock")
    public Boolean rollbackStock(@RequestParam String rollbackMapJsonString);
}
