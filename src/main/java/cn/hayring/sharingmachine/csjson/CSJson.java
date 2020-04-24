package cn.hayring.sharingmachine.csjson;

public class CSJson {
    protected int type;

    public static final int FAILED = 0;

    public static final int SUCCESS = 1;


    public static final int ERROR_MESSAGE = 99;

    public static final int MESSAGE = 100;

    public static final int USER_INSIDE = 101;

    public static final int LOGIN_ARGS = 102;

    public static final int REGIST_ARGS = 103;

    public static final int ADMIN_LOGIN_SUCCESS = 104;

    public static final int MACHINE_INFO = 105;

    public static final int SELECT_ARGS = 106;

    public static final int SELECT_RESULT = 107;

    public static final int DELETE_ORDER = 108;

    public static final int SELECT_MACHINE_ARGS = 109;

    public static final int MACHINE_INSIDE = 110;

    public static final int ADD_MACHINE_SUCCESS = 111;

    public static final int MACHINE_ID = 112;

    public static final int TOKEN = 113;


    public CSJson(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
