/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.mblt.voltageTest.impl;

import com.tec02.FileService.FileService;
import com.tec02.communication.Communicate.Impl.Comport.ComPort;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.mblt.voltageTest.AbsVoltage;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class ThermalShutdown extends AbsVoltage {

    public ThermalShutdown(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    private boolean checkVol(String tpV, String limit) {
        Double value = this.analysisBase.string2Double(tpV);
        Double limitV = this.analysisBase.string2Double(limit);
        return Double.compare(value, limitV) < 1;
    }

    @Override
    protected void defaultConfig(FunctionConfig config) {
        config.setTime_out(20);
        config.setRetry(2);
        config.put("tp", "TP10");
        config.put("InitCmds", List.of("AT+TSD_OUTLOW%"));
        config.put("EndCmds", List.of("AT+TSD_OUTHIGH%"));
        config.put("Cmds", List.of("AT+VOLTAGE%"));
        config.put("readUntil", ";");
        config.put("filePath", "APIVoltage.csv");
    }

    @Override
    protected boolean testfunc() {
        List<String> initCommands = this.config.getJsonList("InitCmds");
        List<String> commands = this.config.getJsonList("Cmds");
        List<String> endCommands = this.config.getJsonList("EndCmds");
        List<String> readUntils = this.config.getJsonList("readUntil");
        String tpVoltage = this.config.getString("tp");
        File volPointPath = new File(this.config.getString("filePath"));
        addLog("Config", "Init test command: %s", initCommands);
        addLog("Config", "Command: %s", commands);
        addLog("Config", "End test command: %s", endCommands);
        addLog("Config", "read until: %s", readUntils);
        addLog("Config", "file path: %s", volPointPath);
        try {
            if (!volPointPath.exists()) {
                addLog(PC, "file path: %s not exists", volPointPath);
                return false;
            }
            try ( ComPort fixture = this.fixtureAction.getComport()) {
                if (!sendFixtureCommand(fixture, initCommands, 500)) {
                    return false;
                }
                if (!this.getVoltageValue(fixture, commands, readUntils)) {
                    return false;
                }
                if (!sendFixtureCommand(fixture, endCommands, 1000)) {
                    return false;
                }
            }
            Map<String, String> tpItemNames = new HashMap<>();
            String stringPoints = new FileService().readFile(volPointPath);
            for (String stringPoint : stringPoints.split("\r\n")) {
                String[] elems = stringPoint.split(",");
                if (elems.length < 3) {
                    continue;
                }
                String tp = elems[0];
                String limit = elems[2];
                if (tp.isBlank() || limit.isBlank()) {
                    continue;
                }
                tpItemNames.put(tp, limit);
            }
            if (this.voltageVals.isEmpty()) {
                return false;
            }
            boolean rs = true;
            String limit;
            String value;
            for (String tp : this.voltageVals.keySet()) {
                if (!tpItemNames.containsKey(tp)) {
                    continue;
                }
                limit = tpItemNames.get(tp);
                value = this.voltageVals.get(tp);
                addLog(CONFIG, "tp: %s ->(Value: %s, upper limit:%s)", tp, value, limit);
                if (tpVoltage != null && tp.equalsIgnoreCase(tpVoltage)) {
                    setResult(value);
                }
                if (!checkVol(value, limit)) {
                    rs = false;
                }
            }
            return rs;
        } catch (IOException ex) {
            ex.printStackTrace();
            ErrorLog.addError(this, ex.getMessage());
            addLog(ERROR, ex.getMessage());
            return false;
        }
    }


}
