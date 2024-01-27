/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.main;

import com.tec02.common.Logger;
import com.tec02.common.MyConst;
import com.tec02.configuration.model.itemTest.ItemConfig;
import com.tec02.function.AbsFunction;
import com.tec02.function.impl.common.SaveLocalLog;
import com.tec02.main.ItemFunciton.IItemFunction;
import com.tec02.main.modeFlow.ModeFlow;
import com.tec02.view.managerUI.UICell;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author Administrator
 */
public class UICellTester {

    private final ModeManagement modeManagement;
    private final ExecutorService pool;
    private final ExecutorService poolRun;
    private final UICell uICell;
    private final IItemFunction functionFactory;
    private final List<Mark> marks;
    private final Logger logger;
    private Thread stopThread;
    private Future rootFuture;
    private String input;
    private boolean stop;

    public UICellTester(UICell uICell, IItemFunction functionFactory) {
        this.uICell = uICell;
        this.modeManagement = ModeManagement.getInsatace();
        this.pool = Executors.newSingleThreadExecutor();
        this.poolRun = Executors.newSingleThreadExecutor();
        this.functionFactory = functionFactory;
        this.marks = new ArrayList<>();
        this.logger = new Logger(String.format("log/CellTester/%s", uICell.getName()));
        this.stop = false;
    }

    public void setInput(String input, int moderun) {
        if (isTesting() || input == null) {
            return;
        }
        this.input = input.toUpperCase();
        ModeFlow modeFlow = this.modeManagement.getModeFlow();
        this.rootFuture = this.pool.submit(() -> {
            this.stop = false;
            for (int i = 0; i < modeFlow.getLoop() && !this.stop; i++) {
                boolean isCoreGroup = false;
                try {
                    String modeName = moderun > 1 ? "*TE-Debug" : this.modeManagement.getCurrentModeName();
                    String modeApi = moderun > 1 ? MyConst.CONFIG.DEBUG : modeFlow.getAPIMode();
                    this.uICell.getDataCell().reset();
                    this.uICell.getDataCell().start(this.input, modeName, modeApi);
                    this.uICell.getAbsSubUi().startTest();
                    List<ItemConfig> items;
                    while ((items = modeFlow.getListItem()) != null && !this.stop) {
                        if (!isCoreGroup) {
                            isCoreGroup = modeFlow.isCoreGroup();
                        }
                        this.uICell.getDataCell().setTestColor(modeFlow.getTestColor());
                        if (run(modeFlow.getBegin(), items, false, moderun)) {
                            modeFlow.nextToPassFlow();
                        } else {
                            this.uICell.getDataCell().setFailColor(modeFlow.getFailColor());
                            modeFlow.nextToFailedFlow();
                        }
                    }
                    waitForMultidone(500);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    this.logger.addLog("Error", ex.getLocalizedMessage());
                } finally {
                    saveLocalLog();
                    if (moderun == 1 && isCoreGroup
                            && !this.uICell.getDataCell().getAPImode()
                                    .equals(MyConst.CONFIG.DEBUG)) {
                        if (this.uICell.getDataCell().isPass()) {
                            this.uICell.addPassCount();
                        } else {
                            this.uICell.addTestFailCount();
                        }
                    } else if (moderun == 2 && this.uICell.getDataCell().isPass()) {
                        this.uICell.resetFailedConsecutive();
                    }
                    this.uICell.getAbsSubUi().endTest();
                }
            }
        });
    }

    public void runDebugItem(List<ItemConfig> allItem) {
        if (isTesting() || allItem == null || allItem.isEmpty()) {
            return;
        }
        this.rootFuture = this.pool.submit(() -> {
            this.stop = false;
            try {
                this.uICell.getDataCell().reset();
                this.uICell.getDataCell().start("","**TE-Debug", MyConst.CONFIG.DEBUG);
                this.uICell.getAbsSubUi().startTest();
                this.uICell.getDataCell().setTestColor(Color.yellow);
                if (!run(null, allItem, true, 3)) {
                    this.uICell.getDataCell().setFailColor(Color.red);
                }
                waitForMultidone(500);
            } catch (Exception ex) {
                ex.printStackTrace();
                this.logger.addLog("Error", ex.getLocalizedMessage());
            } finally {
                this.uICell.getDataCell().updateResultTest();
                this.uICell.getAbsSubUi().endTest();
            }
        });
    }

    private boolean taskHasFailed() {
        List<Mark> hasDones = new ArrayList<>();
        try {
            boolean rs = true;
            for (Mark mark : marks) {
                if (mark.future.isDone()) {
                    hasDones.add(mark);
                    if (!mark.function.isPass()) {
                        rs = false;
                    }
                }
            }
            return rs;
        } finally {
            marks.removeAll(hasDones);
        }
    }

    private boolean run(Integer begin, List<ItemConfig> items, boolean alwaysRun, int modeRun) throws InterruptedException, ExecutionException {
        Future currFt;
        AbsFunction function;
        if (modeRun < 1) {
            modeRun = 1;
        }
        for (ItemConfig itemConfig : items) {
            if (stop && !alwaysRun
                    && !itemConfig.isAlwaysRun()) {
                continue;
            }
            if (modeRun > itemConfig.getModeRun()) {
                continue;
            }
            function = this.functionFactory.getFunction(
                    itemConfig.getFunction(),
                    itemConfig.getTest_name(), begin);
            currFt = this.poolRun.submit(function);
            if (function.getConfig().isWait_multi_done()) {
                waitForMultidone(50);
            }
            this.marks.add(new Mark(currFt, function));
            if (!function.getConfig().isMulti()) {
                currFt.get();
                if (!function.isPass()) {
                    return false;
                }
            }
        }
        return taskHasFailed();
    }

    private boolean waitForMultidone(int delay) {
        boolean rs = true;
        delay = delay < 50 ? 50 : delay;
        while (!marks.isEmpty()) {
            if (!taskHasFailed()) {
                rs = false;
            }
            if (marks.isEmpty()) {
                break;
            }
            try {
                Thread.sleep(delay);
            } catch (Exception ex) {
            }
        }
        return rs;
    }

    public String getCurrentModeName() {
        if (isTesting()) {
            return this.uICell.getDataCell().get(MyConst.MODEL.MODE_NAME);
        }
        return this.modeManagement.getCurrentModeName();
    }

    public String getInput() {
        if (!isTesting()) {
            input = null;
        }
        return input.toUpperCase();
    }

    public void stop() {
        if (!isTesting()) {
            return;
        }
        stop = true;
        this.stopThread = new Thread(() -> {
            while (this.rootFuture != null && !this.rootFuture.isDone()) {
                for (Mark mark : marks) {
                    mark.function.stop();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                }
            }
        });
        this.stopThread.start();
    }

    public boolean isTesting() {
        return this.rootFuture != null && !this.rootFuture.isDone()
                || (stopThread != null && stopThread.isAlive());
    }

    private void saveLocalLog() {
        try {
            this.uICell.getDataCell().updateResultTest();
            SaveLocalLog saveLocalLog = new SaveLocalLog();
            saveLocalLog.setUICell(uICell);
            this.uICell.getDataCell().addItemFunction(saveLocalLog);
            this.poolRun.submit(saveLocalLog).get();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(e.getLocalizedMessage());
        }
    }

    private class Mark {

        private final Future future;
        private final AbsFunction function;

        public Mark(Future future, AbsFunction function) {
            this.future = future;
            this.function = function;
        }

    }

}
