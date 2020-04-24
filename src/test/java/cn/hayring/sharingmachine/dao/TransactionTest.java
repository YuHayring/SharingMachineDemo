package cn.hayring.sharingmachine.dao;

import cn.hayring.sharingmachine.domain.Machine;
import cn.hayring.sharingmachine.service.MachineService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Scanner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml",})
public class TransactionTest {

    private Scanner sc = new Scanner(System.in);

    @Autowired
    private MachineService machineService;


    @Autowired
    private MachineMapperDao machineDao;

    @Test
    public void transactionTest() {

        System.out.println("start");
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                System.out.println("run start");
                /*machineService.*/
                runMachine(5);
                System.out.println("run end");
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                /*machineService.*/
                runSwitchMachine(4, 5);
            }
        }.start();

        System.out.println("end");
        new Scanner(System.in).nextLine();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }


    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void runMachine(Integer machineId) {
        Machine machine = machineDao.selectMachineById(machineId);
        machine.setStatus(Machine.RUN);
        machineDao.updateMachineByIdSelective(machine);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void runSwitchMachine(Integer oldMachineId, Integer newMachineId) {

        System.out.println("stop");
        //停止
        Machine machine = machineDao.selectMachineById(oldMachineId);
//        if (machine.getStatus() != Machine.RUN) {
//            throw new RuntimeException("Machine didn't running");
//        }
        machine.setStatus(Machine.SUSPEND);
        machineDao.updateMachineByIdSelective(machine);

        System.out.println("switch sleep");
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("switch wake");

        machine = machineDao.selectMachineById(newMachineId);
        if (machine == null || machine.getStatus() != Machine.SUSPEND) {
            throw new RuntimeException("Machine running");

        } else {
            machine.setStatus(Machine.RUN);
            machineDao.updateMachineByIdSelective(machine);
        }

    }


}
