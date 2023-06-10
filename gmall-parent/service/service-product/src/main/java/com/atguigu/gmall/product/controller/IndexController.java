package com.atguigu.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.product.service.IndexService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 包名:com.atguigu.gmall.product.controller
 *
 * @author Weirdo
 * 日期2023/3/31 20:52
 */
@RestController
@RequestMapping("/api/product")
public class IndexController {

    @Resource
    private IndexService indexService;

    /**
     * 查询首页分类信息
     * @return
     */
    @GetMapping("/getIndexCategory")
    public List<JSONObject> getIndexCategory(){
        return indexService.getIndexCategory();

    }
}
