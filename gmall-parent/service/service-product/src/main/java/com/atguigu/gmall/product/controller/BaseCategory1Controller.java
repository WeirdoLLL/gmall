package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 包名:com.atguigu.gmall.product.controller
 *
 * @author Weirdo
 * 日期2023/3/23 16:26
 * 商品基本类别表1控制层
 */
@RestController
@RequestMapping("/baseCategory1")
public class BaseCategory1Controller {

    @Resource
    private BaseCategory1Service baseCategory1Service;

    /**
     * 查询所有数据
     * @return
     */
    @GetMapping
    public Result<List<BaseCategory1>> findAll(){
        return Result.ok(baseCategory1Service.findAll());
    }

    /**
     * 查询一条数据 根据id
     * @param id
     * @return
     */
    @GetMapping("/findOne/{id}")
    public Result<BaseCategory1> findOne(@PathVariable Long id){
        return Result.ok(baseCategory1Service.findOne(id));
    }

    /**
     * 新增数据
     * @param baseCategory1
     * @return
     */
    @PostMapping
    public Result<Object> add(@RequestBody BaseCategory1 baseCategory1){
        baseCategory1Service.add(baseCategory1);
        return Result.ok();
    }

    /**
     * 修改数据
     * @param baseCategory1
     * @return
     */
    @PutMapping
    public Result<Object> update(@RequestBody BaseCategory1 baseCategory1){
        baseCategory1Service.update(baseCategory1);
        return Result.ok();
    }

    /**
     * 删除 根据路径id
     * @param id
     */
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id){
        baseCategory1Service.delete(id);
    }

    /**
     * 分页查询所有
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/page/{page}/{size}")
    public Result<IPage<BaseCategory1>> page(@PathVariable("page") Integer page,
                                             @PathVariable("size") Integer size){
        return Result.ok(baseCategory1Service.page(page,size));
    }

    /**
     * 条件查询
     * @param baseCategory1
     * @return
     */
    @PostMapping("search")
    public Result<List<BaseCategory1>> search(@RequestBody BaseCategory1 baseCategory1){
        return Result.ok(baseCategory1Service.search(baseCategory1));
    }

    @PostMapping("/search/{page}/{size}")
    public Result<IPage<BaseCategory1>> search(@PathVariable("page") Integer page,
                                               @PathVariable("size") Integer size,
                                               @RequestBody BaseCategory1 baseCategory1){
        return Result.ok(baseCategory1Service.search(page,size,baseCategory1));
    }

}
