/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module.view;

import com.tec02.configuration.module.AbsModuleView;

/**
 *
 * @author Administrator
 * @param <M>
 */
public abstract class AbsElementTab<M> extends AbsModuleView<M>{

    protected final String tabName;
    protected TabPanel tabParentPanel;
    protected TabPanel tabCurrentPanel;

    protected AbsElementTab(String tabName, M model) {
        this.tabName = tabName;
        this.model = model;
    }

    public void setCurremtTabPanel(TabPanel curremtTabPanel) {
        this.tabCurrentPanel = curremtTabPanel;
    }
    
    public void setTabParentPanel(TabPanel tabParentPanel) {
        this.tabParentPanel = tabParentPanel;
    }
    
    public String getTabName() {
        return tabName;
    }

    public abstract void tabSelected();

    public abstract M getModel();

}
