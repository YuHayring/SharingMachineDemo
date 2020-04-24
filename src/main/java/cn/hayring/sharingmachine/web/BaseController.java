package cn.hayring.sharingmachine.web;

import cn.hayring.sharingmachine.cons.CommonConstant;
import cn.hayring.sharingmachine.domain.Admin;
import cn.hayring.sharingmachine.domain.User;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * controller基类
 *
 * @author hayring
 */
public class BaseController {

    protected MessageSource messageSource;

    protected Gson gson;

    protected volatile static Map<String, HttpSession> loginUsers = new ConcurrentHashMap<>();

    protected volatile static Map<String, HttpSession> loginAdmins = new ConcurrentHashMap<>();

    protected static final String ERROR_MSG_KEY = "errorMsg";

    protected static final String MSG_KEY = "msg";

    /**
     * 获取保存在Session中的User
     *
     * @param request
     * @return
     */
    protected User getSessionUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute(CommonConstant.USER_CONTEXT);
    }

    /**
     * 获取保存在Session中的User
     *
     * @param session
     * @return
     */
    protected User getSessionUser(HttpSession session) {
        return (User) session.getAttribute(CommonConstant.USER_CONTEXT);
    }

    /**
     * 获取保存在Session中的Admin
     *
     * @param session
     * @return
     */
    protected Admin getSessionAdmin(HttpSession session) {
        return (Admin) session.getAttribute(CommonConstant.ADMIN_CONTEXT);
    }


    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Autowired
    @Qualifier(value = "gson")
    public void setGson(Gson gson) {
        this.gson = gson;
    }


}
