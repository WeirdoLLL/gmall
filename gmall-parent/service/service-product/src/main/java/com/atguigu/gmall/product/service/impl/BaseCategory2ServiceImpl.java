package com.atguigu.gmall.product.service.impl;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.atguigu.gmall.product.service.BaseCategory2Service;
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
 * 日期2023/3/23 22:34
 * 商品基本类别表2的接口实现类
 */
@Service
public class BaseCategory2ServiceImpl implements BaseCategory2Service {

    @Resource
    private BaseCategory2Mapper baseCategory2Mapper;

    /**
     * 查询所有数据
     *
     * @return
     */
    @Override
    public List<BaseCategory2> findAll() {
        return baseCategory2Mapper.selectList(null);
    }

    /**
     * 主键id查询
     *
     * @param id
     * @return
     */
    @Override
    public BaseCategory2 findOne(Long id) {

        return baseCategory2Mapper.selectById(id);
    }

    /**
     * 新增数据
     *
     * @param baseCategory2
     */
    @Override
    public void add(BaseCategory2 baseCategory2) {

        if (null == baseCategory2 ||
                StringUtil.isEmpty(baseCategory2.getName())) {
            throw new GmallException(ResultCodeEnum.FAIL);
        }
        int insert = baseCategory2Mapper.insert(baseCategory2);
        if (insert <= 0){
            throw new GmallException(ResultCodeEnum.FAIL);
        }
    }

    /**
     * 修改数据
     *
     * @param baseCategory2
     */
    @Override
    public void update(BaseCategory2 baseCategory2) {

        if (null == baseCategory2 ||
                null == baseCategory2.getId() ||
                StringUtil.isEmpty(baseCategory2.getName())){
            throw new GmallException(ResultCodeEnum.FAIL);
        }
        int i = baseCategory2Mapper.updateById(baseCategory2);
        if (i<0){
            throw new GmallException(ResultCodeEnum.FAIL);
        }
    }

    /**
     * 删除数据 根据id
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        baseCategory2Mapper.deleteById(id);

    }

    /**
     * 分页查询
     *  @param page
     * @param size
     * @return
     */
    @Override
    public Result<IPage<BaseCategory2>> page(Integer page, Integer size) {

        return Result.ok(baseCategory2Mapper.selectPage(new Page<>(page,size),null));
    }

    /**
     * 条件查询
     *
     * @param baseCategory2
     * @return
     */
    @Override
    public List<BaseCategory2> search(BaseCategory2 baseCategory2) {

        if (null == baseCategory2){
            baseCategory2Mapper.selectList(null);
        }
        return baseCategory2Mapper.selectList(buildQueryParams(baseCategory2));
    }

    /**
     * 分页条件查询
     *  @param page
     * @param size
     * @param baseCategory2
     * @return
     */
    @Override
    public IPage<BaseCategory2> search(Integer page, Integer size, BaseCategory2 baseCategory2) {

        if (null == baseCategory2){
            return baseCategory2Mapper.selectPage(new Page<>(page,size),null);
        }
        return baseCategory2Mapper.selectPage(new Page<>(page,size),buildQueryParams(baseCategory2));
    }

    /**
     * 内部方法 构造查询参数
     * @param baseCategory2
     * @return
     */
    private LambdaQueryWrapper<BaseCategory2> buildQueryParams(BaseCategory2 baseCategory2){

        //封装查询方法
        LambdaQueryWrapper<BaseCategory2> wrapper = new LambdaQueryWrapper<>();
        if(null != baseCategory2.getId()){
            wrapper.eq(BaseCategory2::getId,baseCategory2.getId());
        }
        if (!StringUtil.isEmpty(baseCategory2.getName())){
            wrapper.like(BaseCategory2::getName,baseCategory2.getName());
        }
        if (null != baseCategory2.getCategory1Id()){
            wrapper.eq(BaseCategory2::getCategory1Id,baseCategory2.getCategory1Id());
        }
        return wrapper;
    }
}
