/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.main;

import com.tec02.common.Logger;
import com.tec02.common.MyConst;
import com.tec02.configuration.model.itemTest.ItemConfig;
import com.tec02.configuration.module.iml.Socket.AeClientRunner;
import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.common.SaveLocalLog;
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ItemFunciton.ItemFunctionFactory;
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

    public static final int MODE_DEBUG_ITEM = 3;
    public static final int MODE_DEBUG = 2;
    public static final int MODE_ROOT = 1;
    private final ModeManagement modeManagement;
    private final ExecutorService pool;
    private final ExecutorService poolRun;
    private final UICell uICell;
    private final ItemFunctionFactory functionFactory;
    private final List<Mark> marks;
    private final Logger logger;
    private final Core core;
    private Thread stopThread;
    private Future rootFuture;
    private String input;
    private boolean stopTest;

    public UICellTester(UICell uICell) {
        this.uICell = uICell;
        this.modeManagement = ModeManagement.getInsatace();
        this.pool = Executors.newSingleThreadExecutor();
        this.poolRun = Executors.newFixedThreadPool(5);
        this.functionFactory = ItemFunctionFactory.getInsatance();
        this.core = Core.getInstance();
        this.marks = new ArrayList<>();
        this.logger = new Logger(String.format("log/CellTester/%s", uICell.getName()));
        this.stopTest = false;
    }
    
    public void setInput(String input, int moderun) {
        if (isTesting() || input == null) {
            return;
        }
        if (this.core.updateAvailable()) {
            System.exit(0);
        }
        this.input = input.toUpperCase();
        ModeFlow modeFlow = this.modeManagement.getModeFlow();
        this.rootFuture = this.pool.submit(() -> {
            this.stopTest = false;
            for (int i = 0; i < modeFlow.getLoop() && !this.stopTest; i++, modeFlow.reset()) {
                try {
                    String modeName = moderun > 1 ? "*TE-Debug" : this.modeManagement.getCurrentModeName();
                    String modeApi = moderun > 1 ? MyConst.CONFIG.DEBUG : modeFlow.getAPIMode();
                    this.uICell.getDataCell().reset();
                    this.uICell.getDataCell().start(this.input, modeName, modeApi);
                    /////////////////// call UI
                    this.uICell.getAbsSubUi().startTest();
                    List<ItemConfig> items;
                    while ((items = modeFlow.getListItem()) != null && !this.stopTest) {
                        this.uICell.getDataCell().setTestColor(modeFlow.getTestColor());
                        
                        //此组测试完后根据PASS/fail转换到其他组里
                        if (run(items, !modeFlow.isCoreGroup(), moderun)) {
                            modeFlow.nextToPassFlow();
                        } else {
                            this.uICell.getDataCell().setFailColor(modeFlow.getFailColor());
                            modeFlow.nextToFailedFlow();
                        }
                    }
                    waitForMultidone(50, this.marks, new TesterModel());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    this.logger.addLog("Error", ex.getLocalizedMessage());
                } finally {
                    saveLocalLog();
                    if (moderun == MODE_ROOT && this.uICell.getDataCell().isOnSFIS()) {
                        if (this.uICell.getDataCell().isPass()) {
                            this.uICell.addPassCount();
                        } else {
                            this.uICell.addTestFailCount();
                        }
                    } else if (moderun < MODE_DEBUG_ITEM && this.uICell.getDataCell().isPass()) {
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
        if (this.core.updateAvailable()) {
            System.exit(0);
        }
        this.rootFuture = this.pool.submit(() -> {
            this.stopTest = false;
            try {
                this.uICell.getDataCell().reset();
                this.uICell.getDataCell().start("", "**TE-Debug", MyConst.CONFIG.DEBUG);
                this.uICell.getAbsSubUi().startTest();
                this.uICell.getDataCell().setTestColor(Color.yellow);
                this.uICell.getDataCell().setFailColor(Color.red);
                run( allItem, true, MODE_DEBUG_ITEM);
                waitForMultidone(50, this.marks, new TesterModel());
            } catch (Exception ex) {
                ex.printStackTrace();
                this.logger.addLog("Error", ex.getLocalizedMessage());
            } finally {
                this.uICell.getDataCell().updateResultTest();
                this.uICell.getAbsSubUi().endTest();
            }
        });
    }

    private TesterModel taskHasFailed(List<Mark> marks, TesterModel testerModel) {
        List<Mark> hasDones = new ArrayList<>();
        try {
            for (Mark mark : marks) {
                if (mark.future.isDone()) {   //判断此项目测试完没
                    hasDones.add(mark);
                    if (!mark.function.isPass() || !mark.function.isAllSubItemPass()) { //判断此项目以及子项目有没有fail
                        testerModel.pass = false;
                        if (!mark.function.getConfig().isFail_continue()) {  //判断fail了要不要继续测试
                            testerModel.testAble = false;
                        }
                    }
                }
            }
        } finally {
            marks.removeAll(hasDones);
            if (marks != this.marks) {
                this.marks.removeAll(hasDones);
            }
        }
        return testerModel;
    }

    private boolean run(List<ItemConfig> items, boolean alwaysRun, int modeRun) throws InterruptedException, ExecutionException {

        Future currFt;
        AbsFunction function;
        if (modeRun < MODE_ROOT) {
            modeRun = MODE_ROOT;
        }
        List<Mark> localMarks = new ArrayList<>();  
        TesterModel testerModel = new TesterModel();
        for (ItemConfig itemConfig : items) {
            if (stopTest) {                 //lctrl+Q 强制停止
                break;
            }
            if (itemConfig.isStopLocalMutitack()) {
                stopMultitack(localMarks);        ////同组项目同时跑的项目添加到localMarks
            }
            if (itemConfig.isStopAllMutitack()) {
                stopMultitack(this.marks);     ////此组项测试完，同时跑的项目localMarks还未运行完成的转移到marks
            }
            taskHasFailed(this.marks, testerModel); ////检查有没有测试完以及fail 
            if (itemConfig.isWait_local_multi_done()) {   //要不要等待localMarks测试完
                waitForMultidone(50, localMarks, testerModel);   //50ms检查一次也检查有没有fail
            }
            if (itemConfig.isWait_multi_done()) {   //要不要等待marks测试完
                waitForMultidone(50, this.marks, testerModel); //50ms检查一次也检查有没有fail
            }
            
            // fail是不是继续测试&&ctrl+d fail继续测试配置参数is Core group（coreGroup）设置0&&Always run(alwaysRun)
            if ((!testerModel.testAble && !alwaysRun && !itemConfig.isAlwaysRun()) 
                    || modeRun > itemConfig.getModeRun()) {    //mode run(modeRun)模式（123）
                continue;
            }
            
            //new 測試項目
            function = this.functionFactory.getFunction(
                    itemConfig.getFunction(), uICell,
                    itemConfig.getItemName(),
                    itemConfig.getTest_name(), itemConfig.getBegin(), true);
            //如果方法沒有就自動創建空方法並報錯
            if (function == null) {
                function = new AbsFunction(FunctionConstructorModel.builder()
                        .uICell(uICell)
                        .limitName(itemConfig.getTest_name())
                        .configName(itemConfig.getItemName())
                        .begin(itemConfig.getBegin())
                        .build()) {

                    @Override
                    protected boolean test() {
                        String mess = String.format("Function: \"%s\" not exists!",
                                itemConfig.getFunction()
                        );
                        addLog(ERROR, mess);
                        setMessage(mess);
                        return false;
                    }

                    @Override
                    protected void createDefaultConfig(FunctionConfig config) {
                        config.setTest_name(itemConfig.getTest_name());
                        config.setFunction(itemConfig.getFunction());
                    }

                };
                uICell.getDataCell().addItemFunction(function, 1);
            }
            //添加到uICell
            uICell.getDataCell().putData(MyConst.MODEL.TEST_ITEM, function.getModel().getTest_name());
              //發給server測試內容信息的內容
            AeClientRunner.getInstance().sendDefaulDataToServer(uICell);
           
            //把項目添加到線程池裡， 線程池最多5個
            currFt = this.poolRun.submit(function);
            Mark mark = new Mark(currFt, function);
            localMarks.add(mark);
            this.marks.add(mark);
            if (!function.getConfig().isMulti()) {  //如果不是同时测试就要等项目测试完毕
                currFt.get();
            }
        }
        return taskHasFailed(this.marks, testerModel).pass;
    }

    private TesterModel waitForMultidone(int delay, List<Mark> marks, TesterModel testerModel) {
        delay = delay < 50 ? 50 : delay;
        while (!marks.isEmpty()) {
            taskHasFailed(marks, testerModel);
            if (marks.isEmpty()) {
                break;
            }
            try {
                Thread.sleep(delay);
            } catch (Exception ex) {
            }
        }
        return testerModel;
    }

    private void stopMultitack(List<Mark> marks) {
        for (Mark mark : marks) {
            mark.function.stopMultitacking();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
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
        return input;
    }

    public void stop() {
        if (!isTesting()) {
            return;
        }
        stopTest = true;
        this.stopThread = new Thread(() -> {
            while (this.rootFuture != null && !this.rootFuture.isDone()) {
                for (Mark mark : marks) {
                    mark.function.stopNow();
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
            SaveLocalLog saveLocalLog = new SaveLocalLog(
                    FunctionConstructorModel.builder()
                            .uICell(uICell)
                            .build());
            this.uICell.getDataCell().addItemFunction(saveLocalLog, 1);
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

    private class TesterModel {

        boolean pass = true;    //项目PASS标记
        boolean testAble = true;  //还可以继续测试标记
    }

}
