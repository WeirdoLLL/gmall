package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.list.service.SearchService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 包名:com.atguigu.gmall.list.controller
 *
 * @author Weirdo
 * 日期2023/4/3 15:07
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {
    @Resource
    private SearchService searchService;

    /**
     * 商品搜索
     * @param searchData
     * @return
     */
    @GetMapping
    public Map<String, Object> search(@RequestParam Map<String,String> searchData){
        return searchService.search(searchData);
    }
}
