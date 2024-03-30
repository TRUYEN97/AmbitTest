/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.uploadLog.customerAPI;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.Impl.Cmd.Cmd;
import com.tec02.configuration.controller.ConfigurationManagement;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.common.uploadLog.CreateJsonApi;
import com.tec02.function.impl.common.uploadLog.CreateTxt;
import com.tec02.function.impl.common.uploadLog.ZipFile;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import com.tec02.main.dataCell.DataCell;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class UpAPI extends AbsFunction {

    private final CreateJsonApi jsonApi;
    private final CreateTxt createTxt;
    private final ZipFile zipFile;

    public UpAPI(FunctionConstructorModel constructorModel) {
        super(constructorModel);
        this.jsonApi = (CreateJsonApi) getBaseFunction(CreateJsonApi.class);
        this.createTxt = (CreateTxt) getBaseFunction(CreateTxt.class);
        this.zipFile = (ZipFile) getBaseFunction(ZipFile.class);
    }

    @Override
    protected boolean test() {
        try {
            List<String> prefixElem = this.config.getJsonList("LocalPrefix");
            List<String> jsonElem = this.config.getJsonList("LocaljsonName");
            List<String> txtElem = this.config.getJsonList("LocalTxtName");
            String prefix = this.fileBaseFunction.createName(prefixElem);
            String jsonName = this.fileBaseFunction.createName(jsonElem);
            String jsonPath = String.format("%s/%s.json", prefix, jsonName);
            String txtPath = String.format("%s/%s.txt", prefix, this.fileBaseFunction.createName(txtElem));
            String zipPath = txtPath != null ? txtPath.replaceAll(".txt", ".zip") : null;
            String command = this.config.getString("Command");
            if (retry > 0) {
                ConfigurationManagement.getInstance().getItemTestConfig().execute();
                for (AbsFunction itemFunction : dataCell.getFunctions(DataCell.ALL_ITEM)) {
                    if (itemFunction.isDone() && itemFunction.updateLimit()) {
                        itemFunction.checkResult();
                    }
                }
            }
            this.dataCell.updateResultTest();
            if (isCreateJsonOk(jsonPath)
                    && isCreateTxtOk(txtPath)
                    && isCreateZipOk(zipPath, txtPath)) {
                String spec = config.getString("Spec");
                int time = this.config.getInteger("Time", 10);
                addLog(PC, "Waiting for API reponse about %s S", time);
                try ( Cmd cmd = new Cmd()) {
                    for (int i = 0; i < 3; i++) {
                        if (this.baseFunction.sendCommand(cmd,
                                String.format("%s%s", command, jsonName))) {
                            String response = cmd.readAll(new TimeS(time));
                            addLog("Cmd", response);
                            if (response.trim().endsWith(spec)) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }

    private boolean isCreateZipOk(String zipPath, String txtPath) {
        try {
            this.zipFile.setZipPath(zipPath);
            this.zipFile.setTxtpath(txtPath);
            return this.zipFile.saveFileZip();
        } finally {
            addLog("---------------------------------------------------------------");
        }
    }

    private boolean isCreateTxtOk(String txtPath) {
        try {
            this.createTxt.setPath(txtPath);
            return this.createTxt.saveTxtFile();
        } finally {
            addLog("---------------------------------------------------------------");
        }
    }

    private boolean isCreateJsonOk(String jsonPath) {
        try {
            this.jsonApi.setPath(jsonPath);
            this.jsonApi.setGetAll(false);
            return this.jsonApi.test();
        } finally {
            addLog("---------------------------------------------------------------");
        }
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setRetry(3);
        config.put("followLimit", true);
        config.put("limitErrorCode", true);
        config.put("BaseKeys", List.of("error_details", "status", "finish_time",
                "test_software_version", "start_time", "station_type",
                "error_code", "serial", "mode", "station_name", "position"));
        config.put("TestKeys", List.of("upper_limit", "lower_limit", "status", "finish_time", "test_name",
                "error_code", "start_time", "test_value", "units"));
        config.put("LocalPrefix", List.of("python/log"));
        config.put("LocaljsonName", List.of("mlbsn", "station_name", "mode"));
        config.put("LocalPrefix", List.of("python/log"));
        config.put("LocalTxtName", List.of("mlbsn", "station_name", "mode", "{serial}"));
        config.put("Command", "cd python && eero_API_client.py log/");
        config.put("Spec", "200");
    }

}
