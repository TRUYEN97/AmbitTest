/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module.iml.Socket;

import com.alibaba.fastjson2.JSONObject;
import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.common.MyConst;
import com.tec02.communication.socket.Unicast.Client.SocketClient;
import com.tec02.communication.socket.Unicast.Server.ClientHandler;
import com.tec02.communication.socket.Unicast.commons.Interface.IObjectClientReceiver;
import com.tec02.communication.socket.Unicast.commons.Interface.IObjectServerReceiver;
import com.tec02.function.baseFunction.BLE_Scanner;
import com.tec02.main.Core;
import com.tec02.view.Gui;
import javax.swing.Timer;

/**
 *
 * @author Administrator
 */
public class Receiver implements IObjectServerReceiver, IObjectClientReceiver {

    private final Core core;
    private final Gui gui;

    public Receiver() {
        this.core = Core.getInstance();
        this.gui = Gui.getInstance();
        new Timer(2000, (e) -> {
            int status = this.core.getUpdateStatus();
            if (status == 0) {
                return;
            }
            if (status == 1) {
                show("The program has a new version!");
            } else if (status == 2) {
                show("The program will turn off!");
            }
        }).start();
    }

    @Override
    public void receiver(ClientHandler handler, String data) {
        if (data == null) {
            return;
        }
        data = data.trim();
        if (data.matches("^\\[.+\\].*")) {
            startTest(data);
        } else if (data.matches("^\\{.+\\}$")) {
            JSONObject jsonMess = JSONObject.parseObject(data);
            String func = jsonMess.getString(MyConst.SOCKET.FUNC);
            JSONObject jsonData = jsonMess.getJSONObject(MyConst.SOCKET.DATA);
            switch (func) {
                case "BLE" -> {
                    String ip = jsonData.getString(MyConst.SOCKET.IP);
                    String mac = jsonData.getString(MyConst.SOCKET.MAC);
                    String scanLog = null;
                    String macBle = "";
                    if (ip != null && !ip.isBlank() && mac != null && !mac.isBlank()) {
                        int time = jsonData.getIntValue(MyConst.SOCKET.TIME, 5);
                        BLE_Scanner scanner = BLE_Scanner.getInstance();
                        scanner.clear(ip, mac);
                        BLE_Scanner.ScannerData scannerData = scanner.expect(ip, mac, new TimeS(time));
                        if(scannerData != null){
                            scanLog = String.valueOf(scannerData.builder);
                            macBle = String.valueOf(scannerData.mac);
                        }
                    }
                    JSONObject result = new JSONObject();
                    result.put(MyConst.SOCKET.STATUS, scanLog != null);
                    result.put(MyConst.SOCKET.MAC, macBle);
                    result.put(MyConst.SOCKET.DATA, scanLog);
                    handler.send(result.toString());
                    handler.disconnect();
                }
            }
        }
    }

    @Override
    public void receiver(SocketClient client, String data) {
        if (data == null) {
            return;
        }
        data = data.trim();
        showData(client, data);
        if (data.equalsIgnoreCase("name")) {
            client.send(String.format("name:%s", Core.getInstance().getAppName()));
        } else if (data.trim().matches("^\\[.+\\].*")) {
            startTest(data);
        } else if (data.startsWith("{") && data.endsWith("}")) {
            startTest(JSONObject.parseObject(data));
        } else if (data.equalsIgnoreCase("quit")) {
            this.core.setUpdateStatus(2);
        } else if (data.equalsIgnoreCase("update")) {
            this.core.setUpdateStatus(1);
        } else if (data.equalsIgnoreCase("cancel")) {
            this.core.setUpdateStatus(0);
        }

    }

    private void showData(SocketClient client, String data) {
        this.gui.showMess(String.format("%s: %s", client.getHostName(), data));
    }

    private void startTest(String data) {
        try {
            String sn = data.substring(data.indexOf("[") + 1, data.lastIndexOf("]"));
            Thread.sleep(1000);
            this.core.setInput(sn);
        } catch (InterruptedException ex) {
        }
    }

    private void startTest(JSONObject data) {
        try {
            String sn = data.getString("sn");
            String index = data.getString("index");
            if (sn == null) {
                return;
            }
            Thread.sleep(1000);
            this.core.setInput(sn, index);
        } catch (InterruptedException ex) {
        }
    }

    private void show(String log) {
        this.gui.showMess(log);
    }

}
