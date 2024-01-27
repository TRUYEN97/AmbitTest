/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.mblt;

import com.tec02.Time.WaitTime.Class.TimeMs;
import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.Impl.Comport.ComPort;
import com.tec02.configuration.model.errorCode.ItemErrorCode;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.common.FixtureAction;
import com.tec02.main.ErrorLog;

/**
 *
 * @author Administrator
 */
public class OpenShort extends AbsFunction {

    private FixtureAction fixtureAction;

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
    public boolean test() {
        if ((retry > 1 && !reConnectPortFixture()) || !stopAutoboot()) {
            return false;
        }
        return getCurrent();
    }

    public boolean stopAutoboot() {
        String dutCom;
        int dutBaud;
        dutCom = this.config.getString("DutCom");
        dutBaud = this.config.getInteger("DutBaudRate", 9600);
        try ( ComPort dut = this.baseFunction.getComport(dutCom, dutBaud)) {
            if (dut == null) {
                return false;
            }
            String keyWord = this.config.getString("keyword");
            addLog(CONFIG, String.format("Key word: %s", keyWord));
            int time = this.config.getInteger("time", 10);
            addLog(CONFIG, String.format("Test about %s s", time));
            TimeS timer = new TimeS(time);
            String line;
            while (timer.onTime()) {
                line = dut.readLine(new TimeMs(200));
                addLog(DUT, line == null ? "" : line);
                if (line != null && line.trim().endsWith(keyWord)) {
                    dut.insertCommand("reset");
                    return true;
                }
                dut.insertCommand("reboot");
            }
//            if(dataCell.getAPImode().equalsIgnoreCase(Constance)){
//                
//            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }

    public boolean reConnectPortFixture() {
        return this.fixtureAction.test();
    }

    public boolean getCurrent() {
        try ( ComPort fixture = this.fixtureAction.getComport()) {
            String command = this.config.getString("CurrentTestCommand");
            if (fixture == null || !this.baseFunction.sendCommand(fixture, command)) {
                return false;
            }
            String regex = this.config.getString("CurrentRegex");
            String value = this.analysisBase.getValue(fixture, regex, new TimeS(1), null);
            if (this.analysisBase.isNumber(value)) {
                double ampe = Double.parseDouble(value) / 1000.0;
                addLog(PC, String.format("Current: %s A", ampe));
                setResult(ampe);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setTime_out(30);
        config.merge(new FixtureAction().getDefaultConfig());
        config.put("DutCom", "COM4");
        config.put("DutBaudRate", 112500);
        config.put("keyword", "IPQ5018#");
        config.put("time", 20);
        config.put("CurrentTestCommand", "AT+VBUS_CUR%");
        config.put("CurrentRegex", "[0-9]+\\.?[0-9]*");
    }

    @Override
    protected void init() {
        this.fixtureAction = new FixtureAction();
    }
}
