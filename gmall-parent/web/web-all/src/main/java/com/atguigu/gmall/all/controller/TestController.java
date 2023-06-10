package com.atguigu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 包名:com.atguigu.gmall.all.controller
 *
 * @author Weirdo
 * 日期2023/3/29 2:20
 */
@Controller
@RequestMapping("/api/test")
public class TestController {
    @GetMapping
    public String test(Model model){
        model.addAttribute("a","A");
        return "test";
    }
}
