/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.main.dataCell;

import com.alibaba.fastjson2.JSONObject;
import com.tec02.Time.TimeBase;
import com.tec02.common.Common;
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

/**
 *
 * @author Administrator
 */
public class DataCell {

    private final List<AbsFunction> allFunctions;
    private final List<AbsFunction> failedFunctions;
    private final Map<String, Integer> itemCounts;
    private final DataWareHouse wareHouse;
    private final UICell uICell;
    private final TimeBase timeBase;
    private long startTime;
    private Color failColor;
    private Color testColor;

    public DataCell(UICell uICell) {
        this.allFunctions = new ArrayList<>();
        this.failedFunctions = new ArrayList<>();
        this.itemCounts = new HashMap<>();
        this.wareHouse = new DataWareHouse();
        this.wareHouse.putkeyMap(MyConst.MODEL.SN, MyConst.MODEL.HHSN);
        this.wareHouse.putkeyMap(MyConst.MODEL.MLBSN, MyConst.MODEL.SERIAL);
        this.wareHouse.putkeyMap(MyConst.MODEL.PCNAME, MyConst.MODEL.STATION_NAME);
        this.wareHouse.putkeyMap(MyConst.MODEL.STATION, MyConst.MODEL.STATION_TYPE);
        this.uICell = uICell;
        this.timeBase = new TimeBase();
        initWarehouse();
    }

    public void reset() {
        this.itemCounts.clear();
        this.allFunctions.clear();
        this.failedFunctions.clear();
        this.startTime = 0;
        this.failColor = null;
        this.testColor = null;
        initWarehouse();
    }

    private void initWarehouse() {
        this.wareHouse.clear();
        ConfigurationManagement configurationManagement = ConfigurationManagement.getInstance();
        JSONObject model = MyObjectMapper.convertValue(configurationManagement.getSettingConfig().getModel(), JSONObject.class);
        wareHouse.putAll(model);
        var modelFlow = ModeManagement.getInsatace().getModeFlow();
        String mode = "";
        if (modelFlow != null) {
            mode = modelFlow.getAPIMode();
        }
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
        if (itemName == null) {
            return null;
        }
        String baseItemName = Common.getBaseItem(itemName);
        String baseItemNameToLowerCase = baseItemName.toLowerCase();
        begin = Common.checkBeginNumber(itemName, begin);
        Integer count = this.itemCounts.get(baseItemNameToLowerCase);
        if (count != null) {
            if (begin != null && begin >= count) {
                this.itemCounts.put(baseItemNameToLowerCase, begin);
                return String.format("%s_%s", baseItemName, begin);
            } else {
                return String.format("%s_%s", baseItemName, count);
            }
        } else {
            if (begin != null && begin >= 0) {
                this.itemCounts.put(baseItemNameToLowerCase, begin);
                return String.format("%s_%s", baseItemName, begin);
            }
        }
        return itemName;
    }

    public void addItemFunction(AbsFunction absFunction) {
        if (absFunction == null) {
            return;
        }
        String testItemName = absFunction.getBaseItem().trim().toLowerCase();
        Integer count = 0;
        if (this.itemCounts.containsKey(testItemName)
                && (count = this.itemCounts.get(testItemName)) != null) {
            this.itemCounts.put(testItemName, count + 1);
        } else {
            this.itemCounts.put(testItemName, 0);
        }
        this.allFunctions.add(absFunction);
    }

    public void addFailedItemFunction(AbsFunction function) {
        this.failedFunctions.add(function);
    }

    public List<AbsFunction> getFunctions(int funcType) {
        List<AbsFunction> functions = new ArrayList<>();
        if (funcType < ALL_ITEM) {
            funcType = ALL_ITEM;
        } else if (funcType > JUST_PARENT_ITEM) {
            funcType = JUST_PARENT_ITEM;
        }
        if (funcType == ALL_ITEM) {
            return allFunctions;
        }
        for (AbsFunction absFunction : allFunctions) {
            if (funcType == JUST_SUB_ITEM && absFunction.isSubItem()) {
                functions.add(absFunction);
            } else if (funcType == JUST_PARENT_ITEM && !absFunction.isSubItem()) {
                functions.add(absFunction);
            }
        }
        return functions;
    }

    public boolean isTesting() {
        return uICell.isTesting();
    }
    public static final int JUST_PARENT_ITEM = 2;
    public static final int JUST_SUB_ITEM = 1;
    public static final int ALL_ITEM = 0;

    public String getMassage() {
        StringBuilder builder = new StringBuilder();
        String itemName;
        String mess;
        if (allFunctions.isEmpty()) {
            return "Ready";
        }
        if (isPass()) {
            if (getAPImode().equalsIgnoreCase(MyConst.CONFIG.DEBUG)) {
                builder.append(MyConst.CONFIG.DEBUG.toUpperCase());
                builder.append(" ");
            }
            builder.append("PASS\r\n");
        } else if (getFirtFailItem() != null) {
            var fail = getFirtFailItem().getModel();
            builder.append(String.format("%s: \"%s\"\r\n",
                    fail.getTest_name(),
                    fail.getErrorcode()));
        }
        for (AbsFunction itemFunction : allFunctions) {
            mess = itemFunction.getMessage();
            if (mess != null && !mess.isBlank()) {
                itemName = itemFunction.getConfig().getTest_name();
                builder.append(String.format("%s: \"%s\"\r\n",
                        itemName,
                        mess));
            }
        }
        return builder.toString().trim();
    }

    public String getErrorCode() {
        if (failedFunctions.isEmpty()) {
            return "";
        }
        return failedFunctions.get(0).getModel().getErrorcode();
    }

    public boolean isFailed(AbsFunction itemFunction) {
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

    public List<AbsFunction> getFailedFunctions() {
        return failedFunctions;
    }

    public String getString(String key) {
        if (key == null) {
            return null;
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
        return this.wareHouse.getLong(MyConst.MODEL.CYCLE_TIME);
    }

    public AbsFunction getFirtFailItem() {
        if (failedFunctions.isEmpty()) {
            return null;
        }
        return failedFunctions.get(0);
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
        this.wareHouse.put(MyConst.MODEL.CYCLE_TIME, String.valueOf(System.currentTimeMillis() - startTime));
        this.wareHouse.put(MyConst.MODEL.FINISH_TIME, timeBase.getSimpleDateTime());
    }

    public void start(String input, String modeName, String modeAPI) {
        this.uICell.getDataCell().putData(MyConst.MODEL.SN, input);
        this.uICell.getDataCell().putData(MyConst.MODEL.MODE_NAME, modeName);
        this.uICell.getDataCell().putData(MyConst.MODEL.MODE, modeAPI);
        this.wareHouse.put(MyConst.MODEL.START_TIME, timeBase.getSimpleDateTime());
        this.wareHouse.put(MyConst.MODEL.ON_SFIS, modeAPI.equalsIgnoreCase(MyConst.CONFIG.DEBUG) ? "off" : "on");
        this.startTime = System.currentTimeMillis();
    }

    public AbsFunction getFunction(String funcName) {
        for (AbsFunction itemFunction : allFunctions) {
            if (itemFunction.getModel().getTest_name().equalsIgnoreCase(funcName)) {
                return itemFunction;
            }
        }
        return null;
    }

    public int size() {
        return this.allFunctions.size();
    }

    public boolean isOnSFIS() {
        return this.getString(MyConst.MODEL.ON_SFIS, "off").equalsIgnoreCase("on");
    }

}
