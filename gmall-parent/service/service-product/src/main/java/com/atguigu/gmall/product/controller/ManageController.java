package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.constant.ProductConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;



/**
 * 包名:com.atguigu.gmall.product.controller
 *
 * @author Weirdo
 * 日期2023/3/24 14:00
 * 后台管理服务控制层
 */
@RestController
@RequestMapping("/admin/product")
public class ManageController {
    @Resource
    private ManageService manageService;


    /**
     * 获取商品一级分类
     * @return
     */
    @GetMapping("/getCategory1")
    public Result<List<BaseCategory1>> getCategory1(){
        return Result.ok(manageService.getCategory1());
    }

    /**
     * 获取商品二级分类
     * @param category1Id
     * @return
     */
    @GetMapping("/getCategory2/{category1Id}")
    public Result<List<BaseCategory2>> getCategory2(@PathVariable(value = "category1Id") Long category1Id){
        return Result.ok(manageService.getCategory2(category1Id));
    }

    /**
     * 获取商品三级分类
     * @param category2Id
     * @return
     */
    @GetMapping("/getCategory3/{category2Id}")
    public Result<List<BaseCategory3>> getCategory3(@PathVariable(value = "category2Id") Long category2Id){
        return Result.ok(manageService.getCategory3(category2Id));
    }

    /**
     * 保存属性参数
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        manageService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 根据分类di查询属性信息
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result<List<BaseAttrInfo>> getAttrInfoList(@PathVariable(value = "category1Id") Long category1Id,
                                                        @PathVariable(value = "category2Id") Long category2Id,
                                                        @PathVariable(value = "category3Id") Long category3Id){
        if (category3Id == 0){
            return Result.ok();
        }
        return Result.ok(manageService.getBaseAttrInfoList(category3Id));

    }

    /**
     * 删除属性
     * @param attrId
     * @return
     */
    @GetMapping("/deleteAttrInfo/{attrId}")
    public Result deleteAttrInfo(@PathVariable(value = "attrId") Long attrId){
        manageService.deleteAttrInfo(attrId);
        return Result.ok();
    }

    /**
     *商品信息管理功能SPU-添加平台属性查询显示品牌列表
     * @return
     */
    @GetMapping("/baseTrademark/getTrademarkList")
    public Result<List<BaseTrademark>> getTrademarkList(){
        return Result.ok(manageService.getTradeMarkList());
    }

    /**
     *商品信息管理功能SPU-查询销售属性名
     * @return
     */
    @GetMapping("/baseSaleAttrList")
    public Result<List<BaseSaleAttr>> getSaleAttrList(){
        return Result.ok(manageService.getBaseSaleAttrList());
    }

    /**
     * 品牌列表功能-分页显示品牌列表
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/baseTrademark/{page}/{size}")
    public Result<IPage<BaseTrademark>> getPageTradeMark(@PathVariable("page") Integer page,
                                                         @PathVariable("size") Integer size){
        return Result.ok(manageService.getPageTradeMark(page, size));
    }

    /**
     * 保存品牌信息
     * @param baseTrademark
     * @return
     */
    @PostMapping("/baseTrademark/save")
    public Result<Object> saveTradeMark(@RequestBody BaseTrademark baseTrademark){
        manageService.saveTradeMark(baseTrademark);
        return Result.ok();
    }

    /**
     * 保存SPU
     * @param spuInfo
     * @return
     */
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    /**
     * 分页查询SPU
     * @param page
     * @param size
     * @param category3Id
     * @return
     */
    @GetMapping("/{page}/{size}")
    public Result<IPage<SpuInfo>> getPageSpuInfo(@PathVariable("page") Integer page,
                                                 @PathVariable("size") Integer size,
                                                 @RequestParam Long category3Id){
        return Result.ok(manageService.getSpuInfo(category3Id, page, size));
    }

    /**
     * 获取SPU图片列表
     * @param spuId
     * @return
     */
    @GetMapping("/spuImageList/{spuId}")
    public Result<List<SpuImage>> getSpuImageList(@PathVariable Long spuId){
        return Result.ok(manageService.getSpuImageList(spuId));
    }
    /**
     * 分页查询SKUInfo
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list/{page}/{size}")
    public Result<IPage<SkuInfo>> getSkuInfoList(@PathVariable(value = "page") Integer page,
                                                 @PathVariable(value = "size") Integer size){
        return Result.ok(manageService.getSkuList(page, size));
    }

    /**
     * 根据SPUId查询 SPU销售属性的名称和值
     * @param spuId
     * @return
     */
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result<List<SpuSaleAttr>> getSpuAttrAndValueList(@PathVariable(value = "spuId") Long spuId){
        return Result.ok(manageService.getSpuAttrAndValueList(spuId));
    }

    /**
     * 保存SKU信息
     * @param skuInfo
     * @return
     */
    @PostMapping("/saveSkuInfo")
    public Result<Object> saveSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    /**
     * 上架
     * @param skuId
     * @param status
     * @return
     */
    @GetMapping("/onSale/{skuId}")
    public Result<Object> onSale(@PathVariable(value = "skuId") Long skuId, Short status){
        manageService.updateSaleStatusBySkuId(skuId,ProductConst.SKU_ON_SALE);
        return Result.ok();
    }

    /**
     * 下架
     * @param skuId
     * @param status
     * @return
     */
    @GetMapping("/cancelSale/{skuId}")
    public Result<Object> cancelSale(@PathVariable(value = "skuId") Long skuId, Short status){
        manageService.updateSaleStatusBySkuId(skuId,ProductConst.SKU_CANCEL_SALE);
        return Result.ok();
    }
}
