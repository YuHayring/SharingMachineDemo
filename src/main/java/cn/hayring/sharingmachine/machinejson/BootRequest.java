package cn.hayring.sharingmachine.machinejson;

/**
 * 启动请求Json类
 */
public class BootRequest extends MachineJson {
    public BootRequest() {
        type = BOOT_REQUEST;
    }

    public BootRequest(int machineId) {
        type = BOOT_REQUEST;
        this.machineId = machineId;
    }

    /**
     * id
     */
    protected Integer machineId;


    public Integer getMachineId() {
        return machineId;
    }
}
