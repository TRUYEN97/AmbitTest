/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.main.dataCell;

import com.alibaba.fastjson.JSONObject;
import com.tec02.API.DataWareHouse;
import com.tec02.common.Constanct;
import com.tec02.common.MyObjectMapper;
import com.tec02.common.PcInformation;
import com.tec02.configuration.controller.ConfigurationManagement;
import com.tec02.function.AbsFunction;
import com.tec02.function.IFunctionModel;
import com.tec02.main.Core;
import com.tec02.main.ModeManagement;
import com.tec02.view.managerUI.UICell;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

/**
 *
 * @author Administrator
 */
public class DataCell {

    private final List<AbsFunction> itemFunctions;
    private final Map<String, Integer> itemCounts;
    private final DataWareHouse wareHouse;
    private final UICell uICell;
    private Color failColor;
    private Color testColor;

    public DataCell(UICell uICell) {
        this.itemFunctions = new ArrayList<>();
        this.itemCounts = new HashMap<>();
        this.wareHouse = new DataWareHouse();
        this.uICell = uICell;
    }

    public void reset() {
        this.itemCounts.clear();
        this.itemFunctions.clear();
        initWarehouse();
    }

    private void initWarehouse() {
        this.wareHouse.clear();
        ConfigurationManagement configurationManagement = ConfigurationManagement.getInstance();
        JSONObject model = MyObjectMapper.convertValue(configurationManagement.getSettingConfig().getModel(), JSONObject.class);
        wareHouse.putAll(model);
        wareHouse.put(Constanct.MODEL.MODE, ModeManagement.getInsatace().getModeFlow().getAPIMode());
        wareHouse.put(Constanct.MODEL.PCNAME, PcInformation.getInstance().getPcName());
        wareHouse.put(Constanct.MODEL.POSITION, this.uICell.getName());
        wareHouse.put(Constanct.MODEL.SOFTWARE_VERSION, Core.getInstance().getSoftwareVersion());
    }

    public void putData(String key, Object value) {
        this.wareHouse.put(key, value);
    }

    public <T> T get(String key, T defaultVal) {
        Object val = this.wareHouse.get(key);
        if (val == null) {
            return defaultVal;
        }
        return (T) val;
    }

    public <T> T get(String key) {
        return get(key, null);
    }

    public <T> T getModel(Class<T> clazz) {
        return MyObjectMapper.convertValue(wareHouse.toJson(), clazz);
    }

    public String getNextItemName(String itemName, Integer begin) {
        Integer count;
        String simpleName = itemName.toLowerCase();
        if ((count = this.itemCounts.get(simpleName)) != null) {
            if (begin == null || begin < count) {
                return String.format("%s_%s", itemName, count);
            } else {
                this.itemCounts.put(simpleName, begin);
                return String.format("%s_%s", itemName, begin);
            }
        } else {
            if (begin == null || begin < 0) {
                this.itemCounts.put(simpleName, 0);
                return itemName;
            } else {
                this.itemCounts.put(simpleName, begin);
                return String.format("%s_%s", itemName, begin);
            }
        }
    }

    @NonNull
    public void addItemFunction(AbsFunction absFunction) {
        this.itemFunctions.add(absFunction);
    }

    public List<AbsFunction> getItemFunctions() {
        return itemFunctions;
    }

    public String getMassage() {
        StringBuilder builder = new StringBuilder();
        String itemName;
        String mess;
        String errorcode;
        for (AbsFunction itemFunction : itemFunctions) {
            mess = itemFunction.getMessage();
            if (mess != null && !mess.isBlank()) {
                itemName = itemFunction.getConfig().getTest_name();
                if (isFailed(itemFunction)) {
                    errorcode = itemFunction.getModel().getErrorcode();
                    builder.append(String.format("%s: \"%s\" %s\r\n",
                            itemName,
                            mess,
                            errorcode));
                } else {
                    builder.append(String.format("%s: \"%s\"\r\n",
                            itemName,
                            mess));
                }
            }
        }
        return builder.toString().trim();
    }

    public String getErrorCode() {
        for (AbsFunction itemFunction : itemFunctions) {
            if (isFailed(itemFunction)) {
                return itemFunction.getModel().getErrorcode();
            }
        }
        return "";
    }

    private boolean isFailed(AbsFunction itemFunction) {
        return itemFunction.isDone() && !itemFunction.isPass();
    }

    public Color getResultColor() {
        if (isPass()) {
            return Color.GREEN;
        }
        return failColor == null ? Color.red : failColor;
    }

    public void setFailColor(Color failColor) {
        if (this.failColor != null) {
            return;
        }
        this.failColor = failColor;
    }

    public boolean isPass() {
        if (itemFunctions.isEmpty()) {
            return false;
        }
        for (AbsFunction itemFunction : itemFunctions) {
            if (isFailed(itemFunction)) {
                return false;
            }
        }
        return true;
    }

    public void setTestColor(Color testColor) {
        this.testColor = testColor;
    }

    public Color getTestColor() {
        return failColor == null ? Color.YELLOW : testColor;
    }

    public List<AbsFunction> getTestingItemFunctions() {
        List<AbsFunction> functionTestings = new ArrayList<>();
        for (AbsFunction itemFunction : itemFunctions) {
            if (!itemFunction.isDone()) {
                functionTestings.add(itemFunction);
            }
        }
        return functionTestings;
    }

    public AbsFunction getFunction(String functionName) {
        for (AbsFunction itemFunction : itemFunctions) {
            if (itemFunction.getModel().getTest_name().equalsIgnoreCase(functionName)) {
                return itemFunction;
            }
        }
        return null;
    }

    public String getString(String key) {
        return get(key);
    }

    public String getAPImode() {
        return get(Constanct.MODEL.MODE);
    }

    public String getString(String key, String defaultVal) {
        return get(key, defaultVal);
    }

    public long getCycleTestTime() {
        return 0;
    }

    public AbsFunction getFirtFailItem() {
        for (AbsFunction itemFunction : itemFunctions) {
            if (isFailed(itemFunction)) {
                return itemFunction;
            }
        }
        return null;
    }

}
