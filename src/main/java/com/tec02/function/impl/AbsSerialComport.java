/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl;

import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.BaseFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;

/**
 *
 * @author Administrator
 */
public abstract class AbsSerialComport extends AbsFunction{

    public AbsSerialComport(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }
    
    public AbsSerialComport(FunctionConstructorModel constructorModel, int functionType) {
        super(constructorModel, functionType);
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.put(BaseFunction.BAUDRATE, 115200);
        config.put(BaseFunction.COMPORT, 1);
    }
    
    protected abstract void createConfig(FunctionConfig config);
    
}
