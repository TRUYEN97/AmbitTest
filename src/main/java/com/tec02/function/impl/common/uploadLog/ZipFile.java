/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.uploadLog;

import com.tec02.function.AbsBaseFunction;
import com.tec02.function.baseFunction.FileBaseFunction;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;

/**
 *
 * @author Administrator
 */
public class ZipFile extends AbsBaseFunction {

    private final FileBaseFunction fileBaseFunction;
    private String zipPath;
    private String txtpath;

    public ZipFile(FunctionConstructorModel constructorModel) {
        super(constructorModel);
        this.fileBaseFunction = new FileBaseFunction(constructorModel);
    }

    public void setZipPath(String zipPath) {
        this.zipPath = zipPath;
    }

    public void setTxtpath(String txtpath) {
        this.txtpath = txtpath;
    }

    public boolean saveFileZip() {
        addLog("Save file zip!");
        try {
            String suffix = this.config.getString("LocalSuffix", "txt");
            String txtFile = this.txtpath == null ? this.fileBaseFunction.createDefaultStringPath(this.dataCell.isPass()) : txtpath;
            String zipFile = this.zipPath == null ? txtFile.replaceAll(".".concat(suffix), ".zip") : zipPath;
            return this.fileBaseFunction.saveZip(zipFile, txtFile);
        } catch (Exception e) {
            addLog("Save file zip failed: " + e.getMessage());
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }
}
