package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrValue;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 包名:com.atguigu.gmall.product.service
 *
 * @author Weirdo
 * 日期2023/3/23 11:20
 * 平台属性值表接口类
 */
public interface BaseAttrValueService {

    /**
     * 查询所有数据
     * @return
     */
    List<BaseAttrValue> findAll();

    /**
     * 新增数据
     * @param baseAttrValue
     */
    void add(BaseAttrValue baseAttrValue);

    /**
     * 删除数据 根据id
     * @param id
     */
    void deletedById(Long id);

    /**
     * 修改数据
     * @param baseAttrValue
     */
    void updateById(BaseAttrValue baseAttrValue);

    /**
     * 主键查询
     * @param id
     * @return
     */
    BaseAttrValue findById(Long id);

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    IPage<BaseAttrValue> page(Integer page, Integer size);

    /**
     * 条件查询
     * @param baseAttrValue
     * @return
     */
    List<BaseAttrValue> search(BaseAttrValue baseAttrValue);

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param baseAttrValue
     * @return
     */
    IPage search(Integer page, Integer size, BaseAttrValue baseAttrValue);

}
