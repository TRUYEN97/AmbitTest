/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.reader;

import com.tec02.FileService.MD5;
import com.tec02.common.MyConst;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.AmbitUI;
import java.io.File;

/**
 *
 * @author Administrator
 */
public class GetMD5 extends AbsFunction {

    public GetMD5(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        try {
            String path = this.config.getString(PATH, "");
            path = path.isBlank()? AmbitUI.class.getProtectionDomain().getCodeSource().getLocation().getFile(): path;
            File programFile = new File(path);
            if (!programFile.exists()) {
                addLog(ERROR, "File not found!: %s", programFile);
                return false;
            }
            addLog(CONFIG, programFile);
            int length = this.config.getInteger(MD5_LENGTH, -1);
            addLog(CONFIG, "length: %s", length);
            String md5 = new MD5().getMD5(programFile);
            addLog(PC, "MD5: %s -> %s", md5, md5 = subMD5(md5, length));
            dataCell.putData(MyConst.MODEL.MD5, md5);
            setResult(md5);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            addLog(ERROR, e.getLocalizedMessage());
            return false;
        }
    }

    private String subMD5(String md5, int length) {
        if(length < 0){
            return md5;
        }
        return md5.substring(md5.length() - length);
    }
    private static final String MD5_LENGTH = "MD5_length";
    private static final String PATH = "path";

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.put(MD5_LENGTH, 8);
        config.put(PATH, "");
    }

}
