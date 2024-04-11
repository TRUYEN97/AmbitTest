/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.common.MyConst;
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
        String key = this.config.get(KEY);
        boolean isLowerCase = this.config.get(LOWER_CASE, false);
        addLog(CONFIG, "%s: %s", LOWER_CASE, isLowerCase);
        addLog(CONFIG, "%s: %s", KEY, key);
        if (key == null) {
            return false;
        }
        String value = this.dataCell.get(key);
        if (value != null && key.equalsIgnoreCase(MyConst.MODEL.MAC)) {
            value = value.toLowerCase();
        }
        addLog(PC, "Value: %s", value);
        String spec = this.config.get(COMPARE);
        if (spec != null && !spec.isBlank()) {
            setFunctionSpec(spec);
        }
        setResult(value);
        return true;
    }
    protected static final String LOWER_CASE = "isLowerCase";

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setTime_out(60);
        config.put(KEY, "");
        config.put(COMPARE, "");
        config.put(LOWER_CASE, false);
    }
    private static final String COMPARE = "compare";
    private static final String KEY = "key";

}
