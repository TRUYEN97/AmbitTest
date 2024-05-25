/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.baseFunction;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.common.MyConst;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.communication.Communicate.AbsStreamReadable;
import com.tec02.communication.Communicate.ISender;
import com.tec02.communication.Communicate.Impl.Cmd.Cmd;
import com.tec02.communication.Communicate.Impl.Comport.ComPort;
import com.tec02.communication.Communicate.Impl.FtpClient.FtpClient;
import com.tec02.communication.Communicate.Impl.Telnet.Telnet;
import com.tec02.communication.DHCP.DhcpData;
import com.tec02.function.AbsBaseFunction;
import com.tec02.function.model.FunctionConstructorModel;
import java.io.IOException;

/**
 *
 * @author Administrator
 */
public class BaseFunction extends AbsBaseFunction {

    private final AnalysisBase analysisBase;
    private static final String PING_LINUX = "ping -c 2 %s";
    private static final String PING_WINDOWS = "ping %s -n 1";

    public BaseFunction(FunctionConstructorModel constructorModel) {
        super(constructorModel);
        analysisBase = new AnalysisBase(constructorModel);
    }

    public FtpClient initFtp(String user, String passWord, String host, int port) throws IOException {
        addLog("PC", "Connect to ftp!!");
        addLog("Config", "User: " + user);
        addLog("Config", "PassWord: " + passWord);
        addLog("Config", "Host: " + host);
        addLog("Config", "Port: " + port);
        FtpClient ftp = FtpClient.getConnection(host, port, user, passWord);
        if (ftp == null) {
            addLog("PC", "Connect to ftp failed..");
            return null;
        }
        ftp.setDebug(true);
        addLog("PC", "Connect to ftp ok!!");
        return ftp;
    }

    public Telnet getTelnet() throws Exception {
        String ip = this.getIp();
        return getTelnet(ip);
    }

    public Telnet getTelnet(String ip) {
        return getTelnet(ip, 23, null);
    }

    public Telnet getTelnet(String ip, int port) {
        return getTelnet(ip, port, null);
    }

    public Telnet getTelnet(String ip, int port, AbsStreamReadable streamReadable) {
        Telnet telnet;
        if (streamReadable == null) {
            telnet = new Telnet();
        } else {
            addLog("PC", "Read input: " + streamReadable.getName());
            telnet = new Telnet(streamReadable);
        }
        addLog("PC", "Connect to ip: %s", ip);
        addLog("PC", "Port is: %s", port);
        telnet.setDebug(true);
        if (!BaseFunction.this.pingTo(ip, 120, false, true)) {
            addLog("PC", "Ping to \"%s\" failed!", ip);
            addLog("PC", "Telnet to IP: \"%s\" - Port: %s failed!", ip, port);
            return null;
        }
        if (!telnet.connect(ip, port) && !BaseFunction.this.sendCommand(telnet, "\r\n")) {
            addLog("PC", "Telnet to IP: \"%s\" - Port: %s failed!", ip, port);
            return null;
        }
        addLog("PC", "Telnet to IP: \"%s\" - Port: %s ok!", ip, port);
        String response = telnet.readUntil(new TimeS(5), "root@eero-test:/#");
        addLog("Telnet", response);
        if (response == null) {
            addLog("PC", "Telnet did not respond!");
            return null;
        }
        return telnet;
    }

    public String getIp() throws Exception {
        if (this.uICell.getCore().isMultiUICell() && !config.get(NON_DHCP, false)) {
            String ip = null;
            if (this.dhcpDto.isOn()) {
                String mac = this.dataCell.get(MyConst.MODEL.MAC);
                if (mac == null) {
                    addLog("It's DHCP mode but MAC is null!");
                } else {
                    addLog("Setting", "MAC length = %s", DhcpData.getInstance().getMACLength());
                    ip = DhcpData.getInstance().getIP(mac);
                    addLog("PC", "Get IP from the DHCP: \"%s\" - \"%s\"", mac, ip);
                }
            } else {
                ip = dataCell.getString("ip");  //sfis
                addLog("PC", "Get IP from CellModel: %s", ip);
            }
            if (ip == null) {
                ip = String.format("%s.%s", DhcpData.getInstance().getNetIP(), uICell.getId());//获取固定IP uICell.getId()
                addLog("PC", "Get IP with cell_id: %s", ip);
            }
            return ip;
        }
        addLog("Get IP from the function config with key is \"IP\".");
        return config.getString(IP);
    }

