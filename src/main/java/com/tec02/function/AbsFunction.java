/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tec02.configuration.model.itemTest.ItemConfig;
import com.tec02.common.MyConst;
import com.tec02.common.MyObjectMapper;
import com.tec02.configuration.controller.ConfigurationManagement;
import com.tec02.configuration.model.errorCode.ItemErrorCode;
import com.tec02.function.baseFunction.AnalysisBase;
import com.tec02.function.baseFunction.AnalysisResult;
import com.tec02.function.baseFunction.BaseFunction;
import com.tec02.function.baseFunction.FileBaseFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.baseFunction.FunctionLogger;
import com.tec02.function.baseFunction.Model;
import com.tec02.main.ErrorLog;
import com.tec02.main.ItemFunciton.IItemFunction;
import com.tec02.view.managerUI.UICell;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import lombok.NonNull;

/**
 *
 * @author Administrator
 */
public abstract class AbsFunction extends Absbase implements Runnable, IFunctionModel {

    protected FunctionConfig config;
    protected final Model model;
    protected final ItemErrorCode errorCode;
    private final AnalysisResult analysisResult;
    protected BaseFunction baseFunction;
    protected AnalysisBase analysisBase;
    protected FileBaseFunction fileBaseFunction;
    private IItemFunction functionManagement;
    private final ConfigurationManagement configurationManagement;
    protected boolean resultTest;
    protected boolean stop;
    protected Future future;
    private int statusCode;
    private String message;
    private String spec;
    protected int retry;
    private String configName;
    private String limitName;

    public AbsFunction() {
        this.configurationManagement = ConfigurationManagement.getInstance();
        this.config = getDefaultConfig();
        this.model = new Model();
        MyObjectMapper.update(this.config, this.model);
        this.errorCode = new ItemErrorCode();
        this.logger = new FunctionLogger(model);
        this.analysisResult = new AnalysisResult(config, model, errorCode);
        this.resultTest = false;
        this.stop = false;
        this.statusCode = 0;
        this.retry = 0;
    }

    @Override
    public void setUICell(UICell uICell) {
        super.setUICell(uICell);
        this.baseFunction = new BaseFunction(this.logger, this.config, uICell);
        this.analysisBase = new AnalysisBase(this.logger, this.config, uICell);
        this.fileBaseFunction = new FileBaseFunction(logger, config, uICell);
        init();
    }

    protected abstract void init();

    protected AbsFunction getSubItem(String functionName, String item) {
        AbsFunction absFunction = this.functionManagement.getFunction(
                functionName,
                config.getTest_name(),
                item);
        return absFunction;
    }

