package com.atguigu.gmall.user.filter;

import com.atguigu.gmall.common.util.GmallThreadLocalUtil;
import com.atguigu.gmall.user.util.TokenUtil;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @author Weirdo
 * 日期2023/4/8 14:37
 * 用户微服务使用的过滤器
 */
@WebFilter(filterName = "userFilter",urlPatterns = "/*")
@Order(1)
public class UserFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        //获取请求对象 将ServletRequest类型对象servletRequest转换为更具体的HttpServletRequest类型对象request
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        //获取网关中的token
        String token = request.getHeader("Authorization").replace("bearer", "");
        //解析token
        Map<String, String> dcodeToken = TokenUtil.dcodeToken(token);
        if (null != dcodeToken){
            //获取token中存储的用户名信息 实际开发应该是用户id吧
            String username = dcodeToken.get("username");
            //存储
            GmallThreadLocalUtil.set(username);
        }
        //执行完过滤操作--->放行
        filterChain.doFilter(servletRequest,servletResponse);


    }
}
