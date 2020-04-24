package cn.hayring.sharingmachine.machinejson;

import cn.hayring.sharingmachine.domain.Machine;

/**
 * 设备信息
 */
public class Info extends MachineJson {
    public Info() {
        this.type = INFO;
    }

    /**
     * 设备domain
     */
    protected Machine machine;


    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }
}
