package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 包名:com.atguigu.gmall.product.service.impl
 *
 * @author Weirdo
 * 日期2023/3/28 16:30
 */
@Service
@Log4j2
public class ItemServiceImpl implements ItemService {

    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;
    @Resource
    private SkuImageMapper skuImageMapper;
    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;
    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;

    /**
     * 查询商品详情
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        return skuInfoMapper.selectById(skuId);
    }

    /**
     * 内部调用查询商品详情优化
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfoFromDBORRedis(Long skuId) {
        //从redis获取数据 key=sku:skuId:Info
        SkuInfo skuInfo = (SkuInfo) redisTemplate.opsForValue().get("Sku" + skuId + "Info");
        //若SkuInfo有数据则返回
        if (null != skuInfo) {
            return skuInfo;
        }
        //skuInfo为null 则获取锁加锁保护数据库
        RLock lock = redissonClient.getLock("Sku" + skuId + "lock");
        //第一个请求走完一遍 将数据放入redis了 后续被锁卡住的请求进来就可以直接从redis拿数据了
        try {
            if (lock.tryLock(100, 100, TimeUnit.SECONDS)) {
                try {
                    skuInfo = (SkuInfo) redisTemplate.opsForValue().get("Sku" + skuId + "Info");
                    if (null != skuInfo) {
                        return skuInfo;
                    }
                    //数据库查询SkuInfo
                    skuInfo = skuInfoMapper.selectById(skuId);
                    if (null == skuInfo || null == skuInfo.getId()) {
                        //要处理为空的结果 因为数据库也没有是内存穿透 解决大量请求穿透问题
                        //new一个空的结果并设置过期时间 因为不过期会影响后期添加真实数据
                        skuInfo = new SkuInfo();
                        redisTemplate.opsForValue().set("Sku" + skuId + "Info", skuInfo, 300, TimeUnit.SECONDS);
                    } else {
                        //数据库有数据直接放入redis
                        redisTemplate.opsForValue().set("Sku" + skuId + "Info", skuInfo, 24 * 60 * 60, TimeUnit.SECONDS);
                    }
                    return skuInfo;
                } catch (Exception e) {
                    log.error("查询商品的详情信息的时候,逻辑出现异常,异常的内容为:" + e.getMessage());
                } finally {
                    //释放锁
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            log.error("查询商品的详情信息的时候,加锁出现异常,异常的内容为: " + e.getMessage());
        }
        return new SkuInfo();

    }

    /**
     * 根据三级分类id获取完整分类
     *
     * @param category3Id
     * @return
     */
    @Override
    public BaseCategoryView getCategoryByCategory3Id(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }

    /**
     * 内部调用查询sku图片信息
     *
     * @param skuId
     * @return
     */
    @Override
    public List<SkuImage> getSkuImage(Long skuId) {
        return skuImageMapper.selectList(new LambdaQueryWrapper<SkuImage>().eq(SkuImage::getSkuId, skuId));
    }

