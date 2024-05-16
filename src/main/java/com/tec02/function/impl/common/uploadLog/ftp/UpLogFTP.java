/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.uploadLog.ftp;

import com.tec02.common.MyConst;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.common.uploadLog.CreateJsonApi;
import com.tec02.function.impl.common.uploadLog.CreateTxt;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class UpLogFTP extends AbsFunction {

    private final UpFTP upFTP;
    private final CreateJsonApi jsonApi;
    private final CreateTxt createTxt;

    public UpLogFTP(FunctionConstructorModel constructorModel) {
        super(constructorModel);
        this.upFTP = (UpFTP) getBaseFunction(UpFTP.class);
        this.jsonApi = (CreateJsonApi) getBaseFunction(CreateJsonApi.class);
        this.createTxt = (CreateTxt) getBaseFunction(CreateTxt.class);
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setAlwaysRun(true);
        config.setRetry(1);
        config.put("User", "oper");
        config.put("Password", "mfg-oper");
        config.put("Host", "10.90.100.168");
        config.put("Port", 21);
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
        config.put("LocalPrefix", List.of("Log/TestLog"));
        config.put("FtpPrefixTxt", List.of("data","product", "Text", "pnname", "station_type", "start_day", "station_name", "status"));
        config.put("FtpPrefixJson", List.of("data","product", "json", "pnname", "station_type", "start_day", "station_name", "status"));
        config.put("LocalName", List.of("mlbsn", "sn", "position", "status", "mode", "finish_time"));
        config.put("LocalNameFail", List.of("mlbsn", "sn", "position", "status", "error_details", "errorcode", "mode", "finish_time"));
    }

    @Override
    public boolean test() {
        try {
            List<String> localPrefix = this.config.getJsonList("LocalPrefix");
            List<String> ftpPrefixTxt = this.config.getJsonList("FtpPrefixTxt");
            List<String> ftpPrefixJson = this.config.getJsonList("FtpPrefixJson");
            List<String> localName = this.config.getJsonList("LocalName");
            List<String> localNameFail = this.config.getJsonList("LocalNameFail", localName);
            String ftpFolderTxt = this.fileBaseFunction.createDir(ftpPrefixTxt);
            String ftpFolderJson = this.fileBaseFunction.createDir(ftpPrefixJson);
            String localFolder = this.fileBaseFunction.createName(localPrefix);
            String fileName = this.fileBaseFunction.createName(this.dataCell.isPass() ? localName : localNameFail);
            String localJsonPath = String.format("%s/json/%s.json", localFolder, fileName);
            String localTxtPath = String.format("%s/text/%s.txt", localFolder, fileName);
            String ftpJsonPath = String.format("%s/%s.json", ftpFolderJson, fileName);
            String ftpTxtPath = String.format("%s/%s.txt", ftpFolderTxt, fileName);
            if (retry < 1) {
                this.dataCell.updateResultTest();
            }
            if (upJson(localJsonPath, ftpJsonPath) && upTxt(localTxtPath, ftpTxtPath)) {
                this.dataCell.putData(MyConst.MODEL.FTP_PATH, ftpTxtPath);
                setResult(ftpTxtPath);
                return true;
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            ErrorLog.addError(this, ex.getMessage());
            return false;
        }
    }

    private boolean upJson(String localJsonPath, String ftpJsonPath) {
        return isCreatejsonOK(localJsonPath) && isUpFTPOK(localJsonPath, ftpJsonPath);
    }

    private boolean upTxt(String localTxtPath, String ftpTxtPath) {
        return isCreateTxtOK(localTxtPath) && isUpFTPOK(localTxtPath, ftpTxtPath);
    }

    private boolean isCreatejsonOK(String localJsonPath) {
        this.jsonApi.setPath(localJsonPath);
        return this.jsonApi.test();
    }

    private boolean isCreateTxtOK(String localJsonPath) {
        this.createTxt.setPath(localJsonPath);
        return this.createTxt.saveTxtFile();
    }

    private boolean isUpFTPOK(String localPath, String ftpPath) {
        this.upFTP.setLocalPath(localPath);
        this.upFTP.setFtpPath(ftpPath);
        return this.upFTP.upFtp();
    }

}
