/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.main.dataCell;

import com.tec02.Time.TimeBase;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Administrator
 */
@Setter
@Getter
public class CellModel {
    private String product;
    private String station;
    private String line;
    private String mo;
    private String start_time;
    private String mode;
    private String descErrorcde;
    private String mlbsn;
    private String sn;
    private String ethernetmac;
    private String error_code;
    private String errorcode;
    private String position;
    private String pcname;
    private String test_software_version;

    public String getSerial() {
        return mlbsn;
    }

    public String getSn() {
        return sn;
    }
    
    public String getStation_type() {
        return station;
    }

    public String getDescErrorcde() {
        return descErrorcde;
    }
    
    public String getStation_name() {
        return pcname;
    }

    public String getFinish_time() {
        return new TimeBase().getSimpleDateTime();
    }
    
}
