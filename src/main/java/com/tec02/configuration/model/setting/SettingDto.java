/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.model.setting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Administrator
 */
@Setter
@Getter
@NoArgsConstructor
public class SettingDto {
    private String product;
    private String station;
    private String line;
    private String local_log = "log";
    private String view;
    private boolean showMissingErrorocode = true;
    private int row = 1;
    private int column = 1;
}
