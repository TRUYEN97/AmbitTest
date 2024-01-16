/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tec02.view.subUI;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.function.IFunctionModel;
import com.tec02.main.dataCell.DataCell;
import com.tec02.view.Gui;
import com.tec02.view.managerUI.UICell;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author Administrator
 */
public abstract class AbsUI extends JPanel {

    protected final String name;
    private Thread threadUpdate;
    protected UICell uICell;
    protected Gui gui;
    protected List<? extends IFunctionModel> functionModels;
    protected DataCell dataCell;
    protected TimeS timeS;
    private final int timeDelay;

    protected AbsUI(String name, int timeDelay) {
        this.name = name;
        timeDelay = timeDelay < 100 ? 100 : timeDelay;
        this.timeDelay = timeDelay;
    }

    protected String getTestTime() {
        if (timeS == null) {
            return null;
        }
        long time = (long) (timeS.getTime());
        long hour = time / 3600;
        long minute = (time - hour * 3600) / 60;
        long second = time % 60;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public void startTest() {
        if (this.threadUpdate != null && threadUpdate.isAlive()) {
            this.threadUpdate.stop();
        }
        this.threadUpdate = new Thread() {
            @Override
            public void run() {
                while (true) {
                    updateData();
                    try {
                        Thread.sleep(timeDelay);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        };
        this.threadUpdate.start();
        this.timeS.start(0);
    }

    public void endTest() {
        if (this.threadUpdate != null && threadUpdate.isAlive()) {
            this.threadUpdate.stop();
        }
        this.timeS.stop();
        updateData();
    }

    public abstract void updateData();

    public void setUICell(UICell uiCell) {
        this.uICell = uiCell;
        this.functionModels = this.uICell.getDataCell().getItemFunctions();
        this.dataCell = this.uICell.getDataCell();
        this.timeS = this.uICell.getTimeS();
    }

    public UICell getUICell() {
        return uICell;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
