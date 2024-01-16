/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.main;

import com.tec02.common.Logger;
import com.tec02.configuration.model.itemTest.ItemConfig;
import com.tec02.function.AbsFunction;
import com.tec02.main.ItemFunciton.IItemFunction;
import com.tec02.main.modeFlow.ModeFlow;
import com.tec02.view.managerUI.UICell;
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

    public void setInput(String input) {
        if (isTesting() || input == null) {
            return;
        }
        this.input = input.toUpperCase();
        ModeFlow modeFlow = this.modeManagement.getModeFlow();
        this.rootFuture = this.pool.submit(() -> {
            try {
                uICell.getDataCell().reset();
                this.uICell.getDataCell().putData("sn", this.input);
                this.uICell.getDataCell().putData("mode", modeFlow.getAPIMode());
                uICell.getAbsSubUi().startTest();
                this.stop = false;
                List<ItemConfig> items;
                while ((items = modeFlow.getListItem()) != null && !this.stop) {
                    uICell.getDataCell().setTestColor(modeFlow.getTestColor());
                    if (runFlow(modeFlow, items)) {
                        modeFlow.nextToPassFlow();
                    } else {
                        uICell.getDataCell().setFailColor(modeFlow.getFailColor());
                        modeFlow.nextToFailedFlow();
                    }
                }
                waitForMultidone(500);
            } catch (Exception ex) {
                ex.printStackTrace();
                this.logger.addLog("Error", ex.getLocalizedMessage());
            } finally {
                uICell.getAbsSubUi().endTest();
                if (uICell.getDataCell().isPass()) {
                    uICell.resetFailedConsecutive();
                }
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

    private boolean runFlow(ModeFlow modeFlow, List<ItemConfig> items) throws InterruptedException, ExecutionException {
        Integer begin = modeFlow.getBegin();
        int looptimes = modeFlow.getLoop();
        Future currFt;
        AbsFunction function;
        for (int i = 0; i < looptimes && !this.stop; i++) {
            for (ItemConfig itemConfig : items) {
                function = this.functionFactory.getFunction(
                        itemConfig.getFunction(),
                        itemConfig.getTest_name(), begin);
                if (function == null) {
                    throw new RuntimeException(
                            String.format("Func: %s - %s, function not exists!",
                                    itemConfig.getFunction(), itemConfig.getTest_name()));
                }
                function.setFunctionManagement(functionFactory);
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

    private class Mark {

        private final Future future;
        private final AbsFunction function;

        public Mark(Future future, AbsFunction function) {
            this.future = future;
            this.function = function;
        }

    }

}
