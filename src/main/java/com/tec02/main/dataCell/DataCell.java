/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.main.dataCell;

import com.alibaba.fastjson.JSONObject;
import com.tec02.Time.TimeBase;
import com.tec02.common.DataWareHouse;
import com.tec02.common.MyConst;
import com.tec02.common.MyObjectMapper;
import com.tec02.common.PcInformation;
import com.tec02.configuration.controller.ConfigurationManagement;
import com.tec02.function.AbsFunction;
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
    private final TimeBase timeBase;
    private long startTime;
    private Color failColor;
    private Color testColor;

    public DataCell(UICell uICell) {
        this.itemFunctions = new ArrayList<>();
        this.itemCounts = new HashMap<>();
        this.wareHouse = new DataWareHouse();
        this.wareHouse.putkeyMap(MyConst.MODEL.MLBSN, MyConst.MODEL.SERIAL);
        this.wareHouse.putkeyMap(MyConst.MODEL.PCNAME, MyConst.MODEL.STATION_NAME);
        this.wareHouse.putkeyMap(MyConst.MODEL.STATION, MyConst.MODEL.STATION_TYPE);
        this.uICell = uICell;
        this.timeBase = new TimeBase();
    }

    public void reset() {
        this.itemCounts.clear();
        this.itemFunctions.clear();
        this.startTime = 0;
        initWarehouse();
    }

    private void initWarehouse() {
        this.wareHouse.clear();
        ConfigurationManagement configurationManagement = ConfigurationManagement.getInstance();
        JSONObject model = MyObjectMapper.convertValue(configurationManagement.getSettingConfig().getModel(), JSONObject.class);
        wareHouse.putAll(model);
        String mode = ModeManagement.getInsatace().getModeFlow().getAPIMode();
        wareHouse.put(MyConst.MODEL.MODE, mode);
        wareHouse.put(MyConst.MODEL.PCNAME, PcInformation.getInstance().getPcName());
        wareHouse.put(MyConst.MODEL.POSITION, this.uICell.getName());
        wareHouse.put(MyConst.MODEL.SOFTWARE_VERSION, Core.getInstance().getSoftwareVersion());
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

    public synchronized String getNextItemName(String itemName, Integer begin) {
        Integer count;
        String simpleName = itemName.toLowerCase();
        if ((count = this.itemCounts.get(simpleName)) != null) {
            if (begin == null || begin < count) {
                this.itemCounts.put(simpleName, count + 1);
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
                this.itemCounts.put(simpleName, begin +1);
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
        if (itemFunctions.isEmpty()) {
            return "Ready";
        }
        for (AbsFunction itemFunction : itemFunctions) {
            mess = itemFunction.getMessage();
            itemName = itemFunction.getConfig().getTest_name();
            if (isFailed(itemFunction)) {
                errorcode = itemFunction.getModel().getErrorcode();
                builder.append(String.format("%s: \"%s\" %s\r\n",
                        itemName,
                        mess == null ? "" : mess,
                        errorcode));
            } else if (mess != null && !mess.isBlank()) {
                builder.append(String.format("%s: \"%s\"\r\n",
                        itemName,
                        mess));
            }
        }
        if (builder.isEmpty()) {
            if (isPass()) {
                return "PASS";
            } else {
                return "FAIL";
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
        String status = wareHouse.getString(MyConst.MODEL.STATUS, MyConst.MODEL.FAIL);
        return status != null && !status.equalsIgnoreCase(MyConst.MODEL.FAIL);
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
        if (key == null) {
            return null;
        }
        if (key.equals(MyConst.MODEL.STATUS)) {
            return isPass() ? MyConst.MODEL.PASS : MyConst.MODEL.FAIL;
        }
        return get(key);
    }

    public String getTestmode() {
        return get(MyConst.MODEL.MODE_NAME);
    }
    
    public String getAPImode() {
        return get(MyConst.MODEL.MODE);
    }

    public String getString(String key, String defaultVal) {
        return get(key, defaultVal);
    }

    public long getCycleTestTime() {
        return this.wareHouse.getLong(MyConst.MODEL.CYCLE_TIOME);
    }

    public AbsFunction getFirtFailItem() {
        for (AbsFunction itemFunction : itemFunctions) {
            if (isFailed(itemFunction)) {
                return itemFunction;
            }
        }
        return null;
    }

    public void updateResultTest() {
        AbsFunction function = getFirtFailItem();
        if (function != null) {
            var mode = function.getModel();
            this.wareHouse.put(MyConst.MODEL.ERRORCODE, mode.getErrorcode());
            this.wareHouse.put(MyConst.MODEL.ERROR_CODE, mode.getError_code());
            this.wareHouse.put(MyConst.MODEL.ERRORDES, mode.getDescErrorcde());
            this.wareHouse.put(MyConst.MODEL.STATUS, MyConst.MODEL.FAIL);
        } else {
            this.wareHouse.put(MyConst.MODEL.STATUS, MyConst.MODEL.PASS);
        }
        this.wareHouse.put(MyConst.MODEL.CYCLE_TIOME, String.valueOf(System.currentTimeMillis() - startTime));
        this.wareHouse.put(MyConst.MODEL.FINISH_TIME, timeBase.getSimpleDateTime());
    }

    public void start(String input, String modeName, String modeAPI) {
        this.uICell.getDataCell().putData(MyConst.MODEL.SN, input);
                    this.uICell.getDataCell().putData(MyConst.MODEL.MODE_NAME, modeName);
                    this.uICell.getDataCell().putData(MyConst.MODEL.MODE,modeAPI);
        this.wareHouse.put(MyConst.MODEL.START_TIME, timeBase.getSimpleDateTime());
        wareHouse.put(MyConst.MODEL.ON_SFIS, modeAPI.equalsIgnoreCase(MyConst.CONFIG.DEBUG) ? "off" : "on");
        this.startTime = System.currentTimeMillis();
    }

}
