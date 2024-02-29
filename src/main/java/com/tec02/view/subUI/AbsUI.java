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
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Administrator
 */
public abstract class AbsUI extends JPanel {

    protected final String name;
    protected UICell uICell;
    protected Gui gui;
    protected List<? extends IFunctionModel> functions;
    protected DataCell dataCell;
    protected TimeS timeS;
    private final Timer timer;

    protected AbsUI(String name, int timeDelay) {
        this.name = name;
        timeDelay = timeDelay < 100 && timeDelay > 0 ? 100 : timeDelay;
        this.functions = new ArrayList<>();
        this.timeS = new TimeS();
        this.timer = new Timer(timeDelay, (e) -> {
            updateData();
        });
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
        this.timeS.start(0);
        this.timer.start();
        updateData();
    }

    public void endTest() {
        this.timer.stop();
        this.timeS.stop();
        updateData();
    }

    public abstract void updateData();

    public void setUICell(UICell uiCell) {
        this.uICell = uiCell;
        this.dataCell = this.uICell.getDataCell();
        this.timeS = this.uICell.getTimeS();
        this.functions = this.dataCell.getFunctions(DataCell.ALL_ITEM);
    }

    public UICell getUICell() {
        return uICell;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
