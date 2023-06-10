package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 包名:com.atguigu.gmall.product.service.impl
 *
 * @author Weirdo
 * 日期2023/3/22 19:36
 * 平台属性表接口类
 */
public interface BaseAttrInfoService {

    /**
     * 查询所有数据
     *
     * @return
     */
    List<BaseAttrInfo> findAll();

    /**
     * 主键查询
     *
     * @param id
     * @return
     */
    BaseAttrInfo findOne(Long id);

    /**
     * 新增数据
     *
     * @param baseAttrInfo
     */
    void add(BaseAttrInfo baseAttrInfo);

    /**
     * 修改数据
     *
     * @param baseAttrInfo
     */
    void update(BaseAttrInfo baseAttrInfo);

    /**
     * 根据id删除
     *
     * @param id
     */
    void deleteById(Long id);

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    IPage<BaseAttrInfo> page(Integer page, Integer size);

    /**
     * 条件查询
     * @param baseAttrInfo
     * @return
     */
    List<BaseAttrInfo> search(BaseAttrInfo baseAttrInfo);

    /**
     * 分页条件查询
     * @param baseAttrInfo
     * @param page
     * @param size
     * @return
     */
    IPage<BaseAttrInfo> search(BaseAttrInfo baseAttrInfo, Integer page, Integer size);
}
