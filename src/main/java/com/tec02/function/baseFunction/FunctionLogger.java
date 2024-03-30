/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.baseFunction;

import com.tec02.function.model.Model;
import com.tec02.Time.TimeBase;
import java.util.LinkedList;
import java.util.Queue;
import lombok.NonNull;

/**
 *
 * @author Administrator
 */
public class FunctionLogger {

    private Queue<String> testQueue;
    private StringBuffer testLogger;
    private final TimeBase timeBase;
    private final Model model;

    @NonNull
    public FunctionLogger(Model model) {
        this.testQueue = new LinkedList<>();
        this.testLogger = new StringBuffer();
        this.timeBase = new TimeBase();
        this.model = model;
    }
    
    public Queue<String> getTestQueue() {
        return testQueue;
    }

    public void addLog(Object format) {
        if (format == null) {
            add(String.format("%s:  null\r\n",
                    this.timeBase.getDateTime(TimeBase.DATE_TIME_MS)));
            return;
        }
        for (String line : format.toString().split("\n")) {
            add(String.format("%s:   %s\r\n",
                    this.timeBase.getDateTime(TimeBase.DATE_TIME_MS), line.trim()));
        }
    }
    public synchronized void addLog(String key, Object str){
        if (str == null) {
            add(String.format("%s:  [%s] null\r\n",
                    this.timeBase.getDateTime(TimeBase.DATE_TIME_MS), key));
            return;
        }
        for (String line : str.toString().split("\n")) {
            add(String.format("%s:   [%s] %s\r\n",
                    this.timeBase.getDateTime(TimeBase.DATE_TIME_MS), key, line.trim()));
        }
    }

    public void addLog(String format, Object... params) {
        String line;
        if (format != null) {
            String str = String.format(format, params);
            for (String log : str.split("\r\n|\r|\n")) {
                if (log.isBlank()) {
                    continue;
                }
                line = String.format("%s    %s\r\n",
                        this.timeBase.getSimpleDateTime(), log);
                add(line);
            }
        } else {
            line = String.format("%s    null\r\n",
                    this.timeBase.getSimpleDateTime());
            add(line);
        }
    }

    public void addLog(String key, String format, Object... params) {
        String line;
        if (format != null) {
            String str = String.format(format, params);
            for (String log : str.split("\r\n|\r|\n")) {
                if (log.isBlank()) {
                    continue;
                }
                line = String.format("[%s] %s", key, log);
                addLog(line);
            }
        } else {
            line = String.format("[%s] NULL", key);
            addLog(line);
        }
    }

    public synchronized void add(String line) {
        this.testLogger.append(line);
        this.testQueue.add(line);
    }

    public void clearTestLog() {
        if (this.testLogger.isEmpty()) {
            return;
        }
        this.testLogger.delete(0, this.testLogger.length() - 1);
        this.testQueue.clear();
    }

    public String getLog() {
        StringBuilder builder = new StringBuilder();
        builder.append(testLogger);
        builder.append(String.format("-------------------------- TIME[%.3f s] - STATUS[%S] --------------------------",
                model.getCycleTime() / 1000.0, model.getStatus()));
        return builder.toString();
    }

    public void replace(FunctionLogger logger) {
        this.testLogger = logger.testLogger;
        this.testQueue = logger.testQueue;
    }

}
