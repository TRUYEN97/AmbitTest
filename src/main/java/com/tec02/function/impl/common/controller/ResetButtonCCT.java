/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.controller;

import com.alibaba.fastjson2.JSONObject;
import com.tec02.Time.WaitTime.AbsTime;
import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.common.Common;
import com.tec02.communication.Communicate.Impl.Comport.ComPort;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.common.ValueSubItem;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class ResetButtonCCT extends ResetButton {

    public ResetButtonCCT(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }
    
    private Map<String, String> getLedValues(ComPort comport) throws IOException {
        String ledResponce;
        Map<String, String> rs = new HashMap<>();
        String getLedValueCmd = this.config.getString(LED_VALUE_CMD);
        if (!this.baseFunction.sendCommand(comport, getLedValueCmd)) {
            return null;
        }
        ledResponce = comport.readUntil(new TimeS(1), "\n", ";");
        addLog(comport.getName(), ledResponce == null ? "" : ledResponce);
        Map<String, String> apis = this.config.get(APIS);
        addLog(CONFIG, "%s: %s", APIS, apis == null ? null : apis.keySet());
        if (apis == null || apis.isEmpty()) {
            return null;
        }
        String value;
        for (Map.Entry<String, String> entry : apis.entrySet()) {
            value = Common.findGroup(ledResponce, entry.getValue());
            rs.put(entry.getKey(), Common.findGroup(value, "\\d+(\\.\\d+)?"));
        }
        return rs;
    }

    
    private boolean checkLedValue(AbsTime timer, ComPort comport) throws IOException {
        Map<String, String> rs;
        timer.update();
        while (timer.onTime()) {
            rs = getLedValues(comport);
            addLog(CONFIG, "Led values: %s", rs);
            ValueSubItem subItem;
            clearSubItem();
            for (Map.Entry<String, String> entry : rs.entrySet()) {
                subItem = createSubItem(ValueSubItem.class, entry.getKey());
                subItem.setValue(entry.getValue());
                subItem.runTest(1);
            }
            if (!isAllSubItemPass()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean isResetOk(String spec, int time) {
        int waitLedTime = this.config.getInteger(LED_WAIT_TIME);
        TimeS timeS = new TimeS(waitLedTime);
        try ( ComPort comport = this.baseFunction.getComport()) {
            while (timeS.onTime()) {
                if (this.checkLedValue(timeS, comport)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }
    
    private static final String APIS = "apis";
    private static final String LED_WAIT_TIME = "ledWaitTime";
    private static final String LED_VALUE_CMD = "getLedValueCmd";

    @Override
    protected void createConfig(FunctionConfig config) {
        super.createConfig(config);
        config.setTime_out(80);
        config.put(LED_WAIT_TIME, 20);
        config.put(LED_VALUE_CMD, "AT+LEDSTATUS%");
         JSONObject apis = new JSONObject();
        config.put(APIS, apis);
        apis.put("led_r_r", "R\\=?=\\d+(\\.\\d+)?");
        apis.put("led_r_g", "G\\=?=\\d+(\\.\\d+)?");
        apis.put("led_r_b", "B\\=?=\\d+(\\.\\d+)?");
    }

}
