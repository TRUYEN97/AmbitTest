/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.uploadLog.mydas;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

/**
 *
 * @author Administrator
 */
public class MydasClient {

    String ip = "10.117.34.34";
    static int port = 11565;
    Socket socket = null;
    String rev = "";
    static int maxSendLen = 1000;
    public String ClientName = null;//50            
    public String Version = null;//30 
    public String TitleVersion = null;//32           
    public String Product = null;//20
    public String TestSta = null;//20
    public String PN = null;//20
    public int W_R_flag = 0;
    public int PackSeq = 0;//DATA packet sequence
    public int TotalLen = 0;//total data packet size           
    public String Data = null;//1000
    public int len = 0;//transfer data length,the value must be < 1000 byte
    public int OpMode = 0;//index the operation mode 0: query mode 1:SRQ(Service Requirement Query)Mode 
    public int ExitFlag = 0;//we use this flag in SRQ mode, when this flag set to 0,the sevice thread will be exit.
    public boolean conFlag = false;

    public MydasClient(String ip, String hostName, String flowVer, String titleVer, String uut, String sta, String pn) {
        this.ip = ip;
        ClientName = hostName;
        Version = flowVer;
        TitleVersion = titleVer;
        Product = uut;
        TestSta = sta;
        PN = pn;
        W_R_flag = 0;
        TotalLen = 0;
        Data = "";
        len = 0;
        OpMode = 1;
        ExitFlag = 1;
        rev = "";
    }

    public void initClientInfo() {
        rev = "";
        Data = "";
        len = 0;
        TotalLen = 0;
    }

    public int connectPts() {
        try {
            socket = new Socket();
            SocketAddress sa = new InetSocketAddress(ip, port);
            socket.connect(sa, 10000);
            return 1;
        } catch (SocketTimeoutException ex) {
            return 0;
        } catch (IOException ex) {
            return 0;
        }
    }

    private String adjustString(String strTemp, int strLength) {
        if (strTemp == null) {
            return "";
        }
        int strLen = strTemp.length();
        if (strLen > strLength) {
            strTemp = strTemp.substring(0, strLength);
        }
        while (strLen < strLength) {
            strTemp += "\0";
            strLen = strTemp.length();
        }
        return strTemp;
    }

    public void adjustData() {
        ClientName = adjustString(ClientName, 50);
        Version = adjustString(Version, 30);
        TitleVersion = adjustString(TitleVersion, 32);
        Product = adjustString(Product, 20);
        TestSta = adjustString(TestSta, 20);
        PN = adjustString(PN, 20);
        Data = adjustString(Data, 1000);
    }

    public String adjustInteger(int data, int len) {

        byte[] bytes = new byte[4];
        bytes[3] = (byte) ((data >> 24) & 0xFF);
        bytes[2] = (byte) ((data >> 16) & 0xFF);
        bytes[1] = (byte) ((data >> 8) & 0xFF);
        bytes[0] = (byte) (data & 0xFF);

        String ret = "";
        try {
            ret = new String(bytes, "ISO-8859-1");
        } catch (UnsupportedEncodingException ex) {
        }

        return ret;
    }

    public int send() {
        adjustData();
        String str = ClientName + Version + TitleVersion + Product + TestSta + PN + adjustInteger(W_R_flag, 4)
                + adjustInteger(PackSeq, 4) + adjustInteger(TotalLen, 4) + Data + adjustInteger(len, 4)
                + adjustInteger(OpMode, 4) + adjustInteger(ExitFlag, 4);
        int rs = 0;
        byte[] abc;
        try {
            abc = str.getBytes("ISO-8859-1");
            try {
                socket.getOutputStream().write(abc);
                rs = 1;
            } catch (IOException ex) {
            }
        } catch (UnsupportedEncodingException ex) {
        }

        return rs;
    }

    public void setData(String data, int flag) {
        rev += data + "\0";
    }

    public int sendPTS() {
        int num = 0;

        num = rev.length() / maxSendLen;
        int leftNum = rev.length() % maxSendLen;
        TotalLen = rev.length();
        //WriteLog("num = " + num + ",leftNum = "+leftNum+", data = " + Data);
        if (num == 0) {
            if (leftNum > 0) {
                Data = rev;
                PackSeq = 0;
                len += rev.length();
                if (send() == 0) {
                    return 0;
                }
            }
        } else {
            for (int i = 0; i < num; i++) {
                len = maxSendLen;
                Data = rev.substring(maxSendLen * i, maxSendLen * i + maxSendLen);
                PackSeq = i;
                //WriteLog("num = " + num + ", data = " + Data);

                if (send() == 0) {
                    return 0;
                }
            }
            if (leftNum > 0) {

                len = TotalLen - maxSendLen * num;
                Data = rev.substring(maxSendLen * num);
                PackSeq = num;
                //WriteLog("leftNum > 0, data = "+ Data);
                if (send() == 0) {
                    return 0;
                }
            }
        }

        try {
            socket.close();
        } catch (Exception ex) {
        }
        return 1;
    }
}
