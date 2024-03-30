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
public class StopMultiTask extends AbsFunction {

    public StopMultiTask(FunctionConstructorModel constructorModel) {
        super(constructorModel, -1);
    }

    @Override
    protected boolean test() {
        String key = this.config.get("key");
        addLog(CONFIG, "Key: %s", key);
        if (key != null && !key.isBlank()) {
            addLog(PC, "put(%s,true)", key);
            this.dataCell.putData(key, true);
        }
        return true;
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.put("key", "");
    }


}
