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
public abstract class AbsTabElement<M> extends AbsModuleView<M>{

    protected final String tabName;
    protected TabPanel tabPanelParent;
    protected TabPanel tabPanelCurrent;

    protected AbsTabElement(String tabName, M model) {
        this.tabName = tabName;
        this.model = model;
    }

    public void setCurremtTabPanel(TabPanel curremtTabPanel) {
        this.tabPanelCurrent = curremtTabPanel;
    }
    
    public void setTabPanelParent(TabPanel tabPanelParent) {
        this.tabPanelParent = tabPanelParent;
    }
    
    public String getTabName() {
        return tabName;
    }

    public abstract void tabSelected();
    
    public abstract void tabUpdate();

    public abstract M getModel();

}
