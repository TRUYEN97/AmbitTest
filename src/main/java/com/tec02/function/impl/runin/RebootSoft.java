/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.runin;

import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;

/**
 *
 * @author Administrator
 */
public class RebootSoft extends AbsFunction{

    public RebootSoft(FunctionConstructorModel constructorModel) {
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
            int times = this.config.getInteger("times", 1);
            addLog("Config", "Run test %s times", times);
            for (int i = 0; i < times; i++) {
                addLog(PC, "Times: %s", i + 1);
                if (!cycleReboot(ip)) {
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            addLog(ERROR, ex.getLocalizedMessage());
            return false;
        }
    }
    
    private boolean cycleReboot(String ip) {
        String cmd = this.config.getString("command", "reboot");
        addLog(CONFIG, "command: %s", cmd);
        int waitShutdownTime = this.config.getInteger("waitTime", 10);
        addLog(CONFIG, "Wait for DUT to shut down: %s S", waitShutdownTime);
        int pingTime = this.config.getInteger("pingTimes", 120);
        addLog(CONFIG, "Ping time: %s", pingTime);
        try {
            return this.baseFunction.rebootSoft(ip, cmd, waitShutdownTime, pingTime);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getLocalizedMessage());
            addLog(ERROR, e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setRetry(1);
        config.setTime_out(350);
        config.put("IP", "192.168.1.1");
        config.put("ReadUntil", "root@eero-test:/#");
        config.put("times", 3);
        config.put("waitTime", 40);
        config.put("pingTimes", 300);
    }

    
}
