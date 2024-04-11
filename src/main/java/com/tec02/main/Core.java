/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.main;

import com.tec02.configuration.controller.ConfigurationManagement;
import com.tec02.view.managerUI.UICellManagement;
import com.tec02.view.managerUI.UICell;
import javax.swing.JOptionPane;

/**
 *
 * @author Administrator
 */
public class Core {

    private static volatile Core instance;
    private final UICellManagement cellUIManager;
    private final ConfigurationManagement configurationManagement;
    private String softwareVsersion = "1.0.0";
    private String appName = "AmbitUI";
    private int updateStatus = 0;

    private Core() {
        this.configurationManagement = ConfigurationManagement.getInstance();
        this.cellUIManager = UICellManagement.getInstance();
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public static Core getInstance() {
        Core ins = Core.instance;
        if (ins == null) {
            synchronized (Core.class) {
                ins = Core.instance;
                if (ins == null) {
                    Core.instance = ins = new Core();
                }
            }
        }
        return ins;
    }

    public void setSoftwareVsersion(String softwareVsersion) {
        this.softwareVsersion = softwareVsersion;
    }

    public void setInput(String input) {
        String index = "";
        if (cellUIManager.isMultiUi()) {
            index = JOptionPane.showInputDialog(null,
                    String.format("Input index for \"%s\"", input));
        }
        setInput(input, index);
    }
    
    public boolean updateAvailable(){
        return this.updateStatus != 0 && cellUIManager.isNotTest();
    }

    public void setInput(String input, String index) {
        if (input == null) {
            return;
        }
        if (index == null) {
            index = "";
        }
        input = input.trim();
        index = index.trim();
        if (this.cellUIManager.isIndexFree(index)) {
            UICell uICell = this.cellUIManager.getUICell(index);
            uICell.setInput(input);
        }
    }

    public boolean isMultiUICell() {
        return this.configurationManagement.getSettingConfig().isMultiUICell();
    }

    public String getStationName() {
        return this.configurationManagement.getSettingConfig().getStation();
    }

    public String getLineName() {
        return this.configurationManagement.getSettingConfig().getLine();
    }

    public String getSoftwareVersion() {
        return softwareVsersion;
    }

    public String getAppName() {
        return appName;
    }

    public void setUpdateStatus(int i) {
        this.updateStatus = i < 0 ? 0 : i;
    }

    public int getUpdateStatus() {
        return updateStatus;
    }
}
