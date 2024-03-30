/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module.iml.Socket;

import com.tec02.configuration.model.socket.SocketDto;
import com.tec02.configuration.module.AbsModule;
import com.tec02.configuration.module.view.Socket.SocketPanel;

/**
 *
 * @author Administrator
 */
public class Socket extends AbsModule<SocketDto, SocketDto, SocketPanel> {

    private AmbitSocket client;

    public Socket(SocketDto socketDto){
        super(socketDto, socketDto, new SocketPanel());
    }

    @Override
    public void execute() {
        
        if (this.client == null || !this.client.isAlive()) {
            client = new AmbitSocket();
            client.start();
        }
    }

}
