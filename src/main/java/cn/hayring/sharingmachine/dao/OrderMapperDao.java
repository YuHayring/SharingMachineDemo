package cn.hayring.sharingmachine.dao;

import cn.hayring.sharingmachine.domain.Order;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/***
 * 订单
 */
public interface OrderMapperDao {
    /***
     * 根据id查询
     * @param id
     * @return order
     */
    Order selectOrderById(@Param("id") String id);

    /***
     * 根据调条件计算
     * @param userId
     * @param machineId
     * @param fromTime
     * @param toTime
     * @return
     */
    long countOrderByParam(@Param("userId") String userId, @Param("machineId") Integer machineId, @Param("fromTime") Date fromTime, @Param("toTime") Date toTime);

    /**
     * 根据调条件查询selectByParm
     *
     * @param userId
     * @param machineId
     * @param fromTime
     * @param toTime
     * @param index
     * @param size
     * @return
     */
    List selectOrderByParam(@Param("userId") String userId, @Param("machineId") Integer machineId, @Param("fromTime") Date fromTime, @Param("toTime") Date toTime, @Param("index") long index, @Param("size") int size);

    /***
     * 计算总量
     * @return count
     */
    long countAllOrder();

    /**
     * 查询全部
     *
     * @param index
     * @param size
     * @return
     */
    List selectOrderAll(@Param("index") long index, @Param("size") int size);


    /***
     * 插入
     * @param order
     */
    void insertOrder(@Param("order") Order order);

    /***
     * 按已有条件更新
     * @param order
     */
    void updateOrderByIdSelective(@Param("order") Order order);

    /***
     * 更新全部
     * @param order
     */
    void updateOrderAllById(@Param("order") Order order);

    /***
     * 按id删除
     * @param id
     */
    void deleteOrderById(@Param("id") String id);


}
