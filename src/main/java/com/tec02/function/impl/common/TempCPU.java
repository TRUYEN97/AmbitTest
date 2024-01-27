/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class TempCPU extends AbsFunction {

    @Override
    protected boolean test() {
        try {
            String commands = this.config.getString("command");
            if (commands == null) {
                addLog("CONFIG", "command is empty!");
                return false;
            }
            addLog(CONFIG, commands);
            try (AbsCommunicate communicate = this.baseFunction.getTelnetOrComportConnector()) {
                if (communicate == null) {
                    return false;
                }
                ArrayList<Integer> temps = getTempCPU(commands, communicate);
                if (temps == null) {
                    return false;
                }
                addLog(PC, String.format("Temp cpu: %s", temps));
                setResult(getMaxValue(temps));
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            addLog(ERROR, ex.getLocalizedMessage());
            return false;
        }
    }

    private ArrayList<Integer> getTempCPU(String commands, final AbsCommunicate telnet) throws NumberFormatException {
        ArrayList<Integer> temps = new ArrayList<>();
        List<String> startKeys = this.config.getJsonList("Startkeys");
        List<String> endKeys = this.config.getJsonList("Endkeys");
        List<String> Regexs = this.config.getJsonList("Regexs");
        int time = config.getInteger("Time", 10);
        String readUntil = config.getString("ReadUntil");
        int length = Integer.max(startKeys.size(), endKeys.size());
        if (!this.baseFunction.sendCommand(telnet, commands)) {
            return null;
        }
        for (int i = 0; i < length; i++) {
            String startkey = getKey(i, startKeys);
            String endkey = getKey(i, endKeys);
            String regex = getKey(i, Regexs);
            String value = this.analysisBase.getValue(telnet, startkey, endkey, regex, new TimeS(time), readUntil);
            if (!this.analysisBase.isNumber(value)) {
                addLog("ERROR", String.format("value is not number! value: %s", value));
                return null;
            }
            temps.add(Integer.valueOf(value));
        }
        return temps;
    }

    private String getKey(int i, List<String> keys) {
        if (i >= 0 && i < keys.size()) {
            return keys.get(i);
        }
        return null;
    }

    private Integer getMaxValue(ArrayList<Integer> temps) {
        Integer max = Integer.MIN_VALUE;
        for (Integer temp : temps) {
            max = Integer.max(max, temp);
        }
        return max;
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setTime_out(100);
        config.setRetry(1);
        config.put("command", "temperature");
        config.put("type", "telnet");
        config.put("IP", "192.168.1.1");
        config.put("comport", 1);
        config.put("baudrate", 115200);
        config.put("Startkeys", "");
        config.put("Endkeys", "");
        config.put("Regexs", "");
    }

    @Override
    protected void init() {
    }

}
