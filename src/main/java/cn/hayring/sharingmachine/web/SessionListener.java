package cn.hayring.sharingmachine.web;

import cn.hayring.sharingmachine.cons.CommonConstant;
import cn.hayring.sharingmachine.domain.Admin;
import cn.hayring.sharingmachine.domain.User;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {
    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        User user = (User) httpSessionEvent.getSession().getAttribute(CommonConstant.USER_CONTEXT);
        Admin admin = (Admin) httpSessionEvent.getSession().getAttribute(CommonConstant.ADMIN_CONTEXT);
        if (user != null) {
            BaseController.loginUsers.remove(user.getId());
        } else if (admin != null) {
            BaseController.loginAdmins.remove(admin.getId());
        }

    }
}
