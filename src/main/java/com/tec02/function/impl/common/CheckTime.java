/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.Time.TimeBase;
import com.tec02.communication.Communicate.Impl.Cmd.Cmd;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author Administrator
 */
public class CheckTime extends AbsFunction {

    private final TimeBase timeBase;

    public CheckTime(FunctionConstructorModel constructorModel) {
        super(constructorModel);
        this.timeBase = new TimeBase();
    }

    @Override
    protected boolean test() {
        try {
            int spec = this.config.get("delta_time", 900);
            TimeZone tz = TimeZone.getTimeZone(this.config.get("timezone", "UTC"));
            String webUrl = this.config.get("wedUrl", "http://time.windows.com");
            addLog("CONFIG", "time zone: %s", tz.getDisplayName());
            addLog("CONFIG", "wedURl: %s", webUrl);
            String PatternDate = TimeBase.DATE_TIME_MS;
            SimpleDateFormat sdf = new SimpleDateFormat(PatternDate);
            TimeZone.setDefault(tz);
            sdf.setTimeZone(tz);
            Date wedDate = this.timeBase.getWebsiteDatetime(webUrl);
            String dateTime = sdf.format(wedDate);
            try ( Cmd cmd = new Cmd()) {
                this.baseFunction.sendCommand(cmd,
                        String.format("date %s", dateTime.split(" ")[0]));
                cmd.readLine();
                this.baseFunction.sendCommand(cmd,
                        String.format("time %s", dateTime.split(" ")[1]));
                cmd.readLine();
            } catch (Exception e) {
            }
            long wedms = wedDate.getTime();
            long pctime = System.currentTimeMillis();
            spec = spec * 1000;
            String timePc = getDateByFormatUTC(PatternDate);
            addLog("PC", "This time: %s - %s ms", timePc, pctime);
            addLog("PC", "spec time: %s - %s ms", dateTime, wedms);
            addLog("PC", "delta time: %s ms", spec);
            return Math.abs(wedms - pctime) < spec;
        } catch (Exception ex) {
            ex.printStackTrace();
            addLog("Error code", ex.getLocalizedMessage());
            return false;
        }
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.setRetry(1);
        config.setTime_out(10);
        config.setTest_name("checkTime");
        config.put("delta_time", 900);
        config.put("timezone", "UTC");
        config.put("wedUrl", "http://time.windows.com");
    }

    public String getDateByFormatUTC(String format) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Calendar cal_Two = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        String dateStr = "";
        try {
            SimpleDateFormat simFormat = new SimpleDateFormat(format);
            dateStr = simFormat.format(cal_Two.getTime());
        } catch (Exception e) {
        }
        return dateStr;
    }


}
