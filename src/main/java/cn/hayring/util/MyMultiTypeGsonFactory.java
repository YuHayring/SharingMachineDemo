package cn.hayring.util;

import cn.hayring.sharingmachine.machinejson.*;
import com.google.gson.Gson;

/**
 * 我的多重类型json解析器工厂类
 */
public class MyMultiTypeGsonFactory {
    public static Gson getMyMultiTypeGson() {
        Gson gson = new MultiTypeJsonParser.Builder<MachineJson>()
                .registerTypeElementName("type")
                .registerTargetClass(MachineJson.class)
                // 如果所要解析的 jsonObejct 中已经含有能够表示自身类型的字段，不需要注册外层 Type，这样更省事
                //      .registerTargetUpperLevelClass(AttributeWithType.class)
                .registerTypeElementValueWithClassType(String.valueOf(MachineJson.BOOT_REQUEST), BootRequest.class)
                .registerTypeElementValueWithClassType(String.valueOf(MachineJson.INFO), Info.class)
                .registerTypeElementValueWithClassType(String.valueOf(MachineJson.RUN_ORDER), RunOrder.class)
                .registerTypeElementValueWithClassType(String.valueOf(MachineJson.STOP_ADVICE), StopAdvice.class)
                .registerTypeElementValueWithClassType(String.valueOf(MachineJson.SHUTDOWN_ADVICE), ShutDownAdvice.class)
                .build().getTargetParseGson();
        return gson;
    }
}
