package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 包名:com.atguigu.gmall.product.mapper
 *
 * @author Weirdo
 * 日期2023/3/27 10:39
 * SKU信息表Mapper映射
 */
@Mapper
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {
    /**
     * 扣减库存
     * @param skuId
     * @param num
     * @return
     */
    @Update("update sku_info set stock=stock - #{num} where id = #{skuId} and stock >= #{num}")
    public int decountStock(@Param("skuId") Long skuId,
                            @Param("num") Integer num);

    /**
     * 回滚库存
     * @param skuId
     * @param num
     * @return
     */
    @Update("update sku_info set stock=stock + #{num} where id = #{skuId}")
    int rollbackStock(@Param("skuId")Long skuId,
                      @Param("num") Integer num);
}
