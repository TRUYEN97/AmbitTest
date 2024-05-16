/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.mblt.voltageTest;

import com.tec02.Time.WaitTime.Class.TimeMs;
import com.tec02.function.impl.AbsSendRetryCommand;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.function.impl.common.controller.FixtureAction;
import com.tec02.function.model.FunctionConstructorModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public abstract class AbsVoltage extends AbsSendRetryCommand {

    protected final FixtureAction fixtureAction;
    protected final Map<String, String> voltageVals;

    protected AbsVoltage(FunctionConstructorModel constructorModel) {
        super(constructorModel);
        this.fixtureAction = (FixtureAction) createElementFunction(FixtureAction.class);
        this.voltageVals = new HashMap<>();
    }

    protected boolean sendFixtureCommand(AbsCommunicate communicate,
            List<String> commands, int delay) {
        if (commands != null && !commands.isEmpty() && this.fixtureAction.sendCommand(communicate,
                commands,
                config.getJsonList(FIXTURE_KEYS, List.of("OK")),
                config.getInteger(FIXTURE_WAIT, 10))) {
            this.baseFunction.delay(delay);
            return true;
        }
        return false;
    }

    protected boolean getVoltageValue(AbsCommunicate communicate,
            List<String> commands, List<String> readUntils) {
        voltageVals.clear();
        for (String command : commands) {
            if (communicate == null || !this.baseFunction.sendCommand(communicate, command)) {
                return false;
            }
            String inline;
            while ((inline = communicate.readLine(new TimeMs(500))) != null) {
                inline = inline.trim();
                addLog(communicate.getClass().getSimpleName(), inline);
                if (!inline.startsWith("TP") || !inline.contains("=")) {
                    continue;
                }
                if (inline.contains(",")) {
                    inline = inline.replaceAll(",", ".");
                }
                String[] slips = inline.split("=|:|;");
                String tp = slips[0];
                String value = slips[1];
                if (slips.length < 2 || tp.isBlank() || value.isBlank()) {
                    continue;
                }
//                addLog(PC, "Value: %s - %s", tp, value);
                voltageVals.put(tp, value);
                if (this.analysisBase.contains(inline, readUntils)) {
                    break;
                }
            }
        }
        return true;
    }
}
