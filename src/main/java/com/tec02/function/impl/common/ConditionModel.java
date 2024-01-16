/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.alibaba.fastjson.JSONObject;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class ConditionModel extends JSONObject {

    public static final String NOT_CONTAIN = "!contain";
    public static final String NOT_START_WITH = "!start_with";
    public static final String NOT_END_WITH = "!end_with";
    public static final String NOT_EQUAL = "!equal";
    public static final String CONTAIN = "contain";
    public static final String START_WITH = "start_with";
    public static final String END_WITH = "end_with";
    public static final String EQUAL = "equal";
    public static final String LARGER = "larger";
    public static final String SMALLER = "smaller";

    public ConditionModel() {
    }

    public ConditionModel(Map<String, Object> map) {
        super(map);
    }

    public ConditionModel(boolean ordered) {
        super(ordered);
    }

    public ConditionModel(int initialCapacity) {
        super(initialCapacity);
    }

    public ConditionModel(int initialCapacity, boolean ordered) {
        super(initialCapacity, ordered);
    }
    
    
    public String getTarget() {
        return this.getString("target");
    }

    public String getMessage() {
        return this.getString("message");
    }

    public String getKey() {
        return this.getString("key");
    }

    public String getType(String defaultVal) {
        String type = getType();
        return type != null ? type : defaultVal;
    }

    public String getType() {
        return this.getString("type");
    }
}
