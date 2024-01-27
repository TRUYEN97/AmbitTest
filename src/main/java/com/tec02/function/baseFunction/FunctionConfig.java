/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.baseFunction;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tec02.configuration.model.itemTest.IItemConfig;
import com.tec02.configuration.model.itemTest.ItemConfig;
import com.tec02.configuration.model.itemTest.ItemLimit;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class FunctionConfig extends ItemLimit implements IItemConfig{
    
    private final ItemConfig ItemConfigClass;
    
     public void setTime_out(int time_out) {
         this.ItemConfigClass.setTime_out(time_out);
    }

    public void setRetry(int retry) {
        this.ItemConfigClass.setRetry(retry);
    }

    public void setMulti(boolean multi) {
        this.ItemConfigClass.setMulti(multi);
    }

    public void setCheck_spec(boolean check_spec) {
        this.ItemConfigClass.setCheck_spec(check_spec);
    }

    public void setWait_multi_done(boolean wait_multi_done) {
        this.ItemConfigClass.setWait_multi_done(wait_multi_done);
    }

    public void setFail_continue(boolean fail_continue) {
        this.ItemConfigClass.setFail_continue(fail_continue);
    }

    public void setDebugCancellCheckSpec(boolean debugCancellCheckSpec) {
        this.ItemConfigClass.setDebugCancellCheckSpec(debugCancellCheckSpec);
    }

    public void setDebugCancellRun(boolean debugCancellRun) {
      this.ItemConfigClass.setDebugCancellRun(debugCancellRun);
    }

    public void setFunction(String function) {
        this.ItemConfigClass.setFunction(function);
    }

    public void setBonus(JSONObject bonus) {
       this.ItemConfigClass.setBonus(bonus);
    }
    
    public void setAlwaysRun(boolean b) {
       this.ItemConfigClass.setAlwaysRun(b);
    }

    public FunctionConfig(ItemConfig ItemConfigClass) {
        this.ItemConfigClass = ItemConfigClass;
    }

    @Override
    public int getTime_out() {
        return this.ItemConfigClass.getTime_out();
    }

    @Override
    public int getRetry() {
        return this.ItemConfigClass.getRetry();
    }

    @Override
    public boolean isMulti() {
        return this.ItemConfigClass.isMulti();
    }

    @Override
    public boolean isCheck_spec() {
        return this.ItemConfigClass.isCheck_spec();
    }

    @Override
    public boolean isWait_multi_done() {
        return this.ItemConfigClass.isWait_multi_done();
    }

    @Override
    public boolean isFail_continue() {
        return this.ItemConfigClass.isFail_continue();
    }

    @Override
    public boolean isDebugCancellCheckSpec() {
        return this.ItemConfigClass.isDebugCancellCheckSpec();
    }

    @Override
    public boolean isDebugCancellRun() {
        return this.ItemConfigClass.isDebugCancellRun();
    }

    @Override
    public String getFunction() {
        return this.ItemConfigClass.getFunction();
    }

    @Override
    public JSONObject getBonus() {
        return this.ItemConfigClass.getBonus();
    }
    
    @Override
    public String toString() {
        return test_name;
    }

    public <T> T get(String key, T defaultVal) {
        Object val = getBonus().get(key);
        if (val == null) {
            return defaultVal;
        }
        return (T) val;
    }

    public <T> T get(String key) {
        return get(key, null);
    }

    public String getString(String key) {
        return getBonus().getString(key);
    }

    public Integer getInteger(String key) {
        return getBonus().getInteger(key);
    }

    public void put(String key, Object val) {
        getBonus().put(key, val);
    }

    public <T> List<T> getJsonList(String key) {
        return getJsonList(key, null);
    }

    public <T> List<T> getJsonList(String key, List<T> defaultVal) {
        List<T> result = new ArrayList<>();
        JSONArray array;
        try {
            array = getBonus().getJSONArray(key);
        } catch (Exception e) {
            return defaultVal;
        }
        if (array == null) {
            return defaultVal;
        }
        for (Object elem : array) {
            if (elem != null) {
                result.add((T) elem);
            }
        }
        return result;
    }

    public String getString(String key, String dfVal) {
        return get(key, dfVal);
    }

    public int getInteger(String key, int dfVal) {
        return get(key, dfVal);
    }

    @Override
    public boolean isAlwaysRun() {
        return this.ItemConfigClass.isAlwaysRun();
    }

    public void merge(FunctionConfig defaultConfig) {
        if(defaultConfig == null){
            return;
        }
        this.ItemConfigClass.getBonus().putAll(defaultConfig.getBonus());
    }
    
}
