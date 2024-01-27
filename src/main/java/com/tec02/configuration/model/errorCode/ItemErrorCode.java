/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.model.errorCode;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Administrator
 */
@Getter
@Setter
public class ItemErrorCode {

    private String test_name;
    private String desc_errorcode = "";
    private String errorcode = "";
    private String desc_tooLowErrorcode = "";
    private String tooLowErrorcode = "";
    private String desc_tooHighErrorcode = "";
    private String tooHighErrorcode = "";

    public String getErrorcode() {
        if ((errorcode == null || errorcode.isBlank()) && test_name != null) {
            return test_name.replaceAll("_", ".").toUpperCase();
        }
        return errorcode;
    }

    public String getDesc_tooHighErrorcode() {
        if (desc_tooHighErrorcode == null || desc_tooHighErrorcode.isBlank()) {
            return getDesc_errorcode().concat("_TOOHIGH");
        }
        return desc_tooHighErrorcode;
    }

    public String getDesc_tooLowErrorcode() {
        if (desc_tooLowErrorcode == null || desc_tooLowErrorcode.isBlank()) {
            return getDesc_errorcode().concat("_TOOLOW");
        }
        return desc_tooLowErrorcode;
    }

    public String getDesc_errorcode() {
        return desc_errorcode == null || desc_errorcode.isBlank() ? test_name : desc_errorcode;
    }

    public String getTooHighErrorcode() {
        if (tooHighErrorcode == null || tooHighErrorcode.isBlank()) {
            return getErrorcode().concat(".H");
        }
        return tooHighErrorcode;
    }

    public String getTooLowErrorcode() {
        if (tooLowErrorcode == null || tooLowErrorcode.isBlank()) {
            return getErrorcode().concat(".L");
        }
        return tooLowErrorcode;
    }

    @Override
    public String toString() {
        return test_name;
    }

}
