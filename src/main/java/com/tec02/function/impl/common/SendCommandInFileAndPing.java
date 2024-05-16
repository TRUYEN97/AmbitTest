/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.Impl.Telnet.Telnet;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.BaseFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class SendCommandInFileAndPing extends AbsFunction {

    public SendCommandInFileAndPing(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        try ( Telnet telnet = this.baseFunction.getTelnet()) {
            String filename = this.config.getString(FILE_NAME);
            addLog(CONFIG, "file name: %s", filename);
            File file;
            if (filename == null || filename.isBlank() || !(file = new File(filename)).exists()) {
                addLog(PC, "file \"%s\" not exist!", filename);
                return false;
            }
            String readUntil = this.config.getString(READ_UNTIL);
            int time = this.config.getInteger(TIME, 10);
            if (telnet == null) {
                return false;
            }
            List<String> commands = Files.readAllLines(file.toPath());
            for (String command : commands) {
                if (!this.baseFunction.sendCommand(telnet, command)) {
                    return false;
                }
                if (this.analysisBase.readShowUntil(telnet, readUntil, new TimeS(time)) == null) {
                    return false;
                }
            }
            try ( Telnet t = this.baseFunction.getTelnet()) {
                return t != null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            addLog(ERROR, ex.getLocalizedMessage());
            return false;
        }
    }
    private static final String READ_UNTIL = "ReadUntil";
    private static final String FILE_NAME = "fileName";
    private static final String TIME = "Time";

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setRetry(2);
        config.setTime_out(140);
        config.put(BaseFunction.IP, "192.168.1.1");
        config.put(BaseFunction.NON_DHCP, false);
        config.put("keepPing", false);
        config.put(TIME, 120);
        config.put(FILE_NAME, "RUNIN_WIFI_UP.txt");
        config.put(READ_UNTIL, "root@eero-test:/#");
    }

}