    public String getComportName() {
        int com = this.config.get(COMPORT, 1);
        com = com < 1 ? 1 : com;
        if ((this.uICell.isMultiUI() || this.dhcpDto.isOn())
                && !config.get(NON_DHCP, false)) {
            int port = com + this.uICell.getId();
            return String.format("COM%d", port);
        } else {
            return String.format("COM%d", com);
        }
    }
    public static final String COMPORT = "comport";
    public static final String BAUDRATE = "baudrate";
    public static final String TYPE = "type";
    public static final String NON_DHCP = "nonDHCP";
    public static final String IP = "IP";

    public ComPort getComport() {
        String com = this.getComportName();
        int baudrate = this.config.get(BAUDRATE, 115200);
        return this.getComport(com, baudrate);
    }

    public ComPort getComport(String com, Integer baudrate) {
        ComPort comPort = new ComPort();
        comPort.setDebug(true);
        addLog(CONFIG, "comport: %s -- baudrate: %s", com, baudrate);
        if (!comPort.connect(com, baudrate)) {
            addLog(PC, String.format("Connect to %s failed!", com));
            return null;
        }
        return comPort;
    }

    public AbsCommunicate getTelnetOrComportConnector() throws Exception {
        String type = this.config.get(TYPE, "telnet");
        AbsCommunicate absCommunicate;
        if (type.equalsIgnoreCase(COMPORT)) {
            absCommunicate = this.getComport();
        } else {
            absCommunicate = this.getTelnet();
        }
        if (absCommunicate == null) {
            throw new NullPointerException("Connector is null!");
        }
        return absCommunicate;
    }

    public boolean networkCardControl(String cardName, boolean enable) {
        try ( Cmd cmd = new Cmd()) {
            String command = String.format("netsh interface set interface \"%s\" admin=%s", cardName, enable ? "enabled" : "disabled");
            if (!this.sendCommand(cmd, command)) {
                return false;
            }
            String responce = cmd.readAll();
            addLog(CMD, responce);
            return responce.isBlank();
        } catch (Exception e) {
            e.printStackTrace();
            addLog(ERROR, e.getLocalizedMessage());
            return false;
        }
    }

    public boolean rebootSoft(String ip, String cmd, int waitTime, int pingTime) throws IOException {
        try ( Telnet telnet = getTelnet(ip, 23)) {
            if (telnet == null) {
                return false;
            }
            if (!BaseFunction.this.sendCommand(telnet, cmd == null ? "reboot" : cmd)) {
                return false;
            }
            addLog("PC", "Wait about %s S", waitTime);
            if (sendRebootDUT(waitTime, ip) && pingTo(ip, pingTime)) {
                addLog("PC", "*************** Reboot soft ok! *********************");
                return true;
            }
            addLog("PC", "*************** Reboot soft failed! *********************");
            return false;
        }
    }

    public boolean sendCommand(final AbsCommunicate comport, String startPushCmd, String readUntils, int time) {
        if (!this.sendCommand(comport, startPushCmd)) {
            return false;
        }
        return this.analysisBase.isResponseContainKeyAndShow(comport,
                readUntils, readUntils, new TimeS(time));
    }

    private boolean sendRebootDUT(int waitTime, String ip) {
        TimeS waitToSleepTime = new TimeS(waitTime);
        do {
            if (!pingTo(ip, 1)) {
                addLog("PC", "*************** Shut down ok! [%.3f S]*****************", waitToSleepTime.getTime());
                return true;
            }
            delay(1000);
        } while (waitToSleepTime.onTime());
        addLog("PC", "*************** Shut down failed! [%.3f S]*****************", waitToSleepTime.getTime());
        return false;
    }

    public String getMac() {
        String mac = this.dataCell.get(MyConst.MODEL.MAC);
        if (mac == null || ((mac.length() != 17 && mac.contains(":")) || (mac.length() != 12 && !mac.contains(":")))) {
            addLog("MAC is invalid: " + mac);
            return null;
        }
        addLog("Get mac= " + mac);
        return mac;
    }

    public boolean insertCommand(final ISender sender, String command) {
        if (sender == null) {
            return false;
        }
        String name = sender.getName();
        addLog(name, "insert command: " + command);
        if (command == null || !sender.insertCommand(command)) {
            addLog(name, "insert command \" %s \" failed!", command);
            return false;
        }
        return true;
    }

