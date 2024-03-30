/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.model;

import com.tec02.Time.TimeBase;
import com.tec02.configuration.model.itemTest.ItemLimit;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Administrator
 */
@Setter
@Getter
public class Model extends ItemLimit {

    private String status = "";
    private Long finishtime;
    private Long starttime;
    private String descErrorcde = "";
    private String errorcode = "";
    private String test_value = "";
    private String function = "";


    public String getTest_value() {
        return test_value == null || test_value.isBlank() ? status : test_value;
    }

    public long getCycleTime() {
        if (starttime == null) {
            return 0;
        }
        if (finishtime == null || finishtime <= 0) {
            return System.currentTimeMillis() - starttime;
        }
        return finishtime - starttime;
    }

    public String getFinish_time() {
        if (finishtime == null) {
            return null;
        }
        return new TimeBase().simpleDateTimeFormat(new Date(finishtime), TimeBase.SIMPLE_DATE_TIME);
    }

    public String getStart_time() {
        if (starttime == null) {
            return null;
        }
        return new TimeBase().simpleDateTimeFormat(new Date(starttime), TimeBase.SIMPLE_DATE_TIME);
    }

    public void reset() {
        status = "";
        descErrorcde = "";
        errorcode = "";
        error_code = "";
        test_value = "";
    }
}
