/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.tec02.configuration.module.view.ItemTest.group;

import com.tec02.Jmodel.Component.MyListTabel;
import com.tec02.Jmodel.Component.PopupMenu;
import com.tec02.common.Common;
import com.tec02.common.MyObjectMapper;
import com.tec02.configuration.model.itemTest.ItemConfig;
import com.tec02.configuration.model.itemTest.ItemGroupDto;
import com.tec02.configuration.model.itemTest.ItemTestDto;
import com.tec02.configuration.module.AbsModuleView;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Administrator
 */
public class GroupElementPanel extends AbsModuleView<ItemTestDto> {

    private final MyListTabel<Object> modeListTabel;
    private final MyListTabel<Object> applyListTabel;
    private final ShowConfig showConfig;
    private final ItemGroupDto thisModel;
    private final SearchItem searchItemPanel;

    public GroupElementPanel(String tabName, ItemTestDto model) {
        super(tabName);
        setModel(model);
        if (model.getGroups().containsKey(tabName)) {
            this.thisModel = model.getGroups().get(tabName);
        } else {
            this.thisModel = new ItemGroupDto();
        }
        initComponents();
        this.showConfig = new ShowConfig();
        this.modeListTabel = new MyListTabel(listItems);
        this.applyListTabel = new MyListTabel(listItemSelected);
        this.searchItemPanel = new SearchItem(modeListTabel, applyListTabel, showConfig);
        ChangeListener changeListenerF = (ChangeEvent e) -> {
            this.pnFColor.setBackground(new Color((int) spnFr.getValue(),
                    (int) spnFg.getValue(), (int) spnFb.getValue()));
        };
        ChangeListener changeListenerT = (ChangeEvent e) -> {
            this.pnTcolor.setBackground(new Color((int) spnTr.getValue(),
                    (int) spnTg.getValue(), (int) spnTb.getValue()));
        };
        spnFr.addChangeListener(changeListenerF);
        spnFg.addChangeListener(changeListenerF);
        spnFb.addChangeListener(changeListenerF);
        spnTr.addChangeListener(changeListenerT);
        spnTg.addChangeListener(changeListenerT);
        spnTb.addChangeListener(changeListenerT);
        PopupMenu menu = this.modeListTabel.getMenu();
        menu.addItemMenu("New item", (e) -> {
            String newName = JOptionPane.showInputDialog("New item name");
            if (newName == null || newName.isBlank()) {
                return;
            }
            ItemConfig newItemConfig = new ItemConfig();
            newItemConfig.setItemName(newName);
            showItemSelected(newItemConfig);
            this.modeListTabel.addItem(newItemConfig);
            this.update();
            this.modeListTabel.setSelected(newItemConfig);
        });
        menu.addItemMenu("Search", (e) -> {
            this.searchItemPanel.display();
        });
        PopupMenu selectmenu = this.modeListTabel.getSelectedMenu();
        selectmenu.addItemMenu("Copy", (e) -> {
            Object obSelected = this.modeListTabel.getSelectioned();
            copyItemSelected(obSelected, model);
            this.update();
            this.refesh();
        });
        selectmenu.addItemMenu("Delete", (e) -> {
            Object obSelected = this.modeListTabel.getSelectioned();;
            if (obSelected == null) {
                return;
            }
            if (JOptionPane.showConfirmDialog(null,
                    "Xóa hết tất cả Items đã chọn?",
                    "Warning", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                this.modeListTabel.removeItemSelecteds();
                this.update();
                this.refesh();
            }
        });
        selectmenu.addMenu(menu);
        this.modeListTabel.setDoubleClickAction((input) -> {
            Object objectSelected = this.modeListTabel.getSelectioned();
            showItemSelected(objectSelected);
        });
        this.applyListTabel.setDoubleClickAction((input) -> {
            Object objectSelected = this.applyListTabel.getSelectioned();
            showItemSelected(objectSelected);
        });
        this.modeListTabel.addKeyEventAction(KeyEvent.VK_ENTER, (input) -> {
            showItemSelected(this.modeListTabel.getSelectioned());
        });
        this.applyListTabel.addKeyEventAction(KeyEvent.VK_ENTER, (input) -> {
            showItemSelected(this.modeListTabel.getSelectioned());
        });
    }

    private void copyItemSelected(Object obSelected, ItemTestDto model1) {
        if (obSelected instanceof ItemConfig itemConfig) {
            String newName = JOptionPane.showInputDialog("New name: " + itemConfig, itemConfig + "_1");
            if (newName == null || newName.isBlank()) {
                return;
            }
            ItemConfig newItemConfig = MyObjectMapper.convertValue(itemConfig, ItemConfig.class);
            newItemConfig.setItemName(newName);
            this.modeListTabel.addItem(newItemConfig);
        } else if (obSelected instanceof String groupName) {
            groupName = Common.getBaseItem(groupName);
            String newName = JOptionPane.showInputDialog("New name: " + groupName, groupName + "_1");
            if (newName == null || newName.isBlank()) {
                return;
            }
            ItemGroupDto newGroupDto = MyObjectMapper.convertValue(model1.getGroups().get(groupName), ItemGroupDto.class);
            this.model.getGroups().put(newName, newGroupDto);
        }
    }

    public ItemGroupDto getThisModel() {
        return thisModel;
    }

    private void showItemSelected(Object objectSelected) {
        Map<String, ItemConfig> items = this.model.getItems();
        if (objectSelected instanceof ItemConfig itemConfig) {
            showConfig.display(itemConfig);
        } else if (objectSelected instanceof String name) {
            if (Common.isGroupItem(name)) {
                tabCurrentPanel.setSelectedTab(Common.getBaseItem(name));
            } else if (items.containsKey(name)) {
                showConfig.display(items.get(name));
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listItemSelected = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        listItems = new javax.swing.JList<>();
        btAdd = new javax.swing.JButton();
        btRemove = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        spLooptest = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        spnTr = new javax.swing.JSpinner();
        spnTg = new javax.swing.JSpinner();
        spnTb = new javax.swing.JSpinner();
        pnTcolor = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        spnFr = new javax.swing.JSpinner();
        spnFg = new javax.swing.JSpinner();
        spnFb = new javax.swing.JSpinner();
        pnFColor = new javax.swing.JPanel();
        spBegin = new javax.swing.JSpinner();
        cbbPassTo = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        slModerun = new javax.swing.JSlider();
        cbbFailTo = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cbCoreGroup = new javax.swing.JCheckBox();
        btPassTo = new javax.swing.JButton();
        btFailTo = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 204, 204));

        jPanel1.setBackground(new java.awt.Color(0, 153, 153));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Items"));

        jScrollPane1.setViewportView(listItemSelected);

        jScrollPane2.setViewportView(listItems);

        btAdd.setText(">>");
        btAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAddActionPerformed(evt);
            }
        });

        btRemove.setText("<<");
        btRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRemoveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btAdd)
                    .addComponent(btRemove))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btRemove)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(0, 204, 153));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        spLooptest.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        jLabel2.setText("Loop time");

        jLabel1.setText("Pass to");

        jLabel4.setText("Test color");

        spnTr.setModel(new javax.swing.SpinnerNumberModel(255, 0, 255, 1));

        spnTg.setModel(new javax.swing.SpinnerNumberModel(255, 0, 255, 1));

        spnTb.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));

        pnTcolor.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pnTcolor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnTcolorMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnTcolorLayout = new javax.swing.GroupLayout(pnTcolor);
        pnTcolor.setLayout(pnTcolorLayout);
        pnTcolorLayout.setHorizontalGroup(
            pnTcolorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 38, Short.MAX_VALUE)
        );
        pnTcolorLayout.setVerticalGroup(
            pnTcolorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel5.setText("Fail color");

        spnFr.setModel(new javax.swing.SpinnerNumberModel(255, 0, 255, 1));

        spnFg.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));

        spnFb.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));

        pnFColor.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pnFColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnFColorMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnFColorLayout = new javax.swing.GroupLayout(pnFColor);
        pnFColor.setLayout(pnFColorLayout);
        pnFColorLayout.setHorizontalGroup(
            pnFColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 38, Short.MAX_VALUE)
        );
        pnFColorLayout.setVerticalGroup(
            pnFColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        spBegin.setModel(new javax.swing.SpinnerNumberModel(-1, -1, null, 1));

        jLabel6.setText("Begin");

        jLabel3.setText("Failed to");

        slModerun.setMaximum(3);
        slModerun.setMinimum(1);
        slModerun.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel7.setText("Mode run");

        cbCoreGroup.setSelected(true);
        cbCoreGroup.setText("Is Core group");

        btPassTo.setText(">>");
        btPassTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPassToActionPerformed(evt);
            }
        });

        btFailTo.setText(">>");
        btFailTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btFailToActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spLooptest)
                            .addComponent(spBegin)
                            .addComponent(slModerun, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(cbbPassTo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btPassTo, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(cbbFailTo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btFailTo, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(spnTr, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spnTg, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spnTb, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(pnTcolor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(spnFr, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spnFg, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spnFb, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(pnFColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cbCoreGroup)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbCoreGroup)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(slModerun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spLooptest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spBegin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cbbPassTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btPassTo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cbbFailTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btFailTo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(spnTr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(spnTg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(spnTb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addComponent(pnTcolor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnFColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(spnFr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(spnFg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(spnFb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 174, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAddActionPerformed
        // TODO add your handling code here:
        this.applyListTabel.addAllItem(this.modeListTabel.getSelectedValuesList());
    }//GEN-LAST:event_btAddActionPerformed

    private void btRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRemoveActionPerformed
        // TODO add your handling code here:
        this.applyListTabel.removeItemSelecteds();
    }//GEN-LAST:event_btRemoveActionPerformed

    private void pnTcolorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnTcolorMouseClicked
        // TODO add your handling code here:
        Color selectedColor = JColorChooser.showDialog(this, "Choose a Test-Color", this.pnTcolor.getBackground());
        if (selectedColor != null) {
            showTestColor(List.of(selectedColor.getRed(),
                    selectedColor.getGreen(),
                    selectedColor.getBlue()));
        }
    }//GEN-LAST:event_pnTcolorMouseClicked

    private void pnFColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnFColorMouseClicked
        // TODO add your handling code here:
        Color selectedColor = JColorChooser.showDialog(this, "Choose a Failed-Color", this.pnFColor.getBackground());
        if (selectedColor != null) {
            showFailColor(List.of(selectedColor.getRed(),
                    selectedColor.getGreen(),
                    selectedColor.getBlue()));
        }
    }//GEN-LAST:event_pnFColorMouseClicked

    private void btPassToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPassToActionPerformed
        // TODO add your handling code here:
        tabCurrentPanel.setSelectedTab(String.valueOf(this.cbbPassTo.getSelectedItem()));
    }//GEN-LAST:event_btPassToActionPerformed

    private void btFailToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btFailToActionPerformed
        // TODO add your handling code here:
        tabCurrentPanel.setSelectedTab(String.valueOf(this.cbbFailTo.getSelectedItem()));
    }//GEN-LAST:event_btFailToActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btFailTo;
    private javax.swing.JButton btPassTo;
    private javax.swing.JButton btRemove;
    private javax.swing.JCheckBox cbCoreGroup;
    private javax.swing.JComboBox<String> cbbFailTo;
    private javax.swing.JComboBox<String> cbbPassTo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<String> listItemSelected;
    private javax.swing.JList<String> listItems;
    private javax.swing.JPanel pnFColor;
    private javax.swing.JPanel pnTcolor;
    private javax.swing.JSlider slModerun;
    private javax.swing.JSpinner spBegin;
    private javax.swing.JSpinner spLooptest;
    private javax.swing.JSpinner spnFb;
    private javax.swing.JSpinner spnFg;
    private javax.swing.JSpinner spnFr;
    private javax.swing.JSpinner spnTb;
    private javax.swing.JSpinner spnTg;
    private javax.swing.JSpinner spnTr;
    // End of variables declaration//GEN-END:variables

    @Override
    public synchronized void refesh() {
        if (thisModel == null) {
            return;
        }
        Integer begin = thisModel.getBegin();
        this.cbCoreGroup.setSelected(thisModel.isCoreGroup());
        this.slModerun.setValue(thisModel.getModeRun());
        this.spLooptest.setValue(thisModel.getLoop());
        this.spBegin.setValue(begin == null ? -1 : begin);
        var tabGroups = this.model.getGroups().keySet();
        String passSelected = (String) thisModel.getPassTo();
        String failSelected = (String) thisModel.getFailedTo();
        this.cbbFailTo.removeAllItems();
        this.cbbPassTo.removeAllItems();
        this.cbbFailTo.addItem("");
        this.cbbPassTo.addItem("");
        for (Object groupName : tabGroups) {
            this.cbbFailTo.addItem(groupName.toString());
            this.cbbPassTo.addItem(groupName.toString());
        }
        if (passSelected != null && !passSelected.isBlank() && tabGroups.contains(passSelected)) {
            this.cbbPassTo.setSelectedItem(passSelected);
        } else {
            this.cbbPassTo.setSelectedItem("");
        }
        if (failSelected != null && !failSelected.isBlank() && tabGroups.contains(failSelected)) {
            this.cbbFailTo.setSelectedItem(failSelected);
        } else {
            this.cbbFailTo.setSelectedItem("");
        }
        showTestColor(thisModel.getTestColor());
        showFailColor(thisModel.getFailColor());
        refeshListModel();
    }

    private void showTestColor(List<Integer> testColor) {
        this.spnTr.setValue(testColor.get(0));
        this.spnTg.setValue(testColor.get(1));
        this.spnTb.setValue(testColor.get(2));
        this.pnTcolor.setBackground(new Color(testColor.get(0), testColor.get(1), testColor.get(2)));
    }

    private void showFailColor(List<Integer> failColor) {
        this.spnFr.setValue(failColor.get(0));
        this.spnFg.setValue(failColor.get(1));
        this.spnFb.setValue(failColor.get(2));
        this.pnFColor.setBackground(new Color(failColor.get(0), failColor.get(1), failColor.get(2)));
    }

    private synchronized void refeshListModel() {
        Map<String, ItemConfig> items = model.getItems();
        this.applyListTabel.removeAllItem();
        this.modeListTabel.removeAllItem();
        if (items == null) {
            return;
        }
        Map<String, ItemGroupDto> gourpNames = model.getGroups();
        List<String> itemsInConfig = thisModel.getItems();
        for (String item : itemsInConfig) {
            if (items.containsKey(item) || Common.isGroupItem(item)
                    || gourpNames.containsKey(Common.getBaseItem(item))) {
                this.applyListTabel.addItem(item);
            }
        }
        for (String groupName : gourpNames.keySet()) {
            if (!groupName.equals(tabName)) {
                this.modeListTabel.addItem(Common.createGroupCodeName(groupName));
            }
        }
        List<String> itemSorteds = new ArrayList(items.keySet());
        Collections.sort(itemSorteds);
        ItemConfig itemConfig;
        for (String itemName : itemSorteds) {
            itemConfig = items.get(itemName);
            if (itemConfig != null) {
                this.modeListTabel.addItem(itemConfig);
            }
        }
    }

    @Override
    public String toString() {
        return Common.createGroupCodeName(getTabName());
    }

    @Override
    public ItemTestDto getModel() {
        return model;
    }

    @Override
    public void update() {
        if (thisModel == null) {
            return;
        }
        thisModel.setFailColor(List.of((int) spnFr.getValue(), (int) spnFg.getValue(), (int) spnFb.getValue()));
        thisModel.setTestColor(List.of((int) spnTr.getValue(), (int) spnTg.getValue(), (int) spnTb.getValue()));
        thisModel.setModeRun(slModerun.getValue());
        var failTo = cbbFailTo.getSelectedItem();
        thisModel.setFailedTo(failTo == null ? null : failTo.toString());
        var passTo = cbbPassTo.getSelectedItem();
        thisModel.setPassTo(passTo == null ? null : passTo.toString());
        thisModel.setLoop((int) spLooptest.getValue());
        thisModel.setBegin((int) spBegin.getValue());
        thisModel.setCoreGroup(cbCoreGroup.isSelected());
        Set<String> groupKeys = this.model.getGroups().keySet();
        List<String> items = new ArrayList<>();
        String name;
        for (Object item : applyListTabel.getAllItem()) {
            if (item == null) {
                continue;
            }
            name = item.toString();
            if (!Common.isGroupItem(name) || groupKeys.contains(Common.getBaseItem(name))) {
                items.add(name);
            }
        }
        thisModel.setItems(items);
        Map<String, ItemConfig> itemSelects = new HashMap<>();
        for (Object item : modeListTabel.getAllItem()) {
            if (item instanceof ItemConfig it) {
                itemSelects.put(it.getItemName(), it);
            }
        }
        model.setItems(itemSelects);
    }

    @Override
    public void tabSelected() {
        refesh();
    }
}
