/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function;

import com.tec02.configuration.controller.ConfigurationManagement;
import com.tec02.configuration.model.dhcp.DhcpDto;
import com.tec02.configuration.model.itemTest.ItemTestDto;
import com.tec02.configuration.model.setting.SettingDto;
import com.tec02.configuration.model.socket.SocketDto;
import com.tec02.function.baseFunction.FunctionLogger;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.dataCell.DataCell;
import com.tec02.view.managerUI.UICell;
import lombok.Getter;

/**
 *
 * @author Administrator
 */
@Getter
public class Absbase {

    protected final static String DUT = "DUT";
    protected final static String GOLDEN = "GOLDEN";
    protected final static String PC = "PC";
    protected final static String CONFIG = "CONFIG";
    protected final static String ERROR = "ERROR";
    protected final static String COMPORT_LOGKEY = "COMPORT";
    protected final static String CMD = "CMD";
    protected final ItemTestDto itemTestDto;
    protected final SettingDto settingDto;
    protected final DhcpDto dhcpDto;
    protected final SocketDto socketDto;
    protected FunctionLogger logger;
    protected DataCell dataCell;
    protected UICell uICell;

    protected Absbase(FunctionConstructorModel constructorModel){
        ConfigurationManagement management = ConfigurationManagement.getInstance();
        this.itemTestDto = management.getItemTestConfig().getModel();
        this.dhcpDto = management.getDhcpConfig().getModel();
        this.settingDto = management.getSettingConfig().getModel();
        this.socketDto = management.getSocketConfig().getModel();
        if (constructorModel != null) {
            this.uICell = constructorModel.getUICell();
            if (this.uICell != null) {
                this.dataCell = this.uICell.getDataCell();
            }
        }
    }

    public void setLogger(FunctionLogger logger) {
        if (logger == null) {
            this.logger = logger;
        } else {
            this.logger.replace(logger);
        }
    }

    protected void add(String log) {
        this.logger.add(log);
    }

    protected void addLog(Object log) {
        this.logger.addLog(log);
    }

    protected void addLog(String key, Object log) {
        this.logger.addLog(key, log);
    }

    protected void addLog(String format, Object... param) {
        this.logger.addLog(format, param);
    }

    protected void addLog(String key, String format, Object... param) {
        this.logger.addLog(key, format, param);
    }

}
