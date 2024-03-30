/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.uploadLog.teAPI;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.Impl.Cmd.Cmd;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.common.uploadLog.CreateJsonApi;
import com.tec02.function.impl.common.uploadLog.CreateTxt;
import com.tec02.function.model.FunctionConstructorModel;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class UpTeAPI extends AbsFunction {

    private final CreateJsonApi jsonApi;
    private final CreateTxt createTxt;

    public UpTeAPI(FunctionConstructorModel constructorModel) {
        super(constructorModel);
        this.jsonApi = (CreateJsonApi) getBaseFunction(CreateJsonApi.class);
        this.createTxt = (CreateTxt) getBaseFunction(CreateTxt.class);
    }

    @Override
    protected boolean test() {
        if (retry < 1) {
            this.dataCell.updateResultTest();
        }
        List<String> prefixElem = this.config.getJsonList("LocalPrefix");
        List<String> jsonElem = this.config.getJsonList("LocaljsonName");
        List<String> txtElem = this.config.getJsonList("LocalTxtName");
        String prefix = this.fileBaseFunction.createName(prefixElem);
        String jsonName = String.format("%s.json", this.fileBaseFunction.createName(jsonElem));
        String txtName = String.format("%s.txt", this.fileBaseFunction.createName(txtElem));
        String jsonPath = String.format("%s/%s", prefix, jsonName);
        String txtPath = String.format("%s/%s", prefix, txtName);
        Cmd cmd = new Cmd();
        String command = this.config.getString("Command");
        if (isCreateJsonOk(jsonPath)
                && isCreateTxtOk(txtPath)
                && this.baseFunction.sendCommand(cmd,
                        String.format("%s \"log/%s\" \"log/%s\"",
                                command, jsonName, txtName))) {
            String spec = config.getString("Spec");
            int time = this.config.getInteger("Time", 10);
            addLog(PC, "Waiting for API reponse about %s S", time);
            String response = cmd.readAll(new TimeS(time));
            addLog(CMD, response);
            return response.trim().endsWith(spec);
        }
        return false;
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
        config.setAlwaysRun(true);
        config.setRetry(1);
        config.put("followLimit", false);
        config.put("limitErrorCode", false); 
        config.put("BaseKeys", List.of("error_details", "status", "finish_time",
                "test_software_version", "start_time", "onSfis",
                "error_code", "mlbsn", "sn", "mode", "station", "position", "product",
                "line", "pcname", "pnname", "ethernetmac", "mo"));
        config.put("TestKeys", List.of("upper_limit", "lower_limit", "status", "finish_time", "test_name",
                "error_code", "start_time", "test_value", "units"));
        config.put("LocalPrefix", List.of("UploadApi/log"));
        config.put("LocaljsonName", List.of("mlbsn", "station_name", "mode"));
        config.put("LocalTxtName", List.of("mlbsn", "station_name", "mode", "{serial}"));
        config.put("Command", "cd UploadApi && java -jar UploadLogAPI.jar 10.90.100.168");
        config.put("Spec", "{\"result\":true,\"message\":\"Save succeed!\",\"data\":null}");
    }


}
