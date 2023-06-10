package com.atguigu.gmall.list.service;

import java.util.Map;

/**
 * 包名:com.atguigu.gmall.list.service
 *
 * @author Weirdo
 * 日期2023/4/3 14:09
 * 商品搜索接口类
 */
public interface SearchService {

    /**
     * 商品搜索
     * @param searchData
     * @return
     */
    Map<String, Object> search(Map<String,String> searchData);
}
