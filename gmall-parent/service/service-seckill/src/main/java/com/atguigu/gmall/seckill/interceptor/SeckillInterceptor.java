package com.atguigu.gmall.seckill.interceptor;

import com.atguigu.gmall.common.util.GmallThreadLocalUtil;
import com.atguigu.gmall.seckill.util.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Weirdo
 * 日期2023/4/17 10:11
 * 自定义全局拦截器 用于处理本地线程中的用户名
 */
@Component
public class SeckillInterceptor implements HandlerInterceptor {
    /**
     * 前置拦截器获取用户名存储到本地线程
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取令牌
        String token = request.getHeader("Authorization").replace("bearer ", "");
        //解析令牌载荷中的数据
        Map<String, String> map = TokenUtil.dcodeToken(token);
        //获取用户名 存储到本地线程
        GmallThreadLocalUtil.set(map.get("username"));
        //放行
        return true;
    }

    /**
     * 后置拦截器清理本地线程存储的用户名
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清理本地线程的用户名
        GmallThreadLocalUtil.clear();
    }
}
