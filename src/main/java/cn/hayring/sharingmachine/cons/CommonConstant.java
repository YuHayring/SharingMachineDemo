package cn.hayring.sharingmachine.cons;

public class CommonConstant {
    /**
     * 用户对象放到Session中的键名称
     */
    public static final String USER_CONTEXT = "USER_CONTEXT";

    /**
     * Admin对象放到Session中的键名称
     */
    public static final String ADMIN_CONTEXT = "ADMIN_CONTEXT";

    /**
     * 将登录前的URL放到Session中的键名称
     */
    public static final String LOGIN_TO_URL = "toUrl";

    /**
     * 上次查询的总数
     */
    public static final String TOTAL_COUNT = "totalCount";


    /**
     * 缓存失效时间
     */
    public static final long CACHE_EXPIRE_TIME = 600L;

    /**
     * Redis锁失效时间
     */
    public static final long LOCK_EXPIRE_TIME = 20L;
}
