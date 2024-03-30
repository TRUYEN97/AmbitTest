/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.reader;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.common.MyConst;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.communication.Communicate.Impl.Telnet.Telnet;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.common.AbsFucnUseTelnetOrCommportConnector;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class TelnetReadUntilKey extends AbsFucnUseTelnetOrCommportConnector {

    public TelnetReadUntilKey(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        try ( AbsCommunicate communicate = this.baseFunction.getTelnetOrComportConnector()) {
            if (communicate == null) {
                return false;
            }
            return runTest(communicate);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }

    private boolean runTest(AbsCommunicate communicate) {
        List<String> commands = this.config.getJsonList(COMMAND);
        if (commands.isEmpty()) {
            addLog("ERROR", "Commands is empty!");
            return false;
        }
        String readUntil = this.config.getString(READ_UNTIL);
        String spec = this.config.getString(KEY_WORD);
        Integer time = this.config.getInteger(TIME);
        if (time == null) {
            addLog("Config", "Time == null !!");
            return false;
        }
        for (String command : commands) {
            addLog("Config", "Waiting for about %s s", time);
            if (!this.baseFunction.sendCommand(communicate, command)
                    || !this.analysisBase.isResponseContainKeyAndShow(communicate, spec, readUntil, new TimeS(time))) {
                if (communicate instanceof Telnet telnet) {
                    try ( Telnet a = this.baseFunction.getTelnet(telnet.getHost(), 23)) {

                    } catch (IOException ex) {
                        addLog(ERROR, ex.getMessage());
                    }
                }
                return false;
            }
        }
        String limitType = this.config.getLimit_type();
        if (limitType != null && limitType.equalsIgnoreCase(MyConst.CONFIG.MATCH)) {
            setResult(spec);
        }
        return true;
    }

    @Override
    protected void createConfig(FunctionConfig config) {
        config.put(COMMAND, List.of("stress --cpu 8 --io 4 --vm 2 --vm-bytes 128M --timeout 1800s"));
        config.put(KEY_WORD, "successful run completed in");
        config.put(READ_UNTIL, "root@eero-test:/#");
        config.put(TIME, 2200);
    }
    private static final String TIME = "Time";
    private static final String READ_UNTIL = "ReadUntil";
    private static final String KEY_WORD = "keyWord";
    private static final String COMMAND = "command";

}
