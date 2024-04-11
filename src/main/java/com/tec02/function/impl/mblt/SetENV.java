/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.mblt;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.common.Common;
import com.tec02.common.MyConst;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.common.AbsFucnUseTelnetOrCommportConnector;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class SetENV extends AbsFucnUseTelnetOrCommportConnector {

    public SetENV(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        try ( AbsCommunicate communicate = this.baseFunction.getTelnetOrComportConnector()) {
            boolean isLowerCase = this.config.get(LOWER_CASE, false);
            addLog(CONFIG, "%s: %s", LOWER_CASE, isLowerCase);
            String command = createCommand(isLowerCase);
            if (command == null) {
                return false;
            }
            if (!this.baseFunction.sendCommand(communicate, command)) {
                return false;
            }
            String readUntil = config.getString(READ_UNTIL);
            return this.analysisBase.isResponseContainKeyAndShow(communicate,
                    config.getString(SPEC, readUntil), readUntil,
                    new TimeS(config.getInteger(TIME, 10)));
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }
    protected static final String LOWER_CASE = "isLowerCase";
    protected static final String SPEC = "spec";
    protected static final String TIME = "time";
    protected static final String READ_UNTIL = "readUntil";
    private static final String REPLACE = "replace";

    protected String createCommand(boolean isLowerCase) {
        String command = config.getString(COMMAND);
        String replace = config.getString(REPLACE);
        addLog(CONFIG, "%s: %s", REPLACE, replace);
        addLog(CONFIG, "%s: %s", COMMAND, command);
        if (command == null || command.isBlank()) {
            return null;
        }
        String key;
        String value;
        List<String> groups = Common.findGroups(command, "\\{\\w+\\}");
        for (String group : groups) {
            key = group.replaceAll("\\{", "").replaceAll("\\}", "");
            if (key.isBlank()) {
                continue;
            }
            value = dataCell.getString(key);
            if (value == null) {
                addLog(PC, "get data(key: %s, value: null)", key);
                return null;
            }
            if (isLowerCase) {
                value = value.toLowerCase();
            }
            if (replace != null && !replace.isBlank()) {
                StringBuilder builder = new StringBuilder(value);
                int startIndex = builder.length() - 1 < 0 ? 0 : builder.length() - 1;
                builder.replace(startIndex, startIndex + replace.length(), replace);
                value = builder.toString();
            }
            setResult(value);
            addLog(PC, "get data(key: %s, value: %s)", key, value);
            command = command.replaceAll(group.replaceAll("\\{", "\\\\{")
                    .replaceAll("\\}", "\\\\}"),
                    value);
        }
        return command;
    }

    protected static final String COMMAND = "command";

    @Override
    protected void createConfig(FunctionConfig config) {
        config.setTime_out(20);
        config.setDebugCancellRun(true);
        config.put(COMMAND, "fw_setenv serial {mlbsn}");
        config.put(REPLACE, "");
        config.put(LOWER_CASE, false);
        config.put(READ_UNTIL, "root@eero-test:/#");
        config.put(SPEC, "root@eero-test:/#");
        config.put(TIME, 5);
    }
}
