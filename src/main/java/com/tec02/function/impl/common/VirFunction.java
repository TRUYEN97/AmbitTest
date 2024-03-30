/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import javax.swing.JOptionPane;

/**
 *
 * @author Administrator
 */
public class VirFunction extends AbsFunction {

    private String log = null;
    private boolean pass = true;

    public VirFunction(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    public void setLog(String log) {
        this.log = log;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }
    
    @Override
    protected boolean test() {
        String mess = this.config.getString("mess");
        String logConfig = this.config.getString("log");
        if (logConfig != null && !logConfig.isBlank()) {
            addLog(PC, logConfig);
        }
        if (log != null && !log.isBlank()) {
            addLog(PC, log);
        }
        if (mess != null && !mess.isBlank()) {
            JOptionPane.showMessageDialog(null, String.format("%s - %s",
                    this.uICell.getName(), mess));
        }
        return pass;
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.put("mess", "");
        config.put("log", "");
    }

}
