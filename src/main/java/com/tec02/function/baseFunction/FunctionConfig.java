/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.baseFunction;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.tec02.configuration.model.itemTest.IItemConfig;
import com.tec02.configuration.model.itemTest.ItemConfig;
import com.tec02.configuration.model.itemTest.ItemLimit;
import com.tec02.function.FunctionFactory;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class FunctionConfig extends ItemLimit implements IItemConfig {

    private final ItemConfig itemConfig;

    public void setTime_out(int time_out) {
        this.itemConfig.setTime_out(time_out);
    }
    
    public void setItemName(String itemName) {
        this.itemConfig.setItemName(itemName);
    }

    public void setRetry(int retry) {
        this.itemConfig.setRetry(retry);
    }

    public void setMulti(boolean multi) {
        this.itemConfig.setMulti(multi);
    }

    public void setCheck_spec(boolean check_spec) {
        this.itemConfig.setCheck_spec(check_spec);
    }

    public void setWait_multi_done(boolean wait_multi_done) {
        this.itemConfig.setWait_multi_done(wait_multi_done);
    }

    public void setFail_continue(boolean fail_continue) {
        this.itemConfig.setFail_continue(fail_continue);
    }

    public void setDebugCancellCheckSpec(boolean debugCancellCheckSpec) {
        this.itemConfig.setDebugCancellCheckSpec(debugCancellCheckSpec);
    }

    public void setModeRun(int modeRun) {
        this.itemConfig.setModeRun(modeRun);
    }

    public void setDebugCancellRun(boolean debugCancellRun) {
        this.itemConfig.setDebugCancellRun(debugCancellRun);
    }

    public void setFunction(String function) {
        this.itemConfig.setFunction(function);
    }

    public void setBonus(JSONObject bonus) {
        this.itemConfig.setBonus(bonus);
    }

    public void setAlwaysRun(boolean b) {
        this.itemConfig.setAlwaysRun(b);
    }

    public FunctionConfig(ItemConfig ItemConfig) {
        this.itemConfig = ItemConfig;
    }

    @Override
    public int getTime_out() {
        return this.itemConfig.getTime_out();
    }

    @Override
    public int getRetry() {
        return this.itemConfig.getRetry();
    }

    @Override
    public boolean isMulti() {
        return this.itemConfig.isMulti();
    }

    @Override
    public boolean isCheck_spec() {
        return this.itemConfig.isCheck_spec();
    }

    @Override
    public boolean isWait_multi_done() {
        return this.itemConfig.isWait_multi_done();
    }

    @Override
    public boolean isFail_continue() {
        return this.itemConfig.isFail_continue();
    }

    @Override
    public boolean isDebugCancellCheckSpec() {
        return this.itemConfig.isDebugCancellCheckSpec();
    }

    @Override
    public boolean isDebugCancellRun() {
        return this.itemConfig.isDebugCancellRun();
    }

    @Override
    public String getFunction() {
        return this.itemConfig.getFunction();
    }

    @Override
    public JSONObject getBonus() {
        return this.itemConfig.getBonus();
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

    public JSONObject getJSONObject(String key) {
        return getJSONObject(key, null);
    }

    public JSONArray getJSONArray(String key) {
        return getJSONArray(key, null);
    }

    public JSONObject getJSONObject(String key, JSONObject defaulVal) {
        JSONObject value = getBonus().getJSONObject(key);
        if (value == null) {
            return defaulVal;
        }
        return value;
    }

    public JSONArray getJSONArray(String key, JSONArray defaulVal) {
        JSONArray value = getBonus().getJSONArray(key);
        if (value == null) {
            return defaulVal;
        }
        return value;
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
        return this.itemConfig.isAlwaysRun();
    }

    public void merge(Class configClass) {
        if (configClass == null) {
            return;
        }
        merge(configClass.getSimpleName());
    }

    public void merge(String configClassName) {
        FunctionConfig config = FunctionFactory.getInstance().getDefaultConfigOfFunction(configClassName);
        if (config == null) {
            return;
        }
        this.itemConfig.getBonus().putAll(config.getBonus());
    }

    public void merge(FunctionConfig defaultConfig) {
        if (defaultConfig == null) {
            return;
        }
        this.itemConfig.getBonus().putAll(defaultConfig.getBonus());
    }

    public void setFailApiName(String name) {
        this.itemConfig.setFailApiName(name);
    }

    @Override
    public String getFailApiName() {
        return this.itemConfig.getFailApiName();
    }

    @Override
    public String getItemName() {
        return this.itemConfig.getItemName();
    }

}
