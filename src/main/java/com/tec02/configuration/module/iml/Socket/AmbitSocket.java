/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module.iml.Socket;

import com.tec02.common.MyConst;
import com.tec02.communication.socket.Unicast.Client.SocketClient;
import com.tec02.communication.socket.Unicast.Server.SocketServer;
import com.tec02.configuration.controller.ConfigurationManagement;
import com.tec02.configuration.model.socket.SocketClientDto;
import com.tec02.configuration.model.socket.SocketDto;
import com.tec02.view.Gui;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class AmbitSocket extends Thread {

    private final SocketDto socketDto;
    private final Map<String, SocketClient> clients;
    private final Gui gui;
    private final Receiver receiver;
    private SocketServer server;

    public AmbitSocket(){
        this.socketDto = ConfigurationManagement.getInstance().getSocketConfig().getModel();
        this.clients = new HashMap<>();
        this.gui = Gui.getInstance();
        this.receiver = new Receiver();
    }

    @Override
    public void run() {
        if (socketDto.getServer().isFlag() && (server == null || !server.isAlive())) {
            try {
                this.server = new SocketServer(socketDto.getServer().getPort(), this.receiver);
                this.server.start();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        var modelClients = this.socketDto.getClients();
        SocketClientDto clientDto;
        SocketClient client;
        for (Map.Entry<String, SocketClientDto> entry : modelClients.entrySet()) {
            String key = entry.getKey();
            clientDto = entry.getValue();
            if (clientDto == null) {
                continue;
            }
            client = new SocketClient(key, clientDto.getHost(),
                    clientDto.getPort(), 
                    this.receiver);
            if (clientDto.isFlag()) {
                this.clients.put(key, client);
                if (key.equalsIgnoreCase(MyConst.AE_SERVER_NAME)) {
                    AeClientRunner.getInstance().run(client);
                } else {
                    new ClientRunner(client).start();
                }
            }
        }
        if (this.clients.isEmpty() && !isServerRuning()) {
            this.gui.setSocketBackgroup(Color.RED);
            return;
        }
        StringBuilder builder = new StringBuilder();
        while (true) {
            boolean rs = false;
            if (!builder.isEmpty()) {
                builder.delete(0, builder.length());
            }
            builder.append("<html>");
            if (isServerRuning()) {
                builder.append(String.format("Server on \"%s\"<br>", this.socketDto.getServer().getPort()));
                var clients = this.server.getHandlerManager().getClientNames();
                if (clients.isEmpty()) {
                    builder.append("No client connected!<br>");
                } else {
                    for (String name : clients) {
                        builder.append(String.format("Client: %s<br>", name));
                    }
                }
            }
            builder.append("<br>Server:<br>");
            for (Map.Entry<String, SocketClient> entry : clients.entrySet()) {
                String key = entry.getKey();
                SocketClient val = entry.getValue();
                if (val.isConnect()) {
                    rs = true;
                }
                builder.append(String.format("%s: %s - %s, %s<br>",
                        key,
                        val.getHost(), val.getPort(),
                        val.isConnect() == true ? "ON" : "OFF"));
            }
            builder.append("</html>");
            this.gui.setSocketToolTip(builder.toString());
            if (rs) {
                this.gui.setSocketBackgroup(Color.GREEN);
            } else {
                this.gui.setSocketBackgroup(Color.YELLOW);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
    }

    private boolean isServerRuning() {
        return this.server != null && this.isAlive();
    }

    private class ClientRunner extends Thread {

        private final SocketClient client;

        public ClientRunner(SocketClient client) {
            this.client = client;
        }

        @Override
        public void run() {
            while (true) {
                if (this.client.isConnect() || this.client.connect()) {
                    this.client.run();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
        }

    }
}
