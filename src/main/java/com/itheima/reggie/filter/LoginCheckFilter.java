package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//拦截器名称，所有路径都拦截
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
@Component
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //强转向下转型，才能调用其子方法
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();

        log.info("拦截到请求：{}",requestURI);

        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",    //移动端发送短信
                "/user/login"   //移动端登陆
        };

        //2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3、如果不需要处理，则直接放行
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4-1、判断登陆状态，如果已登陆，则直接放行
        if (request.getSession().getAttribute("employee")!=null){
            log.info("用户已登陆，用户id为:{}",request.getSession().getAttribute("employee"));

            //给当前线程中设置当前id
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);


            filterChain.doFilter(request,response);
            return;
        }

        //4-2、判断登陆状态，如果已登陆，则直接放行
        if (request.getSession().getAttribute("user")!=null){
            log.info("用户已登陆，用户id为:{}",request.getSession().getAttribute("user"));

            //给当前线程中设置当前id
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);


            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登陆");
        //5、如果未登陆则返回未登陆结果，通过输出流方式向客户端响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }


    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
