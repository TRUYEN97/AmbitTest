/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.alibaba.fastjson2.JSONObject;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class CheckProduct extends AbsFunction {

    public CheckProduct(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        List<Map> elems = this.config.getJsonList("elems");
        if (elems == null || elems.isEmpty()) {
            addLog("PC", "Nothing to check!");
            return true;
        }
        addLog(PC, "key - value - type - target");
        addLog(PC, "......................................");
        ConditionModel md;
        for (Map elem : elems) {
            md = new ConditionModel(elem);
            String key = md.getKey();
            String target = md.getTarget();
            String mess = md.getMessage();
            String type = md.getType(ConditionModel.EQUAL);
            String value = this.dataCell.getString(key);
            addLog(PC, "%s - %s - %s - %s",
                    key, value, type, target);
            if ((value != null || target != null) && !isTrueTarget(value, target, type)) {
                addLog("PC", "Message: %s", mess);
                this.setMessage(mess);
                return false;
            }
            addLog(PC, "......................................");
        }
        addLog("PC", "Check done!");
        return true;
    }

    private boolean isTrueTarget(String value, String targets, String type) {
        if (targets == null || type == null || value == null) {
            return false;
        }
        switch (type) {
            case ConditionModel.CONTAIN -> {
                if (isContain(targets, value)) {
                    return true;
                }
            }
            case ConditionModel.EQUAL -> {
                if (isEqual(targets, value)) {
                    return true;
                }
            }
            case ConditionModel.START_WITH -> {
                if (isStartWith(targets, value)) {
                    return true;
                }
            }
            case ConditionModel.END_WITH -> {
                if (isEndWith(targets, value)) {
                    return true;
                }
            }
            case ConditionModel.LARGER -> {
                return isLarger(value, targets);
            }

            case ConditionModel.SMALLER -> {
                return isSmaller(value, targets);
            }

            case ConditionModel.NOT_CONTAIN -> {
                if (!isContain(targets, value)) {
                    return true;
                }
            }
            case ConditionModel.NOT_EQUAL -> {
                if (!isEqual(targets, value)) {
                    return true;
                }
            }
            case ConditionModel.NOT_START_WITH -> {
                if (!isStartWith(targets, value)) {
                    return true;
                }
            }

            case ConditionModel.NOT_END_WITH -> {
                if (!isEndWith(targets, value)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSmaller(String value, String targets) {
        if (!this.analysisBase.isNumber(value)) {
            addLog(PC, "The value needs to be a number!");
            return false;
        }
        if (!this.analysisBase.isNumber(targets)) {
            addLog(PC, "The target needs to be a number!");
            return false;
        }
        double val = this.analysisBase.string2Double(value);
        double target = this.analysisBase.string2Double(targets);
        return val < target;
    }

    private boolean isLarger(String value, String targets) {
        if (!this.analysisBase.isNumber(value)) {
            addLog(PC, "The value needs to be a number!");
            return false;
        }
        if (!this.analysisBase.isNumber(targets)) {
            addLog(PC, "The target needs to be a number!");
            return false;
        }
        double val = this.analysisBase.string2Double(value);
        double target = this.analysisBase.string2Double(targets);
        return val > target;
    }

    private boolean isEndWith(String targets, String value) {
        for (String spec : targets.split(SEPARATOR)) {
            if (value.trim().endsWith(spec.trim())) {
                return true;
            }
        }
        return false;
    }

    private boolean isStartWith(String targets, String value) {
        for (String spec : targets.split(SEPARATOR)) {
            if (value.trim().startsWith(spec.trim())) {
                return true;
            }
        }
        return false;
    }

    private boolean isEqual(String targets, String value) {
        for (String spec : targets.split(SEPARATOR)) {
            if (value.trim().equalsIgnoreCase(spec.trim())) {
                return true;
            }
        }
        return false;
    }

    private boolean isContain(String targets, String value) {
        for (String spec : targets.split(SEPARATOR)) {
            if (value.contains(spec)) {
                return true;
            }
        }
        return false;
    }
    private static final String SEPARATOR = ",|\\|";

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setTime_out(10);
        config.setBonus(JSONObject.parseObject("""
                                               {
                                               "elems": [
                                                               {
                                                                   "key": "pnname",
                                                                   "target": "810-01100",
                                                                   "type": "equal",
                                                                   "message": "Sản phẩm không phải là: 810-01100!"
                                                               },
                                                               {
                                                                   "key": "smode",
                                                                   "target": "Production",
                                                                   "type": "equal",
                                                                   "message": "Sản phẩm không phải là hàng Production!"
                                                               },
                                                               {
                                                                   "key": "typemodel",
                                                                   "target": "simple|DEV743",
                                                                   "type": "equal",
                                                                   "message": "Sản phẩm không phải là hàng simple!"
                                                               },
                                                               {
                                                                   "key": "ethernetmac",
                                                                   "target": "0",
                                                                   "type": "end_with",
                                                                   "message": "MAC từ SFIS cần phải kết thúc bằng số 0!"
                                                               }
                                                           ]
                                               }
                                               """));
    }

}
