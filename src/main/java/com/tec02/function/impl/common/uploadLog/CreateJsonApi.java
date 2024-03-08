/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.uploadLog;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tec02.common.MyConst;
import com.tec02.configuration.controller.ConfigurationManagement;
import com.tec02.function.AbsBaseFunction;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FileBaseFunction;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.dataCell.DataCell;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Administrator
 */
public class CreateJsonApi extends AbsBaseFunction {

    private final FileBaseFunction fileBaseFunction;
    private String path;
    private boolean getAll = true;

    public CreateJsonApi(FunctionConstructorModel constructorModel) {
        super(constructorModel);
        this.fileBaseFunction = new FileBaseFunction(constructorModel);
    }

    public void setGetAll(boolean getAll) {
        this.getAll = getAll;
    }

    public boolean test() {
        JSONObject root;
        JSONArray tests;
        boolean followLimit = this.config.get("followLimit", true);
        boolean limitErrorCode = this.config.get("limitErrorCode", true);
        addLog(CONFIG, String.format("Follow limit: %s", followLimit));
        addLog(CONFIG, String.format("Use the limit errorcode: %s", limitErrorCode));
        root = getRootJson(limitErrorCode);
        tests = getTestsDataFollowLomit(isPass(root), followLimit, limitErrorCode);
        if (tests == null) {
            return false;
        }
        root.put("tests", tests);
        return this.fileBaseFunction.saveJson(root,
                this.path == null ? this.fileBaseFunction.createDefaultStringPath(this.dataCell.isPass()) : path);
    }

    public void setPath(String path) {
        this.path = path;
    }

    private boolean isPass(JSONObject root) {
        return root.getString(MyConst.SFIS.STATUS).equalsIgnoreCase("passed");
    }

    private JSONObject getRootJson(boolean limitErrorCode) {
        JSONObject root = new JSONObject();
        List<String> keyBases;
        keyBases = config.getJsonList("BaseKeys");
        for (String keyBase : keyBases) {
            addValueTo(root, keyBase, limitErrorCode);
        }
        return root;
    }

    private void addValueTo(Map data, String key, boolean limitErrorCode) {
        String value;
        if (key == null) {
            return;
        }
        if (!limitErrorCode && key.equalsIgnoreCase(MyConst.MODEL.ERROR_CODE)) {
            value = this.dataCell.getString(MyConst.MODEL.ERRORCODE);
        } else {
            value = this.dataCell.getString(key);
        }
        addLog(PC, "Root: %s = %s", key, value);
        data.put(key, value == null ? "" : value);
        addLog(PC, "-----------------------------------------");
    }

    private JSONArray getTestsDataFollowLomit(boolean statusTest, boolean followLimit, boolean isUseLimitErrorCode) {
        JSONArray tests = new JSONArray();
        JSONObject itemTest;
        List<String> testKeys = config.getJsonList("TestKeys");
        var limits = ConfigurationManagement.getInstance().getItemTestConfig().getModel().getLimits();
        if (limits == null) {
            return null;
        }
        List<AbsFunction> itemTestDatas = this.dataCell.getFunctions(DataCell.ALL_ITEM);
        int required;
        for (AbsFunction absFunction : itemTestDatas) {
            String itemName = absFunction.getConfig().getTest_name();
            if (itemName == null || !absFunction.isDone()) {
                continue;
            }
            itemTest = absFunction.getData(testKeys, isUseLimitErrorCode);
            if (!followLimit) {
                if (itemTest == null) {
                    addLog("PC", "ItemTest: %s = null", itemName);
                } else {
                    addLog("PC", "Add to test json: %s", itemName);
                    tests.add(itemTest);
                }
            } else if (checkLimitContain(limits.keySet(), absFunction)) {
                required = limits.get(itemName).getRequired();
                if (itemTest != null) {
                    if (!getAll && required == 2 && absFunction.isCancelled()) {
                        continue;
                    }
                    addLog("PC", "Add to test json: %s", itemName);
                    tests.add(itemTest);
                } else if (required == 1 && statusTest) {
                    addLog("PC", "Missing \"%s\" item test!", itemName);
                    return null;
                }
            }
        }
        return tests;
    }

    private boolean checkLimitContain(Set<String> limitItems, AbsFunction function) {
        return function != null && (limitItems.contains(function.getConfig().getTest_name())
                || limitItems.contains(function.getBaseItem()));
    }

//    private boolean isItemTestNotEnough(Set<String> limitItems, List<ItemTestData> itemTestDatas, Limit limit) {
//        Set<String> itemTestNames = new HashSet<>();
//        for (ItemTestData itemTestData : itemTestDatas) {
//            if (itemTestData == null) {
//                continue;
//            }
//            itemTestNames.add(itemTestData.getItemName());
//        }
//        addLog("PC", String.format("ItemNames: %s", itemTestNames));
//        addLog("PC", String.format("Limit itemNames: %s", limitItems));
//        boolean rs = false;
//        for (String limitItem : limitItems) {
//            if (limit.getItem(limitItem).getInteger(AllKeyWord.CONFIG.REQUIRED) == 1 && !itemTestNames.contains(limitItem)) {
//                addLog("pc", String.format("Missing item: %s", limitItem));
//                rs = true;
//            }
//        }
//        return rs;
//    }
}
