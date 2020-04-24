package cn.hayring.sharingmachine.web;

import cn.hayring.sharingmachine.cons.CommonConstant;
import cn.hayring.sharingmachine.domain.Admin;
import cn.hayring.sharingmachine.domain.User;
import cn.hayring.sharingmachine.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

public class SharingFilter extends BaseFilter implements Filter {

    //private static final String[] INHERENT_ESCAPE_URIS = {"login.html","/login","register.html","rootLogin.html","/root","machineClient.do"};

    @Override
    public void init(FilterConfig config) throws ServletException {

        ServletContext context = config.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
        jwtUtil = (JwtUtil) ctx.getBean("jwtUtil");

    }

    private JwtUtil jwtUtil;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestUri = request.getRequestURI();
        //共享登录过滤
        User user = getSessionUser(request);
        Admin admin = getSessionAdmin(request);
        //检查用户是否登录
        if (user == null && admin == null) {
//            String toUrl = request.getRequestURL().toString();
//            if(!StringUtils.isEmpty(request.getQueryString())) {
//                toUrl += "?" + request.getQueryString();
//            }
//            //保存目标url
//            request.getSession().setAttribute(CommonConstant.LOGIN_TO_URL,toUrl);

            //转发到登录
            //request.getRequestDispatcher("/login.html?errorMsg=用户未登录").forward(servletRequest,servletResponse);

            response.sendRedirect(request.getContextPath() + "/login.html?msg=未登录");
            return;
        } else if (request.getRequestURL().toString().contains(".do")) {
            String token = request.getHeader("Authorization");
            if (token == null) {
                response.sendRedirect(request.getContextPath() + "/login.html?msg=无token");
                return;
            } else {
                Claims claims = jwtUtil.getClaimByToken(token);
                if (claims == null || JwtUtil.isTokenExpired(claims.getExpiration())) {
                    response.sendRedirect(request.getContextPath() + "/login.html?msg=无token");
                    if (user != null) {
                        request.getSession().removeAttribute(CommonConstant.USER_CONTEXT);
                        BaseController.loginUsers.remove(user.getId());
                    } else if (admin != null) {
                        request.getSession().removeAttribute(CommonConstant.ADMIN_CONTEXT);
                        BaseController.loginAdmins.remove(admin.getId());
                    }
                    return;
                }
            }

//            } else if (claims.getExpiration().getTime() - (System.currentTimeMillis()/1000) < 60 ) {
//                String newToken = jwtUtil.generateToken(claims.getSubject());
//                response.setHeader("Authorization","Bearer"+newToken);
//            }

        }


        filterChain.doFilter(servletRequest, servletResponse);

    }

    @Override
    public void destroy() {

    }

//    /**
//     * 当前URI资源是否需要登录才能访问
//     * @param requestURI
//     * @param request
//     * @return
//     */
//    private boolean isURINeedLogin(String requestURI, HttpServletRequest request) {
//        if (request.getContextPath().equalsIgnoreCase(requestURI)
//                || (request.getContextPath() + "/").equalsIgnoreCase(requestURI))
//            return false;
//        for (String uri :INHERENT_ESCAPE_URIS) {
//            if (requestURI.indexOf(uri) >=0) return false;
//        }
//        return true;
//    }


    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


}
