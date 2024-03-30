/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.alibaba.fastjson2.JSONObject;
import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.common.MyConst;
import com.tec02.communication.socket.Unicast.Client.SocketClient;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;

/**
 *
 * @author Administrator
 */
public class FindMacBLE extends AbsFunction {

    public FindMacBLE(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        try {
            String mac = this.baseFunction.getMac();
            if (mac == null || mac.isBlank()) {
                return false;
            }
            String serverIp = this.config.getString(SERVER_IP, "127.0.0.1");
            int serverPort = this.config.getInteger(SERVER_PORT, 8686);
            int time = this.config.getInteger(TIME, 80);
            addLog(CONFIG, "Server IP: %s", serverIp);
            addLog(CONFIG, "Server PORT: %s", serverPort);
            addLog(CONFIG, "time: %s", time);
            SocketClient socketClient = new SocketClient("BLE Scanner",
                    serverIp, serverPort, null);
            addLog(PC, "Connect to server...");
            TimeS connectTimer = new TimeS(5);
            while (connectTimer.onTime() && !socketClient.connect()) {
                this.baseFunction.delay(500);
            }
            addLog(PC, "Connect to server %s", socketClient.isConnect() ? "success" : "failure!");
            if (!socketClient.isConnect()) {
                return false;
            }
            String request = createRequest("192.168.1.1", mac, time);
            addLog(PC, "Request: %s", request);
            addLog(PC, "Send a BLE scanning request to the socket server with MAC is %s", mac);
            addLog(PC, "Send request %s", socketClient.send(request) ? "Ok" : "failed");
            TimeS timer = new TimeS(time + 1);
            String line;
            JSONObject responce = null;
            while (timer.onTime() && socketClient.isConnect()) {
                line = socketClient.readLine();
                addLog("BLE SCANNER", line);
                responce = JSONObject.parseObject(line);
                break;
            }
            if (!timer.onTime()) {
                addLog(PC, "Time out!");
            }
            boolean isPass = responce != null && responce.getBooleanValue(MyConst.SOCKET.STATUS);
            addLog(PC, "Scan BLE is %s", isPass ? "OK" : "failed");
            return isPass;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getLocalizedMessage());
            addLog(ERROR, e.getMessage());
            return false;
        }
    }

    private static String createRequest(String serverIp, String mac, int time) {
        return JSONObject.of(MyConst.SOCKET.FUNC, "BLE",
                MyConst.SOCKET.DATA, JSONObject.of(MyConst.SOCKET.IP, serverIp,
                        MyConst.SOCKET.MAC, mac,
                        MyConst.SOCKET.TIME, time)).toString();
    }
    private static final String TIME = "time";
    private static final String SERVER_PORT = "serverPort";
    private static final String SERVER_IP = "serverIP";

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setTime_out(90);
        config.put(SERVER_IP, "127.0.0.1");
        config.put(SERVER_PORT, 8686);
        config.put(TIME, 80);
    }

}
