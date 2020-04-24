package cn.hayring.sharingmachine.web;

import cn.hayring.sharingmachine.cons.CommonConstant;
import cn.hayring.sharingmachine.domain.User;
import cn.hayring.sharingmachine.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class UserFilter extends BaseFilter {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //System.out.println("UF" + ((HttpServletRequest)servletRequest).getRequestURI());
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String uri = request.getRequestURI();
        String[] sp = uri.split("/");
        if (checkPasses(sp[sp.length - 1])) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if (!checkUserAuthorization(request)) {
            String method = request.getMethod();
            if (sp[sp.length - 1].equals(PASS) && POST.equals(method)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }


            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;


        }
//        else if (request.getRequestURL().toString().contains(".do")) {
//            String token = request.getHeader("Authorization");
//            HttpServletResponse response = (HttpServletResponse) servletResponse;
//            if ( token == null || "null".equals(token)) {
//                response.sendRedirect(request.getContextPath() + "/login.html?msg=无token");
//                BaseController.loginUsers.remove(user.getId());
//                return;
//            } else {
//                Claims claims = jwtUtil.getClaimByToken(token);
//                if (claims == null || JwtUtil.isTokenExpired(claims.getExpiration())) {
//                    response.sendRedirect(request.getContextPath() + "/login.html?msg=无token");
//                    BaseController.loginUsers.remove(user.getId());
//                    request.getSession().removeAttribute(CommonConstant.USER_CONTEXT);
//                    return;
//                }
//            }
//        }


        filterChain.doFilter(servletRequest, servletResponse);
    }


    @Override
    public void destroy() {

    }

}
