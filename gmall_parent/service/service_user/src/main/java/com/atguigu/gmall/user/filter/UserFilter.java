package com.atguigu.gmall.user.filter;

import com.atguigu.gmall.user.utils.TokenUtil;
import com.atguigu.gmall.user.utils.UserThreadLocalUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * userFilter 用户微服务的过滤器
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/15 14:55
 **/
@WebFilter(filterName = "userFilter",urlPatterns = "/*")
@Order(1)
public class UserFilter extends GenericFilterBean {
    /**
     * 自定义的过滤器逻辑
     * @param servletRequest  The request to process
     * @param servletResponse The response associated with the request
     * @param filterChain    Provides access to the next filter in the chain for this
     *                 filter to pass the request and response to for further
     *                 processing
     * @throws IOException      if an I/O error occurs during this filter's
     *                          processing of the request
     * @throws ServletException if the processing fails for any other reason
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        //获取请求头中的数据,->网关全部过滤器自己放的
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //获取请求头中的参数
        String authorization = request.getHeader("Authorization");
        //去掉bearer和空格, 获得token
        authorization= authorization.replace("bearer ","");
        //解析令牌, 获取用户名
        Map<String, String> map = TokenUtil.decodeToken(authorization);
        //判断map,并取出username存入threadLocal对象
        if(!map.isEmpty()){
            String username = map.get("username");
            if(!StringUtils.isEmpty(username)){
                UserThreadLocalUtils.set(username);
            }
        }
        //放行
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
