/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.mblt;

import com.alibaba.fastjson2.JSONObject;
import com.tec02.FileService.FileService;
import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.Impl.Comport.ComPort;
import com.tec02.configuration.model.errorCode.ItemErrorCode;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.common.FixtureAction;
import com.tec02.main.ErrorLog;
import java.io.File;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class ThermalShutdown extends AbsFunction {

    private final FixtureAction fixtureAction;


    public ThermalShutdown() {
        this.fixtureAction = new FixtureAction();
    }

    @Override
    public void setConfig(FunctionConfig config) {
        super.setConfig(config); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        this.fixtureAction.setConfig(config);
    }

    @Override
    public void setErrorCode(ItemErrorCode errorCode) {
        super.setErrorCode(errorCode); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        this.fixtureAction.setErrorCode(errorCode);
    }
    

    @Override
    protected boolean test() {
        String outLow = this.config.getString("PowerLowCMD");
        addLog(CONFIG, "PowerLowCMD: " + outLow);
        String outHigh = this.config.getString("PowerHighCMD");
        addLog(CONFIG, "PowerHighCMD: " + outHigh);
        String command = this.config.getString("Command");
        addLog(CONFIG, "Command: " + command);
        try ( ComPort fixture = this.fixtureAction.getComport()) {
            if (fixture == null || !this.baseFunction.sendCommand(fixture, outLow)) {
                return false;
            }
            try {
                delay();
                if (!this.baseFunction.sendCommand(fixture, command)) {
                    return false;
                }
                return getValue(fixture);
            } finally {
                delay();
                this.baseFunction.sendCommand(fixture, outHigh);
                delay();
            }
        } catch (Exception e) {
            e.printStackTrace();
            addLog(ERROR, e.getMessage());
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }

    private boolean getValue(final ComPort fixture) throws NumberFormatException {
        JSONObject voltageItems = getVoltageItems();
        String inline;
        List<String> skipTP = this.config.getJsonList("SkipTP");
        addLog(CONFIG, "Skip point: " + skipTP);
        boolean result = true;
        while ((inline = fixture.readLine(new TimeS(1))) != null) {
            addLog(PC, "-------------------------------------");
            addLog(COMPORT, inline);
            if (!inline.startsWith("TP") || !inline.contains("=")) {
                continue;
            }
            if (inline.contains(",")) {
                inline = inline.replaceAll(",", ".");
            }
            String voltPoint = this.analysisBase.findGroup(inline, "^TP[0-9]+");
            if (skipTP.contains(voltPoint)) {
                continue;
            }
            Double value = Double.valueOf(this.analysisBase.findGroup(inline, "\\-?[0-9]+\\.[0-9]+"));
            addLog("PC", String.format("Item: %s | %s", voltPoint, value));
            result = checkVoltPoint(voltageItems, voltPoint, value);
            if (inline.endsWith(":")) {
                break;
            }
        }
        return result;
    }

    private void delay() {
        try {
            addLog(PC, "Delay 200 ms");
            Thread.sleep(200);
        } catch (InterruptedException ex) {
        }
    }

    private boolean checkVoltPoint(JSONObject voltageItems, String voltPoint, Double value) {
        Double spec = voltageItems.getDouble(voltPoint);
        addLog(CONFIG, String.format("Value: %s | Spec: %s", value, spec));
        return spec == null || value <= spec;
    }

    private JSONObject getVoltageItems() {
        String path = this.config.getString("FileVoltageItems");
        addLog(CONFIG, path);
        String volList = new FileService().readFile(new File(path));
        JSONObject voltageItems = JSONObject.parseObject(volList);
        return voltageItems;
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.merge(new FixtureAction().getDefaultConfig());
        config.setTime_out(10);
        config.setRetry(2);
        config.put("PowerLowCMD", "AT+TSD_OUTLOW%");
        config.put("PowerHighCMD", "AT+TSD_OUTHIGH%");
        config.put("Command", "AT+VOLTAGE%");
        config.put("SkipTP", List.of("TP523"));
    }

    @Override
    protected void init() {
    }

}
