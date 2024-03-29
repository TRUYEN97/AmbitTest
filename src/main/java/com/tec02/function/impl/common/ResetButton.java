/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.Impl.Comport.ComPort;
import com.tec02.communication.Communicate.Impl.Telnet.Telnet;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;

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
        try ( ComPort comport = this.baseFunction.getComport()) {
            try ( Telnet telent = this.baseFunction.getTelnet()) {
                if (!this.baseFunction.sendCommand(comport, startPushCmd)) {
                    return false;
                }
                if (!this.analysisBase.isResponseContainKeyAndShow(telent,
                        spec, spec, new TimeS(time))) {
                    return false;
                }
            } finally {
                this.baseFunction.sendCommad(comport, endPushCmds, readUntils, time);
            }
            setResult(spec);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }
    private static final String TIME = "time";
    private static final String SPEC = "spec";
    private static final String READ_UNTIL = "readUntil";
    private static final String END_PUSH_CMDS = "endPushCmds";
    private static final String START_PUSH_CMDS = "startPushCmds";

}
