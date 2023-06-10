package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.all.util.Page;
import com.atguigu.gmall.list.feign.SearchFeign;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 包名:com.atguigu.gmall.all.controller
 *
 * @author Weirdo
 * 日期2023/4/4 13:54
 * 搜索页面的控制层
 */
@Controller
@RequestMapping("/page/search")
public class SearchController {

    @Resource
    private SearchFeign searchFeign;
    @Value("${item.url}")
    private String itemUrl;

    @GetMapping
    public String search(@RequestParam Map<String,String> searchData, Model model){
        //通过feign调用获取搜索结果
        Map<String, Object> searchResult = searchFeign.search(searchData);
        //查询数据放入model
        model.addAllAttributes(searchResult);
        //搜索条件回显
        model.addAttribute("searchData",searchData);
        //获取当前的url
        String url = getUrl(searchData);
        model.addAttribute("url",url);
        //获取排序的url
        String sortUrl  = getSortUrl(searchData);
        model.addAttribute("sortUrl",sortUrl);
        //获取当前页码
        Page pageInfo = new Page<>(Long.parseLong(searchResult.get("totalHits").toString()),
                getPage(searchData.get("pageNum")),
                50);
        model.addAttribute("pageInfo",pageInfo);
        //获取详情页前缀名
        model.addAttribute("itemUrl",itemUrl);
        //打开搜索页面
        return "list";
    }

    /**
     * 计算页码
     *
     * @param pageNum
     * @return
     */
    private int getPage(String pageNum) {
        try {
            int i = Integer.parseInt(pageNum);
            return i > 0 ? i : 1;
        } catch (Exception e) {
            return 1;
        }
    }
    /**
     * 排序的url
     * @param searchData
     * @return
     */
    private String getSortUrl(Map<String,String> searchData) {
        //初始化url
        StringBuffer sb = new StringBuffer("/page/search?");
        //拼接前段传入的条件
        searchData.entrySet().stream().forEach(entry->{
            String key = entry.getKey();
            if(!key.equals("sortField")&&
                !key.equals("sortRule")&&
                    !key.equals("pageNum")){
                String value = entry.getValue();
                sb.append(key+"="+value+"&");
            }
        });
        String url = sb.toString();
        return url.substring(0,url.length()-1);

    }

    /**
     * 拼接当前url
     * @param searchData
     * @return
     */
    private String getUrl(Map<String,String> searchData) {
        //初始化url
        StringBuffer sb = new StringBuffer("/page/search?");
        //拼接前段传入的条件
        searchData.entrySet().stream().forEach(entry->{
            String key = entry.getKey();
            if (!key.equals("pageNum")){
                String value = entry.getValue();
                sb.append(key+"="+value+"&");
            }
        });
        String url = sb.toString();
        return url.substring(0,url.length()-1);

    }

}
