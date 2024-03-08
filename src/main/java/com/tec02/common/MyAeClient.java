/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.common;

import com.tec02.communication.socket.Unicast.Client.SocketClient;
import com.tec02.communication.socket.Unicast.commons.Interface.IObjectClientReceiver;
import com.tec02.main.Core;

/**
 *
 * @author Administrator
 */
public class MyAeClient {

    private static volatile MyAeClient instance;
    private SocketClient socketClient;
    private Core core;

    public static MyAeClient getInstance() {
        MyAeClient ins = MyAeClient.instance;
        if (ins == null) {
            synchronized (MyAeClient.class) {
                ins = MyAeClient.instance;
                if (ins == null) {
                    MyAeClient.instance = ins = new MyAeClient();
                }
            }
        }
        return ins;
    }

    public void setCore(Core core) {
        this.core = core;
    }
    
    public void run(String host, int port) {
        if (isConnect()) {
            socketClient.disconnect();
        }
        socketClient = new SocketClient(host, port, new AeAnalysis());
        while (true) {
            if (!isConnect()) {
                if (socketClient.connect()) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                    }
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
    }

    public SocketClient getSocketClient() {
        return socketClient;
    }

    public boolean send(String data) {
        if (!isConnect()) {
            return false;
        }
        return socketClient.send(data);
    }

    private boolean isConnect() {
        if (socketClient == null) {
            return false;
        }
        return socketClient.isConnect();
    }

    private final class AeAnalysis implements IObjectClientReceiver {

        @Override
        public void receiver(SocketClient client, String data) {
            
        }

    }
}
