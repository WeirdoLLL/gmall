package com.atguigu.gmall.cart.mapper;

import com.atguigu.gmall.model.cart.CartInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author Weirdo
 * 日期2023/4/7 18:49
 * 购物车信息表的Mapper映射
 */
@Mapper
public interface CartInfoMapper extends BaseMapper<CartInfo> {

    /**
     * 全部选中 或 全部取消选中
     * @param status
     * @param username
     */
    @Update("UPDATE cart_info SET is_checked = #{status} WHERE user_id= #{username}")
    int updateAll(@Param("status") Short status,
                   @Param("username") String username);

    /**
     * 单个选中或单个取消
     * @param status
     * @param username
     * @param id
     * @return
     */
    @Update("UPDATE cart_info SET is_checked = #{status} WHERE user_id= #{username} AND id = #{id}")
    int updateOne(@Param("status") Short status,
                  @Param("username") String username,
                  @Param("id") Long id);
}
