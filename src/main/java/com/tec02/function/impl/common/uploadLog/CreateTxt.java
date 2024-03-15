/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.uploadLog;

import com.tec02.common.MyConst;
import com.tec02.function.AbsBaseFunction;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FileBaseFunction;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import com.tec02.main.dataCell.DataCell;
import com.tec02.mylogger.MyLogger;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class CreateTxt extends AbsBaseFunction {

    private final FileBaseFunction fileBaseFunction;
    private String path;

    public CreateTxt(FunctionConstructorModel constructorModel) {
        super(constructorModel);
        this.fileBaseFunction = new FileBaseFunction(constructorModel);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean saveTxtFile() {
        addLog("Save file txt!");
        try {
            MyLogger loger = new MyLogger();
            String logPath = this.path == null ? this.fileBaseFunction.createDefaultStringPath(this.dataCell.isPass()) : path;
            addLog("DIR", logPath);
            loger.setFile(new File(logPath));
            loger.setSaveMemory(true);
            loger.clear();
            createInfo(loger);
            int id = 0;
            List<String> itemLogs = new ArrayList<>();
            for (AbsFunction dataBox : this.dataCell.getFunctions(DataCell.JUST_PARENT_ITEM)) {
                loger.add(String.format("//////////////////////////- ID[%s] -//////////////////////////\r\n", id++));
                itemLogs.add(dataBox.getModel().getTest_name());
                String log = dataBox.getLogger().getLog();
                loger.add(log == null ? "\r\n" : log);
                loger.add("\r\n//////////////////////////////////////////////////////////////\r\n");
            }
            addLog(PC, "Item logs: \"%s\"", itemLogs);
            addLog(PC, "Save file txt at \"%s\"", logPath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            addLog("Save file txt failed: " + e.getLocalizedMessage());
            ErrorLog.addError(this, e.getLocalizedMessage());
            return false;
        }
    }

    private void createInfo(MyLogger loger) throws IOException {
        loger.add("===================================================================\r\n");
        loger.add(String.format("Start at = %s\r\n", this.dataCell.getString(MyConst.MODEL.START_TIME)));
        loger.add(String.format("End test at = %s\r\n", this.dataCell.getString(MyConst.MODEL.FINISH_TIME)));
        loger.add(String.format("Status = %s\r\n", this.dataCell.getString(MyConst.SFIS.STATUS)));
        loger.add(String.format("Version = %s\r\n", this.dataCell.getString(MyConst.MODEL.SOFTWARE_VERSION)));
        if (!this.dataCell.isPass()) {
            loger.add(String.format("Error code = %s\r\n", this.dataCell.getString(MyConst.MODEL.ERROR_CODE)));
            loger.add(String.format("Local error code = %s\r\n", this.dataCell.getString(MyConst.MODEL.ERRORCODE)));
            loger.add(String.format("Error description = %s\r\n", this.dataCell.getString(MyConst.MODEL.ERRORDES)));
        }
        loger.add(String.format("Test time = %.3f s\r\n", this.dataCell.getCycleTestTime() / 1000.0));
        loger.add(String.format("Station = %s\r\n", this.dataCell.getString(MyConst.MODEL.PCNAME)));
        loger.add(String.format("Location = %s\r\n", this.dataCell.getString(MyConst.MODEL.POSITION)));
        loger.add(String.format("HHSN = %s\r\n", this.dataCell.getString(MyConst.SFIS.SN)));
        loger.add(String.format("DEVICESN = %s\r\n", this.dataCell.getString(MyConst.SFIS.MLBSN)));
        loger.add("===================================================================\r\n");
    }

}
