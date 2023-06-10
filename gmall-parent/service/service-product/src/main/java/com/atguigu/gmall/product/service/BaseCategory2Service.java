package com.atguigu.gmall.product.service;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 包名:com.atguigu.gmall.product.service
 *
 * @author Weirdo
 * 日期2023/3/23 22:29
 * 商品基本类别表2接口类
 */
public interface BaseCategory2Service {

    /**
     * 查询所有数据
     * @return
     */
    List<BaseCategory2> findAll();

    /**
     * 主键id查询
     * @return
     */
    BaseCategory2 findOne(Long id);

    /**
     * 新增数据
     * @param baseCategory2
     */
    void add(BaseCategory2 baseCategory2);

    /**
     * 修改数据
     * @param baseCategory2
     */
    void update(BaseCategory2 baseCategory2);

    /**
     * 删除数据 根据id
     * @param id
     */
    void delete(Long id);

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    Result<IPage<BaseCategory2>> page(Integer page, Integer size);

    /**
     * 条件查询
     * @param baseCategory2
     * @return
     */
    List<BaseCategory2> search(BaseCategory2 baseCategory2);

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param baseCategory2
     * @return
     */
    IPage<BaseCategory2> search(Integer page, Integer size, BaseCategory2 baseCategory2);

}
