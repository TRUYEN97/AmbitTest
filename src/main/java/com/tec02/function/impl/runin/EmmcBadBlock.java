/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.runin;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.common.Common;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.AbsFucnUseTelnetOrCommportConnector;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class EmmcBadBlock extends AbsFucnUseTelnetOrCommportConnector {

    public EmmcBadBlock(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

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
        String readUntil = this.config.getString(READ_UNTIL);
        int time = this.config.getInteger(TIME, 10);
        List<String> commands = this.config.getJsonList(COMMAND);
        if (commands == null || commands.isEmpty()) {
            addLog(ERROR, "command in config is null or empty!");
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
            addLog(PC, "Sum of bad blocks: " + sunBadblock);
        }
        setResult(sunBadblock);
        return true;
    }

    private String getValue(String result) {
        if (result == null) {
            return null;
        }
        String startkey = config.getString(STARTKEY);
        String endkey = config.getString(ENDKEY);
        return Common.subString(result, startkey, endkey);
    }
    private static final String ENDKEY = "Endkey";
    private static final String STARTKEY = "Startkey";
    private static final String TIME = "Time";
    private static final String READ_UNTIL = "ReadUntil";
    private static final String COMMAND = "command";

    @Override
    protected void createConfig(FunctionConfig config) {
        config.setTime_out(800);
        config.setRetry(1);
        config.put(COMMAND, List.of("stress --cpu 8 --io 4 --vm 2 --vm-bytes 128M --timeout 1800s"));
        config.put(STARTKEY, "Pass completed,");
        config.put(ENDKEY, "bad blocks found");
        config.put(READ_UNTIL, "root@eero-test:/#");
        config.put(TIME, 2200);
    }
}
