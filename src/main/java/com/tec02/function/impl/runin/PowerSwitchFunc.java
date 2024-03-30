/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.runin;

import com.tec02.communication.Communicate.Impl.PowerSwitch.PowerSwitch;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class PowerSwitchFunc extends AbsFunction {

    public PowerSwitchFunc(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        try {
            String host = getHost();
            addLog("CONFIG", "host: " + host);
            String user = this.config.getString("user");
            addLog("CONFIG", "user: " + user);
            String pass = this.config.getString("password");
            addLog("CONFIG", "passWord: " + pass);
            int times = this.config.getInteger("Times");
            addLog("CONFIG", "Times: " + times);
            int index = this.uICell.getColumn();
            addLog("CONFIG", "index of switch: " + index);
            List<Integer> delays = this.config.getJsonList("Delay");
            addLog("CONFIG", "Delay time: %s", delays);
            PowerSwitch powerSwitch;
            powerSwitch = new PowerSwitch(host, user, pass);
            for (int i = 1; i <= times; i++) {
                addLog(String.format("cycle Times: %d - %d ", i, times));
                if (!run(powerSwitch, index, delays) || !doSomethings()) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            addLog(e.getMessage());
            return false;
        }

    }

    private String getHost() {
        if (isNotIP()) {
            return null;
        }
        return createNewIp();
    }

    private boolean run(PowerSwitch powerSwitch, int index, List<Integer> delayS) {
        List<String> commands = config.getJsonList("Command");
        for (int i = 0; i < commands.size(); i++) {
            try {
                switch (commands.get(i)) {
                    case "on" -> {
                        if (!powerSwitch.setOn(index)) {
                            return false;
                        }
                    }
                    case "off" -> {
                        if (!powerSwitch.setOff(index)) {
                            return false;
                        }
                    }
                    case "cycle" -> {
                        if (!powerSwitch.setCycle(index)) {
                            return false;
                        }
                    }
                    case "onAll" -> {
                        if (!powerSwitch.setOffAll()) {
                            return false;
                        }
                    }
                    case "offAll" -> {
                        if (!powerSwitch.setOffAll()) {
                            return false;
                        }
                    }
                }
            } finally {
                try {
                    addLog("POWER_SWITCH", powerSwitch.getResult());
                    Thread.sleep(getDelay(delayS, i) * 1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    addLog(ex.getLocalizedMessage());
                }
            }
        }
        return true;

    }

    private String createNewIp() throws NumberFormatException {
        Integer oldIp = Integer.valueOf(getIP());
        Integer row = uICell.getRow();
        return String.format("%s.%s", getNetIp(), oldIp + row - 1);
    }

    private String getIP() {
        String ipDefault = this.config.getString("host");
        return ipDefault.substring(ipDefault.lastIndexOf(".") + 1, ipDefault.length());
    }

    private String getNetIp() {
        String ipDefault = this.config.getString("host");
        return ipDefault.substring(0, ipDefault.lastIndexOf("."));
    }

    private boolean isNotIP() {
        String ip = this.config.getString("host");
        return ip == null || !ip.matches("\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b");
    }

    public boolean doSomethings() {
        return true;
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.put("host", "192.168.1.10");
        config.put("user", "admin");
        config.put("password", "1234");
        config.put("Times", 1);
        config.put("Delay", List.of(5, 5));
        config.put("Command", List.of("off", "on"));
    }

    private int getDelay(List<Integer> delays, int i) {
        if (delays.size() <= i) {
            i = delays.size();
        }
        return delays.get(i);
    }
 
 

}
