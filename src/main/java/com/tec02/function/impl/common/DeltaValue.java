/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.function.AbsFunction;
import com.tec02.function.IFunctionModel;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;

/**
 *
 * @author Administrator
 */
public class DeltaValue extends AbsFunction{

    public DeltaValue(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        String itemName = this.config.getString("item");
        addLog("CONFIG", "item: %s", itemName);
        String itemName1 = this.config.getString("item1");
        addLog("CONFIG", "item1: %s", itemName1);
        Double value;
        Double value1;
        if ((value = getValueOf(itemName)) == null || (value1 = getValueOf(itemName1)) == null) {
            return false;
        }
        double delta = value1 - value;
        addLog("PC", "Delta: %s - %s = %s", value1, value, delta);
        setResult(delta);
        return true;
    }
    
    private Double getValueOf(String itemName) {
        IFunctionModel itemTest = this.dataCell.getFunction(itemName);
        if (itemTest == null) {
            addLog("PC", "item %s = null", itemName);
            return null;
        }
        String value = itemTest.getModel().getTest_value();
        if (analysisBase.isNumber(value)) {
            return Double.valueOf(value);
        }
        return null;
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setTime_out(100);
        config.put("item", "");
        config.put("item1", "");
    }
    
}
