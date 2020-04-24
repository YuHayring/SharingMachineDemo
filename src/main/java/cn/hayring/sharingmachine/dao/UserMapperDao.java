package cn.hayring.sharingmachine.dao;

import cn.hayring.sharingmachine.domain.User;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Dao接口
 */
public interface UserMapperDao {

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    User selectUserById(@Param("id") String id);

    /**
     * 根据id查询，排除密码
     *
     * @param id
     * @return
     */
    User selectUserByIdWithoutSec(@Param("id") String id);

    /**
     * 插入
     *
     * @param user
     */
    void insertUser(@Param("user") User user);

    void updateUserByIdSelective(User user);

    /**
     * 更新id
     *
     * @param user
     * @param newId
     */
    void updateUserId(@Param("user") User user, @Param("newId") String newId);

    /***
     * 更新
     * @param user
     */
    void updateUserById(@Param("user") User user);

    /**
     * 删除
     *
     * @param id
     */
    void deleteUserById(@Param("id") String id);


}
