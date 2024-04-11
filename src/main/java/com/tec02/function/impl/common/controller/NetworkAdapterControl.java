/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.controller;

import com.tec02.communication.Communicate.Impl.Cmd.Cmd;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;

/**
 *
 * @author Administrator
 */
public class NetworkAdapterControl extends AbsFunction {

    public NetworkAdapterControl(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        try ( Cmd cmd = new Cmd()) {
            String cardName = this.config.getString(CARDNAME);
            boolean enable = this.config.get(SET_ENABLE, true);
            addLog(CONFIG, "Card name: %s", cardName);
            addLog(CONFIG, "setEnable: %s", enable);
            return this.baseFunction.networkCardControl(cardName, enable);
        } catch (Exception e) {
            e.printStackTrace();
            addLog(ERROR, e.getLocalizedMessage());
            return false;
        }
    }
    
    private static final String SET_ENABLE = "setEnable";
    private static final String CARDNAME = "cardname";

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setTime_out(10);
        config.setRetry(1);
        config.put(CARDNAME, "");
        config.put(SET_ENABLE, true);
    }

}
