/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.runin;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.AbsFucnUseTelnetOrCommportConnector;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class EmmcWriteRead extends AbsFucnUseTelnetOrCommportConnector {

    public EmmcWriteRead(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        String response;
        try ( AbsCommunicate communicate = this.baseFunction.getTelnetOrComportConnector()) {
            if (communicate == null) {
                return false;
            }
            String command = this.config.getString(COMMAND);
            if (!this.baseFunction.sendCommand(communicate, command)) {
                return false;
            }
            response = getResponse(communicate);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
        List<String> items = this.config.getJsonList(ITEM_NAMES);
        List<String> blocks = this.config.getJsonList(BLOCK);
        List<String> KeyWords = this.config.getJsonList(KEY_WORD);
        addLog("Config", "Items: %s", items);
        addLog("Config", "Block: %s", blocks);
        addLog("Config", "KeyWord: %s", KeyWords);
        return runSubItems(items, response, blocks, KeyWords);
    }

    private boolean runSubItems(List<String> items, String response, List<String> blocks, List<String> KeyWords) {
        int itemsSize = items.size();
        for (int i = 0; i < itemsSize; i++) {
            String item = items.get(i);
            addLog("PC", item);
            EmmcSpeed mmc_speed = createSubItem(EmmcSpeed.class, item);
            mmc_speed.setData(response, blocks.get(i), KeyWords.get(i));
            mmc_speed.runTest(1);
        }
        return isAllSubItemPass();
    }
    

    private String getResponse(AbsCommunicate telnet) {
        int time = this.config.getInteger(TIME, 5);
        String until = this.config.getString(READ_UNTIL);
        return this.analysisBase.readShowUntil(telnet, until, new TimeS(time));
    }


    @Override
    protected void createConfig(FunctionConfig config) {
        config.setTime_out(50);
        config.setRetry(2);
        config.setFailApiName("emmc_speed_read");
        config.put(ITEM_NAMES, List.of("emmc_speed_read", "emmc_speed_write"));
        config.put(COMMAND, "test-emmc");
        config.put(KEY_WORD, List.of("Reading", "Writing"));
        config.put(BLOCK, List.of("0x00", "0x00"));
        config.put(READ_UNTIL, "root@eero-test:/#");
        config.put(TIME, 50);
    }
    private static final String ITEM_NAMES = "ItemNames";
    private static final String TIME = "Time";
    private static final String READ_UNTIL = "ReadUntil";
    private static final String BLOCK = "Block";
    private static final String KEY_WORD = "KeyWord";
    private static final String COMMAND = "command";

}
