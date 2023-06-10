package com.atguigu.gmall.seckill.mapper;



import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author Weirdo
 * 日期2023/4/13 16:03
 */
@Mapper
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

    @Update("UPDATE seckill_goods SET stock_count = #{stockCount} WHERE id = #{goodsId};")
    int SeckillGoodsStock(String goodsId, Integer stockCount);
}
