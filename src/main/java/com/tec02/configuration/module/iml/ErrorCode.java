/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module.iml;

import com.tec02.configuration.model.errorCode.InputErrorcode;
import com.tec02.configuration.model.errorCode.ErrorCodeModel;
import com.tec02.configuration.model.errorCode.ItemErrorCode;
import com.tec02.configuration.module.AbsModule;
import com.tec02.configuration.module.view.errorcode.ErrorCodePanel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class ErrorCode extends AbsModule<ErrorCodeModel, InputErrorcode, ErrorCodePanel> {

    public ErrorCode() {
        super(new ErrorCodeModel(), new InputErrorcode(), new ErrorCodePanel());
    }

    @Override
    public void refesh() {
        super.refesh();
        execute();
        this.view.refesh();
    }

    @Override
    public void execute() {
        try {
            String dir = model.getDir();
            if (dir != null && !dir.isBlank() && new File(dir).exists()) {
                String[] elems;
                ItemErrorCode itemErrorCode;
                Map<String, ItemErrorCode> itemErrorCodes = new HashMap<>();
                for (String line : Files.readAllLines(Path.of(dir))) {
                    elems = line.split(",");
                    if (elems.length < 3) {
                        continue;
                    }
                    itemErrorCode = new ItemErrorCode();
                    itemErrorCode.setTest_name(elems[0]);
                    itemErrorCode.setDesc_errorcode(elems[1]);
                    itemErrorCode.setErrorcode(elems[2]);
                    if (elems.length >= 5) {
                        itemErrorCode.setDesc_tooLowErrorcode(elems[3]);
                        itemErrorCode.setTooLowErrorcode(elems[4]);
                    }
                    if (elems.length >= 7) {
                        itemErrorCode.setDesc_tooHighErrorcode(elems[5]);
                        itemErrorCode.setTooHighErrorcode(elems[6]);
                    }
                    itemErrorCodes.put(elems[0], itemErrorCode);
                }
                model.setErrorcodes(itemErrorCodes);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