    @Override
    public Long getRunTime() {
        return this.model.getCycleTime();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public String toString() {
        return String.format("func: %s, item: %s",
                getClass().getSimpleName(),
                getConfig().getTest_name());
    }

    @Override
    public String getFunctionName() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean isWaiting() {
        return statusCode == 0;
    }

    @Override
    public boolean isTesting() {
        return statusCode == 1;
    }

    @Override
    public boolean isDone() {
        return statusCode == 2;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @NonNull
    public void setFunctionManagement(IItemFunction functionManagement) {
        this.functionManagement = functionManagement;
    }

    public void setConfig(FunctionConfig config) {
        MyObjectMapper.update(config, this.config);
        MyObjectMapper.update(this.config, this.model);
    }

    public void setErrorCode(ItemErrorCode errorCode) {
        MyObjectMapper.copy(errorCode, this.errorCode);
    }

    @Override
    public FunctionConfig getConfig() {
        return config;
    }

    public final FunctionConfig getDefaultConfig() {
        FunctionConfig defaultConfig = new FunctionConfig(new ItemConfig());
        createDefaultConfig(defaultConfig);
        defaultConfig.setFunction(getClass().getSimpleName());
        return defaultConfig;
    }

    @Override
    public void run() {
        runTest(config.getRetry() + 1);
    }

    public void runTest(int runtimes) {
        int times = runtimes < 1 ? 1 : runtimes;
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        try {
            begin();
            for (retry = 0; retry < times && !stop; retry++) {
                resultTest = false;
                model.reset();
                if (retry > 0) {
                    addLog("-------------------------------------------");
                    addLog(PC, "----------------- retry %s ---------------", retry);
                }
                this.future = threadPool.submit(() -> {
                    if (this.config.isDebugCancellRun()
                            && dataCell.getAPImode().equalsIgnoreCase(MyConst.CONFIG.DEBUG)) {
                        addLog(PC, "Cancellation run in debug mode");
                        resultTest = true;
                    } else {
                        resultTest = test();
                    }
                });
                this.future.get(this.config.getTime_out(), TimeUnit.SECONDS);
                checkResult();
                if (isPass()) {
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            addLog(ERROR, ex.getMessage());
            ErrorLog.addError(this, ex.getLocalizedMessage());
            checkResult();
        } finally {
            end();
            threadPool.shutdown();
        }
    }

    protected void setResult(Object value) {
        this.model.setTest_value(String.valueOf(value));
    }

    private void end() {
        this.statusCode = 2;
        this.model.setFinishtime(System.currentTimeMillis());
    }

    public void stop() {
        this.stop = true;
        if (this.future == null || this.future.isDone()) {
            return;
        }
        addLog(PC, "Stop testing!");
        while (!this.future.isDone()) {
            this.future.cancel(true);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }
        }
    }

    protected void begin() {
        this.stop = false;
        this.resultTest = false;
        this.spec = null;
        this.model.setStarttime(System.currentTimeMillis());
        this.logger.begin();
        this.statusCode = 1;
    }

    protected void setFunctionSpec(Object spec) {
        this.spec = String.valueOf(spec);
        addLog(PC, "Function spec: %s", spec);
    }

    public String getBaseItem() {
        String itemName = model.getTest_name();
        if (itemName.matches(".+_[0-9]+$")) {
            return itemName.substring(0, itemName.lastIndexOf("_"));
        }
        return itemName;
    }

    public void checkResult() {
        String value = this.model.getTest_value();
        if (spec != null) {
            String mode = dataCell.getAPImode();
            if (this.config.isDebugCancellCheckSpec()
                    && mode.equalsIgnoreCase(MyConst.CONFIG.DEBUG)) {
                addLog(PC, "Ignore compare in debug mode");
            } else {
                resultTest = value != null
                        && value.equalsIgnoreCase(spec);
                addLog(PC, "Compare: %s - %s", value, spec);
            }
        }
        this.analysisResult.checkResult(resultTest);
        this.logger.addLog("***************************************************");
        this.logger.addLog("RESULT", "Item name: \"%s\"", model.getTest_name());
        this.logger.addLog("RESULT", "Item type: \"%s\"", config.getLimit_type());
        this.logger.addLog("RESULT", "Upper limit: \"%s\"", model.getUpper_limit());
        this.logger.addLog("RESULT", "Lower limit: \"%s\"", model.getLower_limit());
        this.logger.addLog("RESULT", "Value: \"%s\"", this.model.getTest_value());
        this.logger.addLog("RESULT", "Status: \"%s\"", model.getStatus());
        this.logger.addLog("RESULT", "Error description: \"%s\"", model.getDescErrorcde());
        this.logger.addLog("RESULT", "Errorcode: \"%s\"", model.getErrorcode());
        this.logger.addLog("RESULT", "Custormer errorcode: \"%s\"", model.getError_code());
        this.logger.addLog("***************************************************");
    }

    protected abstract boolean test();

    protected abstract void createDefaultConfig(FunctionConfig config);

    @Override
    public boolean isPass() {
        String stt = model.getStatus();
        return stt != null && stt.equalsIgnoreCase(MyConst.MODEL.PASS);
    }

    public JSONObject getData(List<String> testKeys, boolean useLimitErrorCode) {
        try {
            JSONObject rs = new JSONObject();
            JSONObject data = JSONObject.parseObject(MyObjectMapper.writeValueAsString(model));
            for (String testKey : testKeys) {
                rs.put(testKey, data.get(testKey));
            }
            return rs;
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            addLog(ERROR, ex.getLocalizedMessage());
            return null;
        }
    }

    public void setConfigName(String configName, String limitName, Integer begin) {
        this.configName = configName;
        this.limitName = this.dataCell.getNextItemName(limitName, begin);
        this.uICell.getDataCell().addItemFunction(this);
    }

    public void updateConfig() {
        FunctionConfig cf = this.configurationManagement.getFunctionConfig(configName, limitName);
        if (cf != null) {
            cf.setTest_name(limitName);
            setConfig(cf);
        }
        ItemErrorCode err = this.configurationManagement.getErrorcode(limitName);
        if (err != null) {
            setErrorCode(err);
        } else if (this.configurationManagement.getSettingConfig().isShowMissingErrorcode()) {
            JOptionPane.showMessageDialog(null, String.format("Missing error code: %s", limitName));
        }
    }

}
