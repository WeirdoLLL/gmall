package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 包名:com.atguigu.gmall.product.mapper
 *
 * @author Weirdo
 * 日期2023/3/22 19:27
 * 平台属性表mapper映射
 */
@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {
    /**
     * 根据分类id查询属性信息
     * @param category3Id
     * @return
     */
    List<BaseAttrInfo> selectBaseAttrInfoByCategoryId(@Param("categoryId") Long category3Id);

    /**
     * 查询指定sku的全部平台属性和对应的值
     * @param skuId
     * @return
     */
    public List<BaseAttrInfo> selectBaseAttrInfoBySkuId(@Param("skuId") Long skuId);

}
