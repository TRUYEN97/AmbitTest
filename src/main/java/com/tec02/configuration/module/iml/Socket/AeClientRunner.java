/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module.iml.Socket;

import com.alibaba.fastjson2.JSONObject;
import com.tec02.common.MyConst;
import com.tec02.communication.socket.Unicast.Client.SocketClient;
import com.tec02.communication.socket.Unicast.commons.Interface.IObjectClientReceiver;
import com.tec02.main.dataCell.DataCell;
import com.tec02.view.managerUI.UICell;
import com.tec02.view.managerUI.UICellManagement;

/**
 *
 * @author Administrator
 */
public class AeClientRunner {

    private static volatile AeClientRunner instance;
    private SocketClient socketClient;
    private Thread thread;

    public AeClientRunner() {
    }

    public static AeClientRunner getInstance() {
        AeClientRunner ins = AeClientRunner.instance;
        if (ins == null) {
            synchronized (AeClientRunner.class) {
                ins = AeClientRunner.instance;
                if (ins == null) {
                    AeClientRunner.instance = ins = new AeClientRunner();
                }
            }
        }
        return ins;
    }

    public void run(SocketClient socketClient) {
        this.socketClient = socketClient;
        if (socketClient == null) {
            return;
        }
        if (isConnect()) {
            socketClient.disconnect();
        }
        if (thread == null || !thread.isAlive()) {
            thread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        if (!isConnect()) {
                            if (socketClient.connect()) {
                                UICellManagement management = UICellManagement.getInstance();
                                var uiCells = management.getUiCells();
                                for (UICell uiCell : uiCells) {
                                    sendDefaulDataToServer(uiCell);
                                }
                                socketClient.run();
                            }
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                        }
                    }
                }
            };
            thread.start();
        }
    }

    public SocketClient getSocketClient() {
        return socketClient;
    }

    public synchronized boolean sendDefaulDataToServer(UICell uiCell) {
        DataCell dataCell;
        if (!isConnect() || uiCell == null || (dataCell = uiCell.getDataCell()) == null) {
            return false;
        }
        JSONObject dataJson = new JSONObject();
        putDataToJson(dataJson, dataCell, MyConst.MODEL.STATION_NAME, "");
        putDataToJson(dataJson, dataCell, MyConst.MODEL.POSITION, "");
        putDataToJson(dataJson, dataCell, MyConst.MODEL.HHSN, "");
        putDataToJson(dataJson, dataCell, MyConst.MODEL.SERIAL, "");
        putDataToJson(dataJson, dataCell, MyConst.MODEL.ERROR_CODE, "");
        putDataToJson(dataJson, dataCell, MyConst.MODEL.ERRORDES, "");
        putDataToJson(dataJson, dataCell, MyConst.MODEL.TEST_ITEM, "");
        putDataToJson(dataJson, dataCell, MyConst.MODEL.MAC, "");
        putDataToJson(dataJson, dataCell, MyConst.MODEL.MODE, "");
        putDataToJson(dataJson, dataCell, MyConst.MODEL.STATUS, "");
        dataJson.put(MyConst.MODEL.MESSAGE, getMessage(dataCell));
        dataJson.put(MyConst.MODEL.PASS_COUNT, uiCell.getPasscount());
        dataJson.put(MyConst.MODEL.FAIL_COUNT, uiCell.getFailcount());
        dataJson.put(MyConst.MODEL.INPUT_COUNT, uiCell.getTestCount());
        dataJson.put(MyConst.MODEL.TEST_YR, uiCell.getPasscount());
        double time = uiCell.getTimeS().getTime();
        dataJson.put(MyConst.MODEL.TEST_TIME_MIN, (int) (time / 60));
        dataJson.put(MyConst.MODEL.TEST_TIME_SEC, (int) (time % 60));
        return send(dataJson.toString());
    }

    private static Object getMessage(DataCell dataCell) {
        return dataCell.isTesting() ? "Testing" : dataCell.getMassage()
                .replaceAll("\r\n", " ");
    }

    private void putDataToJson(JSONObject dataJson, DataCell dataCell, String key, String defaulVal) {
        dataJson.put(key, dataCell.getString(key, defaulVal));
    }

    public boolean send(String data) {
        if (!isConnect()) {
            return false;
        }
        return socketClient.send(data);
    }

    private boolean isConnect() {
        if (socketClient == null) {
            return false;
        }
        return socketClient.isConnect();
    }

    private final class AeAnalysis implements IObjectClientReceiver {

        @Override
        public void receiver(SocketClient client, String data) {

        }

    }
}
