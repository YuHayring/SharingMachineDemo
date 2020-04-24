package cn.hayring.sharingmachine.dao;

import cn.hayring.sharingmachine.domain.Log;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 登录记录Dao接口
 */
public interface LogMapperDao {

    Log selectLogById(@Param("logId") String logId);

    List selectLogByUser(@Param("userId") String userId, @Param("index") long index, @Param("size") int size);

    long countLogByUser(@Param("userId") String userId);

    long countAllLog();

    void insertLog(@Param("log") Log log);

    void deleteLogById(@Param("logId") String logId);
}
