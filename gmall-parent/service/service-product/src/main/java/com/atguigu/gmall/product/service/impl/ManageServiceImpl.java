package com.atguigu.gmall.product.service.impl;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.atguigu.gmall.common.constant.ProductConst;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 包名:com.atguigu.gmall.product.service.impl
 *
 * @author Weirdo
 * 日期2023/3/24 13:57
 * 后台管理服务接口实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ManageServiceImpl implements ManageService {

    @Resource
    private BaseCategory1Mapper baseCategory1Mapper;
    @Resource
    private BaseCategory2Mapper baseCategory2Mapper;
    @Resource
    private BaseCategory3Mapper baseCategory3Mapper;
    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Resource
    private BaseAttrValueMapper baseAttrValueMapper;
    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;
    @Resource
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Resource
    private SpuInfoMapper spuInfoMapper;
    @Resource
    private SpuImageMapper spuImageMapper;
    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Resource
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private SkuImageMapper skuImageMapper;
    @Resource
    private SkuAttrValueMapper skuAttrValueMapper;
    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 一级分类 查询所有
     *
     * @return
     */
    @Override
    public List<BaseCategory1> getCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    /**
     * 获取二级分类
     *
     * @param category1Id
     * @return
     */
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        return baseCategory2Mapper.selectList(
                new LambdaQueryWrapper<BaseCategory2>().
                        eq(BaseCategory2::getCategory1Id, category1Id));
    }

    /**
     * 根据二级分类id 查询三级分类
     *
     * @param category2Id
     * @return
     */
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        return baseCategory3Mapper.selectList(
                new LambdaQueryWrapper<BaseCategory3>().
                        eq(BaseCategory3::getCategory2Id, category2Id));
    }

    /**
     * 保存平台属性
     *
     * @param baseAttrInfo
     */
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //参数校验
        if (null == baseAttrInfo ||
                StringUtil.isEmpty(baseAttrInfo.getAttrName())) {
            throw new RuntimeException("参数异常");
        }
        //id为空新增
        if (null == baseAttrInfo.getId()) {
            //保存平台属性名称表的信息
            int insert = baseAttrInfoMapper.insert(baseAttrInfo);
            if (insert <= 0) {
                throw new RuntimeException("保存平台属性名称失败");
            }
        } else {
            //id不为空则是修改
            int update = baseAttrInfoMapper.updateById(baseAttrInfo);
            if (update < 0) {
                throw new RuntimeException("修改平台属性名称失败");
            }
            //根据id删除旧值
            int delete = baseAttrValueMapper.delete(
                    new LambdaQueryWrapper<BaseAttrValue>()
                            .eq(BaseAttrValue::getAttrId, baseAttrInfo.getId()));
            if (delete < 0) {
                throw new RuntimeException("修改平台属性名称失败!");
            }
        }
        //如果平台属性名称表保存成功 则获取属性名称id
        Long attrId = baseAttrInfo.getId();
        //将id补充到每个属性值中
        List<BaseAttrValue> attrValueList =
                baseAttrInfo.getAttrValueList();
        //流式编程
        attrValueList.parallelStream().forEach(baseAttrValue -> {
            //参数校验
            if (StringUtils.isEmpty(baseAttrValue.getValueName())) {
                throw new RuntimeException("保存平台属性名称失败!");
            }
            //补全attrId
            baseAttrValue.setAttrId(attrId);
            //保存平台属性值
            int insert1 = baseAttrValueMapper.insert(baseAttrValue);
            if (insert1 <= 0) {
                throw new RuntimeException("保存平台属性值失败!");
            }
        });
