package cn.hayring.sharingmachine.dao;

import cn.hayring.sharingmachine.domain.Admin;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Dao接口
 */
public interface AdminMapperDao {

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    Admin selectAdminById(@Param("id") String id);

    /**
     * 根据id查询，排除密码
     *
     * @param id
     * @return
     */
    Admin selectAdminByIdWithoutSec(@Param("id") String id);

    /**
     * 插入
     *
     * @param admin
     */
    void insertAdmin(@Param("admin") Admin admin);

    /**
     * 按参数更新
     *
     * @param admin
     */
    void updateAdminByIdSelective(Admin admin);

    /**
     * 更新id
     *
     * @param admin
     * @param newId
     */
    void updateAdminId(@Param("admin") Admin admin, @Param("newId") String newId);

    /**
     * 删除
     *
     * @param id
     */
    void deleteAdminById(@Param("id") String id);


}
