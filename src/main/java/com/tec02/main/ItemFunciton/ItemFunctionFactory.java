/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.main.ItemFunciton;

import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionFactory;
import com.tec02.main.dataCell.DataCell;
import com.tec02.view.managerUI.UICell;
import lombok.NonNull;

/**
 *
 * @author Administrator
 */
public class ItemFunctionFactory implements IItemFunction{
    
    
    private final FunctionFactory functionFactory;
    private final UICell uICell;
    private final DataCell dataCell;

    @NonNull
    public ItemFunctionFactory(UICell cell) {
        this.functionFactory = FunctionFactory.getInstance();
        this.uICell = cell;
        this.dataCell = cell.getDataCell();
    }
    
    
    @Override
    public AbsFunction getFunction(String functionName, String itemName, String limitName, Integer begin) {
        AbsFunction absFunction = this.functionFactory.getFunction(functionName);
        if (absFunction == null) {
            return null;
        }
        absFunction.setConfigName(itemName, limitName, begin);
        absFunction.updateConfig();
        absFunction.setuICell(uICell);
        this.dataCell.addItemFunction(absFunction);
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
