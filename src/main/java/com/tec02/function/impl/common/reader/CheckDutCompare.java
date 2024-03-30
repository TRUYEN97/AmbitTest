/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.reader;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.common.AbsFucnUseTelnetOrCommportConnector;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;

/**
 *
 * @author Administrator
 */
public class CheckDutCompare extends AbsFucnUseTelnetOrCommportConnector {

    private static final String TIME = "Time";
    private static final String READ_UNTIL = "ReadUntil";
    private static final String REGEX = "Regex";
    private static final String ENDKEY = "Endkey";
    private static final String STARTKEY = "Startkey";
    private static final String COMMAND = "command";
    private static final String COMPARE = "compare";

    public CheckDutCompare(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        try ( AbsCommunicate communicate = this.baseFunction.getTelnetOrComportConnector()) {
            if (communicate == null || !this.baseFunction.sendCommand(communicate, config.getString(COMMAND))) {
                return false;
            }
            String startkey = config.getString(STARTKEY);
            String endkey = config.getString(ENDKEY);
            String regex = config.getString(REGEX);
            String readUntil = config.getString(READ_UNTIL);
            int time = config.getInteger(TIME, 10);
            String key = this.config.getString(COMPARE);
            addLog("CONFIG", "compare key: " + key);
            String value = this.analysisBase.getValue(communicate,
                    startkey, endkey, regex, new TimeS(time), readUntil);
            if (key != null && !key.isBlank()) {
                String spec = dataCell.getString(key);
                setFunctionSpec(spec);
                if (isDebugMode() && spec == null) {
                    this.dataCell.putData(key, value);
                }
            }
            if (value == null) {
                return false;
            }
            setResult(value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }

    private boolean checkValue(String value, String key) {
        String spec = dataCell.getString(key);
        addLog("PC", "Spec is: " + spec);
        setResult(value);
        return spec != null && value != null && spec.equalsIgnoreCase(value);
    }

    @Override
    protected void createConfig(FunctionConfig config) {
        config.setRetry(2);
        config.setTime_out(60);
        config.put(READ_UNTIL, "root@eero-test:/#");
        config.put(COMMAND, "fw_printenv  mac");
        config.put(STARTKEY, "mac=");
        config.put(ENDKEY, "");
        config.put(REGEX, "");
        config.put(TIME, 40);
        config.put(COMPARE, "ethernetmac");
    }

}
