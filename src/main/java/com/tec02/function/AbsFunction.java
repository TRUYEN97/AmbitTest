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
import com.tec02.function.model.FunctionConstructorModel;
import com.tec02.main.ErrorLog;
import com.tec02.main.ItemFunciton.ItemFunctionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JOptionPane;

/**
 *
 * @author Administrator
 */
public abstract class AbsFunction extends Absbase implements Runnable, IFunctionModel {

    private final static int SYSTEM_FUNCTION = -1;
    private final static int SIMPLE_FUNCTION = 0;
    private final static int SUB_FUNCTION = 1;
    private final static int ELEMENT_FUCTION = 2;
    protected final ItemErrorCode errorCode;
    private final AnalysisResult analysisResult;
    private final ConfigurationManagement configurationManagement;
    private final List< AbsFunction> subItems;
    private final ItemFunctionFactory functionManagement;
    protected FunctionConfig config;
    protected BaseFunction baseFunction;
    protected AnalysisBase analysisBase;
    protected FileBaseFunction fileBaseFunction;
    protected final Model model;
    protected boolean resultTest;
    protected boolean stop;
    private int functionType;
    private Thread thread;
    private int statusCode;
    private String message;
    private String spec;
    protected int retry;
    private final Integer begin;
    private final String limitName;
    private final String configName;

    public AbsFunction(FunctionConstructorModel constructorModel, int functionType) {
        super(constructorModel);
        this.functionType = functionType;
        this.subItems = new ArrayList<>();
        this.configurationManagement = ConfigurationManagement.getInstance();
        this.functionManagement = ItemFunctionFactory.getInsatance();
        this.errorCode = new ItemErrorCode();
        constructorModel = checkModel(constructorModel);
        this.model = constructorModel.getModel();
        this.config = constructorModel.getConfig();
        this.logger = constructorModel.getLogger();
        this.begin = constructorModel.getBegin();
        this.configName = constructorModel.getConfigName();
        if (this.dataCell != null) {
            this.limitName = this.dataCell.getNextItemName(config.getTest_name(), this.begin);
        } else {
            this.limitName = this.config.getTest_name();
        }
        this.analysisResult = new AnalysisResult(config, this.model, errorCode);
        this.resultTest = false;
        this.stop = false;
        this.statusCode = 0;
        this.retry = 0;
        this.baseFunction = new BaseFunction(constructorModel);
        this.analysisBase = new AnalysisBase(constructorModel);
        this.fileBaseFunction = new FileBaseFunction(constructorModel);
    }

    protected boolean isAllSubItemPass() {
        boolean rs = true;
        for (AbsFunction subItem : subItems) {
            if (!subItem.isPass()) {
                rs = false;
            }
        }
        return rs;
    }

    public AbsFunction(FunctionConstructorModel constructorModel) {
        this(constructorModel, SIMPLE_FUNCTION);
    }

    public FunctionConstructorModel getConstructorModel() {
        return FunctionConstructorModel.builder()
                .model(model)
                .uICell(uICell)
                .config(config)
                .logger(logger)
                .build();
    }

    private FunctionConstructorModel checkModel(FunctionConstructorModel constructorModel1) {
        Model md = new Model();
        FunctionConfig cf = getDefaultConfig();
        MyObjectMapper.update(cf, md);
        if (constructorModel1 == null) {
            constructorModel1 = FunctionConstructorModel.builder()
                    .model(md)
                    .config(cf)
                    .configName(cf.getTest_name())
                    .itemName(cf.getTest_name())
                    .logger(new FunctionLogger(md))
                    .build();
        } else {
            if (constructorModel1.getModel() == null) {
                constructorModel1.setModel(md);
            }
            if (constructorModel1.getConfig() == null) {
                constructorModel1.setConfig(cf);
                String name = cf.getTest_name();
                if (name == null || name.isBlank()) {
                    cf.setTest_name(constructorModel1.getItemName());
                }
            }
            if (constructorModel1.getLogger() == null) {
                constructorModel1.setLogger(new FunctionLogger(md));
            }
        }
        return constructorModel1;
    }

    @Override
    public boolean isSubItem() {
        return functionType == SUB_FUNCTION;
    }

    @Override
    public boolean isElementFucntion() {
        return functionType == ELEMENT_FUCTION;
    }

    protected <T extends AbsFunction> T createSubItem(Class<T> functionClass, String item) {
        return (T) createSubItem(functionClass.getSimpleName(), item);
    }

    protected <T extends AbsFunction> T createSubItem(String functionClassName, String item) {
        AbsFunction absFunction = this.functionManagement.getFunction(
                functionClassName, FunctionConstructorModel.builder()
                        .model(new Model())
                        .uICell(uICell)
                        .logger(logger)
                        .configName(config.getTest_name())
                        .itemName(item)
                        .begin(begin)
                        .build(), uICell, false);
        absFunction.functionType = SUB_FUNCTION;
        this.subItems.add(absFunction);
        return (T) absFunction;
    }

    protected AbsFunction createElementFunction(Class<? extends AbsFunction> functionClass) {
        return createElementFunction(functionClass.getSimpleName());
    }

