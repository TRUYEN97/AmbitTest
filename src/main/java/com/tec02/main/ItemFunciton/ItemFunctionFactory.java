/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.main.ItemFunciton;

import com.tec02.function.AbsBaseFunction;
import com.tec02.function.AbsFunction;
import com.tec02.function.FunctionFactory;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.view.managerUI.UICell;
import javax.swing.JOptionPane;

/**
 *
 * @author Administrator
 */
public class ItemFunctionFactory {

    private static volatile ItemFunctionFactory instance;
    private final FunctionFactory functionFactory;

    public static ItemFunctionFactory getInsatance() {
        ItemFunctionFactory ins = ItemFunctionFactory.instance;
        if (ins == null) {
            synchronized (ItemFunctionFactory.class) {
                ins = ItemFunctionFactory.instance;
                if (ins == null) {
                    ItemFunctionFactory.instance = ins = new ItemFunctionFactory();
                }
            }
        }
        return ins;
    }

    private ItemFunctionFactory() {
        this.functionFactory = FunctionFactory.getInstance();
    }

    public synchronized AbsFunction getFunction(String functionName,
            FunctionConstructorModel constructorModel, UICell uICell, boolean addToDataCell) {
        if (constructorModel == null) {
            constructorModel = FunctionConstructorModel.builder().build();
        }
        AbsFunction absFunction = getFunction(functionName, constructorModel);
        if(absFunction == null){
            return null;
        }
        absFunction.updateConfigAndResetModel();
        if (uICell != null && uICell.getDataCell() != null && addToDataCell) {
            uICell.getDataCell().addItemFunction(absFunction);
        }
        return absFunction;
    }

    public synchronized AbsBaseFunction getBaseFunction(String functionName, FunctionConstructorModel constructorModel) {
        AbsBaseFunction absFunction = this.functionFactory.getBaseFunction(functionName, constructorModel);
        if (absFunction == null) {
            JOptionPane.showMessageDialog(null,
                    String.format("Base-Func: \"%s\", Base-function not exists!",
                            functionName));
            return null;
        }
        return absFunction;
    }

    public AbsFunction getFunction(String functionName, UICell uICell, String configName, String limitName, Integer begin, boolean addToDataCell) {
        FunctionConstructorModel constructorModel = FunctionConstructorModel.builder()
                .uICell(uICell)
                .limitName(limitName)
                .configName(configName)
                .begin(begin)
                .build();
        return getFunction(functionName, constructorModel, uICell, addToDataCell);
    }

    public AbsFunction getFunction(String functionName, UICell uICell, String configName, Integer begin, boolean addToDataCell) {
        return getFunction(functionName, uICell, configName, configName, begin, addToDataCell);
    }

    public AbsFunction getFunction(String functionName, FunctionConstructorModel constructorModel) {
        AbsFunction absFunction = this.functionFactory.getFunction(functionName, constructorModel);
        return absFunction;
    }
}
