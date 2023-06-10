package com.atguigu.gmall.item.client;

import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 包名:com.atguigu.gmall.item.client
 *
 * @author Weirdo
 * 日期2023/3/29 1:45
 * 供前端调用商品详情微服务Feign接口
 */
@FeignClient(name = "service-item",path = "/api/item")
public interface ItemPageFeign {

    /**
     * 获取商品详情页面信息
     * @param skuId
     * @return
     */
    @GetMapping("/getItemPageInfo/{skuId}")
    Map<String, Object> getItemPageInfo(@PathVariable(value = "skuId") Long skuId);
}
