/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.controller;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.Impl.Comport.ComPort;
import com.tec02.communication.Communicate.Impl.Telnet.Telnet;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.AbsFucnUseTelnetOrCommportConnector;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import java.io.IOException;

/**
 *
 * @author Administrator
 */
public class ResetButton extends AbsFucnUseTelnetOrCommportConnector {

    public ResetButton(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    public ResetButton(FunctionConstructorModel constructorModel, int functionType) {
        super(constructorModel, functionType);
    }

    @Override
    protected void createConfig(FunctionConfig config) {
        config.setTime_out(20);
        config.setRetry(1);
        config.put(START_PUSH_CMDS, "AT+RESET_PRESS%");
        config.put(END_PUSH_CMDS, "AT+RESET_RELEASE%");
        config.put(READ_UNTIL, "ok");
        config.put(SPEC, "RESET PUSH BUTTON EVENT DETECTED");
        config.put(TIME, 10);
    }

    @Override
    protected boolean test() {
        String startPushCmd = this.config.getString(START_PUSH_CMDS);
        String endPushCmds = this.config.getString(END_PUSH_CMDS);
        String readUntils = this.config.getString(READ_UNTIL);
        String spec = this.config.getString(SPEC);
        int time = this.config.getInteger(TIME, 10);
        try {
            try ( ComPort comport = this.baseFunction.getComport()) {
                if (!this.baseFunction.sendCommand(comport, startPushCmd)) {
                    return false;
                }
            }
            if (!isResetOk(spec, time)) {
                return false;
            }
            setResult(spec);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        } finally {
            try ( ComPort comport = this.baseFunction.getComport()) {
                this.baseFunction.sendCommand(comport, endPushCmds, readUntils, time);
            } catch (Exception e) {
                e.printStackTrace();
                ErrorLog.addError(this, e.getMessage());
                return false;
            }
        }
    }

    protected boolean isResetOk(String spec, int time) throws Exception {
        try (final Telnet telent = this.baseFunction.getTelnet()) {
            return this.analysisBase.isResponseContainKeyAndShow(telent,
                    spec, spec, new TimeS(time));
        } catch (IOException e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }
    protected static final String TIME = "time";
    protected static final String SPEC = "spec";
    protected static final String READ_UNTIL = "readUntil";
    protected static final String END_PUSH_CMDS = "endPushCmds";
    protected static final String START_PUSH_CMDS = "startPushCmds";

}
