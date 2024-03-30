/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.mblt;

import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;

/**
 *
 * @author Administrator
 */
public class SetENV_MAC extends SetENV {

    public SetENV_MAC(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected String createCommand() {
        String command = super.createCommand();
        if (command == null || command.isBlank()) {
            return null;
        }
        String replace = config.getString(REPLACE);
        addLog(CONFIG, "%s: %s", REPLACE, replace);
        StringBuilder builder = new StringBuilder(command);
        int startIndex = builder.length() - 1 < 0 ? 0 : builder.length()-1;
        builder.replace(startIndex, startIndex + replace.length(), replace);
        return builder.toString();
    }

    @Override
    protected void createConfig(FunctionConfig config) {
        super.createConfig(config); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        config.put(REPLACE, "");
    }

    private static final String REPLACE = "replace";

}
