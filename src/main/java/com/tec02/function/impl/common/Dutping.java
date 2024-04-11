/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;

/**
 *
 * @author Administrator
 */
public class Dutping extends AbsFunction {

    public Dutping(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        String ip = this.baseFunction.getIp();
        addLog("IP: " + ip);
        if (ip == null) {
            return false;
        }
        boolean type = this.config.get("keepPing", false);
        addLog(CONFIG, "keep Ping: %s", type);
        int timePing = config.getInteger("time_ping", 120);
        setResult(ip);
        return this.baseFunction.pingTo(ip, timePing, !type);
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setRetry(1);
        config.setTime_out(140);
        config.put("IP", "192.168.1.1");
        config.put("nonDHCP", false);
        config.put("keepPing", false);
        config.put("time_ping", 120);
    }
}
