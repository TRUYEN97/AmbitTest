/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module.iml;

import com.tec02.communication.DHCP.DHCP;
import com.tec02.configuration.controller.ConfigurationManagement;
import com.tec02.configuration.model.dhcp.DhcpDto;
import com.tec02.configuration.module.AbsModule;
import com.tec02.configuration.module.view.dhcp.DhcpPanal;

/**
 *
 * @author Administrator
 */
public class Dhcp extends AbsModule<DhcpDto, DhcpDto, DhcpPanal> {

    private final DHCP dhcp;
    private Thread thread;

    public Dhcp(DhcpDto dhcpDto){
        super(dhcpDto, dhcpDto, new DhcpPanal());
        this.dhcp = DHCP.getgetInstance();
    }
    
    public boolean isRunning(){
        return this.thread != null && this.thread.isAlive();
    }

    @Override
    public void execute() {
        if (model.isOn() && (thread == null || !thread.isAlive())) {
            try {
                this.dhcp.setLogdir(String.format("%s/DHCP",
                        ConfigurationManagement.getInstance().getSettingConfig().getLocal_log()));
                this.dhcp.setLeaseTime(model.getLeaseTime());
                this.dhcp.setMacLength(model.getMacLength());
                this.dhcp.setNetIP(model.getNetIP());
                if (this.dhcp.init()) {
                    this.thread = new Thread(this.dhcp);
                    this.thread.start();
                }else{
                    System.exit(0);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
