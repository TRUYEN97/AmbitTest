/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.reader;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.common.Common;
import com.tec02.communication.Communicate.Impl.Cmd.Cmd;
import com.tec02.communication.Communicate.Impl.Telnet.Telnet;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.AbsFucnUseTelnetOrCommportConnector;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;

/**
 *
 * @author Administrator
 */
public class IperfSpeedTest extends AbsFucnUseTelnetOrCommportConnector {

    public IperfSpeedTest(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected void createConfig(FunctionConfig config) {
        config.setTime_out(20);
        config.setRetry(2);
        config.put("IP", "192.168.1.1");
        config.put(READ_UNTIL, "root@eero-test:/#");
        config.put(DUT_COMMAND, "iperf3 -s -D");
        config.put(PC_COMMAND, "iperf3 -c 192.168.1.1 -i 1 -w 4M -t 5 -O 2 -P 8 -B 192.168.1.10 -R");
    }
    private static final String ENDKEY = "Endkey";
    private static final String STARTKEY = "Startkey";

    @Override
    protected boolean test() {
        String dutCommand = this.config.getString(DUT_COMMAND);
        String pcCommand = this.config.getString(PC_COMMAND);
        String readUntil = this.config.getString(READ_UNTIL);
        try ( Telnet telnet = this.baseFunction.getTelnet()) {
            if (!this.baseFunction.sendCommand(telnet, dutCommand)) {
                return false;
            }
            this.analysisBase.readShowUntil(telnet, readUntil, new TimeS(2));
            Double value = null;
            try ( Cmd cmd = new Cmd()) {
                if (!this.baseFunction.sendCommand(cmd, pcCommand)) {
                    return false;
                }
                String line;
                while ((line = cmd.readLine()) != null) {
                    addLog(telnet.getName(), line);
                    if (line.contains("[SUM]") && line.contains("receiver")) {
                        value = Double.valueOf(line.substring(line.lastIndexOf("Bytes") + 6, line.lastIndexOf("bits/sec") - 1).trim());
                        if (line.contains("Gbits/sec")) {
                            value *= 1000;
                        }
                        break;
                    }
                }
            }
            if (value != null) {
                addLog(PC, "Value: %.0f Mbits/sec", value);
                setResult(String.format("%.0f", value));
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }

    }
    private static final String READ_UNTIL = "readUntil";
    private static final String PC_COMMAND = "pcCommand";
    private static final String DUT_COMMAND = "dutCommand";

}