//        //流式编程
//        attrValueList.parallelStream().forEach(baseAttrValue -> {
//            //参数校验
//            if (StringUtil.isEmpty(baseAttrValue.getValueName())) {
//                throw new RuntimeException("保存平台属性名称失败");
//            }
//            //补全attrId
//            baseAttrValue.setAttrId(attrId);
//            //保存平台属性值
//            int insert1 = baseAttrValueMapper.insert(baseAttrValue);
//            if (insert1 <= 0) {
//                throw new RuntimeException("保存平台属性值失败");
//            }
//        });
//        for (BaseAttrValue baseAttrValue : attrValueList) {
//            //参数校验
//            if (StringUtil.isEmpty(baseAttrValue.getValueName())) {
//                throw new RuntimeException("保存平台属性名称失败");
//            }
//            //补全attrId
//            baseAttrValue.setAttrId(attrId);
//            //保存平台属性值
//            int insert1 = baseAttrValueMapper.insert(baseAttrValue);
//            if (insert1 <= 0) {
//                throw new RuntimeException("保存平台属性值失败");
//            }
//        }
    }

    /**
     * 根据分类id查询平台属性的信息
     *
     * @param category3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> getBaseAttrInfoList(Long category3Id) {
        return baseAttrInfoMapper.selectBaseAttrInfoByCategoryId(category3Id);
    }

    /**
     * 删除属性同时删除该属性所有值
     *
     * @param attrId
     */
    @Override
    public void deleteAttrInfo(Long attrId) {
        int i = baseAttrInfoMapper.deleteById(attrId);
        if (i < 0) {
            throw new RuntimeException("删除平台属性名称失败");
        }
        int delete =
                baseAttrValueMapper.delete(
                        new LambdaQueryWrapper<BaseAttrValue>().
                                eq(BaseAttrValue::getAttrId, attrId));
        if (delete < 0) {
            throw new RuntimeException("删除平台属性值失败");
        }

    }

    /**
     * 查询品牌 因为没有做 三级分类与品牌关联 查询所有
     *
     * @return
     */
    @Override
    public List<BaseTrademark> getTradeMarkList() {
        return baseTrademarkMapper.selectList(null);
    }

    /**
     * 获取销售属性名称
     *
     * @return
     */
    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }

    /**
     * 获取分页显示品牌列表
     *
     * @return
     */
    @Override
    public IPage<BaseTrademark> getPageTradeMark(Integer page, Integer size) {
        return baseTrademarkMapper.selectPage(new Page<>(page, size), null);
    }

    /**
     * 保存品牌信息
     *
     * @param baseTrademark
     */
    @Override
    public void saveTradeMark(BaseTrademark baseTrademark) {
        //参数校验
        if (null == baseTrademark) {
            throw new RuntimeException("参数异常");
        }
        //根据主键id判断 不为null则修改
        if (null != baseTrademark.getId()) {
            int i = baseTrademarkMapper.updateById(baseTrademark);
            if (i <= 0) {
                throw new RuntimeException("修改品牌信息失败");
            }
        } else {
            //主键id为null新增
            int insert = baseTrademarkMapper.insert(baseTrademark);
            if (insert <= 0) {
                throw new RuntimeException("新增品牌信息失败");
            }
        }
    }

    /**
     * 查询SPU图片列表
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuImage> getSpuImageList(Long spuId) {
        return spuImageMapper.selectList(new LambdaQueryWrapper<SpuImage>().eq(SpuImage::getSpuId,spuId));
    }

    /**
     * 保存SPU信息
     *
     * @param spuInfo
     */
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //参数校验
        if (null == spuInfo) {
            throw new RuntimeException("参数为空");
        }
        //根据id判断是新增还是修改
        if (null != spuInfo.getId()) {
            //修改
            int update = spuInfoMapper.updateById(spuInfo);
            if (update < 0) {
                throw new RuntimeException("修改失败");
            }
            //删除原图片 属性名称和值
            int delete = spuImageMapper.delete(
                    new LambdaQueryWrapper<SpuImage>()
                            .eq(SpuImage::getSpuId, spuInfo.getId()));
            int delete1 = spuSaleAttrMapper.delete(
                    new LambdaQueryWrapper<SpuSaleAttr>()
                            .eq(SpuSaleAttr::getSpuId, spuInfo.getId()));
            int delete2 = spuSaleAttrValueMapper.delete(
                    new LambdaQueryWrapper<SpuSaleAttrValue>()
                            .eq(SpuSaleAttrValue::getSpuId, spuInfo.getId()));
            if (delete < 0 || delete1 < 0 || delete2 < 0) {
                throw new RuntimeException("修改spu失败");
            }
        } else {
            //新增
            int insert = spuInfoMapper.insert(spuInfo);
            if (insert <= 0) {
                throw new RuntimeException("新增spu失败");
            }
            //新增图片
            saveSpuImageInfo(spuInfo.getSpuImageList(), spuInfo.getId());
            //新增销售属性名和值
            saveSpuSaleAttr(spuInfo.getSpuSaleAttrList(), spuInfo.getId());

        }
    }

    /**
     * 分页条件查询spu
     *
     * @param category3Id
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<SpuInfo> getSpuInfo(Long category3Id, Integer page, Integer size) {
        return spuInfoMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<SpuInfo>().
                        eq(SpuInfo::getCategory3Id, category3Id));
    }


    /**
     * 分页查询SKUInfo表
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<SkuInfo> getSkuList(Integer page, Integer size) {
        return skuInfoMapper.selectPage(new Page<>(page, size), null);
    }

    /**
     * 根据SPUId查询SPU销售属性的名称和值
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuAttrAndValueList(Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrBySpuId(spuId);
    }

    /**
     * 保存SKU信息
     *
     * @param skuInfo
     */
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //参数判断
        if (null == skuInfo) {
            throw new RuntimeException("传入参数为空");
        }
        //根据id为null 判断新增or修改
        if (null != skuInfo.getId()) {
            //id不为null 修改
            int i = skuInfoMapper.updateById(skuInfo);
            if (i < 0) {
                throw new RuntimeException("修改SKUInfo信息失败");
            }
            //删除原值-图片-平台属性-销售属性
        } else {
            //id为null 新增
            int insert = skuInfoMapper.insert(skuInfo);
            if (insert <= 0) {
                throw new RuntimeException("新增SKU信息失败");
            }
        }
        //获取SKUId
        Long skuId = skuInfo.getId();
        //保存SKU图片信息 图片是从SPU选的 只保存id就可以 设计多了写冗余数据要保存
        saveSkuImageList(skuId, skuInfo.getSkuImageList());
        //保存SKU平台属性
        saveAttrValueList(skuId, skuInfo.getSkuAttrValueList());
        //保存SKU销售属性
        saveAttrSaleAttrValueList(skuId, skuInfo.getSpuId(), skuInfo.getSkuSaleAttrValueList());
    }

    /**
     * 根据SKUId修改is_sale字段上下架
     *
     * @param skuId
     * @param status
     */
    @Override
    public void updateSaleStatusBySkuId(Long skuId,Short status) {
        //参数校验
        if (null == skuId){
            return;
        }
        //查询商品是否存在
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (null == skuInfo){
            return;
        }
        //修改是否上架属性状态
        skuInfo.setIsSale(status);
        int i = skuInfoMapper.updateById(skuInfo);
        if(i < 0){
            throw new RuntimeException("上下架失败");
        }
        if (status.equals(ProductConst.SKU_ON_SALE)){
            //上架将数据写入es 发送消息
            rabbitTemplate.convertAndSend("product_exchange",
                    "sku.upper",
                    skuId + "");
        }else{
            //下架将数据从es删除 发送消息
            rabbitTemplate.convertAndSend("product_exchange",
                    "sku.down",
                    skuId + "");
        }
    }

    /**
     * 私有方法保存SKU销售属性
     *
     * @param skuId
     * @param spuId
     * @param skuSaleAttrValueList
     */
    private void saveAttrSaleAttrValueList(Long skuId, Long spuId, List<SkuSaleAttrValue> skuSaleAttrValueList) {

        //遍历新增保存SKU销售属性
        skuSaleAttrValueList.stream().forEach(skuSaleAttrValue -> {
            //补全SKU销售属性表的SKUId和SPUId
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValue.setSpuId(spuId);
            //执行新增SKU销售属性操作
            int insert = skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            if (insert <= 0) {
                throw new RuntimeException("新增SKU销售属性失败");
            }
        });

    }

    /**
     * 私有方法 遍历补全SKUId后执行SKU平台属性新增
     *
     * @param skuId
     * @param skuAttrValueList
     */
    private void saveAttrValueList(Long skuId, List<SkuAttrValue> skuAttrValueList) {
        //遍历新增保存SKU平台属性
        skuAttrValueList.stream().forEach(skuAttrValue -> {
            //补全skuAttrValue的skuId
            skuAttrValue.setSkuId(skuId);
            //执行新增
            int insert = skuAttrValueMapper.insert(skuAttrValue);
            if (insert <= 0) {
                throw new RuntimeException("新增SKU平台属性失败");
            }
        });

    }

    /**
     * 私有方法 补全SKUImage的SKUId后执行新增操作
     *
     * @param skuId
     * @param skuImageList
     */
    private void saveSkuImageList(Long skuId, List<SkuImage> skuImageList) {
        //遍历新增SKU图片
        skuImageList.stream().forEach(skuImage -> {
            //补全SKUImage的SKUId
            skuImage.setSkuId(skuId);
            //执行新增SKUImage
            int insert = skuImageMapper.insert(skuImage);
            //更新失败抛异常
            if (insert <= 0) {
                throw new RuntimeException("新增SKU图片失败");
            }
        });

    }

    /**
     * 保存spu销售属性
     *
     * @param spuSaleAttrList
     * @param spuId
     */
    private void saveSpuSaleAttr(List<SpuSaleAttr> spuSaleAttrList, Long spuId) {
        spuSaleAttrList.stream().forEach(spuSaleAttr -> {
            //保存spuId
            spuSaleAttr.setSpuId(spuId);
            //保存销售属性名称
            int insert = spuSaleAttrMapper.insert(spuSaleAttr);
            if (insert <= 0) {
                throw new RuntimeException("新增spu属性失败");
            }
            //保存属性对应值
            saveSpuSaleAttrValue(spuId, spuSaleAttr.getSpuSaleAttrValueList(), spuSaleAttr.getSaleAttrName());
        });
    }

    /**
     * 保存spu销售属性值
     *
     * @param spuId
     * @param spuSaleAttrValueList
     * @param saleAttrName
     */
    private void saveSpuSaleAttrValue(Long spuId, List<SpuSaleAttrValue> spuSaleAttrValueList, String saleAttrName) {
        //遍历保存
        spuSaleAttrValueList.stream().forEach(spuSaleAttrValue -> {
            //补全spuId
            spuSaleAttrValue.setSpuId(spuId);
            //补全值名
            spuSaleAttrValue.setSaleAttrName(saleAttrName);
            //新增
            int insert = spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            if (insert <= 0) {
                throw new RuntimeException("新增spu销售属性值失败");
            }
        });
    }

    /**
     * 遍历新增图片
     *
     * @param spuImageList
     * @param spuId
     */
    private void saveSpuImageInfo(List<SpuImage> spuImageList, Long spuId) {
        //遍历新增
        spuImageList.stream().forEach(spuImage -> {
            spuImage.setSpuId(spuId);
            int insert = spuImageMapper.insert(spuImage);
            if (insert <= 0) {
                throw new RuntimeException("新增spu图片失败");
            }
        });
    }
}
