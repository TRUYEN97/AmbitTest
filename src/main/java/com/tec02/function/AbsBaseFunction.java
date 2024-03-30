/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function;

import com.tec02.configuration.model.itemTest.ItemConfig;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.baseFunction.FunctionLogger;
import com.tec02.function.model.Model;
import com.tec02.function.model.FunctionConstructorModel;

/**
 *
 * @author Administrator
 */
public class AbsBaseFunction extends Absbase {
    
    protected FunctionConfig config;
    
    protected AbsBaseFunction(FunctionConstructorModel constructorModel) {
        super(constructorModel);
        if (constructorModel != null && constructorModel.getConfig() != null) {
            this.config = constructorModel.getConfig();
        } else {
            this.config = new FunctionConfig(new ItemConfig());
        }
        if (constructorModel != null && constructorModel.getLogger() != null) {
            this.logger = constructorModel.getLogger();
        } else {
            this.logger = new FunctionLogger(new Model());
        }
    }
}
