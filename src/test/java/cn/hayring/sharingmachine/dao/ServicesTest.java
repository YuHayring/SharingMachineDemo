package cn.hayring.sharingmachine.dao;

import cn.hayring.sharingmachine.domain.Machine;
//import cn.hayring.sharingmachine.service.MachineService;
import cn.hayring.sharingmachine.service.AdminService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class ServicesTest {


    @Autowired
    private AdminService adminService;

//    @Autowired
//    private MachineService machineService;
//
//
//    @Test
//    public void simpleServiceTest() {
//        Machine machine = machineService.getMachineById(1);
//        new Thread(){
//            public void run(){
//                try {
//                    machine.setStatus(3);
//                    machineService.update(machine);
//                    Thread.sleep(2000);
//                    machine.setStatus(5);
//                    machineService.update(machine);
//
//                }catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//        new Thread(){
//            public void run(){
//                try {
//                    Thread.sleep(1000);
//                    machine.setStatus(4);
//                    machineService.update(machine);
//                }catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//        try {
//            Thread.sleep(3000);
//            System.out.println(machineService.getMachineById(1));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }


    @Test
    public void regAdmin() {
        adminService.register("admin", "爸爸", "123456");
        throw new RuntimeException();
    }
}
