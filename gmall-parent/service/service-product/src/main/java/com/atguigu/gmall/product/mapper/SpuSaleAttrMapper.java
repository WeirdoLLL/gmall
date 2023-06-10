package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 包名:com.atguigu.gmall.product.mapper
 *
 * @author Weirdo
 * 日期2023/3/26 18:14
 * spu销售属性名称映射
 */
@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    /**
     * 查询SPU销售属性和值
     * @param SpuId
     * @return
     */
    List<SpuSaleAttr> selectSpuSaleAttrBySpuId(@Param("spuId") Long SpuId);

    /**
     * 查询销售属性
     * @param skuId
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> selectSpuSaleAttrBySpuAndSkuId(@Param("skuId") Long skuId,
                                                     @Param("spuId") Long spuId);

}
