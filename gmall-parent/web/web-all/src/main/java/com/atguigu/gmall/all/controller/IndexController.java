package com.atguigu.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.product.feign.IndexFeign;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

/**
 * 包名:com.atguigu.gmall.all.controller
 *
 * @author Weirdo
 * 日期2023/3/31 21:01
 */
@Controller
@RequestMapping("/index")
public class IndexController {
    @Value("${search.url}")
    private String searchUrl;

    @Resource
    private IndexFeign indexFeign;

    /**
     * 打开首页页面
     * @return
     */
    @GetMapping
    public String index(Model model){
        //远程调用feign映射
        List<JSONObject> categoryList = indexFeign.getIndexCategory();
        //存储数据
        model.addAttribute("categoryList",categoryList);
        //存储搜索页面的url
        model.addAttribute("searchUrl",searchUrl);
        //返回页面
        return "index";
    }
}
