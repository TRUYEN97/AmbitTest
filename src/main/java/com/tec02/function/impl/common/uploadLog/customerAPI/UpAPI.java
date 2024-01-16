/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.uploadLog.customerAPI;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.Impl.Cmd.Cmd;
import com.tec02.configuration.controller.ConfigurationManagement;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FileBaseFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.common.uploadLog.CreateJsonApi;
import com.tec02.function.impl.common.uploadLog.CreateTxt;
import com.tec02.function.impl.common.uploadLog.ZipFile;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class UpAPI extends AbsFunction {

    private final CreateJsonApi jsonApi;
    private final CreateTxt createTxt;
    private final ZipFile zipFile;
    private final FileBaseFunction fileBaseFunction;

    public UpAPI() {
        this.createTxt = new CreateTxt(logger, config);
        this.zipFile = new ZipFile(logger, config);
        this.jsonApi = new CreateJsonApi(logger, config);
        this.fileBaseFunction = new FileBaseFunction(logger, config);
    }

    @Override
    protected boolean test() {
        List<String> prefixElem = this.config.getJsonList("LocalPrefix");
        List<String> jsonElem = this.config.getJsonList("LocaljsonName");
        List<String> txtElem = this.config.getJsonList("LocalTxtName");
        String prefix = this.fileBaseFunction.createName(prefixElem);
        String jsonName = this.fileBaseFunction.createName(jsonElem);
        String jsonPath = String.format("%s/%s.json", prefix, jsonName);
        String txtPath = String.format("%s/%s.txt", prefix, this.fileBaseFunction.createName(txtElem));
        String zipPath = txtPath != null ? txtPath.replaceAll(".txt", ".zip") : null;
        Cmd cmd = new Cmd();
        String command = this.config.getString("Command");
        if (retry > 0) {
            ConfigurationManagement.getInstance().getItemTestConfig().execute();
            var limits = ConfigurationManagement.getInstance().getItemTestConfig().getModel().getLimits();
            for (AbsFunction itemFunction : dataCell.getItemFunctions()) {
                itemFunction.updateConfig();
                if (itemFunction.isDone() && (limits.containsKey(itemFunction.getConfig().getTest_name())
                        || limits.containsKey(itemFunction.getBaseItem()))) {
                    itemFunction.checkResult();
                }
            }
        }
        if (isCreateJsonOk(jsonPath)
                && isCreateTxtOk(txtPath)
                && isCreateZipOk(zipPath, txtPath)) {
            String spec = config.getString("Spec");
            int time = this.config.getInteger("Time", 10);
            addLog(PC, "Waiting for API reponse about %s S", time);
            for (int i = 0; i < 3; i++) {
                if (this.baseFunction.sendCommand(cmd, command + jsonName)) {
                    String response = cmd.readAll(new TimeS(time));
                    addLog("Cmd", response);
                    if (response.trim().endsWith(spec)) {
                        return true;
                    }
                }
            }
        }

        return false;
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
            return this.jsonApi.test();
        } finally {
            addLog("---------------------------------------------------------------");
        }
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setRetry(3);
        config.setTime_out(60);
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
        config.put("LocalTxtName", List.of("mlbsn", "station_name", "mode", "serial"));
        config.put("Command", "cd python && eero_API_client.py log/");
        config.put("Spec", "200");
    }

}
