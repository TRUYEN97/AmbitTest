/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.Impl.Comport.ComPort;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.main.ErrorLog;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class FixtureAction extends AbsFunction {

    @Override
    public boolean test() {
        try ( ComPort comPort = getComport()) {
            if (comPort == null) {
                return false;
            }
            List<String> commands = this.config.getJsonList("FixtureCommands");
            List<String> keyWords = this.config.getJsonList("FixtureKeys");
            int time = this.config.getInteger("FixtureWait", 1);
            return sendCommand(comPort, commands, keyWords, time);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }

    public boolean sendCommand(ComPort comPort, List<String> commands, List<String> keyWords, int time) {
        addLog("Config", "keyWord: " + keyWords);
        addLog("Config", "Time: " + time);
        if (commands == null || commands.isEmpty() || keyWords == null || keyWords.isEmpty()) {
            addLog("ERROR", "Config failed");
            return false;
        }
        int keySide = keyWords.size();
        for (int index = 0; index < keySide; index++) {
            keyWords.set(index, getValueOfKey(keyWords.get(index)));
        }
        addLog("PC", String.format("True keys: %s", keyWords));
        try {
            for (String command : commands) {
                if (!this.baseFunction.sendCommand(comPort, command)) {
                    return false;
                }
                if (!this.analysisBase.isResponseContainKey(comPort, keyWords, new TimeS(time))) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }

    private String getValueOfKey(String keyWord) {
        return this.dataCell.getString(keyWord) == null ? keyWord
                : this.dataCell.getString(keyWord);
    }

    public ComPort getComport() {
        String com = this.config.getString("FixtureCom");
        int baud = this.config.getInteger("FixtureBaudRate", 9600);
        return this.baseFunction.getComport(com, baud);
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
