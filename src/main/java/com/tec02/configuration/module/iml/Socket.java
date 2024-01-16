/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module.iml;

import com.tec02.configuration.model.socket.SocketDto;
import com.tec02.configuration.module.AbsModule;
import com.tec02.configuration.module.view.Socket.SocketPanel;

/**
 *
 * @author Administrator
 */
public class Socket extends AbsModule<SocketDto, SocketDto> {

    public Socket(SocketDto socketDto) {
        super(socketDto, socketDto, new SocketPanel());
    }

    @Override
    public void execute() {
    }

}
