/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.main.ItemFunciton;

import com.tec02.function.AbsFunction;
import com.tec02.function.FunctionFactory;
import com.tec02.main.dataCell.DataCell;
import com.tec02.view.managerUI.UICell;
import lombok.NonNull;

/**
 *
 * @author Administrator
 */
public class ItemFunctionFactory implements IItemFunction {

    private final FunctionFactory functionFactory;
    private final UICell uICell;

    @NonNull
    public ItemFunctionFactory(UICell cell) {
        this.functionFactory = FunctionFactory.getInstance();
        this.uICell = cell;
    }

    @Override
    public synchronized AbsFunction getFunction(String functionName, String itemName, String limitName, Integer begin) {
        AbsFunction absFunction = this.functionFactory.getFunction(functionName);
        if (absFunction == null) {
            throw new RuntimeException(
                    String.format("Func: \"%s\" - \"%s\", function not exists!",
                            functionName, itemName));
        }
        absFunction.setUICell(uICell);
        absFunction.setConfigName(itemName, limitName, begin);
        absFunction.updateConfig();
        absFunction.setFunctionManagement(this);
        return absFunction;
    }

    @Override
    public AbsFunction getFunction(String functionName, String itemName, Integer begin) {
        return getFunction(functionName, itemName, itemName, begin);
    }

    @Override
    public AbsFunction getFunction(String functionName, String itemName, String limitName) {
        return getFunction(functionName, itemName, limitName, null);
    }

    @Override
    public AbsFunction getFunction(String functionName, String itemName) {
        return getFunction(functionName, itemName, itemName, null);
    }
}
