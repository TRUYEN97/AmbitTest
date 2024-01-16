/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module.view.ItemTest;

import com.tec02.configuration.module.view.AbsTabElement;
import com.tec02.configuration.module.view.TabPanel;

/**
 *
 * @author Administrator
 * @param <M>
 */
public abstract class AbsItemTestElement<M> extends AbsTabElement<M>{
    protected final TabPanel<AbsTabElement> tabPanelParent;

    public AbsItemTestElement(TabPanel<AbsTabElement> tabPanel, String tabName, M model) {
        super(tabName, model);
        this.tabPanelParent = tabPanel;
    }
    
}
