/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.reader;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.AbsFucnUseTelnetOrCommportConnector;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;

/**
 *
 * @author Administrator
 */
public class PhyItem extends AbsFucnUseTelnetOrCommportConnector {

    public PhyItem(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        try (final AbsCommunicate communicate = this.baseFunction.getTelnet()) {
            if (communicate == null) {
                return false;
            }
            String command = this.config.getString(COMMAND);
            String key = this.config.getString(KEY);
            String readuntil = this.config.getString(READ_UNTIL, "root@eero-test:/#");
            int time = this.config.getInteger(TIME, 10);
            boolean isGetFirstValue = this.config.get(FIRST, false);
            addLog(CONFIG, "%s: %s", COMMAND, command);
            addLog(CONFIG, "%s: %s", KEY, key);
            addLog(CONFIG, "%s: %s", READ_UNTIL, readuntil);
            addLog(CONFIG, "%s: %s", FIRST, isGetFirstValue);
            addLog(CONFIG, "%s: %s", TIME, time);
            if (key == null || command == null || !this.baseFunction.sendCommand(communicate, command)) {
                return false;
            }
            String responce = this.analysisBase.readShowUntil(communicate, readuntil, new TimeS(time));
            StringBuilder builder = new StringBuilder();
            int index;
            for (String line : responce.split("\r\n")) {
                index = line.indexOf(key);
                if (index > -1) {
                    builder.append(line.substring(index).trim());
                    builder.append("|");
                    if (isGetFirstValue) {
                        break;
                    }
                }
            }
            setResult(builder.deleteCharAt(builder.length() - 1));
            return !builder.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }
    private static final String FIRST = "getFirst";
    private static final String READ_UNTIL = "ReadUntil";
    private static final String COMMAND = "command";
    private static final String TIME = "Time";

    @Override
    protected void createConfig(FunctionConfig config) {
        config.setTime_out(10);
        config.setRetry(1);
        config.put(COMMAND, "dmesg|grep 'PHY ID'");
        config.put(READ_UNTIL, "root@eero-test:/#");
        config.put(KEY, "0x");
        config.put(FIRST, false);
        config.put(TIME, 10);

    }
    private static final String KEY = "key";

}
