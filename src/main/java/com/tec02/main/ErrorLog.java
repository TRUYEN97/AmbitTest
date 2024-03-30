/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.main;

import com.tec02.Time.TimeBase;
import com.tec02.mylogger.MyLogger;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author 21AK22
 */
public class ErrorLog {

    private static final MyLogger loger = new MyLogger();

    static {
        String filePath = String.format("Log\\ErrorLog\\%s.txt",
                new TimeBase(TimeBase.UTC).getDate());
        ErrorLog.loger.setFile(new File(filePath));
        ErrorLog.loger.setSaveMemory(true);
    }

    public static void addError(String error) {
        try {
            ErrorLog.loger.addLog(error + "\r\n//////////////////////////////////////");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void addError(Object object, String error) {
        String mess = String.format("error in %s : %s",
                object.getClass().getName(), error);
        addError(mess);
    }

}
