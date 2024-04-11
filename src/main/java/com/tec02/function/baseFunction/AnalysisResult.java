/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.baseFunction;

import com.tec02.function.model.Model;
import com.tec02.common.Common;
import com.tec02.common.MyConst;
import com.tec02.configuration.model.errorCode.ItemErrorCode;
import lombok.NonNull;

/**
 *
 * @author Administrator
 */
public class AnalysisResult {

    private final FunctionConfig config;
    private final Model model;
    private final ItemErrorCode errorCode;

    @NonNull
    public AnalysisResult(FunctionConfig config, Model model, ItemErrorCode errorCode) {
        this.config = config;
        this.model = model;
        this.errorCode = errorCode;
    }

    public void checkResult(boolean status) {
        String stt = this.model.getStatus();
        if (stt != null && stt.equalsIgnoreCase(MyConst.MODEL.CANCELLED)) {
            setCancelled();
            return;
        }
        String value = this.model.getTest_value();
        if (!status) {
            setSimpleErrorcode();
        } else if (isSpecAvailable() && isCheckWithSpec()) {
            checkResultWithLimits(value);
        } else {
            setPass();
        }
    }

    private void checkResultWithLimits(String StringResult) {
        if (StringResult == null || StringResult.isBlank()) {
            setSimpleErrorcode();
            return;
        }
        switch (config.getLimit_type()) {
            case MyConst.CONFIG.MATCH -> {
                if (!checkMatchType(StringResult)) {
                    setSimpleErrorcode();
                } else {
                    setPass();
                }
            }
            case MyConst.CONFIG.LIMIT -> {
                checkLimitType(StringResult);
            }
            default -> {
                setSimpleErrorcode();
            }
        }
    }

    private void setCancelled() {
        this.model.setDescErrorcde("");
        this.model.setError_code("");
        this.model.setErrorcode("");
        this.model.setStatus(MyConst.MODEL.CANCELLED);
    }

    private void setPass() {
        this.model.setDescErrorcde("");
        this.model.setError_code("");
        this.model.setErrorcode("");
        this.model.setStatus(MyConst.MODEL.PASS);
    }

    private void setSimpleErrorcode() {
        String error_code = config.getError_code();
        String errorcode = this.errorCode.getErrorcode();
        String descError = this.errorCode.getDesc_errorcode();
        setErrorCode(errorcode, error_code, descError);
    }

    private void setErrorCode(String errorcode, String error_code, String descError) {
        String itemName = Common.getBaseItem(this.model.getTest_name());
        Integer number = Common.getOrderNumberItem(this.model.getTest_name());
        if (errorcode == null || errorcode.isBlank()) {
            errorcode = itemName.replaceAll("\\-", ".")
                    .replaceAll("\\_", ".")
                    .replaceAll("\\s", ".");
        }
        if (descError == null || descError.isBlank()) {
            descError = itemName.replaceAll("\\-", "_")
                    .replaceAll("\\s", "_");
        }
        if (number != null && number >= 0) {
            setError(String.format("%S.%S", errorcode, number),
                    error_code, String.format("%S_%S", descError, number));
        } else {
            setError(errorcode, error_code, descError);
        }
    }

    private void setError(String errorcode, String error_code, String descError) {
        if (errorcode == null) {
            errorcode = "-1";
        }
        if (error_code == null) {
            error_code = errorcode;
        }
        this.model.setError_code(error_code);
        this.model.setErrorcode(errorcode);
        this.model.setDescErrorcde(descError);
        this.model.setStatus(MyConst.MODEL.FAIL);
    }

    private void setTooLowErrorcode() {
        String error_code = config.getError_code();
        String errorcode = this.errorCode.getTooLowErrorcode();
        String descError = this.errorCode.getDesc_tooLowErrorcode();
        setErrorCode(errorcode, error_code, descError);
    }

    private void setTooHighErrorcode() {
        String error_code = config.getError_code();
        String errorcode = this.errorCode.getTooHighErrorcode();
        String descError = this.errorCode.getDesc_tooHighErrorcode();
        setErrorCode(errorcode, error_code, descError);
    }

    private boolean checkMatchType(String result) {
        try {
            if (getMatch(result, config.getLower_limit())) {
                return true;
            }
            return getMatch(result, config.getUpper_limit());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean getMatch(String result, String configSpec) {
        if (result == null) {
            return false;
        }
        result = result.trim();
        configSpec = configSpec.trim();
        if (result.equals(configSpec)) {
            return true;
        }
        String[] limits = configSpec.split("\\|");
        if (limits != null && limits.length > 0) {
            for (String spec : limits) {
                if (spec.equals(result)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private Double cvtString2Num(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void checkLimitType(String result) {
        Double upper = cvtString2Num(config.getUpper_limit());
        Double lower = cvtString2Num(config.getLower_limit());
        Double value = cvtString2Num(result);
        if ((upper == null && lower == null) || value == null) {
            setSimpleErrorcode();
            return;
        }
        if (lower == null) {
            if (aGreatThanB(value, upper)) {
                setTooHighErrorcode();
                return;
            }
        } else if (upper == null) {
            if (aGreatThanB(lower, value)) {
                setTooLowErrorcode();
                return;
            }
        } else {
            if (aGreatThanB(value, upper)) {
                setTooHighErrorcode();
                return;
            }
            if (aGreatThanB(lower, value)) {
                setTooLowErrorcode();
                return;
            }
        }
        setPass();
    }

    private boolean isRequired(int num) {
        return config.getRequired() == num;
    }

    private static boolean aGreatThanB(Double a, Double b) {
        if (a == null) {
            return false;
        }
        return a.compareTo(b) >= 1;
    }

    private boolean isSpecAvailable() {
        String lowLimit = config.getLower_limit();
        String upperLimit = config.getUpper_limit();
        return (lowLimit != null && !lowLimit.isBlank())
                || (upperLimit != null && !upperLimit.isBlank());
    }

    private boolean isCheckWithSpec() {
        Integer required = config.getRequired();
        String limitType = config.getLimit_type();
        return required > 0 && (limitType != null && !limitType.isBlank());
    }
}
