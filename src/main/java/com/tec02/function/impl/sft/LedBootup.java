/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.sft;

import com.alibaba.fastjson2.JSONObject;
import com.tec02.Time.WaitTime.AbsTime;
import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.common.Common;
import com.tec02.communication.Communicate.Impl.Comport.ComPort;
import com.tec02.function.AbsFunction;
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
public class LedBootup extends AbsFunction {

    public LedBootup(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        try (final ComPort comport = this.baseFunction.getComport()) {
            AbsTime timer = new TimeS(this.config.getInteger("time", 5));
            AbsTime timerOut = new TimeS(this.config.getInteger("timeOut", 70));
            int timeDelay = this.config.getInteger("delayS", 0);
            if (timeDelay > 0) {
                addLog(PC, "Delay: %s S", timeDelay);
                this.baseFunction.delay(timeDelay * 1000);
            }
            while (timerOut.onTime()) {
                if (checkLedValue(timer, comport)) {
                    return true;
                }
                addLog(PC, "========================================================");
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
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

    private static final String APIS = "apis";
    private static final String LED_VALUE_CMD = "getLedValueCmd";

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setTime_out(80);
        config.put("time", 5);
        config.put("timeOut", 70);
        config.put("delayS", 0);
        config.put(LED_VALUE_CMD, "AT+LEDSTATUS%");
        JSONObject apis = new JSONObject();
        config.put(APIS, apis);
        apis.put("led_r_r", "R\\=?=\\d+(\\.\\d+)?");
        apis.put("led_r_g", "G\\=?=\\d+(\\.\\d+)?");
        apis.put("led_r_b", "B\\=?=\\d+(\\.\\d+)?");
    }

}
