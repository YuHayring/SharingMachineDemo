package cn.hayring.sharingmachine.web;

import cn.hayring.sharingmachine.domain.Machine;
import cn.hayring.sharingmachine.machinejson.*;
import cn.hayring.sharingmachine.service.MachineService;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;

/**
 * 设备WebSocket
 */
public class MachineManager extends AbstractWebSocketHandler {


    private MachineService machineService;

    /**
     * 多重解析
     */
    private Gson machineGson;

    /**
     * 存放启动的机器连接
     */
    private static BiMap<WebSocketSession, Integer> idMap = Maps.synchronizedBiMap(HashBiMap.create());


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //解析Json
        MachineJson machineJson = machineGson.fromJson(message.getPayload(), MachineJson.class);
        //判断是否收到过id信息
        if (idMap.containsKey(session)) {
            switch (machineJson.getType()) {
                case MachineJson.STOP_ADVICE: {
                    handleStopAdvice((StopAdvice) machineJson, session);
                }
                break;
                case MachineJson.SHUTDOWN_ADVICE: {
                    handleShutdownAdvice((ShutDownAdvice) machineJson, session);
                }


                default:
            }
        } else {
            //判断是否发来id
            if (machineJson.getType() == MachineJson.BOOT_REQUEST) {
                handleBootRequest((BootRequest) machineJson, session);
            } else {
                //关闭链接
                session.close();
            }
        }

    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        idMap.remove(session);
        super.afterConnectionClosed(session, status);
    }

    @Autowired
    public void setMachineService(MachineService machineService) {
        this.machineService = machineService;
    }


    public static void sendMessage(WebSocketSession session, String test) throws IOException {
        session.sendMessage(new TextMessage(test));
    }


    /**
     * 响应启动请求 Redis passed
     *
     * @param request
     * @param session
     * @throws Exception
     */
    private void handleBootRequest(BootRequest request, WebSocketSession session) throws Exception {
        Machine machine = machineService.pullMachineByIdAuto(request.getMachineId());
        //关闭
        if (machine == null) {
            session.close();
        } else {
            Info info = new Info();
            info.setMachine(machine);
            String json = machineGson.toJson(info);
            sendMessage(session, json);

            machineService.putMachineStatus(machine.getId(), Machine.SUSPEND);
            idMap.put(session, machine.getId());
        }
    }

    /**
     * 处理停止请求，将机器状态置为等待  Redis passed
     *
     * @param advice
     * @param session
     * @throws Exception
     */
    private void handleStopAdvice(StopAdvice advice, WebSocketSession session) throws Exception {
        int id = idMap.get(session);
        machineService.stopMachine(id);
    }

    /**
     * 处理关机请求，将机器状态置为关机 Redis passed
     *
     * @param advice
     * @param session
     * @throws Exception
     */
    private void handleShutdownAdvice(ShutDownAdvice advice, WebSocketSession session) throws Exception {
        int id = idMap.get(session);
        machineService.putMachineStatus(id, Machine.SHUTDOWN);
        session.close();
    }


    @Autowired
    @Qualifier(value = "multiTypeGson")
    public void setMachineGson(Gson machineGson) {
        this.machineGson = machineGson;
    }


    public static BiMap<WebSocketSession, Integer> getIdMap() {
        return idMap;
    }
}
