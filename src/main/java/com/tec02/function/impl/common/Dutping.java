/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;

/**
 *
 * @author Administrator
 */
public class Dutping extends AbsFunction{

    @Override
    protected boolean test() {
         try {
            String ip = this.baseFunction.getIp();
            addLog("IP: " + ip);
            if (ip == null) {
                return false;
            }
            String type = this.config.getString("type", "check");
            addLog("Error", "type: %s", type);
            int timePing = config.getInteger("time_ping", 120);
            return this.baseFunction.pingTo(ip, timePing, !type.equalsIgnoreCase("keep"));
        } catch (Exception ex) {
            ex.printStackTrace();
            addLog(ERROR, ex.getLocalizedMessage());
            return false;
        }
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setRetry(1);
        config.setTime_out(140);
        config.put("IP", "192.168.1.1");
        config.put("type", "check");
        config.put("time_ping", 120);
    }
    
}
