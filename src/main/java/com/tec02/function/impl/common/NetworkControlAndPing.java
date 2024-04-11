/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.communication.Communicate.Impl.Cmd.Cmd;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;

/**
 *
 * @author Administrator
 */
public class NetworkControlAndPing extends AbsFunction {

    public NetworkControlAndPing(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        try{
            String networkName = this.config.getString(NETWORK_CARD_NAME);
            String dutNetworkName = this.config.getString(DUT_NETWORK_CARD_NAME);
            String ip = this.config.getString(IP);
            int time = this.config.getInteger(TIME, 5);
            int timePing = this.config.getInteger(TIME_PING, 60);
            addLog(CONFIG, "%s: %s", NETWORK_CARD_NAME, networkName);
            addLog(CONFIG, "%s: %s", DUT_NETWORK_CARD_NAME, dutNetworkName);
            addLog(CONFIG, "%s: %s", IP, dutNetworkName);
            addLog(CONFIG, "%s: %s", TIME, time);
            addLog(CONFIG, "%s: %s", TIME_PING, timePing);
            if (networkName == null || dutNetworkName == null || ip == null) {
                return false;
            }
            try {
                addLog(PC, "Disable network cards");
                if (!this.baseFunction.networkCardControl(networkName, false)
                        || !this.baseFunction.networkCardControl(dutNetworkName, false)) {
                    return false;
                }
                if (this.baseFunction.pingTo(ip, time)) {
                    return false;
                }
                addLog(PC, "Enable the DUT network card");
                if (!this.baseFunction.networkCardControl(dutNetworkName, true)) {
                    return false;
                }
                return this.baseFunction.pingTo(ip, timePing);
            } finally {
                this.baseFunction.networkCardControl(networkName, true);
                this.baseFunction.networkCardControl(dutNetworkName, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            addLog(ERROR, e.getLocalizedMessage());
            return false;
        }
    }
    private static final String IP = "IP";
    private static final String DUT_NETWORK_CARD_NAME = "DUTNetworkCardName";
    private static final String NETWORK_CARD_NAME = "NetworkCardName";

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setRetry(0);
        config.setTime_out(70);
        config.put(NETWORK_CARD_NAME, "Network");
        config.put(DUT_NETWORK_CARD_NAME, "DUT");
        config.put(IP, "8.8.8.8");
        config.put(TIME, 5);
        config.put(TIME_PING, 60);
    }
    private static final String TIME_PING = "time_ping";
    private static final String TIME = "time";

}
