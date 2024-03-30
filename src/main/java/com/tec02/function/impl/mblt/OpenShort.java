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
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;

/**
 *
 * @author Administrator
 */
public class OpenShort extends AbsSendRetryCommand {

    private final UbootidleTestFixture currentTestFixture;

    public OpenShort(FunctionConstructorModel constructorModel) {
        super(constructorModel);
        this.currentTestFixture = createElementFunction(UbootidleTestFixture.class);
    }

    @Override
    protected void defaultConfig(FunctionConfig config) {
        config.merge(UbootidleTestFixture.class);
        config.setTime_out(30);
        config.setRetry(1);
        config.put("DutCom", "COM4");
        config.put("DutBaudRate", 112500);
        config.put("keyword", "IPQ5018#");
        config.put("time", 20);
    }

    @Override
    protected boolean testfunc() {
        String md = this.dataCell.getString("model");
        if (md != null && md.equalsIgnoreCase("REVERT")) {
            setCancel();
            return true;
        }
        String dutCom;
        int dutBaud;
        dutCom = this.config.getString("DutCom");
        dutBaud = this.config.getInteger("DutBaudRate", 115200);
        try ( ComPort comport = this.baseFunction.getComport(dutCom, dutBaud)) {
            try {
                if (comport == null) {
                    return false;
                }
                String keyWord = this.config.getString("keyword");
                addLog(CONFIG, String.format("Key word: %s", keyWord));
                int time = this.config.getInteger("time", 10);
                addLog(CONFIG, String.format("Test about %s s", time));
                TimeS timer = new TimeS(time);
                String line;
                timer.update();
                while (timer.onTime()) {
                    line = comport.readLine(new TimeMs(500));
                    addLog(COMPORT, line == null ? "" : line);
                    if (line != null && line.trim().endsWith(keyWord)) {
                        this.currentTestFixture.testfunc();
                        return true;
                    }
                    comport.insertCommand("reboot");
                }
                return false;
            } finally {
                comport.insertCommand("reset");
                comport.insertCommand("reboot");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            addLog(ERROR, e.getMessage());
            return false;
        }
    }


}
