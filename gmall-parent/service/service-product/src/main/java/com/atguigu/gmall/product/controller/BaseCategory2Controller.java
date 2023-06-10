package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 包名:com.atguigu.gmall.product.controller
 *
 * @author Weirdo
 * 日期2023/3/23 23:01
 * 商品基本类别表2的控制层
 */
@RestController
@RequestMapping("/baseCategory2")
public class BaseCategory2Controller {

    @Resource
    private BaseCategory2Service baseCategory2Service;

    /**
     * 查询所有数据
     * @return
     */
    @GetMapping
    public List<BaseCategory2> findAll(){return baseCategory2Service.findAll();}

    /**
     * 主键id查询
     * @param id
     * @return
     */
    @GetMapping("/findOne/{id}")
    public BaseCategory2 findOne(@PathVariable Long id){return baseCategory2Service.findOne(id);}

    /**
     * 新增数据
     * @param baseCategory2
     * @return
     */
    @PostMapping
    public Result<Object> add(@RequestBody BaseCategory2 baseCategory2){
        baseCategory2Service.add(baseCategory2);
        return Result.ok();
    }

    /**
     * 修改数据 根据对象id
     * @param baseCategory2
     * @return
     */
    @PutMapping
    public Result<Object> update(@RequestBody BaseCategory2 baseCategory2){
        baseCategory2Service.update(baseCategory2);
        return Result.ok();
    }

    /**
     * 删除数据 根据路径id
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result<Object> delete(@PathVariable Long id){
        baseCategory2Service.delete(id);
        return Result.ok();
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/page/{page}/{size}")
    public Result<Result<IPage<BaseCategory2>>> page(@PathVariable Integer page,
                                                     @PathVariable Integer size){
        return Result.ok(baseCategory2Service.page(page, size));
    }

    /**
     * 条件查询
     * @param baseCategory2
     * @return
     */
    @PostMapping("/search")
    public Result<List<BaseCategory2>> search(@RequestBody BaseCategory2 baseCategory2){
        return Result.ok(baseCategory2Service.search(baseCategory2));
    }

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param baseCategory2
     * @return
     */
    @PostMapping("/search/{page}/{size}")
    public Result<IPage<BaseCategory2>> search(@PathVariable Integer page,
                                               @PathVariable Integer size,
                                               @RequestBody BaseCategory2 baseCategory2){

        return Result.ok(baseCategory2Service.search(page, size, baseCategory2));
    }
}
