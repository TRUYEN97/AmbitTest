/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.view.subUI.SubUI;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.configuration.module.iml.Socket.AeClientRunner;
import com.tec02.view.Gui;
import com.tec02.view.managerUI.UICell;
import com.tec02.view.subUI.AbsUI;
import com.tec02.view.subUI.FormDetail.TabDetail;
import com.tec02.view.subUI.FormDetail.TabItem.TabItem;
import com.tec02.view.subUI.FormDetail.TabView.TabView;
import com.tec02.view.subUI.FormDetail.showGroup.ShowGroupItem;
import java.awt.Color;
import javax.swing.border.LineBorder;

/**
 *
 * @author Administrator
 */
public abstract class AbsSubUI extends AbsUI {

    protected final TabDetail tabDetail;

    protected AbsSubUI(String name, int time) {
        super(name, time);
        this.setBorder(new LineBorder(Color.BLACK, 1));
        this.tabDetail = new TabDetail(this);
        this.timeS = new TimeS();
        this.tabDetail.addTabView("View", new TabView(this.tabDetail));
        this.tabDetail.addTabView("Item", new TabItem(this.tabDetail));
        this.tabDetail.addTabView("Groups", new ShowGroupItem(this.tabDetail));
    }
    

    public void setGui(Gui gui) {
        this.gui = gui;
    }

    protected abstract void showEnd(Color color, boolean status);

    @Override
    public void startTest() {
        super.startTest();
        this.tabDetail.startTest();
        AeClientRunner.getInstance().sendDefaulDataToServer(uICell);
    }

    @Override
    public void endTest() {
        super.endTest();
        this.tabDetail.endTest();
        showEnd(this.dataCell.getResultColor(), this.dataCell.isPass());
        AeClientRunner.getInstance().sendDefaulDataToServer(uICell);
    }

    
    @Override
    public void setUICell(UICell uICell) {
        super.setUICell(uICell);
        this.tabDetail.setUICell(uICell);
    }

}
