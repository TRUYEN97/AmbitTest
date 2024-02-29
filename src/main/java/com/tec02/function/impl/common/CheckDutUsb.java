/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.alibaba.fastjson.JSONObject;
import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class CheckDutUsb extends AbsFucnUseTelnetOrCommportConnector {

    private static final String TIME = "Time";
    private static final String READ_UNTIL = "ReadUntil";
    private static final String ENDKEY = "endKey";
    private static final String STARTKEY = "startKey";
    private static final String COMMAND = "commands";

    public CheckDutUsb(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        try ( AbsCommunicate communicate = this.baseFunction.getTelnetOrComportConnector()) {
            if (communicate == null) {
                return false;
            }
            List<String> commands = config.getJsonList(COMMAND);
            StringBuilder responce = new StringBuilder();
            String readUntil = config.getString(READ_UNTIL);
            int time = config.getInteger(TIME, 10);
            for (String command : commands) {
                if (!this.baseFunction.sendCommand(communicate, command)) {
                    return false;
                }
                responce.append(this.analysisBase.readShowUntil(
                        communicate, readUntil, new TimeS(time)));
            }
            List<String> apis = config.getJsonList(APIS);
            ValueSubItem subItem;
            List<JSONObject> keys = config.get(KEY);
            if (keys == null || keys.isEmpty()) {
                addLog(PC, "Keys is null or empty!");
                return false;
            }
            JSONObject key;
            for (int i = 0; i < apis.size(); i++) {
                if (i >= apis.size()) {
                    key = keys.get(apis.size() - 1);
                } else {
                    key = keys.get(i);
                }
                String apiName = apis.get(i);
                String startkey = key.getString(STARTKEY);
                addLog(CONFIG, "%s: %s", STARTKEY, startkey);
                String endkey = key.getString(ENDKEY);
                addLog(CONFIG, "%s: %s", ENDKEY, endkey);
                subItem = createSubItem(ValueSubItem.class, apiName);
                String value = getValue(responce, startkey, endkey);
                subItem.setValue(value);
                subItem.runTest(1);
            }
            return isAllSubItemPass();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }

    private String getValue(StringBuilder responce, String startkey, String endkey) {
        for (String line : responce.toString().split("\r\n")) {
            String value = this.analysisBase.subString(line, startkey, endkey);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private static final String KEY = "key";
    private static final String APIS = "apis";

    @Override
    protected void createConfig(FunctionConfig config) {
        config.setTime_out(10);
        config.setRetry(1);
        config.setFailApiName("usb_fw");
        config.put(READ_UNTIL, "root@eero-test:/#");
        config.put(TIME, 10);
        config.put(APIS, List.of("usb_fw", "usb_model"));
        config.put(COMMAND, List.of("tps -r devinfo", "rts5452 -r ic_status"));
        config.put(KEY, List.of(
                Map.of(STARTKEY, "FW version ",
                        ENDKEY, ""),
                Map.of(STARTKEY, "",
                        ENDKEY, " is running from flash bank 0")));
    }

}
