/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.model.itemTest;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Administrator
 */
@Getter
@Setter
public class ItemConfig implements IItemConfig {

    private int time_out = Integer.MAX_VALUE;
    private int retry = 0;
    private int modeRun = 3;
    private boolean multi = false;
    private boolean check_spec = true;
    private boolean wait_multi_done = false;
    private boolean wait_local_multi_done = false;
    private boolean fail_continue = false;
    private boolean debugCancellCheckSpec = false;
    private boolean debugCancellRun = false;
    private boolean alwaysRun = false;
    private boolean stopAllMutitack = false;
    private boolean stopLocalMutitack = false;
    private String function = "";
    private String test_name = "";
    private String failApiName = "";
    private String itemName = "";
    private String limit_type = "BOOL";
    private String lower_limit = "";
    private String upper_limit = "";
    private JSONObject bonus = new JSONObject();

    public int getModeRun() {
        if (modeRun < 1) {
            modeRun = 1;
        } else if (modeRun > 3) {
            modeRun = 3;
        }
        return modeRun;
    }

    @Override
    public String getTest_name() {
        if(test_name == null || test_name.isBlank()){
            return itemName;
        }
        return test_name;
    }
    

    @Override
    public String toString() {
        return itemName;
    }

}
