package cn.hayring.sharingmachine.web;

import cn.hayring.sharingmachine.csjson.SelectResult;
import cn.hayring.sharingmachine.service.MaintenanceService;
import cn.hayring.sharingmachine.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * 维护记录资源控制器
 *
 * @author hayring
 */
@RequestMapping("/maintenance")
@RestController
public class MaintenanceController extends BaseController {


    private MaintenanceService maintenanceService;


    /**
     * 查询维护记录
     *
     * @param session   会话
     * @param pageNo    请求参数 页码
     * @param adminId   请求参数 管理员id
     * @param machineId 请求参数 设备id
     * @param fromTime  请求参数 起始时间
     * @param toTime    请求参数 截止时间
     * @return 200 记录集合
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity requestMaintenance(HttpSession session, @RequestParam Integer pageNo,
                                             @RequestParam(required = false) String adminId,
                                             @RequestParam(required = false) Integer machineId,
                                             @RequestParam(required = false) Date fromTime,
                                             @RequestParam(required = false) Date toTime) {

        List data = maintenanceService.getMaintenanceByParam(adminId, machineId, fromTime, toTime, pageNo, Page.DEFAULT_PAGE_SIZE);
        if (data.size() == 0) {
            return ResponseEntity.notFound().build();
        }
        SelectResult result = new SelectResult(data);
        if (pageNo == 1) {
            long totalCount = maintenanceService.countMaintenanceByParam(adminId, machineId, fromTime, toTime);
            result.setTotalCount(totalCount);
        }
        return ResponseEntity.ok(result);
    }


    @Autowired
    public void setMaintenanceService(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

}
