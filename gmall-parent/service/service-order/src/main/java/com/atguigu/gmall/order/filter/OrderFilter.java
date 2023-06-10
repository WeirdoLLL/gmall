package com.atguigu.gmall.order.filter;

import com.atguigu.gmall.common.util.GmallThreadLocalUtil;
import com.atguigu.gmall.order.util.TokenUtil;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @author Weirdo
 * 日期2023/4/9 22:55
 * 订单微服务的过滤器
 */
@WebFilter(filterName = "OrderFilter", urlPatterns = "/*")
@Order(1)
public class OrderFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        //获取请求体对象
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        //获取自己在网关中存入的令牌
        String token =
                request.getHeader("Authorization").replace("bearer ", "");
        //解析token的载荷
        Map<String, String> map = TokenUtil.dcodeToken(token);
        if(map != null){
            //获取用户名
            String username = map.get("username");
            //存储
            GmallThreadLocalUtil.set(username);
        }
        //放行
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
