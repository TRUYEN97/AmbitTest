/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.view;

import com.tec02.common.Logger;
import com.tec02.configuration.controller.ConfigurationManagement;
import com.tec02.view.subUI.SubUI.AbsSubUI;
import com.tec02.view.subUI.SubUI.BigUI.BigUI;
import com.tec02.view.subUI.SubUI.SmallUI.SmallUI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;

/**
 *
 * @author Administrator
 */
public class UIFactory {

    private final Map<String, Class<? extends AbsSubUI>> uiClass;
//    private final Map<String, Class<? extends AbsTabUI>> tabUIClass;
    private final Logger logger;
    private static volatile UIFactory instance;

    private UIFactory() {
        this.uiClass = new HashMap<>();
//        this.tabUIClass = new HashMap<>();
        this.logger = new Logger("Log/UIManagement");
        init();
    }

    public static UIFactory getInstance() {
        UIFactory ins = UIFactory.instance;
        if (ins == null) {
            synchronized (UIFactory.class) {
                ins = UIFactory.instance;
                if (ins == null) {
                    UIFactory.instance = ins = new UIFactory();
                }
            }
        }
        return ins;
    }

    private void init() {
        addUIClass(BigUI.class);
        addUIClass(SmallUI.class);
    }

    @NonNull
    public void addUIClass(Class<? extends AbsSubUI> uiClass) {
        String name = uiClass.getSimpleName();
        this.uiClass.put(name.toLowerCase(), uiClass);
        this.logger.addLog("add: %s", name);
    }
    
//    @NonNull
//    public void addTabUIClass(Class<? extends AbsTabUI> uiClass) {
//        String name = uiClass.getSimpleName();
//        this.tabUIClass.put(name.toLowerCase(), uiClass);
//        this.logger.addLog("add: %s", name);
//    }

    public <T extends AbsSubUI> T getUI(String absUIClass, String name) {
        if (absUIClass == null || !this.uiClass.containsKey(absUIClass = absUIClass.toLowerCase())) {
            return null;
        }
        try {
            return (T) this.uiClass.get(absUIClass).getConstructors()[0].newInstance(name);
        } catch (Exception ex) {
            ex.printStackTrace();
            this.logger.addLog("Error: %s", ex.getLocalizedMessage());
            return null;
        }
    }
    
//    public <T extends AbsTabUI> T getTabUI(String absUIClass, String name) {
//        if (absUIClass == null || !this.uiClass.containsKey(absUIClass = absUIClass.toLowerCase())) {
//            return null;
//        }
//        try {
//            return (T) this.tabUIClass.get(absUIClass).getConstructors()[0].newInstance(name);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            this.logger.addLog("Error: %s", ex.getLocalizedMessage());
//            return null;
//        }
//    }

    public Collection<String> getListUIName() {
        return uiClass.keySet();
    }

    public Map<String, Class<? extends AbsSubUI>> getUiClass() {
        return uiClass;
    }
    
    
}
