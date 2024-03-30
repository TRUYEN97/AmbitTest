/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.mblt;

import com.tec02.Time.WaitTime.Class.TimeMs;
import com.tec02.function.impl.common.AbsSendRetryCommand;
import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.Impl.Comport.ComPort;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.common.VirFunction;
import com.tec02.function.impl.common.controller.FixtureAction;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import java.util.List;
import javax.swing.Timer;

/**
 *
 * @author Administrator
 */
public class UsbsideCheckReboot extends AbsSendRetryCommand {

    private final FixtureAction fixture;

    public UsbsideCheckReboot(FunctionConstructorModel constructorModel) {
        super(constructorModel);
        this.fixture = (FixtureAction) createElementFunction(FixtureAction.class);
    }

    @Override
    protected void defaultConfig(FunctionConfig config) {
        config.merge(FixtureAction.class);
        config.setRetry(2);
        config.put("DutCom", "COM4");
        config.put("DutBaudRate", 115200);
        config.put("DutWait", 2);
        config.put("failTimes", 2);
        config.put("NotContain", List.of());
    }

    @Override
    protected boolean testfunc() {
        if (!this.fixture.testfunc()) {
            return false;
        }
        String dutCom = this.config.getString("DutCom");
        int dutBaud = this.config.getInteger("DutBaudRate", 9600);
        int dutWait = this.config.getInteger("DutWait", 1);
        int failCount = this.config.getInteger("failTimes", 2);
        String subItemName = this.config.getString("subItem");
        List<String> notContains = this.config.getJsonList("NotContain");
        addLog(PC, "Not contain: %s, %s", notContains, failCount);
        addLog(CONFIG, String.format("Test about %s s", dutWait));
        try ( ComPort comport = this.baseFunction.getComport(dutCom, dutBaud)) {
            if (comport == null) {
                return false;
            }
            addLog(PC, "-----------------------------------------------");
            int count = 0;
            StringBuilder builder = new StringBuilder();
            TimeS timer = new TimeS(dutWait);
            String line;
            new Timer(dutWait * 1000, (e) -> {
                comport.stopRead();
            }).start();
            setStopMultiTaskAction(() -> {
                stop = true;
                comport.stopRead();
            });
            while (timer.onTime() && !stop) {
                line = comport.readLine();
                line = line == null ? "" : line;
                addLog(COMPORT, line);
                builder.append(line).append("\r\n");
                if (notContains != null && !notContains.isEmpty() && isContains(notContains, line)) {
                    count += 1;
                    if (count >= failCount) {
                        break;
                    }
                }
            }
            if (builder.isEmpty()) {
                return false;
            }
            if (subItemName != null) {
                VirFunction virFunction = createSubItem(VirFunction.class, subItemName);
                if (count >= failCount) {
                    String log = String.format("Responce is contain: %s, times: %s", notContains, count);
                    virFunction.setLog(log);
                    virFunction.setPass(false);
                }
                virFunction.runTest(1);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            addLog(ERROR, e.getMessage());
            return false;
        }
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

}
