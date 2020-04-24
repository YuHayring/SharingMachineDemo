package cn.hayring.sharingmachine.dao;

import cn.hayring.sharingmachine.domain.Order;
//import cn.hayring.sharingmachine.service.MachineService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class CreateDataTest {


//    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//
//    @Autowired
//    private MachineService machineService;
//
//    @Autowired
//    private OrderMapperDao orderDao;
//
//    Random idRandom = new Random();
//
//
//
//    @Test
//    public void createOrderData() {
//
//        for(int i = 0; i < 100; i++) {
//            Order order = new Order();
//
//
//            Date date = randomDate("2019-01-01","2020-01-01");
//
//            Integer machineId = idRandom.nextInt(12);
//            order.setId(getId(machineId,date));
//            order.setMachineId(machineId);
//            order.setTime(date);
//            order.setUserId(getRandomUserId());
//            orderDao.insertOrder(order);
//        }
//        throw new RuntimeException("不抹去数据");
//    }
//
//
//
//    private static Date randomDate(String beginDate,String endDate){
//        try {
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//            Date start = format.parse(beginDate);
//            Date end = format.parse(endDate);
//
//            if(start.getTime() >= end.getTime()){
//                return null;
//            }
//            long date = random(start.getTime(),end.getTime());
//            return new Date(date);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private static long random(long begin,long end){
//        long rtn = begin + (long)(Math.random() * (end - begin));
//        if(rtn == begin || rtn == end){
//            return random(begin,end);
//        }
//        return rtn;
//    }
//
//
//
//    private static String getRandomUserId() {
//        Random random = new Random();
//        switch (random.nextInt(5)) {
//            case 0: return "Father";
//            case 1: return "KID1412";
//            case 2: return "msk";
//            case 3: return "TM";
//            case 4: return "hayring";
//            default: return "hayring";
//        }
//    }
//
//    private String getId(Integer machineId, Date date) {
//        String dateStr = sdf.format(date);
//        String mId = String.format("%010d", machineId);
//        System.out.println(dateStr+mId);
//        return dateStr+mId;
//    }
}
