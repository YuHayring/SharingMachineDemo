package cn.hayring.sharingmachine.web;

import cn.hayring.sharingmachine.cons.CommonConstant;
import cn.hayring.sharingmachine.csjson.CSJson;
import cn.hayring.sharingmachine.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;


public class TokenUpdater implements HandlerInterceptor {


    private static final long TIME_OUT_LEFT = 60000L;

    private JwtUtil jwtUtil;


    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object attribute = request.getHeader("Authorization");
        if (attribute == null) {
            throw new AuthenticationException("There is no token!");
        }
        String token = (String) attribute;
        Claims claims = jwtUtil.getClaimByToken(token);

        if (claims == null) {
            String newToken = jwtUtil.generateToken(claims.getSubject());
            httpServletResponse.setHeader("Authorization", newToken);
        } else {
            long timeOut = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (timeOut < TIME_OUT_LEFT) {
                String newToken = jwtUtil.generateToken(claims.getSubject());
                httpServletResponse.setHeader("Authorization", newToken);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
