/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.view.managerUI;

import com.tec02.view.Gui;
import com.tec02.view.subUI.SubUI.AbsSubUI;
import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.isNull;

/**
 *
 * @author Administrator
 */
public class UICellManagement {

    private final List<UICell> uiCells;
    private static volatile UICellManagement instance;
    private Gui gui;
            

    private UICellManagement() {
        this.uiCells = new ArrayList<>();
    }
    

    public static UICellManagement getInstance() {
        UICellManagement ins = UICellManagement.instance;
        if (ins == null) {
            synchronized (UICellManagement.class) {
                ins = UICellManagement.instance;
                if (ins == null) {
                    UICellManagement.instance = ins = new UICellManagement();
                }
            }
        }
        return ins;
    }

    public UICell getUICell(String index) {
        for (UICell uiStatuse : uiCells) {
            if (uiStatuse.isName(index)) {
                return uiStatuse;
            }
        }
        return null;
    }

    public boolean containUI(AbsSubUI ui) {
        if (isNull(ui)) {
            return false;
        }
        for (UICell uiStatuse : uiCells) {
            if (uiStatuse.isUI(ui) || uiStatuse.isName(ui.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean addUI(AbsSubUI subUi, int row, int column) {
        if (isNull(subUi) || containUI(subUi)) {
            return false;
        }
        UICell cellUiModel = new UICell(subUi, this.uiCells.size(), row, column);
        subUi.setUICell(cellUiModel);
        subUi.setGui(gui);
        return this.uiCells.add(cellUiModel);
    }

    public void clear() {
        this.uiCells.clear();
    }

    public boolean isNotTest() {
        for (UICell uiStatuse : uiCells) {
            if (uiStatuse.isTesting()) {
                return false;
            }
        }
        return true;
    }

    public int countTesting() {
        int count = 0;
        for (UICell uiStatuse : uiCells) {
            if (uiStatuse.isTesting()) {
                count++;
            }
        }
        return count;
    }

    public int getIndexOf(String index) {
        for (int i = 0; i < uiCells.size(); i++) {
            if (uiCells.get(i).isName(index)) {
                return i;
            }
        }
        return -1;
    }

    public int getIndexOf(UICell uiStatus) {
        if (isNull(uiStatus)) {
            return -1;
        }
        return this.uiCells.indexOf(uiStatus);
    }

    public boolean isIndexFree(String index) {
        UICell status = getUICell(index);
        if (isNull(status)) {
            return false;
        }
        return !status.isTesting();
    }

    public List<UICell> getUiCells() {
        return uiCells;
    }

    public void setGui(Gui gui) {
        this.gui = gui;
    }

    public boolean isMultiUi() {
        return this.uiCells.size() > 1;
    }

}
