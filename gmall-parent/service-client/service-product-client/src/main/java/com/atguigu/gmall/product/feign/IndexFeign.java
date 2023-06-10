package com.atguigu.gmall.product.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 包名:com.atguigu.gmall.product.feign
 *
 * @author Weirdo
 * 日期2023/3/31 21:05
 */
@FeignClient(name = "service-product",path = "/api/product",contextId = "IndexFeign")
public interface IndexFeign {

    /**
     * 查询首页分类信息
     * @return
     */
    @GetMapping("/getIndexCategory")
    public List<JSONObject> getIndexCategory();
}
