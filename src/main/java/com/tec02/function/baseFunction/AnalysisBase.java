/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.baseFunction;

import com.tec02.Time.WaitTime.AbsTime;
import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.common.Common;
import com.tec02.communication.Communicate.AbsCommunicate;
import com.tec02.communication.Communicate.IReadable;
import com.tec02.communication.Communicate.Impl.Cmd.Cmd;
import com.tec02.function.AbsBaseFunction;
import com.tec02.function.model.FunctionConstructorModel;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class AnalysisBase extends AbsBaseFunction {

    public AnalysisBase(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    public String getValue(IReadable readable, String regex, AbsTime time, String readUntil) {
        return getValue(readable, null, null, regex, time, readUntil);
    }

    public String getValue(IReadable readable, String startkey, String endkey, AbsTime time, String readUntil) {
        return getValue(readable, startkey, endkey, null, time, readUntil);
    }

    public String getValue(IReadable readable, String startkey, String endkey, String readUntil) {
        return getValue(readable, startkey, endkey, null, null, readUntil);
    }

    public String getLine(AbsCommunicate communicate, AbsTime timer, int i) {
        String line;
        for (int j = 0; timer.onTime(); j++) {
            line = communicate.readUntil(timer, "\n");
            if (j == i) {
                return line;
            }
        }
        return null;
    }

    public String getValue(IReadable readable, String startkey, String endkey,
            String regex, AbsTime time, String readUntil) {
        String line;
        String name = readable.getName();
        String value = null;
        try {
            if (time != null) {
                time.update();
            }
            while (time == null || time.onTime()) {
                line = this.getLine(time, readable);
                addLog(name, line == null ? "" : line);
                if (line == null) {
                    if (readable instanceof Cmd) {
                        return null;
                    }
                    continue;
                }
                if (regex != null && !regex.isBlank()) {
                    value = Common.findGroup(line, regex);
                } else {
                    value = Common.subString(line, startkey, endkey);
                }
                if (value != null || (readUntil != null && line.contains(readUntil))) {
                    break;
                }
            }
            return value;
        } finally {
            if (startkey != null) {
                addLog("CONFIG", "Start key: \"%s\"", startkey);
            }
            if (endkey != null) {
                addLog("CONFIG", "End key: \"%s\"", endkey);
            }
            if (regex != null) {
                addLog("CONFIG", "Regex: \"%s\"", regex);
            }
            addLog("PC", "Value: \"%s\"", value);
            if (time != null) {
                addLog("PC", "Time: \"%.3f/%.3f (S)\"", time.getTime(), time.getSpec());
            }
        }

    }

    private String getLine(AbsTime time, IReadable readable) {
        return time == null ? readable.readLine() : readable.readLine(time);
    }

    public boolean isResponseContainKey(IReadable readable, List<String> keyWords, TimeS timeS) {
        addLog("Config", String.format("keyWords: %s", keyWords));
        String line;
        String name = readable.getClass().getSimpleName();
        timeS.update();
        while (timeS.onTime()) {
            line = readable.readUntil(keyWords.toArray(String[]::new));
            addLog(name, line);
            if (line != null && keyWords.contains(line.trim())) {
                return true;
            }
        }
        return false;
    }

    public boolean isResponseContainKey(IReadable readable, String spec, String readUntil) {
        return AnalysisBase.this.isResponseContainKey(readable, spec, readUntil, null);
    }

    public boolean isResponseContainKey(IReadable readable, String spec, String readUntil, AbsTime time) {
        try {

            if (readUntil == null) {
                addLog("Config", "Read until == null !!");
                return false;
            }
            if (spec == null) {
                addLog("Config", "spec == null !!");
                return false;
            }
            String name = readable.getClass().getSimpleName();
            String result = readable.readUntil(time, readUntil);
            addLog(name, result);
            return result != null && result.contains(spec);
        } finally {
            addLog("CONFIG", String.format("Spec: \"%s\"", spec));
        }
    }

    public boolean isResponseContainKeyAndShow(IReadable readable, String spec, String readUntil, AbsTime time) {
        try {
            if (readUntil == null) {
                addLog("Config", "Read until == null !!");
                return false;
            }
            if (spec == null) {
                addLog("Config", "spec == null !!");
                return false;
            }
            String response = readShowUntil(readable, readUntil, time);
            if (response != null && response.contains(spec)) {
                return true;
            } else {
                addLog("PC", "Response no content spec");
                return false;
            }
        } finally {
            addLog("CONFIG", String.format("Spec: \"%s\"", spec));
        }
    }

    public String readUntilAndShow(IReadable readable, String until, AbsTime time) {
        if (readable == null) {
            addLog("Config", "readable == null !!");
            return null;
        }
        if (until == null) {
            addLog("Config", "spec == null !!");
            return null;
        }
        addLog("Config", "Time: " + time.getSpec());
        addLog("Config", "ReadUntil: " + until);
        String response = readable.readUntil(time, until);
        addLog(readable.getClass().getSimpleName(), response);
        return response;
    }

    public boolean contains(String response, List<String> specs) {
        if (response == null) {
            addLog(PC, "String data == null");
            return false;
        }
        addLog(CONFIG, "Check contains: %s", specs);
        for (String spec : specs) {
            if (response.contains(spec)) {
                return true;
            }
        }
        return false;
    }

    public boolean macthAll(String response, List<String> specs) {
        if (response == null) {
            addLog(PC, "String data == null");
            return false;
        }
        addLog(CONFIG, "Check contains: %s", specs);
        for (String spec : specs) {
            if (!response.contains(spec)) {
                addLog(PC, "Not contain \"%s\"", spec);
                return false;
            }
        }
        return true;
    }

    public String readShowUntil(IReadable readable, String readUntil, AbsTime time) {
        if (readable == null) {
            addLog("Config", "readable == null !!");
            return null;
        }
        if (time == null) {
            addLog("Config", "timer == null !!");
            return null;
        }
        addLog("Config", "Time: %s", time.getSpec());
        addLog("Config", "ReadUntil: %s", readUntil);
        String readableName = readable.getName();
        StringBuilder respose = new StringBuilder();
        time.update();
        while (time.onTime()) {
            String line = readable.readUntil(time, "\n", readUntil);
            addLog(readableName, line == null ? "" : line);
            respose.append(line).append("\r\n");
            if (readUntil != null && respose.toString().contains(readUntil)) {
                break;
            }
        }
        return respose.toString();
    }

    public Integer string2Integer(String value) {
        if (value == null) {
            addLog("ERROR", "Can't convert null to integer!");
            return null;
        }
        try {
            int result = Integer.parseInt(value);
            addLog("PC", "Convert sucessed! value: " + result);
            return result;
        } catch (NumberFormatException e) {
            addLog("ERROR", e.getLocalizedMessage());
            return null;
        }
    }

    public Double string2Double(String value) {
        if (value == null) {
            addLog("ERROR", "Can't convert null to Double!");
            return null;
        }
        try {
            double result = Double.parseDouble(value);
//            addLog("PC", "Convert sucessed! value: " + result);
            return result;
        } catch (NumberFormatException e) {
            addLog("ERROR", e.getLocalizedMessage());
            return null;
        }
    }

    public boolean isNumber(String value) {
        if (value == null) {
            addLog("PC", value + " is not a number");
            return false;
        }
        try {
            Double.valueOf(value);
            addLog("PC", value + " is a number");
            return true;
        } catch (NumberFormatException e) {
            addLog("PC", value + " is not a number");
            return false;
        }
    }
    
}
