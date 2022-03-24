package com.atguigu.gmall.order.filter;

import com.atguigu.gmall.order.util.OrderThreadLocalUtil;
import com.atguigu.gmall.order.util.TokenUtil;
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
 * 订单微服务的过滤器
 * @author XQ.Zhu
 */
@WebFilter(filterName = "orderFilter", urlPatterns = "/*")
@Order(1)//过滤器的执行顺序
public class OrderFilter extends GenericFilterBean {

    /**
     * 自定义的过滤器逻辑
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {
        //获取请求头中的令牌数据-->网关全局过滤器自己放的
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        //获取请求头中的参数
        String authorization = request.getHeader("Authorization");
        //去掉前缀: bearer+ 空格--->得到的就是token
        authorization = authorization.replace("bearer ", "");
        //解析令牌,从载荷中获取用户名:jwt令牌头+.+载荷+.+签名
        Map<String, String> map = TokenUtil.dcodeToken(authorization);
        //判断
        if(!map.isEmpty()){
            String username = map.get("username");
            if(!StringUtils.isEmpty(username)){
                //将用户名存储在ThreadLocal中
                OrderThreadLocalUtil.set(username);
            }
        }
        //放行
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
