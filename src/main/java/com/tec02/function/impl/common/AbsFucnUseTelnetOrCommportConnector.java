/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;

/**
 *
 * @author Administrator
 */
public abstract class AbsFucnUseTelnetOrCommportConnector extends AbsFunction {

    protected AbsFucnUseTelnetOrCommportConnector(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    protected AbsFucnUseTelnetOrCommportConnector(FunctionConstructorModel constructorModel, int functionType) {
        super(constructorModel, functionType);
    }

    protected AbsCommunicate getTelnetOrComportConnector() throws Exception {
        return this.baseFunction.getTelnetOrComportConnector();
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.put(TYPE, "!comport");
        config.put(BAUDRATE, 115200);
        config.put(COMPORT1, 4);
        config.put(IP, "192.168.1.1");
        config.put(NON_DHCP, false);
        createConfig(config);
    }
    protected static final String NON_DHCP = "nonDHCP";
    protected static final String IP = "IP";
    protected static final String COMPORT1 = "comport";
    protected static final String BAUDRATE = "baudrate";
    protected static final String TYPE = "type";

    protected abstract void createConfig(FunctionConfig config);

}
