/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.reader;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.common.Common;
import com.tec02.communication.Communicate.Impl.Cmd.Cmd;
import com.tec02.communication.Communicate.Impl.Telnet.Telnet;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Administrator
 */
public class UserspaceSpeedtestSpeed extends AbsFunction {

    public UserspaceSpeedtestSpeed(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    public UserspaceSpeedtestSpeed(FunctionConstructorModel constructorModel, int functionType) {
        super(constructorModel, functionType);
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setTime_out(20);
        config.setRetry(1);
        config.put(PC_COMMAND, "userspace_speedtest.exe -t -sport 65499 -dip 192.168.1.1 -dport 65498");
        config.put(DUT_COMMAND, "udpst -sip 192.168.1.1 -sport 65499 -dip 192.168.1.20 -dport 65498 -if eth1 -r");
        config.put(READ_UNTIL, "root@eero-test:/#");
        config.put(START_KEY, "NSS UDP Receive Test Rate = ");
        config.put(END_KEY, "");
        config.put(STOP_AT, "udpst.c:main(): Test running\r\nudpst.c:main(): Test running");
        config.put("IP", "192.168.1.1");
        config.put(RX, true);
        config.put(TIME, 10);
    }

    @Override
    protected boolean test() {
        try ( Telnet telnet = this.baseFunction.getTelnet()) {
            String pcCommand = this.config.getString(PC_COMMAND);
            String dutCommand = this.config.getString(DUT_COMMAND);
            String readUntil = this.config.getString(READ_UNTIL);
            String startKey = this.config.getString(START_KEY);
            String endKey = this.config.getString(END_KEY);
            String stopAt = this.config.getString(STOP_AT);
            boolean isRX = this.config.get(RX, true);
            TimeS timer = new TimeS(this.config.getInteger(TIME, 10));
            String value;
            try ( Cmd cmd = new Cmd()) {
                cmd.setDebug(true);
                if (!this.baseFunction.sendCommand(cmd, pcCommand)) {
                    return false;
                }
                this.baseFunction.delay(500);
                if (!this.baseFunction.sendCommand(telnet, dutCommand)) {
                    return false;
                }
                this.analysisBase.readShowUntil(telnet, stopAt, timer);
                if (!telnet.sendCtrl_C()) {
                    return false;
                }
                String responce = telnet.readUntil(readUntil);
                addLog(telnet.getName(), responce);
                if (!isRX) {
                    responce = cmd.readAll();
                    addLog(CMD, responce);
                }
                if (responce.contains(startKey)) {
                    String subString = Common.subString(responce, startKey, endKey);
                    value = Common.findGroup(subString, "\\d+(\\.\\d+)?");
                } else {
                    return false;
                }
            }
            int finalValue = convetToInt(value);
            addLog(PC, "Value: %s Mbps", finalValue);
            setResult(finalValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }
    private static final String TIME = "time";
    private static final String RX = "isRX";
    private static final String STOP_AT = "stopAt";
    private static final String END_KEY = "endKey";
    private static final String START_KEY = "startKey";
    private static final String READ_UNTIL = "readUntil";
    private static final String DUT_COMMAND = "dutCommand";
    private static final String PC_COMMAND = "pcCommand";

    private int convetToInt(String value) throws NumberFormatException {
        BigDecimal b = new BigDecimal(Double.parseDouble(value) / 1024 / 1024);
        return b.setScale(0, RoundingMode.HALF_UP).intValue();
    }

}
