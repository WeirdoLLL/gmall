package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.client.ItemPageFeign;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 包名:com.atguigu.gmall.all.controller
 *
 * @author Weirdo
 * 日期2023/3/29 1:31
 */
@Controller
@RequestMapping("/item/web")
public class ItemController {

    @Resource
    private ItemPageFeign itemPageFeign;
    @Resource
    private TemplateEngine templateEngine;
    /**
     * 打开商品详情页
     * @param skuId
     * @return
     */
    @GetMapping
    public String item(Long skuId, Model model){
        Map<String,Object> result = itemPageFeign.getItemPageInfo(skuId);
        model.addAllAttributes(result);
        return "item";
    }

    @ResponseBody
    @GetMapping("/creatHtml")
    public Result creatHtml(Long skuId) throws FileNotFoundException, UnsupportedEncodingException {
        //查询查询商品信息
        Map<String, Object> result = itemPageFeign.getItemPageInfo(skuId);
        //初始化容器
        Context context = new Context();
        context.setVariables(result);
        //声明文件
        File file = new File("d:/",skuId+".html");
        //声明输出流
        PrintWriter printWriter = new PrintWriter(file, "UTF-8");
        //生成静态页面
        /**
         * 模板
         * 数据容器
         * 输出流
         */
        templateEngine.process("itemTemplate",context,printWriter);
        //关闭流
        printWriter.flush();
        printWriter.close();
        return Result.ok();
    }
}
