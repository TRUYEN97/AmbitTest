/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.communication.Communicate.Impl.Telnet.Telnet;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;

/**
 *
 * @author Administrator
 */
public class DutTelnet extends AbsFunction {

    public DutTelnet(FunctionConstructorModel constructorModel) {
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
            setResult(ip);
            try ( Telnet telnet = this.baseFunction.getTelnet(ip, 23)) {
                return telnet != null;
            }
        } catch (Exception ex) {
            addLog(ERROR, ex.getLocalizedMessage());
            return false;
        }
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setRetry(1);
        config.setTime_out(60);
        config.put("IP", "192.168.1.1");
    }

}
