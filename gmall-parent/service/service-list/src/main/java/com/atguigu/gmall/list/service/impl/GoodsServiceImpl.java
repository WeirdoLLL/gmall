package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.list.dao.GoodsDao;
import com.atguigu.gmall.list.service.GoodsService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ItemFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 包名:com.atguigu.gmall.list.service.impl
 *
 * @author Weirdo
 * 日期2023/4/2 21:15
 * 商品相关接口类的实现类
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsDao goodsDao;
    @Resource
    private ItemFeign itemFeign;
    /**
     * 将数据库商品写入es
     *
     * @param skuId
     */
    @Override
    public void addSkuIntoEs(Long skuId) {
        //参数校验
        if (skuId ==null){
            return;
        }
        //查询sku信息
        SkuInfo skuInfo = itemFeign.getSkuInfo(skuId);
        if ((null == skuInfo||null == skuInfo.getId())){
            return;
        }
        //构建商品对象
        Goods goods = new Goods();
        //补全goods数据   从product查询相关数据
        goods.setId(skuInfo.getId());
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuName());
        //实时查询
        BigDecimal price = itemFeign.getPrice(skuId);
        goods.setPrice(price.doubleValue());
        goods.setCreateTime(new Date());
        //品牌设置
        BaseTrademark baseTrademark = itemFeign.getBaseTrademark(skuInfo.getId());
        goods.setTmId(baseTrademark.getId());
        goods.setTmName(baseTrademark.getTmName());
        goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        //查询分类
        BaseCategoryView category = itemFeign.getCategory(skuInfo.getCategory3Id());
        goods.setCategory1Id(category.getCategory1Id());
        goods.setCategory1Name(category.getCategory1Name());
        goods.setCategory2Id(category.getCategory2Id());
        goods.setCategory2Name(category.getCategory2Name());
        goods.setCategory3Id(category.getCategory3Id());
        goods.setCategory3Name(category.getCategory3Name());
        //查询平台数据
        List<BaseAttrInfo> baseAttrInfoList =
                itemFeign.getBaseAttrInfo(skuId);
        List<SearchAttr> attrs = baseAttrInfoList.stream().map(baseAttrInfo -> {
            //初始化
            SearchAttr searchAttr = new SearchAttr();
            //设置id
            searchAttr.setAttrId(baseAttrInfo.getId());
            //设置名字
            searchAttr.setAttrName(baseAttrInfo.getAttrName());
            //设置值的名字
            searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
            //返回
            return searchAttr;
        }).collect(Collectors.toList());
        goods.setAttrs(attrs);

        //保存数据
        goodsDao.save(goods);
    }

    /**
     * 将数据从Es移除Sku
     *
     * @param goodsId
     */
    @Override
    public void removeGoods(Long goodsId) {
        goodsDao.deleteById(goodsId);
    }
}
