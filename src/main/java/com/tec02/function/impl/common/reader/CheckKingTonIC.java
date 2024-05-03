/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.reader;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.common.Common;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.common.AbsFucnUseTelnetOrCommportConnector;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class CheckKingTonIC extends AbsFucnUseTelnetOrCommportConnector {

    public CheckKingTonIC(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected void createConfig(FunctionConfig config) {
        config.setTime_out(10);
        config.setRetry(1);
        config.put(CHECK_IC_CMD, "mmc cid read /sys/block/mmcblk0/device");
        config.put(IC_NAME, "MT3204");
        config.put(CONTAINS, List.of("(Kingston)"));
        config.put(READ_UNTIL, "root@eero-test:/#");
        config.put(READ_REMOVAL_TYPE, "mmc extcsd read /dev/mmcblk0 | grep SECURE");
        config.put(REMOVAL_VALUE, "0x36");
        config.put(TIME, 5);
        config.put(RE_WRITE_COMMAND, "");
    }

    @Override
    protected boolean test() {
        try ( AbsCommunicate connect = this.baseFunction.getTelnetOrComportConnector()) {
            String checkIcNameCmd = this.config.getString(CHECK_IC_CMD,
                    "mmc cid read /sys/block/mmcblk0/device");
            List<String> contains = this.config.getJsonList(CONTAINS, List.of());
            String icName = this.config.getString(IC_NAME, "MT3204");
            String readUntil = this.config.getString(READ_UNTIL, "root@eero-test:/#");
            String readRemovalTypeValue = this.config.getString(READ_REMOVAL_TYPE,
                    "mmc extcsd read /dev/mmcblk0 | grep SECURE");
            String removalTypeValue = this.config.getString(REMOVAL_VALUE, "0x36");
            String reWriteCmd = this.config.getString(RE_WRITE_COMMAND);
            int time = this.config.getInteger(TIME, 5);
            if (!this.baseFunction.sendCommand(connect, checkIcNameCmd)) {
                return false;
            }
            String responce = this.analysisBase.readShowUntil(connect, readUntil, new TimeS(time));
            if (responce == null || responce.isBlank() || !this.analysisBase.macthAll(responce, contains)) {
                return false;
            }
            if (responce.contains(icName)) {
                if (!this.baseFunction.sendCommand(connect, readRemovalTypeValue)) {
                    return false;
                }
                if (!this.analysisBase.isResponseContainKeyAndShow(connect, removalTypeValue,
                        readUntil, new TimeS(time))) {
                    if (reWriteCmd == null || reWriteCmd.isBlank()) {
                        return false;
                    }
                    if (!this.baseFunction.sendCommand(connect, reWriteCmd, readUntil, time)) {
                        return false;
                    }
                    if (!this.baseFunction.sendCommand(connect, readRemovalTypeValue)) {
                        return false;
                    }
                    if (!this.analysisBase.isResponseContainKeyAndShow(connect, removalTypeValue,
                            readUntil, new TimeS(time))) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }

    private static final String CONTAINS = "Contains";
    private static final String RE_WRITE_COMMAND = "reWriteCommand";
    private static final String READ_REMOVAL_TYPE = "readRemovalType";
    private static final String REMOVAL_VALUE = "removalValue";
    private static final String TIME = "time";
    private static final String READ_UNTIL = "readUntil";
    private static final String IC_NAME = "icName";
    private static final String CHECK_IC_CMD = "checkIcCmd";

}
