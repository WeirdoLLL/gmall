package com.atguigu.gmall.product.service.impl;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 包名:com.atguigu.gmall.product.service.impl
 *
 * @author Weirdo
 * 日期2023/3/23 16:22
 */
@Service
public class BaseCategory1ServiceImpl implements BaseCategory1Service {
    @Resource
    private BaseCategory1Mapper baseCategory1Mapper;
    /**
     * 查询所有数据
     * @return
     */
    @Override
    public List<BaseCategory1> findAll() {

        return baseCategory1Mapper.selectList(null);
    }

    /**
     * 查询一条数据 根据id查询
     *
     * @param id
     * @return
     */
    @Override
    public BaseCategory1 findOne(Long id) {
        return baseCategory1Mapper.selectById(id);
    }

    /**
     * 新增数据
     *
     * @param baseCategory1
     */
    @Override
    public void add(BaseCategory1 baseCategory1) {
        //判读对象为null name不可为null不可为空字符串
        if(null == baseCategory1||
                StringUtil.isEmpty(baseCategory1.getName())){
            throw new GmallException(ResultCodeEnum.FAIL);
        }
        baseCategory1Mapper.insert(baseCategory1);
    }

    /**
     * 修改数据 根据对象内id
     *
     * @param baseCategory1
     */
    @Override
    public void update(BaseCategory1 baseCategory1) {
        //判断对象 id name
        if(null == baseCategory1||
            null == baseCategory1.getId()||
                StringUtil.isEmpty(baseCategory1.getName())){
            throw new GmallException(ResultCodeEnum.FAIL);
        }
        baseCategory1Mapper.updateById(baseCategory1);
    }

    /**
     * 删除 根据id
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        baseCategory1Mapper.selectById(id);
    }

    /**
     * 分页查询所有
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseCategory1> page(Integer page, Integer size) {

        return baseCategory1Mapper.selectPage(new Page<>(page,size),null);
    }

    /**
     * 条件查询
     *
     * @param baseCategory1
     * @return
     */
    @Override
    public List<BaseCategory1> search(BaseCategory1 baseCategory1) {
        if (null == baseCategory1){
            return baseCategory1Mapper.selectList(null);
        }
        return baseCategory1Mapper.selectList(buildQueryWrapper(baseCategory1));
    }

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param baseCategory1
     * @return
     */
    @Override
    public IPage<BaseCategory1> search(Integer page, Integer size, BaseCategory1 baseCategory1){
        if (null == baseCategory1){
            baseCategory1Mapper.selectPage(new Page<>(page,size),null);
        }
        return baseCategory1Mapper.selectPage(new Page<>(page,size),buildQueryWrapper(baseCategory1));
    }

    /**
     * 查询条件封装
     * @param baseCategory1
     * @return
     */
    private LambdaQueryWrapper<BaseCategory1> buildQueryWrapper(BaseCategory1 baseCategory1) {
        LambdaQueryWrapper<BaseCategory1> wrapper = new LambdaQueryWrapper<>();
        if (null != baseCategory1.getId()){
            wrapper.eq(BaseCategory1::getId, baseCategory1.getId());
        }
        if ((!StringUtil.isEmpty(baseCategory1.getName()))){
            wrapper.like(BaseCategory1::getName, baseCategory1.getName());
        }
        return wrapper;
    }
}
