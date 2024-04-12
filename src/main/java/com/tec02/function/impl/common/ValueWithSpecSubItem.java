/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.common.MyConst;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.CompareValue;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;

/**
 *
 * @author Administrator
 */
public class ValueWithSpecSubItem extends AbsFunction {

    private String value = null;
    private String lowerlimit = "";
    private String upperlimit = "";
    private String limitType = MyConst.CONFIG.BOOL;
    private final CompareValue compareValue;

    public ValueWithSpecSubItem(FunctionConstructorModel constructorModel) {
        super(constructorModel);
        this.compareValue = new CompareValue();
    }

    public void setLowerlimit(String lowerlimit) {
        this.lowerlimit = lowerlimit;
    }

    public void setUpperlimit(String upperlimit) {
        this.upperlimit = upperlimit;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean test() {
        addLog(PC, "value: %s", value);
        if (limitType != null && (limitType.equals(MyConst.CONFIG.LIMIT)
                || limitType.equals(MyConst.CONFIG.MATCH))) {
            addLog(PC, "limit type: %s", limitType);
            addLog(PC, "lower limit: %s", lowerlimit);
            addLog(PC, "upper limit: %s", upperlimit);
        }
        if (this.value == null || this.value.isBlank()) {
            return false;
        }
        setResult(value);
        switch (limitType) {
            case MyConst.CONFIG.BOOL -> {
                return true;
            }
            case MyConst.CONFIG.MATCH -> {
                return this.compareValue.checkMatchType(value, lowerlimit, upperlimit);
            }
            case MyConst.CONFIG.LIMIT -> {
                return this.compareValue.checkLimitType(value,
                        lowerlimit, upperlimit) == CompareValue.TRUE;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
    }

}
