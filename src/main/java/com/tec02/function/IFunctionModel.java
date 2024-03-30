/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tec02.function;

import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.baseFunction.FunctionLogger;
import com.tec02.function.model.Model;



/**
 *
 * @author Administrator
 */
public interface IFunctionModel {

    Model getModel();
    
    boolean isSubItem();
    
    boolean isElementFucntion();

    boolean isWaiting();

    boolean isTesting();

    boolean isDone();

    int getStatusCode();

    FunctionConfig getConfig();
    
    FunctionLogger getLogger();

    boolean isPass();
    
    String getFunctionName();

    Long getRunTime();
}
