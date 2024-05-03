/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.baseFunction;

import com.tec02.Time.WaitTime.AbsTime;
import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.Impl.Telnet.Telnet;
import com.tec02.communication.DHCP.MacUtil;
import com.tec02.mylogger.MyLogger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class BLE_Scanner {

    private static volatile BLE_Scanner instance;
    private final Map<String, Scanner> scanners;
    private final MyLogger myLogger;

    private BLE_Scanner() {
        this.scanners = new HashMap<>();
        this.myLogger = new MyLogger();
        this.myLogger.setSaveMemory(true);
        this.myLogger.setDailyLog(true);
        this.myLogger.setFile("log/MAC_Scanner");
    }

    public static BLE_Scanner getInstance() {
        BLE_Scanner ins = BLE_Scanner.instance;
        if (ins == null) {
            synchronized (BLE_Scanner.class) {
                ins = BLE_Scanner.instance;
                if (ins == null) {
                    BLE_Scanner.instance = ins = new BLE_Scanner();
                }
            }
        }
        return ins;
    }

    public void clear(String ip, String macExpection) {
        if (ip == null || macExpection == null) {
            return;
        }
        if (macExpection.length() > 16) {
            macExpection = macExpection.substring(0, 16);
        }
        macExpection = macExpection.toUpperCase();
        Scanner scanner;
        if (this.scanners.containsKey(ip) && (scanner = this.scanners.get(ip)).isAlive()) {
            scanner.macs.remove(macExpection);
        }
    }

    public ScannerData expect(String ip, String macExpection, AbsTime timer) {
        if (ip == null || macExpection == null) {
            return null;
        }
        if (macExpection.length() > 16) {
            macExpection = macExpection.substring(0, 16);
        }
        macExpection = macExpection.toUpperCase();
        Scanner scanner;
        if (!this.scanners.containsKey(ip) || !(scanner = this.scanners.get(ip)).isAlive()) {
            scanner = new Scanner(ip, 23);
            scanner.start();
            this.scanners.put(ip, scanner);
        }
        while (timer.onTime()) {
            if (scanner.macs.containsKey(macExpection)) {
                return scanner.macs.remove(macExpection);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }
        return null;
    }

    private class Scanner extends Thread {

        private final Telnet telnet;
        private final String ip;
        private final int port;
        private final LinkedHashMap<String, ScannerData> macs;

        public Scanner(String ip, int port) {
            this.telnet = new Telnet();
            this.ip = ip;
            this.port = port;
            this.macs = new LinkedHashMap<>();
        }

        @Override
        public void run() {
            try {
                if (!this.telnet.connect(ip, port)
                        || this.telnet.readUntil(new TimeS(10), TELNET_END_KEY) == null
                        || !sendCommand(new TimeS(40),
                                "startble\r\nQorvoBLEAPP\r\nscan 1", ">")) {
                    return;
                }
                String line;
                while (telnet.isConnect()) {
                    line = telnet.readUntil(TELNET_END_KEY, "\r\n");
                    if (line == null) {
                        continue;
                    }
                    if (line.equalsIgnoreCase(TELNET_END_KEY)
                            || line.contains("Segmentation fault")) {
                        if (!sendCommand(new TimeS(20),
                                "QorvoBLEAPP\r\nscan 1", ">")) {
                            return;
                        }
                    } else if (line.equals("pxScanResultCb:")) {
                        getMACValue(line);
                    }
                }
            } finally {
                scanners.remove(ip);
            }
        }

        private void getMACValue(String line) {
            StringBuilder builder = new StringBuilder();
            builder.append(line).append("\r\n");
            while (telnet.isConnect()) {
                line = telnet.readUntil(TELNET_END_KEY, "\r\n");
                if (line == null) {
                    continue;
                }
                builder.append(line).append("\r\n");
                if (line.startsWith("Peer address: ")) {
                    String mac = getMac(line.toUpperCase());
                    if (mac != null && !macs.containsKey(mac)) {
                        myLogger.addLog(ip, "\"%s\" -> \"%s\"", line, mac);
                        macs.put(mac.substring(0, 16), new ScannerData(builder, mac));
                    }
                }
                if (line.startsWith("adv data: ")
                        || line.contains(TELNET_END_KEY)
                        || line.contains("Segmentation fault")) {
                    break;
                }
            }
        }

        private String getMac(String line) {
            String address = MacUtil.macFormat(line.substring(14));
            if (address != null) {
                String[] elems = address.split(":");
                StringBuilder builder = new StringBuilder();
                for (int i = elems.length - 1; i >= 0; i--) {
                    builder.append(elems[i]).append(":");
                }
                builder.deleteCharAt(builder.length()-1);
                if (builder.length() >= 17) {
                    return builder.toString();
                }
            }
            return null;
        }
        private static final String TELNET_END_KEY = "root@eero-test:/#";

        private boolean sendCommand(AbsTime timer, String command, String readUntil) {
            if (!telnet.sendCommand(command)) {
                return false;
            }
            return this.telnet.readUntil(timer, readUntil) != null;
        }

    }
    
    public static class ScannerData{
        public final StringBuilder builder;
        public final String mac;

        public ScannerData(StringBuilder builder, String mac) {
            this.builder = builder;
            this.mac = mac;
        }
    }
}
