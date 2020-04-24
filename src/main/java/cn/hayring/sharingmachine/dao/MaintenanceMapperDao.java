package cn.hayring.sharingmachine.dao;

import cn.hayring.sharingmachine.domain.Maintenance;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface MaintenanceMapperDao {
    long countMaintenanceByParam(@Param("adminId") String adminId, @Param("machineId") Integer machineId, @Param("fromTime") Date fromTime, @Param("toTime") Date toTime);

    List selectMaintenanceByParam(@Param("adminId") String adminId, @Param("machineId") Integer machineId, @Param("fromTime") Date fromTime, @Param("toTime") Date toTime, @Param("index") long index, @Param("size") int size);

    void insertMaintenance(@Param("maintenance") Maintenance maintenance);

    Maintenance selectMaintenanceById(@Param("id") String id);

    long countAllMaintenance();

    /**
     * 查询全部
     *
     * @param index
     * @param size
     * @return
     */
    List selectMaintenanceAll(@Param("index") long index, @Param("size") int size);

    void deleteMaintenanceById(@Param("id") String id);
}
