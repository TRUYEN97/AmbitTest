/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.common;

import com.tec02.Time.TimeBase;
import com.tec02.mylogger.MyLogger;
import java.io.IOException;
import lombok.NonNull;

/**
 *
 * @author Administrator
 */
public class Logger {

    private final MyLogger logger;
    private final String dir;

    public Logger() {
        this("log");
    }

    @NonNull
    public Logger(String dir) {
        this.logger = new MyLogger();
        this.logger.setSaveMemory(true);
        this.dir = dir;
    }

    public void addLog(String log) {
        this.logger.setFile(String.format("%s/%s/%s.txt",
                dir,
                getClass().getSimpleName(),
                new TimeBase().getDate()));
        try {
            this.logger.addLog(log);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void addLog(String key, Object... params) {
        this.logger.setFile(String.format("%s/%s/%s.txt",
                dir,
                getClass().getSimpleName(),
                new TimeBase().getDate()));
        try {
            this.logger.addLog(key, params);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
