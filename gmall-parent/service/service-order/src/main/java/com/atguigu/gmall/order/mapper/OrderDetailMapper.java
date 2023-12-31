package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.order.OrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Weirdo
 * 日期2023/4/9 18:10
 * 订单详情表Mapper映射
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