    /**
     * 查询价格
     *
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getPrice(Long skuId) {
        return skuInfoMapper.selectById(skuId).getPrice();
    }

    /**
     * 查询销售属性
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> selectSpuSaleAttrBySpuAndSkuId(Long skuId, Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrBySpuAndSkuId(skuId, spuId);
    }

    /**
     * 获取键值对
     *
     * @param spuId
     * @return
     */
    @Override
    public Map getKeyAndValue(Long spuId) {
        //获取指定spu的全部sku的id和对应的值信息
        List<Map> maps = skuSaleAttrValueMapper.selectKeyAndValues(spuId);
        //将List转成Map
        Map result = new ConcurrentHashMap();
        maps.stream().forEach(entry -> {
            Object skuId = entry.get("sku_id");
            Object valuesId = entry.get("values_id");
            result.put(valuesId, skuId);
        });
        return result;
    }

//    /**
//     * 内部调用获取全部分类信息 供用户页面查询
//     * @return
//     */
//    @Override
//    public List<JSONObject> getBaseCategoryList() {
//        //初始化最终结果集
//        List<JSONObject> result = new ArrayList<>();
//        //查询视图获取所有分类信息多对多对多
//        List<BaseCategoryView> baseCategoryViews = baseCategoryViewMapper.selectList(null);
//        //根据以及分类Id进行分组聚合 所有key为一级分类id  value为二级和三级分类的数据
//        Map<Long, List<BaseCategoryView>> category1Map =
//                baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
//        //遍历获取各个一级分类对应的二级分类信息
//        for (Map.Entry<Long, List<BaseCategoryView>> category1 : category1Map.entrySet()) {
//            JSONObject category1Child = new JSONObject();
//            //获取以及分类的id
//            Long category1Id = category1.getKey();
//            //获取以及分类名字
//            String category1Name = category1.getValue().get(0).getCategory1Name();
//            //获取以及分类对应的二级分类列表
//            List<BaseCategoryView> category2List = category1.getValue();
//            //使用stream流  将2级分类中的数据根据id分组聚合
//            Map<Long, List<BaseCategoryView>> category2Map =
//                    category2List.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
//            List<Object> category2ChildList = new ArrayList<>();
//            //遍历category2Map  获取二级分类对应的所有三级分类信息
//            for (Map.Entry<Long, List<BaseCategoryView>> category2 : category2Map.entrySet()) {
//                JSONObject category2Child = new JSONObject();
//                Long category2Id = category2.getKey();
//                String category2Name = category2.getValue().get(0).getCategory2Name();
//                List<BaseCategoryView> category3List = category2.getValue();
//                //使用stream流  将3级分类中的数据根据id分组聚合
//                List<JSONObject> category3ChildList = category3List.stream().map(category3 -> {
//                    //创建三级分类
//                    JSONObject category3Child = new JSONObject();
//                    category3Child.put("categoryId", category3.getCategory3Id());
//                    category3Child.put("getCategory3Name", category3.getCategory3Name());
//                    return category3Child;
//                }).collect(Collectors.toList());
//                //保存二级分类和三级分类的关系
//                category2Child.put("category2Id", category2Id);
//                category2Child.put("category2Name", category2Name);
//                category2Child.put("category3ChildList", category3ChildList);
//                //保存二级分类
//                category2ChildList.add(category2Child);
//            }
//                //保存二级分类与一级分类的关系
//            category1Child.put("category1Id",category1Id);
//            category1Child.put("category1Name",category1Name);
//            category1Child.put("category2ChildList",category2ChildList);
//            //保存结果
//            result.add(category1Child);
//        }
//            return result;
//
//    }


    /**
     * 查询品牌信息
     *
     * @param id
     * @return
     */
    @Override
    public BaseTrademark getBaseTrademark(Long id) {
        return baseTrademarkMapper.selectById(id);
    }

    /**
     * 查询指定sku的平台属性
     *
     * @param skuId
     * @return
     */
    @Override
    public List<BaseAttrInfo> getBaseAttrInfo(Long skuId) {
        return baseAttrInfoMapper.selectBaseAttrInfoBySkuId(skuId);
    }

    /**
     * 扣减库存
     *
     * @param decountMapJsonString
     * @return
     */
    @Override
    public Boolean decountSkuStock(String decountMapJsonString) {
        //反序列化
        Map<String, Object> decountMap = JSONObject.parseObject(decountMapJsonString, Map.class);
        //遍历扣减
        decountMap.entrySet().stream().forEach(entry->{
            //商品id
            Long skuId = Long.valueOf(entry.getKey());
            //需要扣减的库存
            Integer num = Integer.parseInt(entry.getValue().toString());
            //修改数据
            int i = skuInfoMapper.decountStock(skuId, num);
            if(i < 0){
                throw new RuntimeException("库存不足!");
            }
        });
        return true;
    }

    /**
     * 回滚库存
     *
     * @param rollbackMapJsonString
     * @return
     */
    @Override
    public Boolean rollbackStock(String rollbackMapJsonString) {
        //反序列化
        Map<String,Object> rollbackMap = JSONObject.parseObject(rollbackMapJsonString, Map.class);
        //遍历回滚
        rollbackMap.entrySet().stream().forEach(entry->{
            //获取商品id
            Long skuId = Long.valueOf(entry.getKey());
            //获取商品数量
            Integer num = Integer.parseInt(entry.getValue().toString());
            //修改数据
            int i = skuInfoMapper.rollbackStock(skuId, num);
            if (i < 0){
                throw new RuntimeException("回滚库存失败");
            }
        });
        return true;
    }
}
