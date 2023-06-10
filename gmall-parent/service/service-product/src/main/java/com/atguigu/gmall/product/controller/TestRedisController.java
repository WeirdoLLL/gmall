package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.TestRedisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 包名:com.atguigu.gmall.product.controller
 *
 * @author Weirdo
 * 日期2023/3/29 18:51
 */
@RestController
@RequestMapping("/admin/product")
public class TestRedisController {
    @Resource
    private TestRedisService testRedisService;
    @GetMapping("/test")
    public Result<Object> testRedis(){
        testRedisService.testRedis();
        return Result.ok();
    }
    @GetMapping("/testRedisson")
    public Result<Object> testRedisson(){
        testRedisService.testRedisson();
        return Result.ok();
    }
}
