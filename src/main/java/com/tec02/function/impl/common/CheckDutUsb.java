/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;

/**
 *
 * @author Administrator
 */
public class CheckDutUsb extends AbsFucnUseTelnetOrCommportConnector {

    public CheckDutUsb(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        try ( AbsCommunicate communicate = this.baseFunction.getTelnetOrComportConnector()) {
            if (communicate == null) {
                return false;
            }
            String readUntil = "root@eero-test:/#";
            String responce;
            String usb_fw = null;
            String usb_model = null;
            if (!this.baseFunction.sendCommand(communicate, "tps -r devinfo")) {
                return false;
            }
            responce = this.analysisBase.getLine(communicate, new TimeS(10), 1);
            addLog(communicate.getName(), responce);
            if (!responce.contains("Could not")) {
                int index = responce.indexOf("FW");
                usb_model = responce.substring(0, index);
                usb_fw = responce.substring(index);
            } else if (!this.baseFunction.sendCommand(communicate, "rts5452 -r ic_status")) {
                return false;
            } else {
                responce = this.analysisBase.readShowUntil(communicate, readUntil, new TimeS(10));
                String lines[] = responce.split("\n");
                if (!responce.contains("Could not") && lines.length >= 2) {
                    usb_fw = lines[0].substring(lines[0].indexOf("version ") + 8).trim();
                    usb_model = lines[1].substring(0, lines[1].indexOf(" is running")).trim();
                }
            }
            ValueSubItem subItem;
            subItem = createSubItem(ValueSubItem.class, "usb_fw");
            subItem.setValue(usb_fw);
            subItem.runTest(1);
            subItem = createSubItem(ValueSubItem.class, "usb_model");
            subItem.setValue(usb_model);
            subItem.runTest(1);
            return isAllSubItemPass();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }

    @Override
    protected void createConfig(FunctionConfig config) {
        config.setTime_out(10);
        config.setRetry(1);
        config.setFailApiName("usb_fw");
    }

}
