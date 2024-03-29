/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * TagLog.java
 *
 * Created on Feb 7, 2022, 6:28:13 PM
 */
package com.tec02.view.subUI.FormDetail.TabItem;

import com.tec02.Jmodel.Component.MyTable;
import com.tec02.configuration.model.itemTest.ItemConfig;
import com.tec02.function.IFunctionModel;
import com.tec02.main.ErrorLog;
import com.tec02.main.ModeManagement;
import com.tec02.main.UICellTester;
import com.tec02.main.modeFlow.ModeFlow;
import com.tec02.view.subUI.FormDetail.AbsTabUI;
import com.tec02.view.subUI.FormDetail.TabDetail;
import com.tec02.view.subUI.FormDetail.TabItem.ShowLog.ShowLog;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author Administrator
 */
public class TabItem extends AbsTabUI {

    private static final String STATUS_COULMN = "Status";
    private static final String TIME_COULMN = "Time";
    private static final String ITEM_COULMN = "Item";
    private static final String STT_COULMN = "STT";
    private static final String ERROR_CODE_COULMN = "Error code";
    private static final String CUS_ERROR_CODE_COULMN = "Cus error code";
    private static final String DEBUG_ABLE = "debug-item";
    private final Map<String, ShowLog> itemLogs;
    private final MyTable myTable;
    private final List<Boolean> itemFinish;
    private static final List<String> testColoumns = List.of(STT_COULMN,
            ITEM_COULMN, TIME_COULMN,
            STATUS_COULMN, CUS_ERROR_CODE_COULMN, ERROR_CODE_COULMN);
    private static final List<String> showColoumns = List.of(STT_COULMN,
            ITEM_COULMN, DEBUG_ABLE);

    /**
     * Creates new form TagLog
     *
     * @param tabDetail
     */
    public TabItem(TabDetail tabDetail) {
        super(tabDetail, "Item", 1000);
        initComponents();
        this.itemFinish = new ArrayList<>();
        this.itemLogs = new HashMap<>();
        this.myTable = new MyTable(tableItem);
    }

