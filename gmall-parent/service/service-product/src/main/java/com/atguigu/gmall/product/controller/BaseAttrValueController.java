package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 包名:com.atguigu.gmall.product.controller
 *
 * @author Weirdo
 * 日期2023/3/23 11:31
 * 平台属性值类控制层
 */
@RestController
@RequestMapping("/baseAttrValue")
public class BaseAttrValueController {

    @Resource
    private BaseAttrValueService baseAttrValueService;

    /**
     * 查询所有数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return Result.ok(baseAttrValueService.findAll());
    }

    /**
     * RESTFul 新增
     *
     * @param baseAttrValue
     * @return
     */
    @PostMapping
    public Result add(@RequestBody BaseAttrValue baseAttrValue) {
        baseAttrValueService.add(baseAttrValue);
        return Result.ok();
    }

    /**
     * 删除数据 根据id
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Long id) {
        baseAttrValueService.deletedById(id);
        return Result.ok();
    }

    /**
     * 修改数据根据传入参数内的id
     *
     * @param baseAttrValue
     * @return
     */
    @PutMapping
    public Result updateById(@RequestBody BaseAttrValue baseAttrValue) {
        baseAttrValueService.updateById(baseAttrValue);
        return Result.ok();
    }

    /**
     * 根据主键id查询
     *
     * @param id
     * @return
     */
    @GetMapping("/findById/{id}")
    public Result findById(@PathVariable Long id) {
        return Result.ok(baseAttrValueService.findById(id));
    }

    /**
     * 分页查询所有
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/page/{page}/{size}")
    public Result<IPage<BaseAttrValue>> page(@PathVariable("page") Integer page,
                                             @PathVariable("size") Integer size) {
        return Result.ok(baseAttrValueService.page(page, size));

    }

    /**
     * 条件查询
     * @param baseAttrValue
     * @return
     */
    @PostMapping("/search")
    public Result<List<BaseAttrValue>> search(@RequestBody BaseAttrValue baseAttrValue) {

        return Result.ok(baseAttrValueService.search(baseAttrValue));
    }

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param baseAttrValue
     * @return
     */
    @PostMapping("/search/{page}/{size}")
    public Result<IPage> search(@PathVariable("page") Integer page,
                                @PathVariable("size") Integer size,
                                @RequestBody BaseAttrValue baseAttrValue){
        return Result.ok(baseAttrValueService.search(page,size,baseAttrValue));
    }
}



