package cn.hayring.sharingmachine.service;

import cn.hayring.sharingmachine.cons.CommonConstant;
import cn.hayring.sharingmachine.dao.AdminMapperDao;
import cn.hayring.sharingmachine.domain.Admin;
import cn.hayring.sharingmachine.domain.User;
import cn.hayring.sharingmachine.utils.MD5Util;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

@Service
public class AdminService {
    private AdminMapperDao adminDao;


    private static final String NAME = "name";

    private static final String ADMIN = "admin";


    private RedisTemplate redisTemplate;

    private HashOperations operations;


    /**
     * 动态写入缓存
     *
     * @param adminId
     * @param name
     * @param second
     */
    private void putAdminSelectiveToRedis(@Nullable String adminId, String name, Long second) {
        String key = ADMIN + adminId;
        if (name != null) {
            operations.put(key, name, name);
        }
        if (second != null) {
            redisTemplate.expire(key, second, TimeUnit.SECONDS);
        }
    }

    /**
     * 从缓存中获取用户，确保redis含有该用户
     *
     * @param adminId
     * @return
     */
    private Admin pullAdminFromRedis(String adminId) {
        String key = ADMIN + adminId;
        Admin admin = new Admin();
        admin.setId(adminId);
        admin.setName((String) operations.get(key, NAME));
        return admin;
    }


    /***
     * 注册
     * @param id
     * @param password
     * @return success?
     */
    public Admin register(String id, String name, String password) {
        String key = ADMIN + id;
        if (redisTemplate.hasKey(key)) {
            return null;
        }
        if (adminDao.selectAdminByIdWithoutSec(id) != null) {
            return null;
        }
        Admin admin = new Admin();
        admin.setId(id);
        byte[] passwordBytes = BCrypt.hashpw(password, BCrypt.gensalt()).getBytes();
        admin.setPassword(passwordBytes);
        admin.setName(name);

        //写入Redis和数据库
        putAdminSelectiveToRedis(key, name, CommonConstant.CACHE_EXPIRE_TIME);
        adminDao.insertAdmin(admin);
        admin.setPassword(null);
        return admin;
    }

    /**
     * 登录
     *
     * @param id
     * @param password
     * @return
     */
    public Admin login(String id, String password) {
        //查询管理员
        Admin admin = adminDao.selectAdminById(id);
        //id错误
        if (admin == null) return null;
        //返回匹配结果
        if (BCrypt.checkpw(password, new String(admin.getPassword()))) {
            admin.setPassword(null);
            String key = ADMIN + id;
            //设置不过期
            putAdminSelectiveToRedis(key, null, 0L);
            return admin;
        } else {
            return null;
        }
    }

    /**
     * 更改密码
     *
     * @param id
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public boolean changePassword(String id, String oldPassword, String newPassword) {
        //查询
        Admin admin = adminDao.selectAdminById(id);
        //id错误
        if (admin == null) return false;

        if (BCrypt.checkpw(oldPassword, new String(admin.getPassword()))) {
            String passwordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            admin.setPassword(passwordHash.getBytes());
            adminDao.updateAdminByIdSelective(admin);
            return true;
        } else {
            return false;
        }

    }

    /**
     * 登出
     *
     * @param id
     */
    public void logout(String id) {
        redisTemplate.expire(ADMIN + id, CommonConstant.CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    /***
     * 删除管理员
     * @param id
     * @param password
     * @return
     */
    public boolean deleteAdmin(String id, String password) {
        if (null != login(id, password)) {
            adminDao.deleteAdminById(id);
            return true;
        } else return false;
    }


    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        operations = redisTemplate.opsForHash();
    }


    @Autowired
    public void setDao(AdminMapperDao adminDao) {
        this.adminDao = adminDao;
    }
}
