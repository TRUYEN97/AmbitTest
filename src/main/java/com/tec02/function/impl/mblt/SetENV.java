/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.mblt;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.common.Common;
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
            String command = createCommand();
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
    private static final String SPEC = "spec";
    private static final String TIME = "time";
    private static final String READ_UNTIL = "readUntil";

    protected String createCommand() {
        String command = config.getString(COMMAND);
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
            addLog(PC, "get data(key: %s, value: %s)", key, value);
            if (value == null) {
                return null;
            }
            command = command.replaceAll(group.replaceAll("\\{", "\\\\{")
                    .replaceAll("\\}", "\\\\}"),
                     value);
        }
        return command;
    }

    private static final String COMMAND = "command";

    @Override
    protected void createConfig(FunctionConfig config) {
        config.setTime_out(20);
        config.setDebugCancellRun(true);
        config.put(COMMAND, "");
        config.put(READ_UNTIL, "root@eero-test:/#");
        config.put(SPEC, "root@eero-test:/#");
        config.put(TIME, 5);
    }
}
