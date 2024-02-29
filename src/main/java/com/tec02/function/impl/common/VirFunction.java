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
public class VirFunction extends AbsFunction{

    public VirFunction(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
         String mess = this.config.getString("mess");
        if(mess != null && !mess.isBlank()){
            JOptionPane.showMessageDialog(null, String.format("%s - %s"
                , this.uICell.getName(), mess));
        }
        return true;
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.put("mess", "");
    }

    
}
