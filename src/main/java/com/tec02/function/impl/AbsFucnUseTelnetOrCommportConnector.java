/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl;

import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.function.baseFunction.BaseFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;

/**
 *
 * @author Administrator
 */
public abstract class AbsFucnUseTelnetOrCommportConnector extends AbsSerialComport {

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
        super.createDefaultConfig(config);
        config.put(BaseFunction.TYPE, "!comport");
        config.put(BaseFunction.IP, "192.168.1.1");
        config.put(BaseFunction.NON_DHCP, false);
        createConfig(config);
    }


}
