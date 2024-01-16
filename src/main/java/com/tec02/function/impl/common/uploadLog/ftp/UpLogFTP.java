/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.uploadLog.ftp;


import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.common.uploadLog.CreateJsonApi;
import com.tec02.function.impl.common.uploadLog.CreateTxt;
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


    public UpLogFTP() {
        this.upFTP = new UpFTP(logger, config);
        this.jsonApi = new CreateJsonApi(logger, config);
        this.createTxt = new CreateTxt(logger, config);
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
            if (upJson(localJsonPath, ftpJsonPath) && upTxt(localTxtPath, ftpTxtPath)) {
                this.dataCell.putData("ftppath", ftpTxtPath);
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

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
