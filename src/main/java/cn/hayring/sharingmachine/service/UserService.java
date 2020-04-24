package cn.hayring.sharingmachine.service;

import cn.hayring.sharingmachine.cons.CommonConstant;
import cn.hayring.sharingmachine.dao.LogMapperDao;
import cn.hayring.sharingmachine.dao.UserMapperDao;
import cn.hayring.sharingmachine.domain.User;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private UserMapperDao userDao;

    private LogMapperDao logDao;

    private static final String NAME = "name";

    private static final String USER = "user";


    private RedisTemplate redisTemplate;

    private HashOperations operations;


    /**
     * 动态写入缓存
     *
     * @param userId
     * @param name
     * @param second
     */
    private void putUserSelectiveToRedis(@Nullable String userId, String name, Long second) {
        String key = USER + userId;
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
     * @param userId
     * @return
     */
    private User pullUserFromRedis(String userId) {
        String key = USER + userId;
        User user = new User();
        user.setId(userId);
        user.setName((String) operations.get(key, NAME));
        return user;
    }


    /***
     * 获取用户，先检查缓存，再从数据库获取
     * @param userId
     * @return
     */
    public User pullUserByIdAuto(String userId) {
        User user;
        String key = USER + userId;
        if (redisTemplate.hasKey(key)) {
            user = pullUserFromRedis(userId);
        } else {
            //从数据库中取出
            user = userDao.selectUserById(userId);
            putUserSelectiveToRedis(userId, user.getName(), CommonConstant.CACHE_EXPIRE_TIME);
        }

        return user;
    }

    /***
     * 注册
     * @param id
     * @param password
     * @return success?
     */
    public User register(String id, String name, String password) {
        String key = USER + id;
        if (redisTemplate.hasKey(key)) {
            return null;
        }
        if (userDao.selectUserByIdWithoutSec(id) != null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        user.setPassword(passwordHash.getBytes());
        user.setName(name);

        //写入Redis和数据库
        putUserSelectiveToRedis(key, name, CommonConstant.CACHE_EXPIRE_TIME);
        userDao.insertUser(user);
        user.setPassword(null);
        return user;
    }

    /**
     * 登录
     *
     * @param id
     * @param password
     * @return
     */
    public User login(String id, String password) {
        //查询用户
        User user = userDao.selectUserById(id);
        //用户名错误
        if (user == null) return null;
        //返回匹配结果
        if (BCrypt.checkpw(password, new String(user.getPassword()))) {
            user.setPassword(null);
            String key = USER + id;
            //设置不过期
            putUserSelectiveToRedis(key, null, 0L);
            return user;
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
        User user = userDao.selectUserById(id);
        //id错误
        if (user == null) return false;
        if (BCrypt.checkpw(oldPassword, new String(user.getPassword()))) {
            String passwordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            user.setPassword(passwordHash.getBytes());
            userDao.updateUserByIdSelective(user);
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
        redisTemplate.expire(USER + id, CommonConstant.CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
    }


    @Autowired
    public void setLogDao(LogMapperDao logDao) {
        this.logDao = logDao;
    }

    @Autowired
    public void setDao(UserMapperDao userDao) {
        this.userDao = userDao;
    }


    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        operations = redisTemplate.opsForHash();
    }
}
