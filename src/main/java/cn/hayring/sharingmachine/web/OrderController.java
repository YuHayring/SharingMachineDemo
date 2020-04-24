package cn.hayring.sharingmachine.web;

import cn.hayring.sharingmachine.csjson.SelectResult;
import cn.hayring.sharingmachine.domain.User;
import cn.hayring.sharingmachine.service.OrderService;
import cn.hayring.sharingmachine.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * 记录资源控制器
 *
 * @author hayring
 */
@RestController
@RequestMapping("/order")
public class OrderController extends BaseController {

    private OrderService orderService;


    /**
     * 查询记录
     *
     * @param session   会话
     * @param pageNo    请求参数 页码
     * @param machineId 请求参数 设备编号
     * @param fromTime  请求参数 起始时间
     * @param toTime    请求参数 截止时间
     * @return 200 记录集合
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity requestOrders(HttpSession session, @RequestParam Integer pageNo,
                                        @RequestParam(required = false) String userId,
                                        @RequestParam(required = false) Integer machineId,
                                        @RequestParam(required = false) Date fromTime,
                                        @RequestParam(required = false) Date toTime) {
        User user = getSessionUser(session);
        if (user != null) {
            userId = user.getId();
        }

        List data = orderService.getOrderByParam(userId, machineId, fromTime, toTime, pageNo, Page.DEFAULT_PAGE_SIZE);
        if (data.size() == 0) {
            return ResponseEntity.notFound().build();
        }
        SelectResult result = new SelectResult(data);
        if (pageNo == 1) {
            long totalCount = orderService.countOrderByParam(userId, machineId, fromTime, toTime);
            result.setTotalCount(totalCount);
        }
        return ResponseEntity.ok(result);
    }


    /**
     * 删除记录
     *
     * @param id 记录id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }


    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }


}
