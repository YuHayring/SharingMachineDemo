package cn.hayring.sharingmachine.dao;

import cn.hayring.sharingmachine.domain.Machine;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/***
 * 机器Dao查询接口
 */
public interface MachineMapperDao {
    /***
     * 根据id查询
     * @param id
     * @return machine
     */
    Machine selectMachineById(@Param("id") Integer id);


    /***
     * 根据状态查询
     * @param status
     * @return
     */
    List selectMachineByParam(@Param("status") Integer status, @Param("index") long index, @Param("size") int size);

    /***
     * 根据调条件计
     * @param status
     * @return
     */
    long countMachineByParam(@Param("status") Integer status);

    /**
     * 插入机器，返回主键
     *
     * @param machine
     * @return key
     */
    void insertMachine(@Param("machine") Machine machine);

    /***
     * 按已有条件更新
     * @param machine
     */
    void updateMachineByIdSelective(@Param("machine") Machine machine);

    /**
     * 全部更新
     *
     * @param machine
     */
    void updateMachineAllById(@Param("machine") Machine machine);

    /***
     * 删除
     * @param id
     */
    void deleteMachineById(@Param("id") Integer id);

    /***
     * 更新设备状态
     * @param
     */
    void updateMachineStatus(@Param("machineId") Integer machineId, @Param("status") int status);

    /**
     * 改变所有设备的状态
     *
     * @param status
     */
    void updateAllMachineStatus(@Param("status") int status);

}
