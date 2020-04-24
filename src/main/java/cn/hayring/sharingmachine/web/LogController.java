package cn.hayring.sharingmachine.web;

import cn.hayring.sharingmachine.csjson.SelectResult;
import cn.hayring.sharingmachine.service.LogService;
import cn.hayring.sharingmachine.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 登录记录控制器
 *
 * @author hayring
 */
@RequestMapping("/log")
@RestController
public class LogController extends BaseController {
    private LogService logService;


    /**
     * 查询记录
     *
     * @param session 会话
     * @param pageNo  请求参数 页码
     * @return 200 登录记录集合
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity requestLogs(HttpSession session, @RequestParam Integer pageNo) {
        String userId = getSessionUser(session).getId();

        List data = logService.getLogByUser(userId, pageNo, Page.DEFAULT_PAGE_SIZE);
        if (data.size() == 0) {
            return ResponseEntity.notFound().build();
        }
        SelectResult result = new SelectResult(data);
        if (pageNo == 1) {
            long totalCount = logService.countLogByUser(userId);
            result.setTotalCount(totalCount);
        }
        return ResponseEntity.ok(result);
    }

    @Autowired
    public void setLogService(LogService logService) {
        this.logService = logService;
    }


}
