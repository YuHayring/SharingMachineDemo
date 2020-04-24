package cn.hayring.sharingmachine.machinejson;

/**
 * Json基类
 */
public class MachineJson {
    /**
     * json类型
     */
    protected int type;

    //启动请求
    public static final int BOOT_REQUEST = 0;
    //服务端返回信息，客户端发出更新
    public static final int INFO = 1;
    //启动命令
    public static final int RUN_ORDER = 2;
    //停止通知
    public static final int STOP_ADVICE = 3;
    //关机通知
    public static final int SHUTDOWN_ADVICE = 4;
    //维护命令
    public static final int MAINTENANCE_ORDER = 5;
    //维护结束
    public static final int MAINTENANCE_FINISHED = 6;


    public int getType() {
        return type;
    }
}
