/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.controller;

import com.alibaba.fastjson2.JSONObject;
import com.tec02.common.Common;
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
import com.tec02.configuration.module.IRefeshAndUpdate;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.gui.frameGui.Component.MyChooser;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Administrator
 */
public class ConfigurationManagement implements Iexecute, IRefeshAndUpdate {

    private static volatile ConfigurationManagement management;
    private final MyChooser myChooser;
    private final List<AbsModule> modules;
    private File file;
    private final Logger logger;
    private final TabConfiguramentPanel tabPanel;

    public static ConfigurationManagement getInstance(){
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

    private ConfigurationManagement(){
        this.modules = new ArrayList<>();
        this.myChooser = new MyChooser(System.getProperty("user.dir"));
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
        modules.clear();
        addModel(new ErrorCode());
        addModel(new Setting(new SettingDto()));
        addModel(new Socket(new SocketDto()));
        addModel(new ItemTest(new ItemTestDto()));
        addModel(new Dhcp(new DhcpDto()));
        if (file == null) {
            for (AbsModule entry : modules) {
                entry.refesh();
            }
            return true;
        } else {
            try {
                JSONObject data = JSONObject.parseObject(Files.readString(file.toPath()));
                Object modelObject;
                for (AbsModule module1 : modules) {
                    modelObject = data.get(module1.getName());
                    if (modelObject instanceof JSONObject moduleData) {
                        module1.setData(moduleData);
                    } else {
                        String error = String.format("init(): \"%s\" model not found!", module1.getName());
                        this.logger.addLog(error);
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
    }

    @NonNull
    public final void addModel(AbsModule model) {
        this.modules.add(model);
    }

    @Override
    public void update() {
        for (AbsModule module : modules) {
            module.update();
        }
    }
    
    @Override
    public void refesh() {
        for (AbsModule module : modules) {
            module.refesh();
        }
    }

    @Override
    public void execute() {
        for (AbsModule module : modules) {
            module.execute();
        }
    }

    public boolean save() {
        var model = this.getInputModel();
        if (model == null) {
            JOptionPane.showMessageDialog(null, "model == null!");
            return false;
        }
        if (JFileChooser.APPROVE_OPTION
                == this.myChooser.showSaveDialog(null,
                        String.format("config_%s.json",
                                System.currentTimeMillis()))) {
            try {
                File f = myChooser.getSelectedFile();
                JSONObject resultJson = new JSONObject(model);
                JSONObject ItemTest = resultJson.getJSONObject("ItemTest");
                if (ItemTest != null) {
                    ItemTest.remove("limits");
                    resultJson.put("ItemTest", ItemTest);
                }
                Files.writeString(f.toPath(), MyObjectMapper.prettyPrintJsonUsingDefaultPrettyPrinter(resultJson.toJSONString()));
                JOptionPane.showMessageDialog(null, String.format("Save ok! %s", f));
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getLocalizedMessage());
            }
            return true;
        }
        return false;
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

    public FunctionConfig getItemTestConfig(String configName) {
        Map<String, ItemConfig> itemsCongfig = getItemTestConfig().getModel().getItems();
        ItemConfig itemConfig;
        if (itemsCongfig == null
                || ((itemConfig = itemsCongfig.get(configName)) == null
                && (itemConfig = itemsCongfig.get(Common.getBaseItem(configName))) == null)) {
            return null;
        }
        return new FunctionConfig(itemConfig);
    }

    public ItemLimit getItemLimit(String itemName) {
        Map<String, ItemLimit> itemsLimit = getItemTestConfig().getModel().getLimits();
        ItemLimit itemLimit;
        if (itemsLimit == null
                || ((itemLimit = itemsLimit.get(itemName)) == null
                && (itemLimit = itemsLimit.get(Common.getBaseItem(itemName))) == null)) {
            return null;
        }
        return itemLimit;
    }

    public ItemErrorCode getErrorcode(String limitName) {
        Map<String, ItemErrorCode> errorcodes = this.getErrorCode().getModel().getErrorcodes();
        ItemErrorCode itemErrorCode;
        limitName = limitName.toUpperCase();
        if (errorcodes == null || ((itemErrorCode = errorcodes.get(limitName)) == null
                && (itemErrorCode = errorcodes.get(Common.getBaseItem(limitName))) == null)) {
            return null;
        }
        return itemErrorCode;
    }

}