    protected AbsFunction createElementFunction(String functionName) {
        AbsFunction absFunction = this.functionManagement.getFunction(functionName,
                getConstructorModel());
        absFunction.functionType = ELEMENT_FUCTION;
        return absFunction;
    }

    protected AbsBaseFunction getBaseFunction(Class<? extends AbsBaseFunction> functionClass) {
        return getBaseFunction(functionClass.getSimpleName());
    }

    protected AbsBaseFunction getBaseFunction(String functionName) {
        AbsBaseFunction absBaseFunction = this.functionManagement.getBaseFunction(functionName,
                getConstructorModel());
        return absBaseFunction;
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

    public void setConfig(FunctionConfig config) {
        MyObjectMapper.update(config, this.config);
        MyObjectMapper.update(this.config, this.model);
        this.model.reset();
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
            long timeout = (long) config.getTime_out() * 1000;
            begin();
            for (retry = 0; retry < times && !stop; retry++) {
                resultTest = false;
                model.reset();
                subItems.clear();
                if (retry > 0) {
                    addLog("-------------------------------------------");
                    addLog(PC, "----------------- retry %s ---------------", retry);
                }
                this.thread = new Thread(() -> {
                    if (this.config.isDebugCancellRun()
                            && isDebugMode()) {
                        addLog(PC, "Cancellation run in debug mode");
                        resultTest = true;
                    } else {
                        resultTest = test();
                    }
                });
                this.thread.start();
                this.thread.join(timeout);
                stop();
                checkResult();
                if (isPass()) {
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            addLog(ERROR, ex.getLocalizedMessage());
            ErrorLog.addError(this, String.format("%s - %s",
                    uICell.getName(), ex.getLocalizedMessage()));
            checkResult();
        } finally {
            end();
            threadPool.shutdown();
        }
    }

    public void stopNow() {
        this.stop = true;
        stop();
    }

    private synchronized void stop() {
        for (AbsFunction subFucn : subItems) {
            subFucn.stopNow();
        }
        while (thread != null && thread.isAlive()) {
            addLog(PC, "Try to stop testing!");
            this.thread.stop();
            try {
                this.thread.join(500);
            } catch (InterruptedException ex) {
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
    }

    protected void setResult(Object value) {
        this.model.setTest_value(String.valueOf(value));
    }

    private void end() {
        this.statusCode = 2;
        if (functionType == SIMPLE_FUNCTION && !isPass() && subItems.isEmpty()) {
            String failApi = this.config.getFailApiName();
            if (failApi != null && !failApi.isBlank()
                    && !failApi.equalsIgnoreCase(this.config.getTest_name())) {
                // addd default failed API item 
                AbsFunction defaulApiItem = createSubItem("VirFunction", failApi);
                defaulApiItem.runTest(1);
            }
        }
        for (AbsFunction function : subItems) {
            this.dataCell.addItemFunction(function);
            if (!function.isPass()) {
                this.dataCell.addFailedItemFunction(function);
            }
        }
        subItems.clear();
        if (!isPass()) {
            this.dataCell.addFailedItemFunction(this);
        }
        this.model.setFinishtime(System.currentTimeMillis());
    }

    protected void begin() {
        this.stop = false;
        this.resultTest = false;
        this.spec = null;
        this.model.setStarttime(System.currentTimeMillis());
        this.statusCode = 1;
        switch (functionType) {
            case SUB_FUNCTION -> {
                addLog(PC, "-------------- SubItem[%s] - Func[%s] --------------",
                        this.model.getTest_name(), getFunctionName());
            }
            case SIMPLE_FUNCTION, SYSTEM_FUNCTION -> {
                add(String.format("------------------ ITEM[%s] - FUNCTION[%s] - TIMEOUT[%s S] ------------------\r\n",
                        model.getTest_name(), model.getFunction(), config.getTime_out()));
            }
        }
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

    public Integer getOrderNumberItem() {
        String itemName = model.getTest_name();
        if (itemName.matches(".+_[0-9]+$")) {
            return Integer.getInteger(itemName.substring(itemName.lastIndexOf("_") + 1).trim());
        }
        return null;
    }
    
    protected boolean isDebugMode(){
        return dataCell.getAPImode().equalsIgnoreCase(MyConst.CONFIG.DEBUG);
    }

    public void checkResult() {
        if (isElementFucntion()) {
            return;
        }
        String value = this.model.getTest_value();
        if (spec != null) {
            if (this.config.isDebugCancellCheckSpec() && isDebugMode()) {
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
        this.logger.addLog("RESULT", "Value: \"%s\"", model.getTest_value());
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

    public final void updateConfig() {
        FunctionConfig cf = this.configurationManagement.getFunctionConfig(configName, limitName);
        if (cf != null) {
            cf.setTest_name(limitName);
            setConfig(cf);
        }
        ItemErrorCode err = this.configurationManagement.getErrorcode(limitName);
        if (err != null) {
            setErrorCode(err);
        } else if (functionType != SYSTEM_FUNCTION
                && this.configurationManagement.getSettingConfig().isShowMissingErrorcode()) {
            JOptionPane.showMessageDialog(null, String.format("Missing error code: %s", limitName));
        }
    }

}
