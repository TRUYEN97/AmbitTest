/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;

/**
 *
 * @author Administrator
 */
public class ValueSubItem extends AbsFunction {

    private String value = null;

    public ValueSubItem(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean test() {
        addLog(PC, "value: %s", value);
        if (this.value == null || this.value.isBlank()) {
            return false;
        }
        setResult(value);
        return true;
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
    }


}
