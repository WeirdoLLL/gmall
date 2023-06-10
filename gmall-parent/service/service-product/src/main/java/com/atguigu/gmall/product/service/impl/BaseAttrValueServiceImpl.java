package com.atguigu.gmall.product.service.impl;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.BaseAttrValueService;
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
 * 日期2023/3/23 11:22
 * 平台属性值接口实现类
 */
@Service
public class BaseAttrValueServiceImpl implements BaseAttrValueService {

    @Resource
    private BaseAttrValueMapper baseAttrValueMapper;

    /**
     * 查询所有数据
     * @return
     */
    @Override
    public List<BaseAttrValue> findAll() {
        return baseAttrValueMapper.selectList(null);
    }

    /**
     * 新增数据
     *
     * @param baseAttrValue
     */
    @Override
    public void add(BaseAttrValue baseAttrValue) {
        //条件判断 对象不为null或名字不为null或名字不为空字符串
        if (null == baseAttrValue||
                StringUtil.isEmpty(baseAttrValue.getValueName())){
            throw  new GmallException(ResultCodeEnum.SECKILL_ILLEGAL);
        }
        baseAttrValueMapper.insert(baseAttrValue);

    }

    /**
     * 删除数据 根据id
     *
     * @param id
     */
    @Override
    public void deletedById(Long id) {

        baseAttrValueMapper.deleteById(id);
    }

    /**
     * 修改数据
     *
     * @param baseAttrValue
     */
    @Override
    public void updateById(BaseAttrValue baseAttrValue) {
        if (null == baseAttrValue ||
                StringUtil.isEmpty(baseAttrValue.getValueName())||
                    null == baseAttrValue.getId()){
            throw new GmallException(ResultCodeEnum.FAIL);
        }
        baseAttrValueMapper.updateById(baseAttrValue);
    }

    /**
     * 主键查询
     *
     * @param id
     * @return
     */
    @Override
    public BaseAttrValue findById(Long id) {
        return baseAttrValueMapper.selectById(id);
    }

    /**
     * 分页查询
     *  @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseAttrValue> page(Integer page, Integer size) {

        return baseAttrValueMapper.selectPage(new Page<>(page,size),null);
    }

    /**
     * 条件查询
     *
     * @param baseAttrValue
     * @return
     */
    @Override
    public List<BaseAttrValue> search(BaseAttrValue baseAttrValue) {
        //判断传入参数是否为null
        if(null == baseAttrValue){
            return baseAttrValueMapper.selectList(null);
        }
        //生成查询条件
        LambdaQueryWrapper wrapper = buildQueryParams(baseAttrValue);
        return baseAttrValueMapper.selectList(wrapper);
    }

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param baseAttrValue
     * @return
     */
    public IPage search(Integer page,
                        Integer size,
                        BaseAttrValue baseAttrValue){
        //判断参数为null 分页查询所有
        if (null == baseAttrValue){
            baseAttrValueMapper.selectPage(new Page<>(page,size),null);
        }
        //分页条件查询并返回结果
        return baseAttrValueMapper.selectPage(new Page<>(page,size),buildQueryParams(baseAttrValue));
    }


    /**
     * 构建查询参数
     * @param baseAttrValue
     * @return
     */
    private LambdaQueryWrapper buildQueryParams(BaseAttrValue baseAttrValue) {
        //新建查询封装器
        LambdaQueryWrapper<BaseAttrValue> wrapper = new LambdaQueryWrapper<>();
        //查询条件判断
        if (null != baseAttrValue.getId()){
            wrapper.eq(BaseAttrValue::getId, baseAttrValue.getId());
        }
        if (!StringUtil.isEmpty(baseAttrValue.getValueName())){
            wrapper.like(BaseAttrValue::getValueName, baseAttrValue.getValueName());
        }
        if (null != baseAttrValue.getAttrId()){
            wrapper.eq(BaseAttrValue::getAttrId, baseAttrValue.getAttrId());
        }
        return wrapper;
    }
}