    public boolean sendCommand(final ISender sender, String command) {
        if (sender == null) {
            return false;
        }
        String name = sender.getName();
        addLog(name, "Send command: " + command);
        if (command == null || !sender.sendCommand(command)) {
            addLog(name, "send command \" %s \" failed!", command);
            return false;
        }
        return true;
    }

    public boolean dutPing(String targetIp, int time) throws Exception {
        return this.dutPing(getIp(), targetIp, time, true);
    }

    public boolean dutPing(String ip, String targetIp, int time) {
        return this.dutPing(ip, targetIp, time, true);
    }

    public boolean dutPing(String ip, String targetIp, int time, boolean checkPing) {
        if (ip == null) {
            addLog(ERROR, "IP == null ");
            return false;
        }
        if (time <= 0) {
            addLog(ERROR, "Ping time out <= 0: %s", time);
            return false;
        }
        try (final Telnet telnet = new Telnet()) {
            if (!telnet.connect(ip, 23)) {
                addLog("PC", "Telnet to IP: \"%s\" - Port: %s failed!", ip, 23);
                return false;
            }
            String readUntil = "root@eero-test:/#";
            telnet.readUntil(new TimeS(5), readUntil);
            String name = telnet.getName();
            addLog(name, "Ping to IP: %s - %s S", targetIp, time);
            boolean pingRs;
            TimeS timeS = new TimeS(time);
            try {
                String command = String.format(PING_LINUX, ip);
                for (int i = 1; i == 1 || timeS.onTime(); i++) {
                    addLog(name, "------------------------------------ " + i);
                    try {
                        if (!this.sendCommand(telnet, command)) {
                            return false;
                        }
                        String repsonce = telnet.readUntil(readUntil);
                        addLog(telnet.getName(), repsonce);
                        pingRs = repsonce.contains("ttl=");
                        if (checkPing) {
                            if (pingRs) {
                                return true;
                            }
                        } else {
                            if (!pingRs) {
                                return false;
                            }
                        }
                    } finally {
                        addLog(name, "------------------------------------");
                    }
                }
            } finally {
                addLog(PC, "ping time: %.3f S", timeS.getTime());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return !checkPing;
    }

    public boolean pingTo(String ip, int times) {
        return BaseFunction.this.pingTo(ip, times, !this.dhcpDto.isOn(), true);
    }

    public boolean pingTo(String ip, int times, boolean checkPing) {
        return BaseFunction.this.pingTo(ip, times, !this.dhcpDto.isOn(), checkPing);
    }

    public boolean pingTo(String ip, int time, boolean arp_d, boolean checkPing) {
        if (ip == null) {
            addLog(ERROR, "IP == null ");
            return false;
        }
        if (time <= 0) {
            addLog(ERROR, "Ping time out <= 0: %s", time);
            return false;
        }
        addLog(PC, "Ping to IP: %s - %s S", ip, time);
        String arp = "arp -d";
        TimeS timer = new TimeS(time);
        try ( Cmd cmd = new Cmd()) {
            try {
                String command;
                if (cmd.isWindowsOs()) {
                    command = String.format(PING_WINDOWS, ip);
                } else {
                    command = String.format(PING_LINUX, ip);
                }
                for (int i = 1; timer.onTime(); i++) {
                    addLog(CMD, "------------------------------------ " + i);
                    try {
                        if (arp_d && BaseFunction.this.sendCommand(cmd, arp)) {
                            addLog(CMD, cmd.readAll().trim());
                        }
                        if (BaseFunction.this.sendCommand(cmd, command)) {
                            String response = cmd.readAll().trim();
                            addLog(CMD, response);
                            if (checkPing) {
                                if (response.contains("TTL=") || response.contains("ttl=")) {
                                    return true;
                                }
                            } else {
                                if (!response.contains("TTL=") || response.contains("ttl=")) {
                                    return false;
                                }
                            }
                        } else {
                            break;
                        }
                    } finally {
                        addLog(CMD, "------------------------------------");
                    }
                }
            } finally {
                addLog("PC", "ping time: %.3f S", timer.getTime());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return !checkPing;
    }

    public void delay(int i) {
        if (i > 0) {
            try {
                Thread.sleep(i);
            } catch (Exception e) {
            }
        }
    }
}
