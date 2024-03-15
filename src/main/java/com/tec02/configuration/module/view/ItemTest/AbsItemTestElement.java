/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module.view.ItemTest;

import com.tec02.configuration.module.view.AbsElementTab;
import com.tec02.configuration.module.view.TabPanel;

/**
 *
 * @author Administrator
 * @param <M>
 */
public abstract class AbsItemTestElement<M> extends AbsElementTab<M>{
    protected final TabPanel<AbsElementTab> tabPanelParent;

    public AbsItemTestElement(TabPanel<AbsElementTab> tabPanel, String tabName, M model) {
        super(tabName, model);
        this.tabPanelParent = tabPanel;
    }
    
}
