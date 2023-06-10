package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 包名:com.atguigu.gmall.product.mapper
 *
 * @author Weirdo
 * 日期2023/3/28 19:03
 * 查询分类视图表Mapper映射
 */
@Mapper
public interface BaseCategoryViewMapper extends BaseMapper<BaseCategoryView> {

}
