package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Weirdo
 * 日期2023/4/9 18:10
 * 订单信息表Mapper映射
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
}
