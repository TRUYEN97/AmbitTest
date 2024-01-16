/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function;

import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.baseFunction.FunctionLogger;


/**
 *
 * @author Administrator
 */
public class AbsBaseFunction extends Absbase {

    protected FunctionConfig config;

    protected AbsBaseFunction(FunctionLogger logger, FunctionConfig config) {
        this.logger = logger;
        this.config = config;
    }
}
