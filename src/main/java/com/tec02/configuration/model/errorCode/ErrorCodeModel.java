/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.model.errorCode;

import java.util.HashMap;
import java.util.Map;
import lombok.Setter;

/**
 *
 * @author Administrator
 */
@Setter
public class ErrorCodeModel {
    private String dir;
    private Map<String, ItemErrorCode> errorcodes = new HashMap<>();

    public String getDir() {
        return dir;
    }

    public Map<String, ItemErrorCode> getErrorcodes() {
        return errorcodes;
    }
    
}
