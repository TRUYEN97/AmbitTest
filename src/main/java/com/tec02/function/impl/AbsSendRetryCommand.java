/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.communication.Communicate.Impl.Comport.ComPort;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import java.util.List;

/**
 *
 * @author Administrator
 */
public abstract class AbsSendRetryCommand extends AbsFunction {

    public AbsSendRetryCommand(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    public boolean test() {
        if (retry > 0) {
            List<String> retryCommands = this.config.getJsonList(FIXTURE_RETRY_CMDS);
            addLog(CONFIG, "Retry commands: %s", retryCommands);
            if (retryCommands != null && !retryCommands.isEmpty()) {
                try ( ComPort comPort = getComport()) {
                    List<String> keyWords = this.config.getJsonList(FIXTURE_KEYS);
                    int time = this.config.getInteger(FIXTURE_WAIT, 1);
                    if (!sendCommand(comPort, retryCommands, keyWords, time)) {
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    addLog(ERROR, e.getMessage());
                    ErrorLog.addError(e.getMessage());
                    return false;
                }
            }
        }
        return testfunc();
    }
    protected static final String FIXTURE_WAIT = "FixtureWait";
    protected static final String FIXTURE_KEYS = "FixtureKeys";
    protected static final String FIXTURE_RETRY_CMDS = "FixtureRetryCmds";

    public boolean sendCommand(AbsCommunicate communicate, List<String> commands, List<String> keyWords, int time) {
        addLog(CONFIG, "Commands: " + commands);
        addLog(CONFIG, "Time: " + time);
        int index = 0;
        for (String keyWord : keyWords) {
            keyWords.set(index, this.dataCell.getString(keyWord, keyWord));
            index += 1;
        }
        addLog(PC, "keyWord: %s", keyWords);
        if (commands == null || commands.isEmpty() || keyWords == null || keyWords.isEmpty()) {
            addLog(ERROR, "Config failed");
            return false;
        }
        for (String command : commands) {
            if (!this.baseFunction.sendCommand(communicate, command)) {
                return false;
            }
            if (!this.analysisBase.isResponseContainKey(communicate, keyWords, new TimeS(time))) {
                return false;
            }
        }
        return true;
    }

    public ComPort getComport() {
        String com = this.config.getString(FIXTURE_COM);
        int baud = this.config.getInteger(FIXTURE_BAUD_RATE, 115200);
        return this.baseFunction.getComport(com, baud);
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setTime_out(25);
        config.setRetry(1);
        config.put(FIXTURE_RETRY_CMDS, List.of());
        config.put(FIXTURE_COM, "COM5");
        config.put(FIXTURE_BAUD_RATE, 115200);
        config.put(FIXTURE_KEYS, List.of("OK"));
        config.put(FIXTURE_WAIT, 20);
        defaultConfig(config);
    }
    protected static final String FIXTURE_BAUD_RATE = "FixtureBaudRate";
    protected static final String FIXTURE_COM = "FixtureCom";

    protected abstract void defaultConfig(FunctionConfig config);

    protected abstract boolean testfunc();

}
