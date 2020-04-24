package cn.hayring.sharingmachine.web;

import cn.hayring.sharingmachine.cons.CommonConstant;
import cn.hayring.sharingmachine.csjson.*;
import cn.hayring.sharingmachine.domain.Admin;
import cn.hayring.sharingmachine.domain.Maintenance;
import cn.hayring.sharingmachine.domain.User;
import cn.hayring.sharingmachine.service.AdminService;
import cn.hayring.sharingmachine.service.MachineService;
import cn.hayring.sharingmachine.service.MaintenanceService;
import cn.hayring.sharingmachine.service.OrderService;
import cn.hayring.sharingmachine.utils.JwtUtil;
import cn.hayring.sharingmachine.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 管理员控制器
 *
 * @author hayring
 */
@RestController
@RequestMapping("/admin")
public class AdminController extends BaseController {

    public static long ONE_DAY = 86400000;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    private JwtUtil jwtUtil;

    private OrderService orderService;

    private MachineService machineService;

    private AdminService adminService;


    private static final String ID = "id";
    private static final String MACHINE_ID = "machineId";
    private static final String TO_TIME = "toTime";
    private static final String FROM_TIME = "fromTime";
    private static final String PAGE_NO = "pageNo";


    /**
     * 登出
     *
     * @param session 会话
     * @param id
     * @return 403 没有权限使他人登出
     */
    @RequestMapping(value = "/{id}/session", method = RequestMethod.DELETE)
    public ResponseEntity logout(HttpSession session, @PathVariable String id) {
        //获取adminId
        String adminId = ((Admin) session.getAttribute(CommonConstant.ADMIN_CONTEXT)).getId();
        if (id.equals(adminId)) {
            //从session中删除
            session.removeAttribute(CommonConstant.ADMIN_CONTEXT);
            //从登录表中删除
            loginAdmins.remove(adminId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            //没有权限使他人登出
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }


    /**
     * 登录
     *
     * @param session  会话
     * @param id
     * @param password 请求参数 密码
     * @return 401 用户名或密码错误
     */
    @RequestMapping(value = "/{id}/session", method = RequestMethod.POST)
    public ResponseEntity login(HttpSession session, @PathVariable String id, @RequestParam String password) {
        Admin admin;
        if (null != (admin = adminService.login(id, password))) {
            if (loginAdmins.containsKey(id)) {
                //登出
                HttpSession oldSession = loginAdmins.get(id);
                if (!session.equals(oldSession)) {
                    oldSession.invalidate();
                }
            }
            //登录成功
            //设置Context
            loginAdmins.put(id, session);
            session.setAttribute(CommonConstant.ADMIN_CONTEXT, admin);
            String token = jwtUtil.generateToken(admin.getId());
            return ResponseEntity.ok(new Token(admin.getId(), token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    /**
     * 获取管理员信息
     *
     * @param session 会话
     * @param id
     * @return 403 没有权限获取他人信息
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity info(HttpSession session, @PathVariable String id) {
        //获取userId
        Admin admin = getSessionAdmin(session);
        if (id.equals(admin.getId())) {
            return ResponseEntity.ok(admin);
        } else {
            //没有权限获取他人信息
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Autowired
    public void setMachineService(MachineService machineService) {
        this.machineService = machineService;
    }


    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }


}
