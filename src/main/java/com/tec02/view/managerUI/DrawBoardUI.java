/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.view.managerUI;

import com.tec02.configuration.controller.ConfigurationManagement;
import com.tec02.view.UIFactory;
import com.tec02.view.subUI.SubUI.AbsSubUI;
import java.awt.GridLayout;
import javax.swing.JPanel;
import lombok.NonNull;

/**
 *
 * @author Administrator
 */
public class DrawBoardUI {

    private final UICellManagement cellUIManager;
    private final ConfigurationManagement configurationManagement;
    private final UIFactory uIManagement;
    private JPanel drawPanel;

    public DrawBoardUI() {
        this.cellUIManager = UICellManagement.getInstance();
        this.configurationManagement = ConfigurationManagement.getInstance();
        this.uIManagement = UIFactory.getInstance();
    }

    @NonNull
    public void setDrawPanel(JPanel drawPanel) {
        this.drawPanel = drawPanel;
    }

    public void draw() {
        if (this.drawPanel == null) {
            return;
        }
        this.cellUIManager.clear();
        this.drawPanel.removeAll();
        int colSize = this.configurationManagement.getSettingConfig().getModel().getColumn();
        int rowSize = this.configurationManagement.getSettingConfig().getModel().getRow();
        this.drawPanel.setLayout(new GridLayout(rowSize, colSize, 2, 2));
        String uiType = this.configurationManagement.getSettingConfig().getModel().getView();
        if (colSize > 1 || rowSize > 1) {
            for (int row = 1; row <= rowSize; row++) {
                for (int col = 1; col <= colSize; col++) {
                    AbsSubUI subUi = (AbsSubUI) this.uIManagement.getUI(uiType,
                            String.format("%s%s", (char) ('A' + row - 1), col));
                    if (subUi == null) {
                        throw new RuntimeException(String.format("subUI: %s not exists!", uiType));
                    }
                    if (this.cellUIManager.addUI(subUi, row, col)) {
                        this.drawPanel.add(subUi);
                    }
                }
            }
        } else {
            AbsSubUI subUi = this.uIManagement.getUI(uiType, "");
            if (subUi != null && this.cellUIManager.addUI(subUi, 1, 1)) {
                this.drawPanel.add(subUi);
            }
        }
    }

}
