/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.mblt;

import com.tec02.function.impl.common.AbsSendRetryCommand;
import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.Impl.Comport.ComPort;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;

/**
 *
 * @author Administrator
 */
public class UbootidleTestFixture extends AbsSendRetryCommand {

    public UbootidleTestFixture(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected void defaultConfig(FunctionConfig config) {
        config.setTime_out(10);
        config.setRetry(1);
        config.setTime_out(30);
        config.put("cmd", "AT+VBUS_CUR%");
        config.put("regex", "");
        config.put("startKey", "=");
        config.put("endKey", "");
        config.put("ratio", 1000);
    }

    @Override
    protected boolean testfunc() {
        try ( ComPort fixture = this.getComport()) {
            String command = this.config.getString("cmd");
            addLog(CONFIG, "cmd: %s", command);
            String regex = this.config.getString("regex");
//            addLog(CONFIG, "regex: %s", regex);
            String startKey = this.config.getString("startKey");
//            addLog(CONFIG, "startKey: %s", startKey);
            String endKey = this.config.getString("endKey");
//            addLog(CONFIG, "endKey: %s", endKey);
            int ratio = this.config.getInteger("ratio", 1);
            if (fixture == null || !this.baseFunction.sendCommand(fixture, command)) {
                return false;
            }
            String value = this.analysisBase.getValue(fixture,
                    startKey, endKey, regex, new TimeS(1), null);
            if (this.analysisBase.isNumber(value)) {
                addLog(CONFIG, "ratio: %s", ratio);
                double finalValue = Double.parseDouble(value) / ratio;
                addLog(PC, String.format("Value: %s", finalValue));
                setResult(finalValue);
                return true;
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
