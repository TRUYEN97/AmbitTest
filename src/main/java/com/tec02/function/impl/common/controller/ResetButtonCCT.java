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
import com.tec02.function.impl.common.ValueWithSpecSubItem;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class ResetButtonCCT extends ResetButton {

    public ResetButtonCCT(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    private JSONObject getLedValues(ComPort comport) throws IOException {
        String ledResponce;
        JSONObject rs = new JSONObject();
        String getLedValueCmd = this.config.getString(LED_VALUE_CMD);
        if (!this.baseFunction.sendCommand(comport, getLedValueCmd)) {
            return null;
        }
        ledResponce = comport.readUntil(new TimeS(1), "\n", ";");
        addLog(comport.getName(), ledResponce == null ? "" : ledResponce);
        JSONObject apis = this.config.get(APIS);
        addLog(CONFIG, "%s: %s", APIS, apis == null ? null : apis.keySet());
        if (apis == null || apis.isEmpty()) {
            return null;
        }
        String regex;
        JSONObject itemInfo;
        for (String key : apis.keySet()) {
            itemInfo = apis.getJSONObject(key);
            regex = itemInfo.getString(REGEX);
            if (regex != null) {
                itemInfo.put(VAL, Common.findGroup(Common.findGroup(ledResponce, regex), "\\d+(\\.\\d+)?"));
                rs.put(key, itemInfo);
            }
        }
        return rs;
    }
    private static final String VAL = "val";
    private static final String UPPER_LIMIT = "upper_limit";
    private static final String LOWER_LIMIT = "lower_limit";
    private static final String REGEX = "regex";
    private static final String LIMIT_TYPE = "limit_type";

    private boolean checkLedValue(AbsTime timer, ComPort comport) throws IOException {
        JSONObject rs;
        boolean useLocalLimit = this.config.get(USER_LOCAL_LIMITS, false);
        timer.update();
        while (timer.onTime()) {
            rs = getLedValues(comport);
            addLog(CONFIG, "Led values: %s", rs);
            clearSubItem();
            JSONObject jsVal;
            if (useLocalLimit) {
                ValueWithSpecSubItem subItem;
                for (String key : rs.keySet()) {
                    jsVal = rs.getJSONObject(key);
                    subItem = createSubItem(ValueWithSpecSubItem.class, key);
                    subItem.setLowerlimit(jsVal.getString(LOWER_LIMIT));
                    subItem.setUpperlimit(jsVal.getString(UPPER_LIMIT));
                    subItem.setValue(jsVal.getString(VAL));
                    subItem.setLimitType(jsVal.getString(LIMIT_TYPE));
                    subItem.runTest(1);
                }
            } else {
                ValueSubItem subItem;
                for (String key : rs.keySet()) {
                    jsVal = rs.getJSONObject(key);
                    subItem = createSubItem(ValueSubItem.class, key);
                    subItem.setValue(jsVal.getString(VAL));
                    subItem.runTest(1);
                }
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
        TimeS timer = new TimeS(time);
        TimeS timeS = new TimeS(waitLedTime);
        try ( ComPort comport = this.baseFunction.getComport()) {
            while (timer.onTime()) {
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
    private static final String USER_LOCAL_LIMITS = "UserLocalLimits";

    @Override
    protected void createConfig(FunctionConfig config) {
        super.createConfig(config);
        config.setRetry(0);
        config.setTime_out(80);
        config.put(TIME, 20);
        config.put(LED_WAIT_TIME, 1);
        config.put(USER_LOCAL_LIMITS, false);
        config.put(LED_VALUE_CMD, "AT+LEDPARAMETER%");
        JSONObject apis = new JSONObject();
        config.put(APIS, apis);
        apis.put("led_b_x", Map.of(REGEX, "COR_X\\=?=\\d+(\\.\\d+)?",
                LOWER_LIMIT, "0.683", UPPER_LIMIT, "0.733", LIMIT_TYPE, "LIMIT"));
        apis.put("led_b_y", Map.of(REGEX, "COR_Y\\=?=\\d+(\\.\\d+)?",
                LOWER_LIMIT, "0.270", UPPER_LIMIT, "0.320", LIMIT_TYPE, "LIMIT"));
    }

}
