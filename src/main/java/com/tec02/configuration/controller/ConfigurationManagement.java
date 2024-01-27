/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.controller;

import com.alibaba.fastjson.JSONObject;
import com.tec02.common.Logger;
import com.tec02.common.MyObjectMapper;
import com.tec02.configuration.Iexecute;
import com.tec02.configuration.model.dhcp.DhcpDto;
import com.tec02.configuration.model.errorCode.ItemErrorCode;
import com.tec02.configuration.model.itemTest.ItemLimit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import lombok.NonNull;
import com.tec02.configuration.module.AbsModule;
import com.tec02.configuration.module.TabConfiguramentPanel;
import com.tec02.configuration.module.iml.Dhcp;
import com.tec02.configuration.module.iml.ErrorCode;
import com.tec02.configuration.module.iml.ItemTest;
import com.tec02.configuration.module.iml.Setting;
import com.tec02.configuration.module.iml.Socket.Socket;
import com.tec02.configuration.model.itemTest.ItemConfig;
import com.tec02.configuration.model.itemTest.ItemTestDto;
import com.tec02.configuration.model.setting.SettingDto;
import com.tec02.configuration.model.socket.SocketDto;
import com.tec02.function.baseFunction.FunctionConfig;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class ConfigurationManagement implements Iexecute {

    private static volatile ConfigurationManagement management;
    private final List<AbsModule> modules;
    private File file;
    private final Logger logger;
    private final TabConfiguramentPanel tabPanel;

    public static ConfigurationManagement getInstance() {
        ConfigurationManagement ins = ConfigurationManagement.management;
        if (ins == null) {
            synchronized (ConfigurationManagement.class) {
                ins = ConfigurationManagement.management;
                if (ins == null) {
                    ConfigurationManagement.management = ins = new ConfigurationManagement();
                }
            }
        }
        return ins;
    }

    public TabConfiguramentPanel getTabPanel() {
        return tabPanel;
    }

    private ConfigurationManagement() {
        this.modules = new ArrayList<>();
        addModel(new ErrorCode());
        addModel(new Setting(new SettingDto()));
        addModel(new Socket(new SocketDto()));
        addModel(new ItemTest(new ItemTestDto()));
        addModel(new Dhcp(new DhcpDto()));
        this.logger = new Logger("Log/ConfigurationManagement");
        this.tabPanel = new TabConfiguramentPanel(this);
    }

    public final void setFile(String filePath) throws FileNotFoundException {
        this.file = new File(filePath);
        if (!this.file.exists()) {
            String error = String.format("%s not found", this.file);
            this.logger.addLog(error);
            throw new FileNotFoundException(error);
        }
    }

    public List<AbsModule> getModules() {
        return modules;
    }

    public AbsModule getModel(String name) {
        if (name == null) {
            return null;
        }
        for (AbsModule module : modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public boolean init() {
        if (file == null) {
            return false;
        }
        try {
            JSONObject data = JSONObject.parseObject(Files.readString(file.toPath()));
            AbsModule module;
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String modelName = entry.getKey();
                if (entry.getValue() instanceof JSONObject moduleData) {
                    if ((module = this.getModel(modelName)) == null) {
                        String error = String.format("init(): \"%s\" model not found!", modelName);
                        this.logger.addLog(error);
                        return false;
                    }
                    module.setData(moduleData);
                    module.refesh();
                }
            }
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            String error = String.format("init(): %s", ex.getLocalizedMessage());
            this.logger.addLog(error);
            throw new RuntimeException(ex.getLocalizedMessage());
        }
    }

    @NonNull
    public final void addModel(AbsModule model) {
        this.modules.add(model);
    }

    public void updateModel() {
        for (AbsModule module : modules) {
            module.update();
        }
    }

    public JSONObject getInputModel() {
        JSONObject model = new JSONObject();
        try {
            for (AbsModule module : modules) {
                model.put(module.getName(), module.getInputModel());
            }
            return model;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ErrorCode getErrorCode() {
        return (ErrorCode) this.getModel(ErrorCode.class);
    }

    public ItemTest getItemTestConfig() {
        return (ItemTest) this.getModel(ItemTest.class);
    }

    public Dhcp getDhcpConfig() {
        return (Dhcp) this.getModel(Dhcp.class);
    }

    public Setting getSettingConfig() {
        return (Setting) this.getModel(Setting.class);
    }

    public Socket getSocketConfig() {
        return (Socket) this.getModel(Socket.class);
    }

    public AbsModule getModel(Class clazz) {
        return getModel(clazz.getSimpleName());
    }

    public FunctionConfig getFunctionConfig(String itemName) {
        return getFunctionConfig(itemName, itemName);
    }

    public FunctionConfig getFunctionConfig(String configName, String limitName) {
        FunctionConfig config = getItemTestConfig(configName);
        if (config == null) {
            return null;
        }
        ItemLimit itemLimit = getItemLimit(limitName);
        if (itemLimit != null) {
            MyObjectMapper.update(itemLimit, config);
        }
        return config;
    }

    private String getBaseItem(String itemName) {
        if (itemName.matches(".+_[0-9]+$")) {
            return itemName.substring(0, itemName.lastIndexOf("_"));
        }
        return itemName;
    }

    public FunctionConfig getItemTestConfig(String configName) {
        Map<String, ItemConfig> itemsCongfig = getItemTestConfig().getModel().getItems();
        ItemConfig itemConfig;
        if (itemsCongfig == null
                || ((itemConfig = itemsCongfig.get(configName)) == null
                && (itemConfig = itemsCongfig.get(getBaseItem(configName))) == null)) {
            return null;
        }
        return new FunctionConfig(itemConfig);
    }

    public ItemLimit getItemLimit(String itemName) {
        Map<String, ItemLimit> itemsLimit = getItemTestConfig().getModel().getLimits();
        ItemLimit itemLimit;
        if (itemsLimit == null
                || ((itemLimit = itemsLimit.get(itemName)) == null
                && (itemLimit = itemsLimit.get(getBaseItem(itemName))) == null)) {
            return null;
        }
        return itemLimit;
    }

    @Override
    public void execute() {
        for (AbsModule module : modules) {
            module.execute();
        }
    }

    public ItemErrorCode getErrorcode(String limitName) {
        Map<String, ItemErrorCode> errorcodes = this.getErrorCode().getModel().getErrorcodes();
        ItemErrorCode itemErrorCode;
        limitName = limitName.toUpperCase();
        if (errorcodes == null || ((itemErrorCode = errorcodes.get(limitName)) == null
                && (itemErrorCode = errorcodes.get(getBaseItem(limitName))) == null)) {
            return null;
        }
        return itemErrorCode;
    }

}
