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

//    public String getTooHighErrorcode() {
//        if (tooHighErrorcode == null) {
//            return getErrorcode();
//        }
//        return tooHighErrorcode;
//    }
//
//    public String getErrorcode() {
//        if (errorcode == null) {
//            return "-1";
//        }
//        return errorcode;
//    }
//
//    public String getTooLowErrorcode() {
//        if (tooLowErrorcode == null) {
//            return getErrorcode();
//        }
//        return tooLowErrorcode;
//    }
    @Override
    public String toString() {
        return test_name;
    }

}
