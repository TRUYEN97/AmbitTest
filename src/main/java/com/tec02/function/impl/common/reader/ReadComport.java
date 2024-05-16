/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common.reader;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.communication.Communicate.Impl.Comport.ComPort;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.AbsSerialComport;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import javax.swing.Timer;

/**
 *
 * @author Administrator
 */
public class ReadComport extends AbsSerialComport {

    private boolean isHaveString = false;

    public ReadComport(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected void createConfig(FunctionConfig config) {
        config.setRetry(1);
        config.put(TIME, -1);
        config.put(WAIT_TIME, 5);
    }

    @Override
    protected boolean test() {
        try ( ComPort comport = this.baseFunction.getComport()) {
            if (comport == null) {
                setErrorCode("COM.OPEN", "COMPORT");
                return false;
            }
            int timeOut = this.config.getInteger(TIME, -1);
            TimeS timeS = null;
            if (timeOut > 0) {
                new Timer(timeOut * 1000, (e) -> {
                    stop = true;
                    comport.stopRead();
                }).start();
                timeS = new TimeS(timeOut);
            }
            ///////// stop multi
            setStopMultiTaskAction(() -> {
                stop = true;
                comport.stopRead();
            });
            //////////// check comport is readable
            int waitTime = this.config.getInteger(WAIT_TIME, 5);
            new Timer(waitTime * 1000, (e) -> {
                if (!isHaveString) {
                    stop = true;
                    comport.stopRead();
                }
            }).start();
            String line;
            while ((timeS != null && timeS.onTime() && !stop) || (timeS == null && !stop)) {
                line = comport.readLine();
                if (line != null) {
                    addLog(COMPORT_LOGKEY, line);
                    isHaveString = true;
                }
            }
            return isHaveString;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getMessage());
            return false;
        }
    }
    protected static final String WAIT_TIME = "waitTime";
    protected static final String TIME = "time";

}
