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
public class Usbside extends AbsFunction {

    private final FixtureAction fixture;


    public Usbside() {
        this.fixture = new FixtureAction();
    }
    
     @Override
    public void setConfig(FunctionConfig config) {
        super.setConfig(config); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        this.fixture.setConfig(config);
    }

    @Override
    public void setErrorCode(ItemErrorCode errorCode) {
        super.setErrorCode(errorCode); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        this.fixture.setErrorCode(errorCode);
    }

    @Override
    public boolean test() {
        if (!this.fixture.test()) {
            return false;
        }
        String dutCom = this.config.getString("DutCom");
        int dutBaud = this.config.getInteger("DutBaudRate", 9600);
        int dutWait = this.config.getInteger("DutWait", 1);
        addLog("Config", String.format("Test about %s s", dutWait));
        try ( ComPort dut = this.baseFunction.getComport(dutCom, dutBaud)) {
            if (dut == null) {
                return false;
            }
            TimeS timer = new TimeS(dutWait);
            String line;
            while (timer.onTime()) {
                line = dut.readAll(new TimeMs(500));
                addLog("DUT", line == null ? "" : line);
                if (line != null && !line.trim().isBlank()) {
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

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setTime_out(10);
        config.setRetry(2);
        config.merge(new FixtureAction().getDefaultConfig());
        config.put("DutCom", "COM4");
        config.put("DutBaudRate", 115200);
        config.put("DutWait", 2);
    }

    @Override
    protected void init() {
    }

}
