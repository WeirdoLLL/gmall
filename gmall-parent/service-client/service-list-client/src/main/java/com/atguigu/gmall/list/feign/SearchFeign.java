package com.atguigu.gmall.list.feign;

import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 包名:com.atguigu.gmall.list.feign
 *
 * @author Weirdo
 * 日期2023/4/4 14:47
 * 商品搜索使用的feign接口
 */
@FeignClient(name = "service-list",path = "/api/search",contextId = "searchFeign")
public interface SearchFeign {

    /**
     * 商品搜索
     * @param searchData
     * @return
     */
    @GetMapping
    public Map<String, Object> search(@RequestParam Map<String,String> searchData);


}
