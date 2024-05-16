/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.function.impl.AbsFucnUseTelnetOrCommportConnector;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.tec02.Time.WaitTime.AbsTime;
import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.common.Common;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.communication.Communicate.Impl.Comport.ComPort;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class LedTest extends AbsFucnUseTelnetOrCommportConnector {

    public LedTest(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected void createConfig(FunctionConfig config) {
        config.setTime_out(20);
        config.setRetry(2);
        config.setFailApiName("led_r_r");
        config.put(MBLTPN, "");
        config.put(TIME, 5);
        config.put(CHECK_LED_MODEL, "cat /sys/class/leds/W/device/name");
        config.put(LED_VALUE_CMD, "AT+LEDSTATUS%");
        config.put(READ_UNTIL, "root@eero-test:/#");
        JSONObject apis = new JSONObject();
        config.put(APIS, apis);
        apis.put("led_r_r", "R\\=?=\\d+(\\.\\d+)?");
        apis.put("led_r_g", "G\\=?=\\d+(\\.\\d+)?");
        apis.put("led_r_b", "B\\=?=\\d+(\\.\\d+)?");
        JSONObject ledModelConfig = new JSONObject();
        JSONObject ledModel;
        config.put(LED_MODEL, ledModelConfig);
        ledModel = new JSONObject();
        ledModel.put(INT_COMMANDS, List.of());
        ledModel.put(COMMANDS, List.of("i2cset -f -y 0 0x30 0x70 0x00",
                "i2cset -f -y 0 0x30 0x04 0x00", "i2cset -f -y 0 0x30 0x03 0x00",
                "i2cset -f -y 0 0x30 0x02 0x00", "i2cset -f -y 0 0x30 0x0e 0x00",
                "i2cset -f -y 0 0x30 0x04 0xff", "i2cset -f -y 0 0x30 0x07 0xdd"));
        ledModel.put(END_COMMANDS, List.of());
        ledModel.put(PNS, List.of("830-01100", "830-01100-B", "830-01120"));
        ledModelConfig.put("lp5562", ledModel);
        ledModel = new JSONObject();
        ledModel.put(INT_COMMANDS, List.of());
        ledModel.put(COMMANDS, List.of("i2cset -f -y 0 0x30 0x4 0x0",
                "i2cset -f -y 0 0x30 0x4 0x1", "i2cset -f -y 0 0x30 0x6 0xb1"));
        ledModel.put(END_COMMANDS, List.of());
        ledModel.put(PNS, List.of("830-01122", "830-01102"));
        ledModelConfig.put("ktd2027", ledModel);
    }

    @Override
    protected boolean test() {
        try (final AbsCommunicate communicate = this.baseFunction.getTelnet()) {
            String pn = this.dataCell.getString(MBLTPN, config.getString(MBLTPN));
            addLog(PC, "mbltpn: %s", pn);
            TimeS timer = new TimeS(config.getInteger(TIME, 10));
            String readUntil = config.getString(READ_UNTIL);
            JSONObject pnModel = getPnModel(pn, communicate, timer);
            if (pnModel == null) {
                addLog(CONFIG, "PN model is null!");
                return false;
            }
            JSONArray initLedCommands = pnModel.getJSONArray(INT_COMMANDS);
            addLog(CONFIG, "led init-commands: %s", initLedCommands);
            if (initLedCommands != null && !initLedCommands.isEmpty()
                    && !sendCommands(initLedCommands, communicate, timer, readUntil)) {
                return false;
            }
            JSONArray ledCommands = pnModel.getJSONArray(COMMANDS);
            addLog(CONFIG, "led commands: %s", ledCommands);
            if (!sendCommands(ledCommands, communicate, timer, readUntil)) {
                return false;
            }
            this.baseFunction.delay(500);
            Map<String, String> rs = getLedValues();
            JSONArray endLedCommands = pnModel.getJSONArray(END_COMMANDS);
            addLog(CONFIG, "led end-commands: %s", endLedCommands);
            if (endLedCommands != null && !endLedCommands.isEmpty()
                    && !sendCommands(endLedCommands, communicate, timer, readUntil)) {
                return false;
            }
            addLog(CONFIG, "Led values: %s", rs);
            ValueSubItem subItem;
            for (Map.Entry<String, String> entry : rs.entrySet()) {
                subItem = createSubItem(ValueSubItem.class, entry.getKey());
                subItem.setValue(entry.getValue());
                subItem.runTest(1);
            }
            return isAllSubItemPass();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }

    private boolean sendCommands(JSONArray ledCommands, final AbsCommunicate communicate, AbsTime timer, String readUntil) {
        if (ledCommands == null) {
            return false;
        }
        for (Object ledCommand : ledCommands) {
            if (ledCommand instanceof String com) {
                if (!this.baseFunction.sendCommand(communicate, com)) {
                    return false;
                }
                addLog(communicate.getName(), communicate.readUntil(timer, readUntil));
            }
        }
        return true;
    }

    private JSONObject getPnModel(String pn, final AbsCommunicate communicate, AbsTime timer) {
        JSONObject ledModelConfig = this.config.getJSONObject(LED_MODEL);
        if (ledModelConfig == null) {
            addLog(CONFIG, "led model is null!");
            return null;
        }
        if (pn == null || pn.isBlank()) {
            if (isDebugMode()) {
                String ledModel = getLedModel(communicate, timer);
                addLog(PC, "Led model: %s", ledModel);
                if (ledModel != null && ledModelConfig.containsKey(ledModel)) {
                    return ledModelConfig.getJSONObject(ledModel);
                }
            }
        } else {
            JSONArray pns;
            JSONObject pnModel;
            for (String key : ledModelConfig.keySet()) {
                pnModel = ledModelConfig.getJSONObject(key);
                pns = pnModel.getJSONArray(PNS);
                if (pns != null && pns.contains(pn)) {
                    return pnModel;
                }
            }
        }
        return null;
    }

    private Map<String, String> getLedValues() throws IOException {
        String ledResponce;
        Map<String, String> rs = new HashMap<>();
        try (final ComPort comport = this.baseFunction.getComport()) {
            String getLedValueCmd = this.config.getString(LED_VALUE_CMD);
            if (!this.baseFunction.sendCommand(comport, getLedValueCmd)) {
                return null;
            }
            ledResponce = comport.readUntil(new TimeS(1), "\n", ";");
            addLog(comport.getName(), ledResponce == null ? "" : ledResponce);
        }
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
    private static final String PNS = "pns";

    private String getLedModel(final AbsCommunicate communicate, AbsTime timer) {
        String checkLedModeCmd = this.config.getString(CHECK_LED_MODEL);
        if (!this.baseFunction.sendCommand(communicate, checkLedModeCmd)) {
            return null;
        }
        String ledModel = this.analysisBase.getLine(communicate, timer, 1);
        return ledModel;
    }
    private static final String INT_COMMANDS = "initCommands";
    private static final String COMMANDS = "commands";
    private static final String END_COMMANDS = "end_Commands";
    private static final String TIME = "time";
    private static final String LED_MODEL = "ledModel";
    private static final String READ_UNTIL = "readUntil";
    private static final String MBLTPN = "mbltpn";
    private static final String CHECK_LED_MODEL = "checkLedModel";


}
