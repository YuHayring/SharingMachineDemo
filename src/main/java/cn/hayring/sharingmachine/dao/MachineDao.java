package cn.hayring.sharingmachine.dao;

import cn.hayring.sharingmachine.domain.Machine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class MachineDao {

    private RedisTemplate redisTemplate;

    private MachineMapperDao machineMapperDao;

    private HashOperations operations;


//    @Cacheable(cacheNames = "redis",key = "'machine'.concat(#id)", unless = "#result == null")


    public List selectMachineByParam(Integer status, long index, int size) {
        return machineMapperDao.selectMachineByParam(status, index, size);
    }

    public long countMachineByParam(Integer status) {
        return machineMapperDao.countMachineByParam(status);
    }

    public void insertMachine(Machine machine) {
        machineMapperDao.insertMachine(machine);
    }

    //@CacheEvict(cacheNames = "redis",key = "'machine'.concat(#machine.id)")
    public Machine updateMachineByIdSelective(Machine machine) {
        machineMapperDao.updateMachineByIdSelective(machine);
        return machine;
    }

    //@CacheEvict(cacheNames = "redis",key = "'machine'.concat(#id)")
    public Machine updateMachineAllById(Machine machine) {
        machineMapperDao.updateMachineAllById(machine);
        return machine;
    }

    public void deleteMachineById(Integer id) {
        machineMapperDao.deleteMachineById(id);
    }

    public void updateAllMachineStatus(int status) {
        machineMapperDao.updateAllMachineStatus(status);
    }

    @Autowired
    public void setMachineMapperDao(MachineMapperDao machineMapperDao) {
        this.machineMapperDao = machineMapperDao;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.operations = redisTemplate.opsForHash();
    }
}
