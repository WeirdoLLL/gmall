package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jodd.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 包名:com.atguigu.gmall.product.service.impl
 * @author Weirdo
 * 日期2023/3/22 19:36
 */
@Service
public class BaseAttrInfoInfoServiceImpl implements BaseAttrInfoService {

    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;
    /**
     * 查询所有数据
     * @return
     */
    @Override
    public List<BaseAttrInfo> findAll() {
        return baseAttrInfoMapper.selectList(null);
    }

    /**
     * 主键查询
     * @param id
     * @return
     */
    @Override
    public BaseAttrInfo findOne(Long id) {
        return baseAttrInfoMapper.selectById(id);
    }

    /**
     * 新增数据
     * @param baseAttrInfo
     */
    @Override
    public void add(BaseAttrInfo baseAttrInfo) {
        if(null == baseAttrInfo ||
                StringUtil.isEmpty(baseAttrInfo.getAttrName())){
            throw new GmallException(ResultCodeEnum.SECKILL_ILLEGAL);
        }
        int insert = baseAttrInfoMapper.insert(baseAttrInfo);
        if (insert <= 0){
            throw new GmallException(ResultCodeEnum.FAIL);
        }
    }

    /**
     * 修改数据
     *
     * @param baseAttrInfo
     */
    @Override
    public void update(BaseAttrInfo baseAttrInfo) {
        if (null==baseAttrInfo||
                StringUtil.isEmpty(baseAttrInfo.getAttrName())||
                    null == baseAttrInfo.getId()){
            throw new GmallException(ResultCodeEnum.FAIL);
        }
        int update = baseAttrInfoMapper.updateById(baseAttrInfo);
        if (update < 0){
            throw new GmallException(ResultCodeEnum.FAIL);
        }
    }

    /**
     * 根据id删除
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        int delete = baseAttrInfoMapper.deleteById(id);
        if (delete < 0){
            throw new GmallException(ResultCodeEnum.FAIL);
        }
    }

    /**
     * 分页查询
     *  @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseAttrInfo> page(Integer page, Integer size) {
        //
        return baseAttrInfoMapper.selectPage(new Page<>(page,size),null);
    }

    /**
     * 条件查询
     *
     * @param baseAttrInfo
     * @return
     */
    @Override
    public List<BaseAttrInfo> search(BaseAttrInfo baseAttrInfo) {
        //参数校验
        if (null == baseAttrInfo){
            return baseAttrInfoMapper.selectList(null);
        }
        //构建查询条件
        LambdaQueryWrapper wrapper = buildQueryParams(baseAttrInfo);
        //返回查询
        return baseAttrInfoMapper.selectList(wrapper);
    }

    /**
     * 分页条件查询
     *  @param baseAttrInfo
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseAttrInfo> search(BaseAttrInfo baseAttrInfo
                        , Integer page, Integer size) {
        //判断参数参数为空
        if(null == baseAttrInfo){
            return baseAttrInfoMapper.selectPage(new Page<>(page,size),null);
        }
        //构建查询条件
        LambdaQueryWrapper wrapper = buildQueryParams(baseAttrInfo);
        //返回分页条件查询结果
        return baseAttrInfoMapper.selectPage(new Page<>(page,size),wrapper);
    }

    /**
     * 构建条件查询
     * @param baseAttrInfo
     * @return
     */
    private LambdaQueryWrapper buildQueryParams(BaseAttrInfo baseAttrInfo) {
        //条件查询构造器
        LambdaQueryWrapper<BaseAttrInfo> wrapper = new LambdaQueryWrapper<>();
        //判读id不为空 做查询条件
        if (null != baseAttrInfo.getId()){
            wrapper.eq(BaseAttrInfo::getId, baseAttrInfo.getId());
        }
        //名字模糊查询
        if(!StringUtil.isEmpty(baseAttrInfo.getAttrName())){
            wrapper.like(BaseAttrInfo::getAttrName, baseAttrInfo.getAttrName());
        }
        //分类id判断查询
        if (null != baseAttrInfo.getCategoryId()){
            wrapper.eq(BaseAttrInfo::getCategoryId, baseAttrInfo.getCategoryId());
        }
        //返回查询条件
        return wrapper;
    }
}

