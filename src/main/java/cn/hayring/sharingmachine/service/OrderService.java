package cn.hayring.sharingmachine.service;

import cn.hayring.sharingmachine.dao.OrderMapperDao;
import cn.hayring.sharingmachine.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    private OrderMapperDao orderDao;

    static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");

    public void addOrder(Order order) {
        orderDao.insertOrder(order);
    }

    public Order getOrderById(String id) {
        return orderDao.selectOrderById(id);
    }

    public void updateOrder(Order order) {
        orderDao.updateOrderByIdSelective(order);
    }

    public void deleteOrder(String id) {
        if (orderDao.selectOrderById(id) != null) orderDao.deleteOrderById(id);
    }


    public long countOrderByParam(String userId, Integer machineId, Date fromTime, Date toTime) {
        return orderDao.countOrderByParam(userId, machineId, fromTime, toTime);
    }


    public List getOrderByParam(String userId, Integer machineId, Date fromTime, Date toTime, int pageNo, int pageSize) {
        long index = ((long) pageNo - 1L) * ((long) pageSize);
        return orderDao.selectOrderByParam(userId, machineId, fromTime, toTime, index, pageSize);
    }


    public long countAll() {
        return orderDao.countAllOrder();
    }

    public List getAllOrder(int pageNo, int pageSize) {
        long index = ((long) pageNo - 1L) * ((long) pageSize);
        return orderDao.selectOrderAll(index, pageSize);
    }


    @Autowired
    public void setOrderDao(OrderMapperDao orderDao) {
        this.orderDao = orderDao;
    }
}
