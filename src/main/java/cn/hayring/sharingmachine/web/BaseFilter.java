package cn.hayring.sharingmachine.web;

import cn.hayring.sharingmachine.cons.CommonConstant;
import cn.hayring.sharingmachine.domain.Admin;
import cn.hayring.sharingmachine.domain.User;
import cn.hayring.sharingmachine.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public abstract class BaseFilter implements Filter {

    protected String[] passes = {".css", ".html", ".js"};

    public static final String PASS = "session";

    public static final String POST = "POST";

    protected JwtUtil jwtUtil;

    public static String AUTHORIZATION = "Authorization";


    protected MessageSource messageSource;


    protected static final String FILTERED_REQUEST = "@@session_context_filtered_request";


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext context = filterConfig.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
        jwtUtil = (JwtUtil) ctx.getBean("jwtUtil");
    }


    protected User getSessionUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute(CommonConstant.USER_CONTEXT);
    }


    protected Admin getSessionAdmin(HttpServletRequest request) {
        return (Admin) request.getSession().getAttribute(CommonConstant.ADMIN_CONTEXT);
    }


    protected boolean checkUserAuthorization(HttpServletRequest request) {
        return (request.getSession().getAttribute(CommonConstant.USER_CONTEXT) != null)
                && (checkToken(request.getHeader(AUTHORIZATION)));
    }

    protected boolean checkAdminAuthorization(HttpServletRequest request) {
        return (request.getSession().getAttribute(CommonConstant.ADMIN_CONTEXT) != null)
                && (checkToken(request.getHeader(AUTHORIZATION)));
    }

    protected boolean checkToken(String token) {
        if (token == null || "null".equals(token)) return false;
        Claims claims = jwtUtil.getClaimByToken(token);
        if (claims == null || JwtUtil.isTokenExpired(claims.getExpiration())) {
            return false;
        }
        return true;


    }

    public boolean checkPasses(String uri) {
        for (String elem : passes) {
            if (uri.indexOf(elem) > -1) return true;
        }
        return false;
    }


    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
