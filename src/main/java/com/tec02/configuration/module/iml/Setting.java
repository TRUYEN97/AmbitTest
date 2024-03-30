/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module.iml;

import com.tec02.configuration.model.setting.SettingDto;
import com.tec02.configuration.module.AbsModule;
import com.tec02.configuration.module.view.setting.SettingPanel;

/**
 *
 * @author Administrator
 */
public class Setting extends AbsModule<SettingDto, SettingDto, SettingPanel> {

    public Setting(SettingDto settingDto){
        super(settingDto, settingDto, new SettingPanel());
    }

    @Override
    public void execute() {
    }

    public boolean isMultiUICell() {
        return model.getColumn() > 1 || model.getRow() > 1;
    }

    public String getStation() {
        return model.getStation();
    }
    
    public String getLine(){
        return model.getLine();
    }

    public String getProduct(){
        return model.getProduct();
    }

    public String getLocal_log() {
        return model.getLocal_log();
    }

    public boolean isShowMissingErrorcode() {
        return model.isShowMissingErrorocode();
    }
}
