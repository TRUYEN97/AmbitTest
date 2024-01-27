/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.runin;

import com.alibaba.fastjson.JSONObject;
import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.main.ErrorLog;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class EmmcBadBlock extends AbsFunction {
    
    @Override
    protected boolean test() {
        try {
            String ip = this.baseFunction.getIp();
            addLog("IP: " + ip);
            if (ip == null) {
                return false;
            }
            return check(ip);
        } catch (Exception ex) {
            ex.printStackTrace();
            addLog(ERROR, ex.getLocalizedMessage());
            return false;
        }
    }
    
    private boolean check(String ip) {
        try ( AbsCommunicate communicate = this.baseFunction.getTelnetOrComportConnector()) {
            if (communicate == null) {
                return false;
            }
            return runCommand(communicate);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }
    
    private boolean runCommand(AbsCommunicate telnet) {
        int sunBadblock = 0;
        String readUntil = this.config.getString("ReadUntil");
        int time = this.config.getInteger("Time", 10);
        List<String> commands = this.config.getJsonList("command");
        if (commands == null || commands.isEmpty()) {
            addLog("ERROR", "command in config is null or empty!");
            return false;
        }
        for (String subCommand : commands) {
            if (!this.baseFunction.sendCommand(telnet, subCommand)) {
                return false;
            }
            String result = this.analysisBase.readShowUntil(telnet, readUntil, new TimeS(time));
            String value = getValue(result);
            if (!this.analysisBase.isNumber(value)) {
                return false;
            }
            sunBadblock += this.analysisBase.string2Integer(value);
            addLog("PC", "Sum of bad blocks: " + sunBadblock);
        }
        setResult(sunBadblock);
        return true;
    }
    
    private String getValue(String result) {
        if (result == null) {
            return null;
        }
        String startkey = config.getString("Startkey");
        String endkey = config.getString("Endkey");
        return this.analysisBase.subString(result, startkey, endkey);
    }
    
    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setTime_out(800);
        config.setRetry(1);
        config.setBonus(JSONObject.parseObject("""
                                               {"type": "telnet",
                                                           "IP": "192.168.1.1",
                                                           "comport": 1,
                                                           "baudrate": 115200,
                                                           "Time": 700,
                                                           "command": ["badblocks -svw /dev/mmcblk0p17", "badblocks -svw /dev/mmcblk0p20"],
                                                           "Startkey": "Pass completed,",
                                                           "Endkey": "bad blocks found",
                                                           "ReadUntil": "root@eero-test:/#"}"""));
    }

    @Override
    protected void init() {
    }
    
}
