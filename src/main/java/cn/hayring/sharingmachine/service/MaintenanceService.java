package cn.hayring.sharingmachine.service;

import cn.hayring.sharingmachine.dao.MaintenanceMapperDao;
import cn.hayring.sharingmachine.domain.Maintenance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MaintenanceService {
    private MaintenanceMapperDao maintenanceDao;


    public List getMaintenanceByParam(String userId, Integer machineId, Date fromTime, Date toTime, int pageNo, int pageSize) {
        long index = ((long) pageNo - 1L) * ((long) pageSize);
        return maintenanceDao.selectMaintenanceByParam(userId, machineId, fromTime, toTime, index, pageSize);

    }

    public long countMaintenanceByParam(String userId, Integer machineId, Date fromTime, Date toTime) {
        return maintenanceDao.countMaintenanceByParam(userId, machineId, fromTime, toTime);
    }

    public void addMaintenance(Maintenance maintenance) {
        maintenanceDao.insertMaintenance(maintenance);
    }


    @Autowired
    public void setMaintenanceDao(MaintenanceMapperDao maintenanceDao) {
        this.maintenanceDao = maintenanceDao;
    }
}
