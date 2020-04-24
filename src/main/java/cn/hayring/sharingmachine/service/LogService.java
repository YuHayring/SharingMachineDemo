package cn.hayring.sharingmachine.service;

import cn.hayring.sharingmachine.dao.LogMapperDao;
import cn.hayring.sharingmachine.domain.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService {
    private LogMapperDao logDao;


    public List getLogByUser(String userId, int pageNo, int pageSize) {
        long index = ((long) pageNo - 1L) * ((long) pageSize);
        return logDao.selectLogByUser(userId, index, pageSize);
    }

    public long countLogByUser(String userId) {
        return logDao.countLogByUser(userId);
    }

    public void addLog(Log log) {
        logDao.insertLog(log);
    }

    @Autowired
    public void setLogDao(LogMapperDao logDao) {
        this.logDao = logDao;
    }
}
