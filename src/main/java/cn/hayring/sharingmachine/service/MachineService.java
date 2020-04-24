package cn.hayring.sharingmachine.service;

import cn.hayring.sharingmachine.cons.CommonConstant;
import cn.hayring.sharingmachine.dao.MachineDao;
import cn.hayring.sharingmachine.dao.MachineMapperDao;
import cn.hayring.sharingmachine.dao.OrderMapperDao;
import cn.hayring.sharingmachine.domain.Machine;
import cn.hayring.sharingmachine.domain.Order;
import cn.hayring.sharingmachine.machinejson.RunOrder;

//import cn.hayring.sharingmachine.web.MachineManager;
import cn.hayring.sharingmachine.machinejson.StopAdvice;
import cn.hayring.sharingmachine.web.MachineManager;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.collections.DefaultRedisMap;
import org.springframework.data.redis.support.collections.RedisMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import javax.crypto.Mac;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class MachineService {


    private static final String ID = "id";
    private static final String ADDRESS = "address";
    private static final String STATUS = "status";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";

    private static final String MACHINE = "machine";
    private static final String LOCK = "lock";

    private RedisTemplate redisTemplate;

    private HashOperations operations;


    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    private BiMap<Integer, WebSocketSession> sessionMap;

    private MachineMapperDao machineDao;

    private MachineDao machineDaoImpl;

    private Gson machineGson;

    private OrderMapperDao orderDao;


    /**
     * 正在使用设备的用户
     */
//    private Map<Integer, String> runningUser = Maps.synchronizedBiMap(HashBiMap.create());
//    private Map<String, Integer> runningMachine = ((BiMap)runningUser).inverse();


    private Map<Integer, String> runningUser;
    private Map<String, Integer> runningMachine;


    /**
     * 锁超时时间，防止线程在入锁以后，防止阻塞后面的线程无法获取锁
     */
    private int expireMsecs = 60 * 1000;


    /**
     * 动态写入缓存
     *
     * @param machineId
     * @param status
     * @param longitude
     * @param latitude
     * @param address
     * @param second
     */
    private void putMachineSelectiveToRedis(@Nullable Integer machineId, Integer status,
                                            Double longitude, Double latitude, String address, Long second) {
        String key = MACHINE + machineId;
        if (status != null) {
            operations.put(key, STATUS, status.toString());
        }
        if (longitude != null) {
            operations.put(key, LONGITUDE, longitude.toString());
        }
        if (latitude != null) {
            operations.put(key, LATITUDE, latitude.toString());
        }
        if (address != null) {
            operations.put(key, ADDRESS, address.toString());
        }
        if (second != null) {
            redisTemplate.expire(key, second, TimeUnit.SECONDS);
        }
    }

    /**
     * 从缓存中获取设备，确保redis含有该设备
     *
     * @param machineId
     * @return
     */
    private Machine pullMachineFromRedis(Integer machineId) {
        String key = MACHINE + machineId;
        Machine machine = new Machine();
        machine.setId(machineId);
        machine.setStatus(Integer.valueOf((String) operations.get(key, STATUS)));
        machine.setLatitude(Double.valueOf((String) operations.get(key, LATITUDE)));
        machine.setLongitude(Double.valueOf((String) operations.get(key, LONGITUDE)));
        machine.setAddress((String) operations.get(key, ADDRESS));
        return machine;
    }

    /***
     * 获取设备，先检查缓存，再从数据库获取Redis passed
     * @param id
     * @return
     */
    public Machine pullMachineByIdAuto(Integer id) {
//        //return machineDao.selectMachineById(id);
//        return machineDaoImpl.selectMachineById(id);
        Machine machine;
        String key = MACHINE + id;
        if (redisTemplate.hasKey(key)) {
            machine = pullMachineFromRedis(id);
        } else {
            //从数据库中取出
            machine = machineDao.selectMachineById(id);
            putMachineSelectiveToRedis(id, machine.getStatus(), machine.getLongitude(),
                    machine.getLatitude(), machine.getAddress(), CommonConstant.CACHE_EXPIRE_TIME);
        }

        return machine;
    }

//    /**
//     * 新增机器到缓存
//     * @param machine
//     */
//    public void addNewMachine(Machine machine) {
//        machine.setStatus(Machine.SHUTDOWN);
//        //写入数据库
//        machineDao.insertMachine(machine);
//        //更新缓存
//        putMachineSelectiveToRedis(machine.getId(), machine.getStatus(),
//                machine.getLongitude(), machine.getLatitude(), machine.getAddress(), CommonConstant.CACHE_EXPIRE_TIME);
//    }


    /**
     * 新增机器到缓存 Redis passed
     *
     * @param id
     * @param address
     * @param longitude
     * @param latitude
     */
    public Machine addNewMachine(String address, Double longitude, Double latitude) {
        Machine machine = new Machine();
        machine.setAddress(address);
        machine.setLatitude(latitude);
        machine.setLongitude(longitude);
        machine.setStatus(Machine.SHUTDOWN);
        //写入数据库
        machineDao.insertMachine(machine);
        //更新缓存
        putMachineSelectiveToRedis(machine.getId(), Machine.SHUTDOWN,
                longitude, latitude, address, CommonConstant.CACHE_EXPIRE_TIME);
        //返回数据库生成的主键
        return machine;
    }


    /**
     * 动态更新设备 Redis passed
     *
     * @param id
     * @param address
     * @param longitude
     * @param latitude
     */
    public void updateMachine(@Nullable Integer id, String address, Double longitude, Double latitude) {
        String key = MACHINE + id;
        if (!redisTemplate.hasKey(key)) {
            pullMachineByIdAuto(id);
        }
        //只更新缓存
        putMachineSelectiveToRedis(id, null, longitude, latitude, address, null);
        //更新数据库
        Machine machine = new Machine();
        machine.setId(id);
        machine.setAddress(address);
        machine.setLongitude(longitude);
        machine.setLatitude(latitude);
        machineDao.updateMachineByIdSelective(machine);
    }


    /**
     * 更改状态 Redis passed
     *
     * @param id
     * @param status
     */
    public void putMachineStatus(Integer id, int status) {
//        Machine machine = new Machine();
        //Machine machine = machineDao.selectMachineById(id);
//        machine.setStatus(status);
//        machineDao.updateMachineByIdSelective(machine);

        //从缓存中修改
        String key = MACHINE + id;
        int oldState = Integer.valueOf((String) operations.get(key, STATUS));
        //写入数据库
        machineDao.updateMachineStatus(id, status);
        //写入缓存
        operations.put(key, STATUS, Integer.valueOf(status).toString());
        if (status == Machine.SHUTDOWN) {
            //设备关闭，设置缓存失效时间
            redisTemplate.expire(key, CommonConstant.CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        } else if (oldState == -1 && status != -1) {
            //清除缓存失效时间
            redisTemplate.expire(key, 0, TimeUnit.SECONDS);
        }
    }


    /**
     * 启动机器 Redis passed
     *
     * @param
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean runMachine(Integer machineId, String userId) {
        String key = MACHINE + machineId;
        if (runningMachine.containsKey(userId) || !redisTemplate.hasKey(key)) {
            return false;
        } else {
            try {
                lockMachine(machineId);
                //Machine machine = machineDao.selectMachineById(machineId);
                //从缓存中获取
                int status = Integer.valueOf((String) operations.get(key, STATUS));
                if (status != Machine.SUSPEND) {

                    return false;
                } else {

//                //写入数据库
//                machineDao.updateMachineStatus(machineId, Machine.RUN);
//                //写入缓存
//                operations.put(key, STATUS, Machine.RUN.toString());

                    //改变设备状态
                    putMachineStatus(machineId, Machine.RUN);


                    //建立记录
                    Order order = new Order();
                    Date date = new Date();
                    order.setId(getId(machineId, date));
                    order.setMachineId(machineId);
                    order.setTime(date);
                    order.setUserId(userId);
                    orderDao.insertOrder(order);

                    runningMachine.put(userId, machineId);
                    runningUser.put(machineId, userId);

                    //更新状态
                    //machine.setStatus(Machine.RUN);
                    //发送消息
                    WebSocketSession session = sessionMap.get(machineId);
                    RunOrder runOrder = new RunOrder();
                    String json = machineGson.toJson(runOrder);
                    try {
                        MachineManager.sendMessage(session, json);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    return true;
                }
            } finally {
                unLockMachine(machineId);
            }

        }
    }


    /**
     * 停止设备 Redis passed
     *
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void stopMachine(Integer id) {
//        Machine machine = machineDao.selectMachineById(id);
//        if (machine.getStatus() != Machine.RUN) {
//            throw new RuntimeException("Machine didn't running");
//        }
//        //machine.setId(id);
//        machine.setStatus(Machine.SUSPEND);
//        machineDao.updateMachineByIdSelective(machine);
//        runningUser.remove(id);

        //从缓存中修改
        String key = MACHINE + id;
        int status = Integer.valueOf((String) operations.get(id, STATUS));
        if (status != Machine.RUN) {
            throw new RuntimeException("Machine didn't running");
        }
//        operations.put(key, STATUS, Machine.SUSPEND.toString());
        putMachineStatus(id, Machine.SUSPEND);
        runningMachine.remove(runningUser.remove(id));
    }


    /**
     * 从服务器停止设备 Redis passed
     *
     * @param machineId
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void stopMachineFromServer(Integer machineId) throws IOException {
//        Machine machine = machineDao.selectMachineById(machineId);
//        if (machine.getStatus() != Machine.RUN) {
//            throw new RuntimeException("Machine didn't running");
//        }
//        //machine.setId(machineId);
//        machine.setStatus(Machine.SUSPEND);
//        machineDao.updateMachineByIdSelective(machine);


        //修改缓存
        String key = MACHINE + machineId;
        int status = Integer.valueOf((String) operations.get(machineId, STATUS));
        if (status != Machine.RUN) {
            throw new RuntimeException("Machine didn't running");
        }
        //operations.put(key, STATUS, Machine.SUSPEND.toString());
        putMachineStatus(machineId, Machine.SUSPEND);


        StopAdvice stopAdvice = new StopAdvice();
        String json = machineGson.toJson(stopAdvice);
        WebSocketSession session = sessionMap.get(machineId);
        MachineManager.sendMessage(session, json);

        runningMachine.remove(runningUser.remove(machineId));
    }

    /**
     * 停止用户正在运行的机器 Redis passed
     *
     * @param userId
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void stopMachineFromServer(String userId) throws IOException {
        Integer machineId = runningMachine.get(userId);
        stopMachineFromServer(machineId);
    }


    /**
     * 更改用户使用的设备 Redis passed
     *
     * @param userId
     * @param newMachineId
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void runSwitchMachine(String userId, Integer newMachineId) {
        Integer oldMachineId = runningMachine.get(userId);


        //停止
        //Machine machine = machineDao.selectMachineById(oldMachineId);
//        if (machine.getStatus() != Machine.RUN) {
//            throw new RuntimeException("Machine didn't running");
//        }
//        machine.setId(oldMachineId);
//        machine.setStatus(Machine.SUSPEND);
//        machineDao.updateMachineByIdSelective(machine);
//        runningUser.remove(oldMachineId);
        String key = MACHINE + oldMachineId;
        int status = Integer.valueOf((String) operations.get(oldMachineId, STATUS));
        if (status != Machine.RUN) {
            throw new RuntimeException("Machine didn't running");
        }
//        operations.put(key, STATUS, Machine.SUSPEND.toString());
        putMachineStatus(oldMachineId, Machine.SUSPEND);
        runningMachine.remove(runningUser.remove(oldMachineId));
//
//        machine = machineDao.selectMachineById(newMachineId);
        key = MACHINE + newMachineId;
        status = Integer.valueOf((String) operations.get(key, STATUS));
        if /*machine == null || machine.getStatus()*/ (status != Machine.SUSPEND || runningMachine.containsKey(userId)) {
            throw new RuntimeException("Machine running");
        } else {
//            machine.setStatus(Machine.RUN);
//            update(machine);
            //写入缓存
//            operations.put(key, STATUS, Machine.RUN.toString());
            putMachineStatus(newMachineId, Machine.RUN);

            Order order = new Order();
            Date date = new Date();
            order.setId(getId(newMachineId, date));
            order.setMachineId(newMachineId);
            order.setTime(date);
            order.setUserId(userId);
            orderDao.insertOrder(order);

            runningMachine.put(userId, newMachineId);
            runningUser.put(newMachineId, userId);
        }


        try {
            StopAdvice stopAdvice = new StopAdvice();
            String json = machineGson.toJson(stopAdvice);
            WebSocketSession session = sessionMap.get(oldMachineId);
            MachineManager.sendMessage(session, json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        WebSocketSession session2 = sessionMap.get(newMachineId);
        RunOrder runOrder = new RunOrder();
        String json2 = machineGson.toJson(runOrder);
        try {
            MachineManager.sendMessage(session2, json2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /***
     * 根据条件计算总数
     * @param status
     * @return
     */
    public long countMachineByParam(Integer status) {
        return machineDao.countMachineByParam(status);
    }

    /**
     * 根据条件获取机器 Redis passed
     *
     * @param status
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List pullMachineByParam(Integer status, int pageNo, int pageSize) {
        long index = ((long) pageNo - 1L) * ((long) pageSize);
        List<Machine> list = machineDao.selectMachineByParam(status, index, pageSize);
        for (Machine machine : list) {
            String key = MACHINE + machine.getId();
            if (!redisTemplate.hasKey(key)) {
                putMachineSelectiveToRedis(machine.getId(), machine.getStatus(),
                        machine.getLongitude(), machine.getLatitude(), machine.getAddress(), CommonConstant.CACHE_EXPIRE_TIME);
            }
        }
        return list;
    }

    @Autowired
    public void setOrderDao(OrderMapperDao orderDao) {
        this.orderDao = orderDao;
    }

    @Autowired
    public void setMachineDao(MachineMapperDao machineDao) {
        this.machineDao = machineDao;
    }

    @Autowired
    @Qualifier(value = "sessionMap")
    public void setSessionMap(BiMap sessionMap) {
        this.sessionMap = sessionMap.inverse();
    }

    @Autowired
    @Qualifier(value = "multiTypeGson")
    public void setMachineGson(Gson machineGson) {
        this.machineGson = machineGson;
    }

    /**
     * 给某个设备加锁
     *
     * @param machineId
     * @return
     */
    private boolean lockMachine(Integer machineId) {
        String key = MACHINE + machineId + LOCK;
        long expires = System.currentTimeMillis() + expireMsecs + 1;
        String expiresStr = String.valueOf(expires); //锁到期时间
        if (redisTemplate.opsForValue().setIfAbsent(key, expiresStr)) {
            return true;
        }


        //redis里key的时间
        String currentValue = (String) redisTemplate.opsForValue().get(key);


        //判断锁是否已经过期，过期则重新设置并获取
        if (currentValue != null && Long.parseLong(currentValue) < System.currentTimeMillis()) {
            //设置锁并返回旧值
            String oldValue = (String) redisTemplate.opsForValue().getAndSet(key, expiresStr);
            //比较锁的时间，如果不一致则可能是其他锁已经修改了值并获取
            if (oldValue != null && oldValue.equals(currentValue)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 释放获取到的锁
     */
    public synchronized void unLockMachine(Integer machineId) {
        String key = MACHINE + machineId + LOCK;
        redisTemplate.delete(key);
    }


    /**
     * 删除机器 Redis passed
     */
    public boolean deleteMachine(Integer machineId) {
        String key = MACHINE + machineId;
        int status = Integer.valueOf((String) operations.get(key, STATUS));
        if (status == Machine.RUN) {
            return false;
        }
        redisTemplate.delete(key);
        if (machineDao.selectMachineById(machineId) != null) machineDao.deleteMachineById(machineId);
        return true;
    }


    /**
     * 全部关机
     */
    public void updateShutdownAllMachine() {
        machineDao.updateAllMachineStatus(Machine.SHUTDOWN);
    }


    /**
     * 根据日期与设备生成id
     *
     * @param machineId
     * @return
     */
    private String getId(Integer machineId, Date date) {
        String dateStr = sdf.format(date);
        String mId = String.format("%010d", machineId);
        System.out.println(dateStr + mId);
        return dateStr + mId;
    }


    @PreDestroy
    public void destroy() {
        updateShutdownAllMachine();
    }


    @Autowired
    public void setMachineDaoImpl(MachineDao machineDaoImpl) {
        this.machineDaoImpl = machineDaoImpl;
    }


    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        operations = redisTemplate.opsForHash();
        runningUser = new DefaultRedisMap<Integer, String>("runningUser", operations.getOperations());
        runningMachine = new DefaultRedisMap<String, Integer>("runningMachine", operations.getOperations());
    }


}


