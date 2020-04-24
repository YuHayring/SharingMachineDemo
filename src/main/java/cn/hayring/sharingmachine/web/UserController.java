package cn.hayring.sharingmachine.web;

import cn.hayring.sharingmachine.cons.CommonConstant;
import cn.hayring.sharingmachine.csjson.*;
import cn.hayring.sharingmachine.domain.User;
import cn.hayring.sharingmachine.service.LogService;
import cn.hayring.sharingmachine.service.MachineService;
import cn.hayring.sharingmachine.service.OrderService;
import cn.hayring.sharingmachine.service.UserService;
import cn.hayring.sharingmachine.utils.JwtUtil;
import cn.hayring.sharingmachine.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 用户控制器
 *
 * @author hayring
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
    public static long ONE_DAY = 86400000;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private JwtUtil jwtUtil;

    private UserService userService;


    /**
     * 登出
     *
     * @param session 会话
     * @param id
     * @return 403 没有权限使他人登出
     */
    @RequestMapping(value = "/{id}/session", method = RequestMethod.DELETE)
    public ResponseEntity logout(HttpSession session, @PathVariable String id) {
        //获取userId
        String userId = getSessionUser(session).getId();
        if (id.equals(userId)) {
            //从session中删除
            session.removeAttribute(CommonConstant.USER_CONTEXT);
            //从登录表中删除
            loginUsers.remove(userId);
            //
            userService.logout(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            //没有权限使他人登出
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }


    }


    /**
     * 获取用户信息
     *
     * @param session 会话
     * @param id
     * @return 403 没有权限获取他人信息
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity info(HttpSession session, @PathVariable String id) {
        //获取userId
        User user = getSessionUser(session);
        if (id.equals(user.getId())) {
            return ResponseEntity.ok(user);
        } else {
            //没有权限获取他人信息
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
        User user;
        if (null != (user = userService.login(id, password))) {

            if (loginUsers.containsKey(id)) {
                HttpSession oldSession = loginUsers.get(id);
                if (!session.equals(oldSession)) {
                    oldSession.invalidate();
                }
            }
            //登录成功
            //设置Context
            loginUsers.put(id, session);
            session.setAttribute(CommonConstant.USER_CONTEXT, user);
            String token = jwtUtil.generateToken(user.getId());
            return ResponseEntity.ok(new Token(user.getId(), token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    /**
     * 注册
     *
     * @param id
     * @param password 请求参数 密码
     * @param name     请求参数 名称
     * @return 403 用户已存在
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity register(@PathVariable String id, @RequestParam String name, @RequestParam String password) {
        User user = userService.register(id, name, password);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

    }


    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


//
//
//
//
//    @RequestMapping("/runMachineJson")
//    public BooleanJson runMachine(@RequestBody MachineId machineId, HttpServletRequest request) {
//        try {
//            User user = getSessionUser(request);
//            machineService.runMachine(machineId.getMachineId(),user.getId());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return BooleanJson.FALSE;
//        }
//
//        return BooleanJson.TURE;
//    }
//
//
//    @RequestMapping("/runMachine")
//    public BooleanJson runMachine(@RequestParam Integer machineId, HttpServletRequest request) {
//        try {
//            User user = getSessionUser(request);
//            machineService.runMachine(machineId, user.getId());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return BooleanJson.FALSE;
//        }
//
//        return BooleanJson.TURE;
//    }
//
//    @RequestMapping("/requestOrdersJson")
//    public CSJson requestOrders(HttpSession session, @RequestBody SelectArgs args) {
//        String userId = getSessionUser(session).getId();
//        if (args.getPageNo()==null) {
//            return new Message(messageSource.getMessage("common.param_illegal", null, null));
//        }
//
//        List data = orderService.getOrderByParam(userId,args.getMachineId(),args.getFromTime(),args.getToTime(), args.getPageNo(), Page.DEFAULT_PAGE_SIZE);
//        if (data.size() == 0) {
//            return new Message(messageSource.getMessage("order.no_order",null,null));
//        }
//        SelectResult result = new SelectResult(data);
//        if (args.getPageNo() == 1) {
//            long totalCount = orderService.countOrderByParam(userId,args.getMachineId(),args.getFromTime(),args.getToTime());
//            result.setTotalCount(totalCount);
//        }
//        return result;
//    }
//
//    @RequestMapping("/requestOrders")
//    public CSJson requestOrders(HttpSession session, @RequestParam Integer pageNo,
//                                @RequestParam(required = false) Integer machineId,
//                                @RequestParam(required = false) Date fromTime,
//                                @RequestParam(required = false) Date toTime) {
//        String userId = getSessionUser(session).getId();
//        List data = orderService.getOrderByParam(userId, machineId, fromTime, toTime, pageNo, Page.DEFAULT_PAGE_SIZE);
//        if (data.size() == 0) {
//            return new Message(messageSource.getMessage("order.no_order",null,null));
//        }
//        SelectResult result = new SelectResult(data);
//        if (pageNo == 1) {
//            long totalCount = orderService.countOrderByParam(userId, machineId, fromTime, toTime);
//            result.setTotalCount(totalCount);
//        }
//        return result;
//    }
//
//
//    @RequestMapping("/requestLogsJson")
//    public CSJson requestLogs(HttpSession session, @RequestBody SelectArgs args) {
//        String userId = getSessionUser(session).getId();
//        if (args.getPageNo()==null) {
//            return new Message(messageSource.getMessage("common.param_illegal", null, null));
//        }
//
//        List data = logService.getLogByUser(userId, args.getPageNo(), Page.DEFAULT_PAGE_SIZE);
//        if (data.size() == 0) {
//            return new Message(messageSource.getMessage("log.no_log",null,null));
//        }
//        SelectResult result = new SelectResult(data);
//        if (args.getPageNo() == 1) {
//            long totalCount = logService.countLogByUser(userId);
//            result.setTotalCount(totalCount);
//        }
//        return result;
//    }
//
//    @RequestMapping("/requestLogs")
//    public CSJson requestLogs(HttpSession session, @RequestParam Integer pageNo) {
//        String userId = getSessionUser(session).getId();
//
//        List data = logService.getLogByUser(userId, pageNo, Page.DEFAULT_PAGE_SIZE);
//        if (data.size() == 0) {
//            return new Message(messageSource.getMessage("log.no_log",null,null));
//        }
//        SelectResult result = new SelectResult(data);
//        if (pageNo == 1) {
//            long totalCount = logService.countLogByUser(userId);
//            result.setTotalCount(totalCount);
//        }
//        return result;
//    }


}
