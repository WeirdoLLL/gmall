package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.common.constant.CartConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author Weirdo
 * 日期2023/4/7 19:22
 * 购物车服务控制层
 */
@RestController
@RequestMapping("/api/cart")
public class CartInfoController {

    @Resource
    private CartInfoService cartInfoService;

    /**
     * 购物车芯新增
     *
     * @return
     */
    @GetMapping("/addCart")
    public Result<Object> addCart(Long skuId, Integer num) {
        cartInfoService.addCart(skuId, num);
        return Result.ok();
    }

    /**
     * 查询购物车数据
     * @return
     */
    @GetMapping("/getCartInfo")
    public Result<List<CartInfo>> getCartInfo(){
        return Result.ok(cartInfoService.getCartInfo());
    }

    /**
     * 删除购物车数据
     * @param
     * @param id
     * @return
     */
    @GetMapping("/removeCart")
    public Result removeCart(Long id){
        cartInfoService.removeCart(id);
        return Result.ok();
    }

    /**
     * 选中和全选
     * @return
     */
    @GetMapping("/check")
    public Result check(Long id){
        cartInfoService.updateCheckStatus(id,CartConst.CART_CHECK);
        return Result.ok();
    }
    /**
     * 取消选中和取消全选
     * @return
     */
    @GetMapping("/uncheck")
    public Result uncheck(Long id){
        cartInfoService.updateCheckStatus(id,CartConst.CART_UNCHECK);
        return Result.ok();
    }

    /**
     * 合并购物车: 不是给用户调用的,登录以后触发的
     * @param cartInfoList
     * @return
     */
    @PostMapping("/mergerCartInfo")
    public Result mergerCartInfo(@RequestBody List<CartInfo> cartInfoList){
        cartInfoService.mergeCartInfo(cartInfoList);
        return Result.ok();
    }

    /**
     * 提交订单 确认购物车数据信息
     * @return
     */
    @GetMapping("/getOrderConfirmCartInfo")
    public Result<Map<String, Object>> gerOrderConfirmCartInfo(){
        return Result.ok(cartInfoService.getOrderConfirmCartInfo());
    }
}
