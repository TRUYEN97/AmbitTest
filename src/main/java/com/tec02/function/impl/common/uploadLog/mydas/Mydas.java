/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.uploadLog.mydas;

import com.tec02.Time.TimeBase;
import com.tec02.common.MyConst;
import com.tec02.common.PcInformation;
import com.tec02.function.AbsFunction;
import com.tec02.function.IFunctionModel;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.dataCell.DataCell;

/**
 *
 * @author Administrator
 */
public class Mydas extends AbsFunction {

    public Mydas(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        String IP = this.config.getString("IP");
        addLog("CONFIG", String.format(String.format("IP: %s", IP)));
        boolean sendDetail = this.config.get("sendDetail", true);
        addLog("CONFIG", String.format(String.format("Send detail: %s", sendDetail)));
        String pcName = PcInformation.getInstance().getPcName();
        addLog("CONFIG", String.format(String.format("PC name: %s", pcName)));
        String dutModel = this.settingDto.getProduct();
        addLog("CONFIG", String.format(String.format("Product: %s", dutModel)));
        String station = this.settingDto.getStation();
        addLog("CONFIG", String.format(String.format("Station: %s", station)));
        String pn = this.dataCell.getString("pnname");
        addLog("CONFIG", String.format(String.format("PN name: %s", pn)));
        String flowVer = this.dataCell.getString("flowVer", "3.0");
        addLog("CONFIG", String.format(String.format("flowVer: %s", flowVer)));
        String titleVer = this.dataCell.getString("titleVer", "3.0");
        addLog("CONFIG", String.format(String.format("titleVer: %s", titleVer)));
        if (retry < 1) {
            this.dataCell.updateResultTest();
        }
        MydasClient mydasClient = new MydasClient(IP, pcName, flowVer, titleVer, dutModel, station, pn);
        return sendMydas(mydasClient, sendDetail);
    }

    private boolean sendMydas(MydasClient mydasClient, boolean sendDetail) {
        try {
            if (mydasClient.connectPts() != 1) {
                addLog("connect PTS (Mydas) failed!");
                return false;
            } else {
                addLog("connect PTS (Mydas) ok!");
                String detail = getDetail(sendDetail);
                String errorInfo = getErrorInfo();
                String mainInfo = getMainInfo();
                addLog(PC, "DETAILINFO=[ %s ]", detail);
                addLog(PC, "ERRORINFO=[ %s ]", errorInfo);
                addLog(PC, "MAININFO=[ %s ]", mainInfo);
                if (sendDatasToMyDas(mydasClient, detail, errorInfo, mainInfo)) {
                    addLog("Send to pts Passed");
                    return true;
                }
                addLog("Send to pts failed....");
                return false;
            }
        } finally {
            addLog("------------mydas----------------");
        }
    }

    private boolean sendDatasToMyDas(MydasClient mydasClient, String detail, String errorInfo, String mainInfo) {
        mydasClient.initClientInfo();
        mydasClient.setData(detail, 0);
        mydasClient.setData(errorInfo, 1);
        mydasClient.setData(mainInfo, 2);
        return mydasClient.sendPTS() != 0;
    }

    private String getErrorInfo() {
        IFunctionModel itemTestData = this.dataCell.getFirtFailItem();
        String errorInfo = "";
        if (itemTestData != null) {
            String errorDes = itemTestData.getModel().getDescErrorcde();
            String location = PcInformation.getInstance().getPcName();
            if (errorDes.contains("dut_ping") && location != null && !location.isBlank()) {
                errorDes = String.format("%s_%s", errorDes, location);
            }
            errorInfo = String.format("%s,%s,%s|", itemTestData.getModel().getErrorcode(),
                    errorDes, itemTestData.getModel().getTest_value());
        }
        return errorInfo;
    }

    private String getDetail(boolean sendDetail) {
        String detail;
        if (sendDetail) {
            detail = getDetaiLItem();
        } else {
            detail = "NA,NA,0;";
        }
        return detail;
    }

    private String getDetaiLItem() {
        StringBuilder builder = new StringBuilder();
        for (IFunctionModel itemTestData : this.dataCell.getFunctions(DataCell.ALL_ITEM)) {
            if (itemTestData == null) {
                continue;
            }
            builder.append(String.format("%s,%s,%.3f;",
                    itemTestData.isPass() ? "PASS" : "FAIL",
                    itemTestData.getModel().getTest_value(),
                    itemTestData.getModel().getCycleTime() / 1000.0));
        }
        return builder.toString();
    }

    private String getMainInfo() {
        StringBuilder builder = new StringBuilder();
        int status = this.dataCell.isPass() ? 1 : 0;
        builder.append(this.dataCell.getString(MyConst.SFIS.MLBSN, "")).append(",");
        builder.append(status).append(",");
        builder.append(this.dataCell.getString("ftppath", "")).append(",");
        builder.append(this.dataCell.getString(MyConst.MODEL.SOFTWARE_VERSION, "")).append(",");
        builder.append(this.dataCell.getString(MyConst.MODEL.PCNAME, "")).append(",");
        builder.append(this.dataCell.getString("cycle_time", "")).append(",");
        builder.append(getFinishTime()).append(",");
        builder.append(",").append(this.uICell.getName()).append(",");
        return builder.toString();
    }

    private String getFinishTime() {
        return new TimeBase().conVertToFormat(this.dataCell.getString(MyConst.MODEL.FINISH_TIME, ""),
                TimeBase.SIMPLE_DATE_TIME, TimeBase.MM_DD_YYYY_HH_MM_SS);

    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setTime_out(60);
        config.setAlwaysRun(true);
        config.put("flowVer", "3.0");
        config.put("titleVer", "3.0");
        config.put("IP", "10.90.100.168");
        config.put("sendDetail", false);
    }

}
