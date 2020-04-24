package cn.hayring.sharingmachine.dao;

import cn.hayring.sharingmachine.domain.*;
import cn.hayring.sharingmachine.utils.Page;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import cn.hayring.sharingmachine.utils.MD5Util;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.util.Date;

import static org.testng.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class DaoTest {

    @Autowired
    private AdminMapperDao adminDao;

    @Autowired
    private UserMapperDao userDao;

    @Autowired
    private MachineMapperDao machineDao;

    @Autowired
    private OrderMapperDao orderDao;

    @Autowired
    private MaintenanceMapperDao maintenanceDao;

    @Autowired
    private LogMapperDao logDao;


    @Test
    public void adminDaoTest() {

        Admin admin = new Admin();
        Admin adminSelect;
        String id = "admin";
        String adminName = "Admin";
        String passwordHash = BCrypt.hashpw("KID1412", BCrypt.gensalt());
        byte[] passwordBytes = passwordHash.getBytes();
        admin.setId(id);
        admin.setPassword(passwordBytes);
        admin.setName(adminName);


        try {


            adminDao.insertAdmin(admin);
            adminSelect = adminDao.selectAdminById(admin.getId());
            String selectPswStr = new String(adminSelect.getPassword());
            assertTrue(BCrypt.checkpw("KID1412", selectPswStr));
            assertEquals(id, adminSelect.getId());
            assertEquals(adminName, admin.getName());


            adminSelect = adminDao.selectAdminByIdWithoutSec(admin.getId());
            assertEquals(id, adminSelect.getId());
            assertEquals(adminName, admin.getName());


            adminName += "2";
            passwordHash = BCrypt.hashpw("NEWKID1412", BCrypt.gensalt());
            passwordBytes = passwordHash.getBytes();
            admin.setName(adminName);
            admin.setPassword(passwordBytes);
            adminDao.updateAdminByIdSelective(admin);

            adminSelect = adminDao.selectAdminById(admin.getId());
            selectPswStr = new String(adminSelect.getPassword());
            assertTrue(BCrypt.checkpw("NEWKID1412", selectPswStr));
            assertEquals(adminName, admin.getName());

            id = "newId";
            adminDao.updateAdminId(admin, id);
            adminSelect = adminDao.selectAdminById(id);
            assertEquals(adminSelect.getId(), id);
        } catch (Exception e) {
            adminDao.deleteAdminById(id);
            throw e;
        }
        adminDao.deleteAdminById(id);
        adminSelect = adminDao.selectAdminById(id);
        assertNull(adminSelect);
    }

    @Test
    public void userDaoTest() {
        User user = new User();
        User userSelect;
        String id = "idTest";
        String userName = "UserName";
        String passwordHash = BCrypt.hashpw("KID1412", BCrypt.gensalt());
        byte[] passswordBytes = passwordHash.getBytes();
        user.setId(id);
        user.setPassword(passswordBytes);
        user.setName(userName);

        try {

            userDao.insertUser(user);
            userSelect = userDao.selectUserById(user.getId());
            String selectPswStr = new String(userSelect.getPassword());
            assertTrue(BCrypt.checkpw("KID1412", passwordHash));
            assertEquals(userSelect.getName(), userName);
            assertEquals(userSelect.getId(), id);


            passwordHash = BCrypt.hashpw("newPassword", BCrypt.gensalt());
            userName = "newName";
            user.setName(userName);
            user.setPassword(passwordHash.getBytes());
            userDao.updateUserByIdSelective(user);

            userSelect = userDao.selectUserById(user.getId());
            assertTrue(BCrypt.checkpw("newPassword", passwordHash));
            assertEquals(userSelect.getName(), userName);

            id = "newId";
            userDao.updateUserId(user, id);
            userSelect = userDao.selectUserById(id);
            assertEquals(userSelect.getId(), id);
        } catch (Exception e) {
            userDao.deleteUserById(id);
            throw e;
        }

        userDao.deleteUserById(id);
        userSelect = userDao.selectUserById(id);
        assertNull(userSelect);
    }

    //
//    @Test
//    public void machineDaoTest() {
//        Machine machine = new Machine();
//        Machine machineSelect;
//        machine.setAddress("中南海");
//        machine.setLatitude(0.0);
//        machine.setLongitude(0.0);
//        machine.setStatus(0);
//
//        machineDao.insertMachine(machine);
//        int id = machine.getId();
//        machineSelect = machineDao.selectMachineById(id);
//
//        System.out.println(machine);
//        System.out.println(machineSelect);
//
//        String add = "人民大会堂";
//        machine.setAddress(add);
//        machineDao.updateMachineByIdSelective(machine);
//        machineSelect = machineDao.selectMachineById(id);
//        assertEquals(add,machineSelect.getAddress());
//
//        machine.setLongitude(1.0);
//        machine.setLatitude(2.0);
//        machine.setStatus(2);
//        machineDao.updateMachineAllById(machine);
//        machineSelect = machineDao.selectMachineById(id);
//        assertEquals(machineSelect.getLongitude(),1.0);
//        assertEquals(machineSelect.getLatitude(),2.0);
//        assertEquals(machineSelect.getStatus(),Integer.valueOf(2));
//
//        machineDao.deleteMachineById(id);
//
//        assertNull(machineDao.selectMachineById(id));
//
//
//
//    }
//
    @Test
    public void orderDaoTest() {
        //assertEquals(orderDao.countAll(),12);
        //assertEquals(orderDao.countOrderByParam("stamford",null,null,null),4);
        //assertEquals(orderDao.countOrderByParam(null,1,null,null),12);
        assertEquals(orderDao.countOrderByParam(null, null, new Date(1422112730000L), new Date(1643037530000L)), 7);

//        List<Order> stamfords = orderDao.selectByParam("stamford",null,null,null,0,10);
//        List<Order> two = orderDao.selectByParam(null,2,null,null,0,10);
//        List<Order> newGenerationOne = orderDao.selectByParam(null,null,new Date(1511536730000L),new Date(1643037530000L),0,2);
//        List<Order> newGenerationTwo = orderDao.selectByParam(null,null,new Date(1511536730000L),new Date(1643037530000L),2,2);
//        List<Order> newGeneration = orderDao.selectByParam(null,null,new Date(1511536730000L),new Date(1643037530000L),0,4);

        Order order = orderDao.selectOrderById("1");
        assertEquals(order.getId(), "1");
        assertEquals(order.getMachineId(), Integer.valueOf(1));
        assertEquals(order.getUserId(), "hayring");


        String id = "18";
        Order order1 = new Order();
        order1.setId(id);
        order1.setMachineId(1);
        order1.setUserId("stamford");
        order1.setTime(new Date(1666626540000L));
        orderDao.insertOrder(order1);
        Order orderSelect = orderDao.selectOrderById(id);
        assertEquals(orderSelect.getMachineId(), Integer.valueOf(1));
        assertEquals(orderSelect.getUserId(), "stamford");
        assertEquals(orderSelect.getTime().getTime(), 1666626540000L);

        orderSelect.setMachineId(2);
        orderDao.updateOrderAllById(orderSelect);
        orderSelect = orderDao.selectOrderById(id);
        assertEquals(orderSelect.getMachineId(), Integer.valueOf(2));
        orderSelect.setUserId("hayring");
        orderDao.updateOrderByIdSelective(orderSelect);
        orderSelect = orderDao.selectOrderById(id);
        assertEquals(orderSelect.getUserId(), "hayring");

        orderDao.deleteOrderById(id);
        assertNull(orderDao.selectOrderById(id));


    }
//
//    @Test
//    public void cacheTest() {
//        User user1 = userDao.selectUserById("hayring");
//        System.out.println(user1);
//
//        User user2 = userDao.selectUserById("hayring");
//        System.out.println(user2);
//    }
//
//
//    @Test
//    public void showDaoClass() {
//        System.out.println(adminDao);
//        System.out.println(machineDao);
//        System.out.println(orderDao);
//        System.out.println(userDao);
//    }
//
//
//
//    @Test
//    public void addTableTest() {
//        Log log = new Log();
//        log.setUserId("hayring");
//        log.setTime(new Date());
//        logDao.insertLog(log);
//        long i = logDao.countLogByUser("hayring");
//        log = (Log)logDao.selectLogByUser("hayring",0L, Page.DEFAULT_PAGE_SIZE).get(0);
//        System.out.println("count:"+i);
//        System.out.println(log);
//
//
//        Maintenance maintenance = new Maintenance();
//        maintenance.setAdminId("admin");
//        maintenance.setMachineId(1);
//        maintenance.setTime(new Date());
//        maintenanceDao.insertMaintenance(maintenance);
//        i = maintenanceDao.countMaintenanceByParam(null,null,null,null);
//        maintenance = (Maintenance) maintenanceDao.selectMaintenanceByParam(null,null,null,null,0L,Page.DEFAULT_PAGE_SIZE).get(0);
//        System.out.println("count:"+i);
//        System.out.println(maintenance);
//    }
//


}
