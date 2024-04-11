/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.controller;

import com.tec02.communication.Communicate.Impl.Cmd.Cmd;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class AutoClickOnScreen extends AbsFunction {

    public AutoClickOnScreen(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        try ( Cmd cmd = new Cmd()) {
            List<String> commands = this.config.getJsonList(COMMANDS, List.of());
            String windowsTitle = this.config.getString(WINDOWS_TITLE, "");
            String dir = this.config.getString(DIR, "");
            String app = this.config.getString(APP, "");
            addLog(CONFIG, "%s: %s", COMMANDS, commands);
            addLog(CONFIG, "%s: %s", WINDOWS_TITLE, windowsTitle);
            addLog(CONFIG, "%s: %s", DIR, dir);
            addLog(CONFIG, "%s: %s", APP, app);
            if (app.isBlank() || windowsTitle.isBlank() || dir.isBlank() || commands.isEmpty()) {
                return false;
            }
            StringBuilder cmdbuilder = new StringBuilder(app);
            cmdbuilder.append(" \"").append(windowsTitle).append("\"");
            cmdbuilder.append(" \"").append(dir).append("\"");
            for (String command : commands) {
                cmdbuilder.append(" \"").append(command).append("\"");
            }
            if (!this.baseFunction.sendCommand(cmd, cmdbuilder.toString())) {
                return false;
            }
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = cmd.readLine()) != null) {
                addLog(CMD, line);
                builder.append(line).append("\r\n");
            }
            return builder.toString().trim().endsWith("Done. passed");
        } catch (Exception e) {
            e.printStackTrace();
            addLog(ERROR, e.getLocalizedMessage());
            return false;
        }
    }
    private static final String APP = "app";
    private static final String DIR = "dir";
    private static final String WINDOWS_TITLE = "windowsTitle";
    private static final String COMMANDS = "commands";

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setRetry(retry);
        config.put(WINDOWS_TITLE, "AnLink");
        config.put(APP, "java -jar AutoClickOnScreen.jar");
        config.put(DIR, "image");
        config.put(COMMANDS, List.of("show"));
    }

}
