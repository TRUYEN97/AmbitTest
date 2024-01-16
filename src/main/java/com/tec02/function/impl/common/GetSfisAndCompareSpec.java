/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;

/**
 *
 * @author Administrator
 */
public class GetSfisAndCompareSpec extends AbsFunction{

    @Override
    protected boolean test() {
        String key = this.config.get("key");
        addLog(CONFIG, "key: %s", key);
        if(key == null){
            return false;
        }
        setFunctionSpec(this.config.get("compare"));
        setResult(this.dataCell.get(key));
        return true;
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setTime_out(60);
        config.get("key", "");
        config.get("compare", null);
    }
    
}
