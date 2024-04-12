/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.reader;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.common.MyConst;
import com.tec02.communication.Communicate.Impl.Comport.ComPort;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import java.util.List;
import javax.swing.Timer;

/**
 *
 * @author Administrator
 */
public class CheckComport extends AbsFunction {

    public CheckComport(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        try ( ComPort dut = this.baseFunction.getComport()) {
            if (dut == null) {
                setErrorCode("COM.OPEN", "COMPORT");
                return false;
            }
            int failCount = this.config.getInteger("failTimes", 2);
            List<String> notContains = this.config.getJsonList("NotContain");
            List<String> contains = this.config.getJsonList("Contain",
                    List.of("Started ", "Qorvo", "BLE"));
            String mac = this.dataCell.getString(MyConst.MODEL.MAC);
            int timeOut = this.config.getInteger("time", 70);
//            addLog(PC, "time out: %s", timeOut);
//            addLog(PC, "MAC: %s", mac);
//            addLog(PC, "Not contain: %s, %s", notContains, failCount);
//            addLog(PC, "Contain: %s", contains);
            StringBuilder builder = new StringBuilder();
            String line;
            new Timer(timeOut * 1000, (e) -> {
                dut.stopRead();
            }).start();
            TimeS timeS = new TimeS(timeOut);
            int count = 0;
            while (timeS.onTime()) {
                line = dut.readLine();
                line = line == null ? "" : line;
                addLog(COMPORT, line);
                builder.append(line).append("\r\n");
                if (notContains != null && !notContains.isEmpty() && isContains(notContains, line)) {
                    count += 1;
                    if (count >= failCount) {
//                        addLog(PC, "Responce is contain: %s, times: %s", notContains, count);
                        setErrorCode("REBOOT", "REBOOT");
                        return false;
                    }
                }
                if (contains != null && !contains.isEmpty() && isContainsAll(contains, line)) {
                    break;
                }
            }
            boolean rs = true;
            String responce = builder.toString();
            String errorcode = "";
            String desErrorcode = "";
            if (mac != null && !mac.isBlank()) {
                if (!responce.contains(mac.toLowerCase())) {
//                    addLog(PC, "Responce is not contain: %s", mac);
                    errorcode = "MAC.CHECK";
                    desErrorcode = "MAC_CHECK";
                    setErrorCode(errorcode, desErrorcode);
                    this.config.setFail_continue(false);
                    rs = false;
                } else {
//                    addLog(PC, "Responce is contain: %s", mac);
                }
            }
            if (contains != null && !contains.isEmpty()) {
                if (!isContainsAll(contains, responce)) {
//                    addLog(PC, "Responce is not contain: %s", contains);
                    if (errorcode.isBlank()) {
                        errorcode = "BLE.CHECK";
                        desErrorcode = "BLE_CHECK";
                    }
                    rs = false;
                } else {
//                    addLog(PC, "Responce is contain: %s", contains);
                }
            }
            if (!rs) {
                setErrorCode(errorcode, desErrorcode);
            }
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            addLog(ERROR, e.getMessage());
            return false;
        }
    }

    private boolean isContainsAll(List<String> contains, String line) {
        if (contains == null || contains.isEmpty()) {
            return true;
        }
        for (String contain : contains) {
            if (!line.contains(contain)) {
                return false;
            }
        }
        return true;
    }

    private boolean isContains(List<String> contains, String line) {
        if (contains == null || contains.isEmpty()) {
            return true;
        }
        for (String contain : contains) {
            if (line.contains(contain)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.put("failTimes", 1);
        config.put("NotContain", List.of());
        config.put("Contain", List.of());
        config.put("time", 70);
    }

}
