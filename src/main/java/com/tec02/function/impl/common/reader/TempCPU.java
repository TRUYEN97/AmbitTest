/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.reader;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.common.AbsFucnUseTelnetOrCommportConnector;
import com.tec02.function.model.FunctionConstructorModel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class TempCPU extends AbsFucnUseTelnetOrCommportConnector {

    public TempCPU(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        try {
            String commands = this.config.getString(COMMAND);
            if (commands == null) {
                addLog("CONFIG", "command is empty!");
                return false;
            }
            addLog(CONFIG, commands);
            try ( AbsCommunicate communicate = this.baseFunction.getTelnetOrComportConnector()) {
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
        List<String> startKeys = this.config.getJsonList(STARTKEYS);
        List<String> endKeys = this.config.getJsonList(ENDKEYS);
        List<String> Regexs = this.config.getJsonList(REGEXS);
        int time = config.getInteger(TIME, 10);
        String readUntil = config.getString(READ_UNTIL);
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
    private static final String READ_UNTIL = "ReadUntil";
    private static final String TIME = "Time";

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
    protected void createConfig(FunctionConfig config) {
        config.setTime_out(100);
        config.setRetry(1);
        config.put(COMMAND, "temperature");
        config.put(STARTKEYS, "");
        config.put(ENDKEYS, "");
        config.put(REGEXS, "");
        config.put(TIME, 10);
        config.put(READ_UNTIL, "root@eero-test:/#");
    }
    private static final String REGEXS = "Regexs";
    private static final String ENDKEYS = "Endkeys";
    private static final String STARTKEYS = "Startkeys";
    private static final String COMMAND = "command";


}
