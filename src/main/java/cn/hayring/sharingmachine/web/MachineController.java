package cn.hayring.sharingmachine.web;

import cn.hayring.sharingmachine.cons.CommonConstant;
import cn.hayring.sharingmachine.csjson.*;
import cn.hayring.sharingmachine.domain.Admin;
import cn.hayring.sharingmachine.domain.Machine;
import cn.hayring.sharingmachine.domain.Maintenance;
import cn.hayring.sharingmachine.domain.User;
import cn.hayring.sharingmachine.service.MachineService;
import cn.hayring.sharingmachine.service.MaintenanceService;
import cn.hayring.sharingmachine.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * 设备控制器
 *
 * @author hayring
 */
@RestController
@RequestMapping("/machine")
public class MachineController extends BaseController {

    private MachineService machineService;


    private MaintenanceService maintenanceService;


    /**
     * 获取设备信息Redis passed
     *
     * @param id
     * @return 404 找不到设备
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity requestMachine(@PathVariable Integer id) {
        Machine machine = machineService.pullMachineByIdAuto(id);
        if (machine == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(machine);
        }
    }


    /**
     * 查询设备Redis passed
     *
     * @param session 请求参数 会话
     * @param status  请求参数 状态
     * @param pageNo  请求参数 页码
     * @return 200 记录集合
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity requestMachine(HttpSession session, @RequestParam(required = false) Integer status, @RequestParam Integer pageNo) {
        List data = machineService.pullMachineByParam(status, pageNo, Page.DEFAULT_PAGE_SIZE);
        if (data.size() == 0) {
            return ResponseEntity.notFound().build();
        }
        SelectResult result = new SelectResult(data);
        if (pageNo == 1) {
            long totalCount = machineService.countMachineByParam(status);
            result.setTotalCount(totalCount);
        }
        return ResponseEntity.ok(result);
    }


    /**
     * 更新设备
     *
     * @param id
     * @param address   请求参数 地址
     * @param longitude 请求参数 经度
     * @param latitude  请求参数 纬度
     * @return 204 成功
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateMachine(@PathVariable Integer id,
                                        @RequestParam(required = false) String address,
                                        @RequestParam(required = false) Double longitude,
                                        @RequestParam(required = false) Double latitude) {
        if (address == null && longitude == null && latitude == null) return ResponseEntity.noContent().build();

        machineService.updateMachine(id, address, longitude, latitude);
        return ResponseEntity.noContent().build();
    }


    /**
     * 删除设备 Redis passed
     *
     * @param id 设备id
     * @return 204 成功
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteMachine(HttpSession session, @PathVariable Integer id) {
        if (session.getAttribute(CommonConstant.ADMIN_CONTEXT) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (machineService.deleteMachine(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }


    /**
     * 增加设备 Redis passed
     *
     * @param address   地址
     * @param longitude 经度
     * @param latitude  纬度
     * @return 200 设备id
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity addMachine(@RequestParam String address, @RequestParam Double longitude, @RequestParam Double latitude) {
        Machine machine = machineService.addNewMachine(address, longitude, latitude);
        return ResponseEntity.ok(machine);
    }


    /**
     * 运行设备
     *
     * @param id      设备id
     * @param session 会话
     * @return 204 成功
     */
    @RequestMapping(value = "/{id}/session", method = RequestMethod.POST)
    public ResponseEntity runMachine(@PathVariable Integer id, HttpSession session) {
        User user = (User) session.getAttribute(CommonConstant.USER_CONTEXT);
        if (machineService.runMachine(id, user.getId())) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


    /***
     * 设备维护
     * @param session 会话
     * @param id 设备id
     * @return 204 成功
     */
    @RequestMapping(value = "/{id}/maintain", method = RequestMethod.POST)
    public ResponseEntity maintainMachine(HttpSession session, @PathVariable Integer id) {
        Maintenance maintenance = new Maintenance();
        maintenance.setTime(new Date());
        maintenance.setMachineId(id);
        String adminId = ((Admin) session.getAttribute(CommonConstant.ADMIN_CONTEXT)).getId();
        maintenance.setAdminId(adminId);
        maintenanceService.addMaintenance(maintenance);
        return ResponseEntity.noContent().build();
    }


    @Autowired
    public void setMachineService(MachineService machineService) {
        this.machineService = machineService;
    }

    @Autowired
    public void setMaintenanceService(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }
}
