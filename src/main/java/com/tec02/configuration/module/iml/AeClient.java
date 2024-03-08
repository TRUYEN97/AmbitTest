/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module.iml;

import com.tec02.common.MyAeClient;
import com.tec02.communication.socket.Unicast.Client.SocketClient;
import com.tec02.communication.socket.Unicast.commons.Interface.IObjectClientReceiver;
import com.tec02.configuration.model.AeServer.AeClientDto;
import com.tec02.configuration.module.AbsModule;
import com.tec02.configuration.module.view.AeServer.AeClientPanel;

/**
 *
 * @author Administrator
 */
public class AeClient extends AbsModule<AeClientDto, AeClientDto> {

    private Thread thread;

    public AeClient(AeClientDto model) {
        super(model, model, new AeClientPanel());
    }

    @Override
    public void execute() {
        if (thread == null || !thread.isAlive()) {
            thread = new Thread() {
                @Override
                public void run() {
                    MyAeClient.getInstance().run(model.getHost(), model.getPort());
                }
            };
        }
    }

}
