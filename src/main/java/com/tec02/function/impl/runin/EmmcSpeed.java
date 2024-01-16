/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.runin;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.main.ErrorLog;

/**
 *
 * @author Administrator
 */
public class EmmcSpeed extends AbsFunction{

    private String data;
    private String block;
    private String key;
    
    @Override
    protected boolean test() {
        if (data == null) {
            try {
                try ( AbsCommunicate telnet = this.baseFunction.getTelnetOrComportConnector()) {
                    if (telnet == null) {
                        return false;
                    }
                    if (!this.baseFunction.sendCommand(telnet, this.config.getString("command"))) {
                        return false;
                    }
                    int time = this.config.getInteger("Time", 5);
                    String until = this.config.getString("ReadUntil");
                    data = this.analysisBase.readUntilAndShow(telnet, until, new TimeS(time));
                } catch (Exception e) {
                    e.printStackTrace();
                    ErrorLog.addError(this, e.getMessage());
                    return false;
                }
                addLog("Telnet", data);
            } catch (Exception ex) {
                ex.printStackTrace();
                addLog(ERROR, ex.getLocalizedMessage());
                return false;
            }
        }
        return checkResponse();
    }
    
    public void setData(String data, String block, String key) {
        this.data = data;
        this.block = block;
        this.key = key;
    }

    private boolean checkResponse() {
        if (!checkConfig()) {
            return false;
        }
        return getSpeed();
    }

    private boolean checkConfig() {
        if (block == null) {
            block = config.getString("Block");
        }
        addLog("Config", "Block: " + block);
        if (key == null) {
            key = this.config.getString("KeyWord");
        }
        addLog("Config", "KeyWord: %s", key);
        return key != null && block != null;
    }

    private boolean getSpeed() {
        String[] blockData = data.split("\r\n");
        int model = 0;
        for (String line : blockData) {
            if (line.contains("B of " + block + " in ")) {
                model = 1;
            }
            if (line.contains(key) && model == 1) {
                model = 2;
            }
            int start = line.lastIndexOf(" seconds, ") + 10;
            int end = line.lastIndexOf("MB/s");
            if (model == 2 && start < end && end > -1) {
                String value = line.substring(start, end);
                if (this.analysisBase.isNumber(value)) {
                    setResult(value);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setTime_out(60);
    }
    
}
