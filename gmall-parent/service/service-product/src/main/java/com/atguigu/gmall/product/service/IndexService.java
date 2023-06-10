package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 包名:com.atguigu.gmall.product.service
 *
 * @author Weirdo
 * 日期2023/3/31 19:30
 *
 * 首页使用的接口
 */
public interface IndexService {

    /**
     * 查询首页分类信息
     * @return
     */
    List<JSONObject> getIndexCategory();
}
