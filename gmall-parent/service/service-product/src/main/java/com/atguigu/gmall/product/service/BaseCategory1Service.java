package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 包名:com.atguigu.gmall.product.service
 *
 * @author Weirdo
 * 日期2023/3/23 16:20
 * 商品基本类别表1接口类
 */
public interface BaseCategory1Service {

    /**
     * 查询所有数据
     * @return
     */
    List<BaseCategory1> findAll();

    /**
     * 查询一条数据 根据id查询
     * @param id
     * @return
     */
    BaseCategory1 findOne(Long id);

    /**
     * 新增数据
     * @param baseCategory1
     */
    void add(BaseCategory1 baseCategory1);

    /**
     * 修改数据 根据对象内id
     * @param baseCategory1
     */
    void update(BaseCategory1 baseCategory1);

    /**
     * 删除 根据id
     * @param id
     */
    void delete(Long id);

    /**
     * 分页查询所有
     * @param page
     * @param size
     * @return
     */
    IPage<BaseCategory1> page(Integer page, Integer size);

    /**
     * 条件查询
     * @param baseCategory1
     * @return
     */
    List<BaseCategory1> search(BaseCategory1 baseCategory1);

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param baseCategory1
     * @return
     */
    IPage<BaseCategory1> search(Integer page, Integer size, BaseCategory1 baseCategory1);
    
}
