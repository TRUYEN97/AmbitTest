/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.communication.Communicate.Impl.Cmd.Cmd;
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
        config.put(PC_IP, "192.168.1.20");
        config.put("IP", "192.168.1.1");
        config.put(RX, true);
        config.put(TIME, 15);
    }

    @Override
    protected boolean test() {
        try ( AbsCommunicate telnet = this.baseFunction.getTelnet()) {
            String pcCommand = this.config.getString(PC_COMMAND);
            String dutCommand = this.config.getString(DUT_COMMAND);
            String readUntil = this.config.getString(READ_UNTIL);
            String startKey = this.config.getString(START_KEY);
            String endKey = this.config.getString(END_KEY);
            String stopAt = this.config.getString(STOP_AT);
            String ip = this.config.getString(PC_IP);
            boolean isRX = this.config.get(RX, true);
            int time = this.config.getInteger(TIME, 15);
            if (!this.baseFunction.ubuntuPingTo(telnet, ip, 1)) {
                return false;
            }
            Cmd cmd = new Cmd();
            if (!this.baseFunction.sendCommand(cmd, pcCommand)) {
                return false;
            }
            if (!this.baseFunction.sendCommand(telnet, dutCommand)) {
                return false;
            }
            if (!this.analysisBase.isResponseContainKeyAndShow(cmd,
                    stopAt, stopAt, new TimeS(time))) {
                return false;
            }
            if (!telnet.sendCtrl_C()) {
                return false;
            }
            String value;
            if (isRX) {
                value = this.analysisBase.getValue(telnet, startKey, endKey, readUntil, 0);
            } else {
                value = this.analysisBase.getValue(cmd, startKey, endKey, null, 0);
            }
            setResult(convetToInt(value));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }
    private static final String TIME = "time";
    private static final String RX = "isRX";
    private static final String PC_IP = "pc_ip";
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
