package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 包名:com.atguigu.gmall.product.mapper
 *
 * @author Weirdo
 * 日期2023/3/27 10:43
 * SKU销售属性表Mapper映射
 */
@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {

    /**
     * 查询键值对
     * @param spuId
     * @return
     */
    @Select("SELECT sku_id,GROUP_CONCAT(DISTINCT sale_attr_value_id order by sale_attr_value_id SEPARATOR ',')" +
            " as values_id FROM sku_sale_attr_value WHERE spu_id = #{spuId} GROUP BY sku_id")
    List<Map> selectKeyAndValues(@Param("spuId") Long spuId);
}
