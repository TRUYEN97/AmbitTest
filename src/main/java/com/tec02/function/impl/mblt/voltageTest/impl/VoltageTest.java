/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.mblt.voltageTest.impl;

import com.tec02.FileService.FileService;
import com.tec02.communication.Communicate.Impl.Comport.ComPort;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.mblt.voltageTest.AbsVoltage;
import com.tec02.function.impl.common.ValueSubItem;
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
public class VoltageTest extends AbsVoltage {

    public VoltageTest(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    private boolean checkVol(String tpItemNames, String vol) {
        ValueSubItem voltagePonit = createSubItem( ValueSubItem.class
                , tpItemNames);
        voltagePonit.setValue(vol);
        voltagePonit.runTest(1);
        return voltagePonit.isPass();
    }

    @Override
    protected void defaultConfig(FunctionConfig config) {
        config.setTime_out(20);
        config.setRetry(1);
        config.setFailApiName("v_tp10_dvdd3.3");
        config.put("Cmds", List.of("AT+VOLTAGE%"));
        config.put("readUntil", ";");
        config.put("filePath", "APIVoltage.csv");
    }

    @Override
    protected boolean testfunc() {
        List<String> commands = this.config.getJsonList("Cmds");
        List<String> readUntils = this.config.getJsonList("readUntil");
        File volPointPath = new File(this.config.getString("filePath"));
        addLog(CONFIG, "Command: %s", commands);
        addLog(CONFIG, "read until: %s", readUntils);
        addLog(CONFIG, "file path: %s", volPointPath);
        try {
            if (!volPointPath.exists()) {
                addLog(PC, "file path: %s not exists", volPointPath);
                return false;
            }
            try ( ComPort fixture = this.fixtureAction.getComport()) {
                if (!this.getVoltageValue(fixture, commands, readUntils)) {
                    return false;
                }
            }
            Map<String, String> tpItemNames = new HashMap<>();
            String stringPoints = new FileService().readFile(volPointPath);
            for (String stringPoint : stringPoints.split("\r\n")) {
                String[] elems = stringPoint.split(",");
                if (elems.length < 2) {
                    continue;
                }
                String tp = elems[0];
                String itemName = elems[1];
                if (tp.isBlank() || itemName.isBlank()) {
                    continue;
                }
                tpItemNames.put(tp, itemName);
            }
            if (this.voltageVals.isEmpty()) {
                return false;
            }
            boolean rs = true;
            String itemName;
            for (String tp : this.voltageVals.keySet()) {
                if (!tpItemNames.containsKey(tp)) {
                    addLog(PC, "no have %s in point list", tp);
                    continue;
                }
                itemName = tpItemNames.get(tp);
                addLog(CONFIG, "point convert to item name: %s -> %s",
                        tp, itemName);
                if (!checkVol(itemName, this.voltageVals.get(tp))) {
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
