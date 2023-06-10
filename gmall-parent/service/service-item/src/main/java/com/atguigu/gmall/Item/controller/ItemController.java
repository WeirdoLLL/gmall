package com.atguigu.gmall.Item.controller;

import com.atguigu.gmall.Item.service.ItemService;
import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 包名:com.atguigu.gmall.Item.controller
 *
 * @author Weirdo
 * 日期2023/3/28 15:45
 * 商品详情页面控制层
 */
@RestController
@RequestMapping("/api/item")
public class ItemController {

    @Resource
    private ItemService itemService;

    /**
     * 获取商品详情页面信息
     * @param skuId
     * @return
     */
    @GetMapping("/getItemPageInfo/{skuId}")
    public Map<String, Object> getItemPageInfo(@PathVariable(value = "skuId") Long skuId){
        return itemService.getItemPageInfo(skuId);
    }

    }
