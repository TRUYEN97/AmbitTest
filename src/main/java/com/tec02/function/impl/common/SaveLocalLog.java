/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

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
public class SaveLocalLog extends AbsFunction {

    private final CreateJsonApi jsonApi;
    private final CreateTxt createTxt;

    public SaveLocalLog(FunctionConstructorModel constructorModel) {
        super(constructorModel, -1);
        this.jsonApi = (CreateJsonApi) getBaseFunction(CreateJsonApi.class);
        this.createTxt = (CreateTxt) getBaseFunction(CreateTxt.class);
    }

    private boolean isCreatejsonOK(String localJsonPath) {
        this.jsonApi.setPath(localJsonPath);
        return this.jsonApi.test();
    }

    private boolean isCreateTxtOK(String localJsonPath) {
        this.createTxt.setPath(localJsonPath);
        return this.createTxt.saveTxtFile();
    }

    @Override
    protected boolean test() {
        List<String> localPrefix = this.config.getJsonList("LocalPrefix");
        List<String> localName = this.config.getJsonList("LocalName");
        List<String> localNameFail = this.config.getJsonList("LocalNameFail", localName);
        String localFolder = this.fileBaseFunction.createName(localPrefix);
        String fileName = this.fileBaseFunction.createName(this.dataCell.isPass() ? localName : localNameFail);
        String localJsonPath = String.format("%s/json/%s.json", localFolder, fileName);
        String localTxtPath = String.format("%s/text/%s.txt", localFolder, fileName);
        return isCreatejsonOK(localJsonPath) && isCreateTxtOK(localTxtPath);
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setAlwaysRun(true);
        config.setTest_name("*Save_local_log");
        config.put("followLimit", false);
        config.put("limitErrorCode", false);
        config.put("BaseKeys", List.of("mo", "sn",
                "error_details", "status", "finish_time",
                "test_software_version", "start_time", "station_type",
                "error_code", "errorcode", "errorDes",
                "serial", "mode", "station_name",
                "position", "cycle_time"));
        config.put("TestKeys", List.of("upper_limit",
                "lower_limit", "status", "finish_time", "test_name",
                "error_code", "errorcode", "errorDes",
                "start_time", "test_value", "units"));
        config.put("LocalPrefix", List.of("Log/TestLog/", "position"));
        config.put("LocalName", List.of("mlbsn", "sn", "position", "status", "mode", "finish_time"));
        config.put("LocalNameFail", List.of("mlbsn", "sn", "position", "status", "error_details", "errorcode", "mode", "finish_time"));
    }

}
