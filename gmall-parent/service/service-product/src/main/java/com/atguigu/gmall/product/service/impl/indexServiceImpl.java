package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.IndexService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 包名:com.atguigu.gmall.product.service.impl
 *
 * @author Weirdo
 * 日期2023/3/31 19:32
 * 首页接口实现类
 */
@Service
public class indexServiceImpl implements IndexService {

    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;
    /**
     * 查询首页分类信息
     * @return
     */
    @Override
    public List<JSONObject> getIndexCategory() {

        //查询商品分类视图 获取商品分类表所有数据
        List<BaseCategoryView> baseCategoryView1List =
                baseCategoryViewMapper.selectList(null);
        //根据查询的List类型数据做分桶处理 分桶条件为以及分类的id
        Map<Long, List<BaseCategoryView>> category1Map =
                baseCategoryView1List.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        //先将Map集合用entrySet遍历出集合在交给流再遍历每个对象获取数据(不太对)
        //category1Entry 类型为 Map.Entry<Long, List<BaseCategoryView>>
        return category1Map.entrySet().stream().map(category1Entry->{
            JSONObject category1Json = new JSONObject();
            //获取一级分类id
            Long category1Id = category1Entry.getKey();
            category1Json.put("categoryId",category1Id);
            //获取一级分类名字
            String category1Name = category1Entry.getValue().get(0).getCategory1Name();
            category1Json.put("categoryName",category1Name);
            //获取一级分类对应和后面所有分类数据
            List<BaseCategoryView> baseCategoryView2List = category1Entry.getValue();
            //对二级分类进行分桶
            Map<Long, List<BaseCategoryView>> category2Map =
                    baseCategoryView2List.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));


            //分桶后得到二级分类的map类型数据 有二级分类自己的id,name 还有对应三级分类的数据集合
            ////category2Entry 类型为 Map.Entry<Long, List<BaseCategoryView>>
            List<JSONObject> category2JsonList =category2Map.entrySet().stream().map(category2Entry->{
                JSONObject category2Json = new JSONObject();
                //获取二级分类id
                Long category2Id = category2Entry.getKey();
                category2Json.put("categoryId",category2Id);
                //获取二级分类名字
                String category2Name = category2Entry.getValue().get(0).getCategory2Name();
                category2Json.put("categoryName",category2Name);
                //获取二级分类中对应的所有三级分类
                List<BaseCategoryView> BaseCategoryView3List = category2Entry.getValue();
                //三级分类最后一级了 不分桶了 直接遍历拿数据
                List<JSONObject> category3JsonList =
                        BaseCategoryView3List.stream().map(baseCategoryView->{
                            JSONObject category3Json = new JSONObject();
                            //获取三级分类id
                            Long category3Id = baseCategoryView.getCategory3Id();
                            category3Json.put("categoryId",category3Id);
                            //获取三级分类名字
                            String category3Name = baseCategoryView.getCategory3Name();
                            category3Json.put("categoryName",category3Name);
                            //返回三级分类数据
                            return category3Json;
                        }).collect(Collectors.toList());
                //保存这个二级分类对应的全部的三级分类
                category2Json.put("childCategory",category3JsonList);
                return category2Json;
            }).collect(Collectors.toList());
            //保存这个一级分类对应的全部的二级分类的信息
            category1Json.put("childCategory", category2JsonList);
            return category1Json;
        }).collect(Collectors.toList());
    }
}
