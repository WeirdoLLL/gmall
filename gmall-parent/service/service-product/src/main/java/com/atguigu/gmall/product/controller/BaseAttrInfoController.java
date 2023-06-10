package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * 包名:com.atguigu.gmall.product.controller
 * @author Weirdo
 * 日期2023/3/22 19:47
 * 平台属性控制层
 */
@RestController
@RequestMapping("/baseAttrInfo")
public class BaseAttrInfoController {
    @Resource
    private BaseAttrInfoService baseAttrInfoService;

    /**
     * 查询所有数据
     * @return
     */
    @GetMapping("/findAll")
    public Result findAll(){
        return Result.ok(baseAttrInfoService.findAll());
    }

    /**
     * 主键查询
     * @param id
     * @return
     */
    @GetMapping("/findById/{id}")
    public Result findById(@PathVariable Long id){
        return Result.ok(baseAttrInfoService.findOne(id));
    }

    /**
     * 新增数据
     * @param baseAttrInfo
     * @return
     */
    @PostMapping
    public Result add(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.add(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 更新数据 通过id
     * @param baseAttrInfo
     * @return
     */
    @PutMapping
    public Result update(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.update(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 删除数据 通过id
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Long id){
        baseAttrInfoService.deleteById(id);
        return Result.ok();
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/page/{page}/{size}")
    public Result page(@PathVariable(value = "page") Integer page,
                        @PathVariable(value = "size")  Integer size){

        return Result.ok(baseAttrInfoService.page(page, size));
    }

    /**
     * 条件查询
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/search")
    public Result search(@RequestBody BaseAttrInfo baseAttrInfo){
        return Result.ok(baseAttrInfoService.search(baseAttrInfo));
    }

    /**
     * 分页条件查询
     * @param baseAttrInfo
     * @param page
     * @param size
     * @return
     */
    @PostMapping("/search/{page}/{size}")
    public Result search(@RequestBody BaseAttrInfo baseAttrInfo,
                         @PathVariable(value = "page") Integer page,
                         @PathVariable(value = "size")Integer size){

        return Result.ok(baseAttrInfoService.search(baseAttrInfo,page,size));
    }
}

