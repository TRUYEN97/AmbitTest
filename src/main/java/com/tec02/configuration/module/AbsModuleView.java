/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module;

import com.tec02.configuration.module.view.TabPanel;
import javax.swing.JPanel;

/**
 *
 * @author Administrator
 * @param <M> model
 */
public abstract class AbsModuleView<M> extends JPanel implements IRefeshAndUpdate {

    protected final String tabName;
    protected AbsModule module;
    protected TabPanel tabParentPanel;
    protected TabPanel tabCurrentPanel;
    protected M model;

    public AbsModuleView(String tabName) {
        this.tabName = tabName;
    }

    public void setModule(AbsModule module) {
        this.module = module;
    }

    public void setModel(M model) {
        this.model = model;
    }

    public void setTabCurremtPanel(TabPanel curremtTabPanel) {
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