    private void showItemLogSelected() {
        var functionName = this.myTable.getRowSelectedValue(ITEM_COULMN);
        if (functionName instanceof String funcName) {
            IFunctionModel dataBox = this.dataCell.getFunction(funcName);
            if (dataBox == null) {
                return;
            }
            if (itemLogs.containsKey(funcName)) {
                itemLogs.get(funcName).showLog();
            } else {
                ShowLog itemLog = new ShowLog(dataBox, uICell);
                itemLog.showLog();
                itemLogs.put(funcName, itemLog);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tableItem = new javax.swing.JTable();

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tableItem.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        tableItem.setModel(new javax.swing.table.DefaultTableModel(
            null,
            new String [] {}
        ));
        tableItem.setName("tableItem"); // NOI18N
        tableItem.setRowHeight(30);
        tableItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableItemMouseClicked(evt);
            }
        });
        tableItem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                keyEvent(evt);
            }
        });
        jScrollPane1.setViewportView(tableItem);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tableItemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableItemMouseClicked
        if (evt.getClickCount() > 1) {
            showItemLogSelected();
        }
    }//GEN-LAST:event_tableItemMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tableItem;
    // End of variables declaration//GEN-END:variables

    @Override
    public void startTest() {
        for (ShowLog itemLog : itemLogs.values()) {
            itemLog.dispose();
        }
        this.itemLogs.clear();
        this.itemFinish.clear();
        this.myTable.initTable(testColoumns);
        super.startTest();
    }

    @Override
    public synchronized void updateData() {
        if (!this.isVisible()) {
            return;
        }
        try {
            showItemTest(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDataTest(IFunctionModel dataBox, int row) {
        try {
            var model = dataBox.getModel();
            this.myTable.setValueAt(row, ITEM_COULMN, model.getTest_name());
            this.myTable.setValueAt(row, TIME_COULMN, String.format("%.3f S", dataBox.getRunTime() / 1000.0));
            this.myTable.setValueAt(row, STATUS_COULMN, getStatus(dataBox));
            this.myTable.setValueAt(row, ERROR_CODE_COULMN, model.getErrorcode());
            this.myTable.setValueAt(row, CUS_ERROR_CODE_COULMN, model.getError_code());
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getLocalizedMessage());
        }
    }

    private boolean isHasFinish(int row) {
        return row < this.itemFinish.size() && this.itemFinish.get(row);
    }

    private boolean isAccepToStopTest() {
        return JOptionPane.showConfirmDialog(null,
                "Chọn \"Yes\" để dừng test", "Messager",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    @Override
    public void keyEvent(KeyEvent evt) {
        int a = evt.getKeyChar();
        switch (a) {
            case CTRL_Q -> {
                if (this.uICell.isTesting() && isAccepToStopTest()) {
                    this.uICell.getCellTester().stop();
                }
            }
            case CTRL_S -> {
                if (!this.uICell.isTesting()) {
                    this.myTable.initTable(showColoumns);
                    ModeFlow modeFlow = ModeManagement.getInsatace().getModeFlow();
                    List<ItemConfig> itemConfigs = modeFlow.getPassToItems();
                    ItemConfig itemConfig;
                    for (int i = 0; i < itemConfigs.size(); i++) {
                        itemConfig = itemConfigs.get(i);
                        this.myTable.addRow(Map.of(STT_COULMN, i,
                                ITEM_COULMN, itemConfig,
                                DEBUG_ABLE, itemConfig.getModeRun() > 1));
                    }
                }
            }
            case CTRL_W -> {
                if (!this.uICell.isTesting()) {
                    this.myTable.initTable(testColoumns);
                    showItemTest(true);
                }
            }
            case CTRL_D -> {
                if (!this.uICell.isTesting()) {
                    var rows = this.myTable.getRowSelectedMapValues();
                    List<ItemConfig> items = new ArrayList<>();
                    Object val;
                    for (Map<String, Object> row : rows) {
                        val = row.get(ITEM_COULMN);
                        if (val instanceof ItemConfig item) {
                            if (item.getModeRun() >= UICellTester.MODE_DEBUG_ITEM) {
                                items.add(item);
                            }
                        }
                    }
                    if (!items.isEmpty()) {
                        this.uICell.getCellTester().runDebugItem(items);
                    }
                }
            }
            case CTRL_R -> {
                if (!this.uICell.isTesting()) {
                    this.uICell.resetFailedConsecutive();
                }
            }
        }
    }

    private void showItemTest(boolean reset) {
        try {
            IFunctionModel dataBox;
            if (reset) {
                this.itemFinish.clear();
            }
            int a = 0;
            for (int row = 0; row < functions.size(); row++) {
                dataBox = functions.get(row);
                if (dataBox.isSubItem()) {
                    a += 1;
                    continue;
                }
                int tempIndex = row - a;
                if (isHasFinish(tempIndex)) {
                    continue;
                }
                if (tempIndex > this.myTable.getRowCount() - 1) {
                    this.myTable.addRow(new Object[]{this.myTable.getRowCount()});
                    this.itemFinish.add(false);
                    showDataTest(dataBox, tempIndex);
                } else {
                    showDataTest(dataBox, tempIndex);
                }
                if (dataBox.isDone()) {
                    this.itemFinish.set(tempIndex, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLog.addError(this, e.getLocalizedMessage());
        }
    }

    private String getStatus(IFunctionModel dataBox) {
        if (dataBox.isWaiting()) {
            return "Waiting";
        } else if (dataBox.isTesting()) {
            return "Testing";
        } else {
            return dataBox.getModel().getStatus();
        }
    }

    @Override
    public void tabSelected(ChangeEvent evt) {
        updateData();
    }
}
