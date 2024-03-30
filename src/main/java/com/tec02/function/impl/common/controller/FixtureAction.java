/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.controller;

import com.tec02.communication.Communicate.Impl.Comport.ComPort;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.common.AbsSendRetryCommand;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class FixtureAction extends AbsSendRetryCommand {

    public FixtureAction(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    public boolean testfunc() {
        try ( ComPort comPort = getComport()) {
            if (comPort == null) {
                return false;
            }
            List<String> commands = this.config.getJsonList("FixtureCmds");
            List<String> keyWords = this.config.getJsonList("FixtureKeys");
            int time = this.config.getInteger("FixtureWait", 1);
            int delay = this.config.getInteger("Delay", -1);
            boolean rs = sendCommand(comPort, commands, keyWords, time);
            if (rs && delay > 0) {
                addLog(PC, "Delay %s Ms", delay);
                this.baseFunction.delay(delay);
            }
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            addLog(ERROR, e.getMessage());
            return false;
        }
    }

    @Override
    protected void defaultConfig(FunctionConfig config) {
        config.setTime_out(25);
        config.put("FixtureCmds", List.of(""));
        config.put("Delay", 0);
    }

}
