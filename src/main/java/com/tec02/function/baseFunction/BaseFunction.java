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

    public Telnet getTelnet() {
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
            addLog("PC", "Read input: " + streamReadable.getClass().getSimpleName());
            telnet = new Telnet(streamReadable);
        }
        String readUntil = this.config.get("ReadUntil");
        readUntil = readUntil == null ? "root@eero-test:/#" : readUntil;
        addLog("PC", "ReadUntil: %s", readUntil);
        addLog("PC", "Connect to ip: %s", ip);
        addLog("PC", "Port is: %s", port);
        telnet.setDebug(true);
        if (!pingTo(ip, 120)) {
            addLog("PC", "Ping to \"%s\" failed!", ip);
            addLog("PC", "Telnet to IP: \"%s\" - Port: %s failed!", ip, port);
            return null;
        }
        if (!telnet.connect(ip, port)) {
            addLog("PC", "Telnet to IP: \"%s\" - Port: %s failed!", ip, port);
            return null;
        }
        addLog("PC", "Telnet to IP: \"%s\" - Port: %s ok!", ip, port);
        String response = telnet.readUntil(new TimeS(10), readUntil);
        addLog("Telnet", response);
        if (response == null) {
            addLog("PC", "Telnet did not respond!");
            return null;
        }
        return telnet;
    }

    public String getIp() {
        if (this.dhcpDto.isOn() && !config.get("nonDHCP", false)) {
            String mac = this.dataCell.get(MyConst.MODEL.MAC);
            if (mac == null) {
                addLog("It's DHCP mode but MAC is null!");
                return null;
            }
            addLog("PC", "Get IP from the DHCP with MAC is \"%s\"", mac);
            addLog("Setting", "MAC length = %s", DhcpData.getInstance().getMACLength());
            return DhcpData.getInstance().getIP(mac);
        }
        addLog("Get IP from the function config with key is \"IP\".");
        return config.getString("IP");
    }

    public String getComportName() {
        Integer com = this.config.get("comport", 1);
        if (this.dhcpDto.isOn()) {
            int port = com + this.uICell.getId();
            return String.format("COM%d", port);
        } else {
            return String.format("COM%d", com);
        }
    }

    public ComPort getComport() {
        String com = this.getComportName();
        int baudrate = this.config.get("baudrate", 115200);
        addLog(COMPORT, "comport: %s -- baudrate: %s", com, baudrate);
        return this.getComport(com, baudrate);
    }

    public ComPort getComport(String com, Integer baud) {
        ComPort comPort = new ComPort();
        comPort.setDebug(true);
        addLog("ComPort", "Connect to: " + com);
        addLog("ComPort", "BaudRate is: " + baud);
        if (!comPort.connect(com, baud)) {
            addLog("ComPort", String.format("Connect %s failed", com));
            return null;
        }
        addLog("ComPort", String.format("Connect %s ok", com));
        return comPort;
    }

    public AbsCommunicate getTelnetOrComportConnector() throws Exception {
        String type = this.config.get("type", "telnet");
        AbsCommunicate absCommunicate;
        if (type.equalsIgnoreCase("comport")) {
            absCommunicate = this.getComport();
        } else {
            absCommunicate = this.getTelnet();
        }
        if (absCommunicate == null) {
            throw new NullPointerException("Connector is null!");
        }
        return absCommunicate;
    }

    public boolean rebootSoft(String ip, String cmd, int waitTime, int pingTime) throws IOException {
        try ( Telnet telnet = getTelnet(ip, 23)) {
            if (telnet == null) {
                return false;
            }
            if (!sendCommand(telnet, cmd == null ? "reboot" : cmd)) {
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

    public boolean sendcommad(final AbsCommunicate comport, String startPushCmd, String readUntils, int time) {
        if (!this.sendCommand(comport, startPushCmd)) {
            return true;
        }
        if (this.analysisBase.isResponseContainKeyAndShow(comport,
                readUntils, readUntils, new TimeS(time))) {
        }
        return false;
    }

    private boolean sendRebootDUT(int waitTime, String ip) {
        TimeS waitToSleepTime = new TimeS(waitTime);
        do {
            if (!pingTo(ip, 1)) {
                addLog("PC", "*************** Shut down ok! [%.3f S]*****************", waitToSleepTime.getTime());
                return true;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
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
        String name = sender.getClass().getSimpleName();
        addLog(name, "insert command: " + command);
        if (command == null || !sender.insertCommand(command)) {
            addLog(name, "insert command \" %s \" failed!", command);
            return false;
        }
        return true;
    }

    public boolean sendCommand(final ISender sender, String command) {
        String name = sender.getClass().getSimpleName();
        addLog(name, "Send command: " + command);
        if (command == null || !sender.sendCommand(command)) {
            addLog(name, "send command \" %s \" failed!", command);
            return false;
        }
        return true;
    }

    public boolean ubuntuPingTo(final AbsCommunicate communicate, String ip, int time) {
        return ubuntuPingTo(communicate, ip, time, true);
    }

    public boolean ubuntuPingTo(final AbsCommunicate communicate, String ip, int time, boolean checkPing) {
        if (communicate == null) {
            return false;
        }
        if (ip == null) {
            addLog(ERROR, "IP == null ");
            return false;
        }
        if (time <= 0) {
            addLog(ERROR, "Ping time out <= 0: %s", time);
            return false;
        }
        addLog(communicate.getName(), "Ping to IP: %s - %s S", ip, time);
        boolean pingRs;
        TimeS timeS = new TimeS(time);
        while (timeS.onTime()) {
            if (!this.sendCommand(communicate, String.format("ping -c 2 %s", ip))) {
                return false;
            }
            pingRs = this.analysisBase.isResponseContainKeyAndShow(communicate,
                    "ttl=", "root@eero-test:/#", timeS);
            if (checkPing) {
                if (pingRs) {
                    return true;
                }
            } else {
                if (!pingRs) {
                    return false;
                }
            }
        }
        return !checkPing;
    }

    public boolean pingTo(String ip, int times) {
        return pingTo(ip, times, !this.dhcpDto.isOn(), true);
    }

    public boolean pingTo(String ip, int times, boolean checkPing) {
        return pingTo(ip, times, !this.dhcpDto.isOn(), checkPing);
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
        Cmd cmd = new Cmd();
        String arp = "arp -d";
        String command = String.format("ping %s -n 1", ip);
        TimeS timer = new TimeS(time);
        try {
            for (int i = 1; timer.onTime(); i++) {
                addLog(CMD, "------------------------------------ " + i);
                try {
                    if (arp_d && sendCommand(cmd, arp)) {
                        addLog(CMD, cmd.readAll().trim());
                    }
                    if (sendCommand(cmd, command)) {
                        String response = cmd.readAll();
                        addLog(CMD, response.trim());
                        if (checkPing) {
                            if (response.contains("TTL=")) {
                                return true;
                            }
                        } else {
                            if (!response.contains("TTL=")) {
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
