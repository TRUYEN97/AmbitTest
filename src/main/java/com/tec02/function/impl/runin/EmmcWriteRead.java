/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.runin;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.main.ErrorLog;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class EmmcWriteRead extends AbsFunction {

    @Override
    protected boolean test() {
        String response;
        try ( AbsCommunicate communicate = this.baseFunction.getTelnetOrComportConnector()) {
            if (communicate == null) {
                return false;
            }
            String command = this.config.getString("command");
            if (!this.baseFunction.sendCommand(communicate, command)) {
                return false;
            }
            response = getResponse(communicate);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
        List<String> items = this.config.getJsonList("ItemNames");
        List<String> blocks = this.config.getJsonList("Block");
        List<String> KeyWords = this.config.getJsonList("KeyWord");
        addLog("Config", "Items: %s", items);
        addLog("Config", "Block: %s", blocks);
        addLog("Config", "KeyWord: %s", KeyWords);
        return runSubItems(items, response, blocks, KeyWords);
    }

    private boolean runSubItems(List<String> items, String response, List<String> blocks, List<String> KeyWords) {
        int itemsSize = items.size();
        boolean rs = true;
        for (int i = 0; i < itemsSize; i++) {
            String item = items.get(i);
            addLog("PC", item);
            EmmcSpeed mmc_speed = (EmmcSpeed) getSubItem("EmmcSpeed", item);
            mmc_speed.setData(response, blocks.get(i), KeyWords.get(i));
            mmc_speed.runTest(1);
            if (!mmc_speed.isPass()) {
                rs = false;
            }
        }
        return rs;
    }
    

    private String getResponse(AbsCommunicate telnet) {
        int time = this.config.getInteger("Time", 5);
        String until = this.config.getString("ReadUntil");
        return this.analysisBase.readShowUntil(telnet, until, new TimeS(time));
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.put("type", "telnet");
        config.put("IP", "192.168.1.1");
        config.put("comport", 1);
        config.put("baudrate", 115200);
        config.put("ItemNames", List.of("emmc_speed_read", "emmc_speed_write"));
        config.put("command", "test-emmc");
        config.put("KeyWord", List.of("Reading", "Writing"));
        config.put("Block", List.of("0x00", "0x00"));
        config.put("ReadUntil", "root@eero-test:/#");
        config.put("Time", 50);
    }

    @Override
    protected void init() {
    }

}
