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
public class GetSfisAndCompareSpec extends AbsFunction {

    public GetSfisAndCompareSpec(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        String key = this.config.get("key");
        addLog(CONFIG, "key: %s", key);
        if (key == null) {
            return false;
        }
        String value = this.dataCell.get(key);
        addLog(PC, "Value: %s", value);
        String spec = this.config.get("compare");
        if (spec != null && !spec.isBlank()) {
            setFunctionSpec(spec);
        }
        setResult(value);
        return true;
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setTime_out(60);
        config.put("key", "");
        config.put("compare", "");
    }

}
