/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module.iml.Socket;

import com.tec02.communication.socket.Unicast.Client.Client;
import com.tec02.communication.socket.Unicast.Server.Server;
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
    private final Map<String, Client> Clients;
    private final Gui gui;
    private final Receiver receiver;
    private Server server;

    public AmbitSocket() {
        this.socketDto = ConfigurationManagement.getInstance().getSocketConfig().getModel();
        this.Clients = new HashMap<>();
        this.gui = Gui.getInstance();
        this.receiver = new Receiver();
    }

    @Override
    public void run() {
        if (socketDto.getServer().isFlag() && (server == null || !server.isAlive())) {
            try {
                this.server = new Server(socketDto.getServer().getPort(), new Receiver());
                this.server.start();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        for (Map.Entry<String, SocketClientDto> entry : this.socketDto.getClients().entrySet()) {
            String key = entry.getKey();
            SocketClientDto clientDto = entry.getValue();
            Client client = new Client(clientDto.getHost(),
                    clientDto.getPort(),
                    this.receiver);
            if (clientDto.isFlag()) {
                this.Clients.put(key, client);
            }
        }
        if (this.Clients.isEmpty() && !isServerRuning()) {
            this.gui.setSocketBackgroup(Color.RED);
            return;
        }
        for (String name : this.Clients.keySet()) {
            new ClientRunner(this.Clients.get(name)).start();
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
            for (Map.Entry<String, Client> entry : Clients.entrySet()) {
                String key = entry.getKey();
                Client val = entry.getValue();
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

        private final Client client;

        public ClientRunner(Client client) {
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
