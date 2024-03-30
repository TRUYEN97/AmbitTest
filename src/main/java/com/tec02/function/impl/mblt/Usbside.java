/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.mblt;

import com.tec02.function.impl.common.AbsSendRetryCommand;
import com.tec02.Time.WaitTime.Class.TimeMs;
import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.Impl.Comport.ComPort;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.common.controller.FixtureAction;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import javax.swing.Timer;

/**
 *
 * @author Administrator
 */
public class Usbside extends AbsSendRetryCommand {

    private final FixtureAction fixture;

    public Usbside(FunctionConstructorModel constructorModel) {
        super(constructorModel);
        this.fixture = (FixtureAction) createElementFunction(FixtureAction.class);
    }

    @Override
    protected void defaultConfig(FunctionConfig config) {
        config.merge(FixtureAction.class);
        config.setTime_out(10);
        config.setRetry(2);
        config.put("DutCom", "COM4");
        config.put("DutBaudRate", 115200);
        config.put("DutWait", 2);
    }

    @Override
    protected boolean testfunc() {
        if (!this.fixture.testfunc()) {
            return false;
        }
        String dutCom = this.config.getString("DutCom");
        int dutBaud = this.config.getInteger("DutBaudRate", 9600);
        int dutWait = this.config.getInteger("DutWait", 1);
        addLog("Config", String.format("Test about %s s", dutWait));
        try ( ComPort comport = this.baseFunction.getComport(dutCom, dutBaud)) {
            if (comport == null) {
                return false;
            }
            TimeS timer = new TimeS(dutWait);
            String line;
            new Timer(dutWait * 1000, (e) -> {
                comport.stopRead();
            }).start();
            while (timer.onTime()) {
                line = comport.readAll(new TimeMs(500));
                addLog("DUT", line == null ? "" : line);
                if (line != null && !line.trim().isBlank()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            addLog(ERROR, e.getMessage());
            return false;
        }
    }

}
