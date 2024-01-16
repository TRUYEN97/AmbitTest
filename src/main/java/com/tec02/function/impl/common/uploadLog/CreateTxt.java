/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.uploadLog;

import com.tec02.common.Constanct;
import com.tec02.function.AbsBaseFunction;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FileBaseFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.baseFunction.FunctionLogger;
import com.tec02.main.ErrorLog;
import com.tec02.mylogger.MyLogger;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Administrator
 */
public class CreateTxt extends AbsBaseFunction {

    private final FileBaseFunction fileBaseFunction;
    private String path;

    public CreateTxt(FunctionLogger logger, FunctionConfig config) {
        super(logger, config);
        this.fileBaseFunction = new FileBaseFunction(logger, config);
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
            for (AbsFunction dataBox : this.dataCell.getItemFunctions()) {
                loger.add(String.format("//////////////////////////- ID[%s] -//////////////////////////\r\n", id++));
                addLog(PC, " - add item: " + dataBox.getFunctionName());
                String log = dataBox.getLogger().getLog();
                loger.add(log == null ? "\r\n" : log);
                loger.add("//////////////////////////////////////////////////////////////\r\n");
            }
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
        loger.add(String.format("Start at = %s\r\n", this.dataCell.getString(Constanct.MODEL.START_TIME)));
        loger.add(String.format("End test at = %s\r\n", this.dataCell.getString(Constanct.MODEL.FINISH_TIME)));
        loger.add(String.format("Status = %s\r\n", this.dataCell.getString(Constanct.SFIS.STATUS)));
        loger.add(String.format("Version = %s\r\n", this.dataCell.getString(Constanct.MODEL.SOFTWARE_VERSION)));
        if (!this.dataCell.isPass()) {
            loger.add(String.format("Error code = %s\r\n", this.dataCell.getString(Constanct.MODEL.ERROR_CODE)));
            loger.add(String.format("Error des = %s\r\n", this.dataCell.getString(Constanct.MODEL.ERROR_DES)));
            loger.add(String.format("Local error code = %s\r\n", this.dataCell.getString(Constanct.MODEL.ERRORCODE)));
            loger.add(String.format("Local error des = %s\r\n", this.dataCell.getString(Constanct.MODEL.ERRORDES)));
        }
        loger.add(String.format("Test time = %.3f s\r\n", this.dataCell.getCycleTestTime()));
        loger.add(String.format("Station = %s\r\n", this.dataCell.getString(Constanct.MODEL.PCNAME)));
        loger.add(String.format("Location = %s\r\n", this.dataCell.getString(Constanct.MODEL.POSITION)));
        loger.add(String.format("HHSN = %s\r\n", this.dataCell.getString(Constanct.SFIS.SN)));
        loger.add(String.format("DEVICESN = %s\r\n", this.dataCell.getString(Constanct.SFIS.MLBSN)));
        loger.add("===================================================================\r\n");
    }

}
