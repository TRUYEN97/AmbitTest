/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.alibaba.fastjson2.JSONObject;
import com.tec02.API.Response;
import com.tec02.API.RestAPI;
import com.tec02.Time.TimeBase;
import com.tec02.common.MyConst;
import com.tec02.communication.DHCP.DhcpData;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import com.tec02.mylogger.MyLogger;
import com.tec02.view.Gui;
import java.io.File;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class SfisFunction extends AbsFunction {

    public static final String SEND_SFIS__EXCEPTION = "Send SFIS Exception : ";
    private static final String DATA_FORMAT = "data_format";
    private static final String SEND_FORMAT = "send_format";
    private static final String SEND_FORMAT_FAIL = "send_format_fail";
    private static final MyLogger sfisLog = new MyLogger();
    private final RestAPI restAPI = new RestAPI();

    private synchronized static void writeLog(String location, String log, Object... param) {
        sfisLog.setFile(new File(String.format("../sfislog/%s.txt", new TimeBase().getDate())));
        sfisLog.setSaveMemory(true);
        sfisLog.addLog(location, log, param);
    }

    public SfisFunction(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        try {
            String url = this.config.getString(URL);
            boolean send_final = this.config.get(SEND_FINAL, false);
            if (url == null || url.isBlank()) {
                addLog("URL_CHECK_SN is null or emtpty!");
                return false;
            }
            if (send_final) {
                this.uICell.getDataCell().updateResultTest();
                addLog("Send test result to sfis");
            } else {
                addLog("Check SN....");
            }
            addLog("send to url: " + url);
            String command = getCommand(send_final);
            if (command == null) {
                return false;
            }
            writeLog("TE->API", String.format("%s %s", this.uICell.getName(), command));
            Response response = this.restAPI.sendPost(url, command);
            String messResponce = String.format("%s %s", this.uICell.getName(), response);
            writeLog("API->TE", messResponce);
            Gui.getInstance().showSfisText(messResponce);
            addLog("Response is: " + response);
            if (send_final) {
                return checkFinalResponse(response);
            } else {
                return checkResponse(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getLocalizedMessage());
            addLog(ERROR, e.getMessage());
            return false;
        }
    }
    private static final String SEND_FINAL = "send_final";
    private static final String URL = "url";

    private String getCommand(boolean send_final) {
        String command;
        if (!send_final || this.dataCell.isPass()) {
            command = this.createCommand(SEND_FORMAT);
        } else {
            command = this.createCommand(SEND_FORMAT_FAIL);
        }
        return command;
    }

    private String createCommand(String keyWord) {
        JSONObject command = new JSONObject();
        List<String> listKey = this.config.getJsonList(keyWord);
        int maxLength = this.config.getInteger(MAX_LENGTH, -1);
        addLog("Input", "Input: " + this.dataCell.getString(MyConst.SFIS.SN));
        addLog("Config", "MaxLength: " + maxLength);
        addLog(keyWord, listKey);
        if (listKey == null || listKey.isEmpty()) {
            addLog(keyWord + " is null or empty!");
            return null;
        }
        for (String key : listKey) {
            String value = this.dataCell.getString(key);
            key = key.toUpperCase();
            addLog("key: " + key + " = " + value);
            if (value == null) {
                continue;
            }
            if (key.equalsIgnoreCase(MyConst.SFIS.PCNAME)) {
                String location = this.dataCell.getString(MyConst.MODEL.POSITION, "");
                command.put(key, location.isBlank() ? value : String.format("%s-%s", value, location));
            } else if (key.equalsIgnoreCase(MyConst.SFIS.STATUS)) {
                this.dataCell.putData(MyConst.MODEL.ON_SFIS, "on");
                command.put(key, value.equalsIgnoreCase(MyConst.MODEL.PASS) ? PASS : FAIL);
            } else if (maxLength != -1
                    && key.equalsIgnoreCase(MyConst.SFIS.SN)
                    && value.length() > maxLength) {
                command.put(key, value.substring(0, maxLength));
            } else {
                command.put(key, value);
            }
        }
        addLog("command: " + command);
        return command.toJSONString();
    }
    private static final String MAX_LENGTH = "MaxLength";

    public boolean checkResponse(Response response) {
        if (response == null) {
            this.addLog("response is null!");
            return false;
        }
        if (response.getResponse().contains(SEND_SFIS__EXCEPTION)) {
            this.setMessage(response.getResponse());
            return false;
        }
        if (response.getStatus()) {
            JSONObject data = response.getData();
            List<String> listKey = this.config.getJsonList(DATA_FORMAT);
            addLog(DATA_FORMAT, listKey);
            if (!listKey.isEmpty() && !dataContainKeys(data, listKey)) {
                addLog("Error", "Sfis is not enough data.");
                return false;
            }
            if (!getDataToProductInfo(data)) {
                return false;
            }
            try {
                if (this.dhcpDto.isOn() && !putMacDHCP()) {
                    addLog("Get MAC from SFIS for DHCP failed!");
                    return false;
                }
            } catch (Exception e) {
                addLog(ERROR, e.getLocalizedMessage());
                return false;
            }
            addLog("add data to production info done.");
            return true;
        } else {
            this.addLog(response.getMessage());
            this.setMessage(response.getMessage());
            return false;
        }
    }

    private boolean getDataToProductInfo(JSONObject data) {
        String value;
        for (String key : data.keySet()) {
            if (key.equals(MyConst.SFIS.MAC)) {
                value = createTrueMac(getValueInData(data, key));
            } else {
                value = getValueInData(data, key);
            }
            addLog(String.format("add key: %s -- Value: %s", key, value));
            this.dataCell.putData(key, value);
        }
        return true;
    }

    private String createTrueMac(String value) {
        if (value.contains(":")) {
            return value;
        }
        StringBuilder builder = new StringBuilder();
        int index = 0;
        for (char kitu : value.toCharArray()) {
            if (index != 0 && index % 2 == 0) {
                builder.append(':');
            }
            builder.append(kitu);
            index++;
        }
        return builder.toString();
    }

    private String getValueInData(JSONObject data, String key) {
        String value = data.getString(key);
        if (value == null) {
            value = "";
        }
        return value;
    }

    private boolean putMacDHCP() {
        String mac = this.dataCell.getString(MyConst.SFIS.MAC);
        String oldIP = DhcpData.getInstance().getIP(mac);
        if (mac == null || mac.isBlank()
                || !DhcpData.getInstance().put(mac, uICell.getId())) {
            return false;
        }
        if (oldIP != null) {
            addLog("PC", "Old IP: %s in DHCP data", oldIP);
        }
        addLog("PC", "add Mac: %s -- Ip: %s to DHCP data",
                mac, DhcpData.getInstance().getIP(mac));
        return true;
    }

    private boolean checkFinalResponse(Response response) {
        if (response == null) {
            return false;
        }
        if (response.getResponse().contains(SEND_SFIS__EXCEPTION)) {
            this.setMessage(response.getResponse());
            return false;
        }
        try {
            setMessage(response.getMessage());
            return response.getStatus();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            setMessage(e.getMessage());
            return false;
        }
    }
    private static final String PASS = "PASS";
    private static final String FAIL = "FAIL";

    private boolean dataContainKeys(JSONObject data, List<String> listKey) {
        for (String key : listKey) {
            if (!data.containsKey(key)) {
                addLog("Error", "Sfis not contain key: " + key);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setTime_out(100);
        config.setWait_multi_done(true);
        config.put(MAX_LENGTH, -1);
        config.put(SEND_FORMAT, List.of("sn", "pcname"));
        config.put(DATA_FORMAT, List.of("mlbsn", "ethernetmac", "pnname", "model"));
        config.put(SEND_FORMAT_FAIL, List.of("sn", "pcname", "status", "errorcode"));
        config.put(URL, "http://10.90.100.20/sfcapi/api/connect");
        config.put(SEND_FINAL, false);
    }

}
