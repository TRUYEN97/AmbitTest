/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.view.managerUI;

import com.tec02.Time.WaitTime.Class.TimeS;
import com.tec02.main.Core;
import com.tec02.main.ItemFunciton.ItemFunctionFactory;
import com.tec02.main.ModeManagement;
import com.tec02.main.UICellTester;
import com.tec02.main.dataCell.DataCell;
import com.tec02.view.subUI.SubUI.AbsSubUI;
import lombok.Getter;
import lombok.NonNull;

/**
 *
 * @author 21AK22
 */
@Getter
public class UICell {

    private final AbsSubUI absSubUi;
    private final int id;
    private final int row;
    private final int column;
    private final UICellManagement cellUImangement;
    private final Core core;
    private final ModeManagement modeManagement;
    private final UICellTester cellTester;
    private final ItemFunctionFactory itemFunctionFactory;
    private final DataCell dataCell;
    private final TimeS timeS;
    private String input;
    private int failcount = 0;
    private int failConsecutivecount = 0;
    private int testcount = 0;

    @NonNull
    public UICell(AbsSubUI absSubUi, int id, int row, int column) {
        this.absSubUi = absSubUi;
        this.id = id;
        this.row = row;
        this.column = column;
        this.timeS = new TimeS();
        this.cellUImangement = UICellManagement.getInstance();
        this.core = Core.getInstance();
        this.modeManagement = ModeManagement.getInsatace();
        this.dataCell = new DataCell(this);
        this.itemFunctionFactory = new ItemFunctionFactory(this);
        this.cellTester = new UICellTester(this, this.itemFunctionFactory);
    }

    public String getName() {
        return absSubUi.getName();
    }

    public boolean isTesting() {
        return this.cellTester.isTesting();
    }

    public boolean isName(String name) {
        return name != null && name.equalsIgnoreCase(absSubUi.getName());
    }

    public boolean isUI(AbsSubUI ui) {
        return absSubUi.equals(ui);
    }

    public void setInput(String input) {
        if (input != null && !isTesting()) {
            this.cellTester.setInput(input);
        }
    }

    public int getTestCount() {
        return testcount;
    }

    public int getTestFailed() {
        return failcount;
    }

    public int getTestFailedConsecutive() {
        return failConsecutivecount;
    }

    public void resetFailedConsecutive() {
        failConsecutivecount = 0;
    }

    public int addTestCount() {
        return testcount += 1;
    }

    public int addTestFailCount() {
        return failcount += 1;
    }

    public int addTestFailConsecutiveCount() {
        return failConsecutivecount += 1;
    }
}
