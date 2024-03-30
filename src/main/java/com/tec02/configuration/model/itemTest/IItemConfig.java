/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tec02.configuration.model.itemTest;

import com.alibaba.fastjson2.JSONObject;


/**
 *
 * @author Administrator
 */
public interface IItemConfig {

    int getTime_out();

    int getRetry();
    
    String getTest_name();
    
    String getItemName();

    boolean isMulti();

    boolean isCheck_spec();
    
    boolean isAlwaysRun();

    boolean isWait_multi_done();

    boolean isFail_continue();

    boolean isDebugCancellCheckSpec();

    boolean isDebugCancellRun();

    String getFunction();
    
    String getFailApiName();

    JSONObject getBonus();
}
