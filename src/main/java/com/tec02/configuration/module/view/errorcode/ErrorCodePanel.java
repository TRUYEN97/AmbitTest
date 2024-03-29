/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.tec02.configuration.module.view.errorcode;

import com.tec02.Jmodel.Component.MyListTabel;
import com.tec02.common.CSVcreater;
import com.tec02.configuration.model.errorCode.ErrorCodeModel;
import com.tec02.configuration.model.errorCode.ItemErrorCode;
import com.tec02.configuration.module.AbsModuleView;
import com.tec02.gui.frameGui.Component.MyChooser;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author Administrator
 */
public class ErrorCodePanel extends AbsModuleView<ErrorCodeModel> {

    private final MyListTabel<ItemErrorCode> listTabel;
    private final MyChooser myChooser;

    /**
     * Creates new form ErrorcodePanel
     */
    public ErrorCodePanel() {
        initComponents();
        this.listTabel = new MyListTabel(jList1);
        this.myChooser = new MyChooser();
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
        jList1 = new javax.swing.JList<>();
        jPanel1 = new javax.swing.JPanel();
        txtItemName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtDescErrorcode = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDescErrorcodeToolow = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtDescErrorcodeToohigh = new javax.swing.JTextField();
        txtErrorcodeToohigh = new javax.swing.JTextField();
        txtErrorcode = new javax.swing.JTextField();
        txtErrorcodeToolow = new javax.swing.JTextField();
        btUpdate = new javax.swing.JButton();
        txtDir = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btSave = new javax.swing.JButton();
        btAdd = new javax.swing.JButton();
        txtNewErrorcde = new javax.swing.JTextField();

        setBackground(new java.awt.Color(0, 204, 153));

        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jList1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jList1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jPanel1.setBackground(new java.awt.Color(0, 204, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtItemName.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Item name");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Error code");

        txtDescErrorcode.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Too low");

        txtDescErrorcodeToolow.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Too high");

        txtDescErrorcodeToohigh.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        txtErrorcodeToohigh.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        txtErrorcode.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        txtErrorcodeToolow.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        btUpdate.setText("Update");
        btUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btUpdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtItemName, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDescErrorcode)
                    .addComponent(txtErrorcode)
                    .addComponent(txtDescErrorcodeToolow)
                    .addComponent(txtErrorcodeToolow)
                    .addComponent(txtDescErrorcodeToohigh)
                    .addComponent(txtErrorcodeToohigh))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btUpdate)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtItemName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescErrorcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtErrorcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescErrorcodeToolow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtErrorcodeToolow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescErrorcodeToohigh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtErrorcodeToohigh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btUpdate)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtDir.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Path");

        btSave.setText("Save");
        btSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSaveActionPerformed(evt);
            }
        });

        btAdd.setText("Add");
        btAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAddActionPerformed(evt);
            }
        });

        txtNewErrorcde.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtNewErrorcde.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNewErrorcdeKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDir))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNewErrorcde)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btSave)
                    .addComponent(btAdd)
                    .addComponent(txtNewErrorcde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private ItemErrorCode itemErrorCode;

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        // TODO add your handling code here:
//        if (evt.getClickCount() > 1) {
        itemErrorCode = this.listTabel.getSelectioned();
        if (itemErrorCode != null) {
            showInfo(itemErrorCode);
        } else {
            clearInfo();
        }
//        }
    }//GEN-LAST:event_jList1MouseClicked

    private void showInfo(ItemErrorCode itemErrorCode) {
        txtDescErrorcode.setText(itemErrorCode.getDesc_errorcode());
        txtErrorcode.setText(itemErrorCode.getErrorcode());
        txtDescErrorcodeToohigh.setText(itemErrorCode.getDesc_tooHighErrorcode());
        txtErrorcodeToohigh.setText(itemErrorCode.getTooHighErrorcode());
        txtDescErrorcodeToolow.setText(itemErrorCode.getDesc_tooLowErrorcode());
        txtErrorcodeToolow.setText(itemErrorCode.getTooLowErrorcode());
        txtItemName.setText(itemErrorCode.getTest_name());
    }

    private void clearInfo() {
        txtDescErrorcode.setText(null);
        txtErrorcode.setText(null);
        txtDescErrorcodeToohigh.setText(null);
        txtErrorcodeToohigh.setText(null);
        txtDescErrorcodeToolow.setText(null);
        txtErrorcodeToolow.setText(null);
        txtItemName.setText(null);
    }

    private void jList1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jList1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            this.listTabel.removeItemSelecteds();
            itemErrorCode = null;
            clearInfo();
        }
    }//GEN-LAST:event_jList1KeyPressed

    private void txtNewErrorcdeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNewErrorcdeKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            addNewErrorcode();
        }
    }//GEN-LAST:event_txtNewErrorcdeKeyPressed

    private void btAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAddActionPerformed
        addNewErrorcode();
    }//GEN-LAST:event_btAddActionPerformed

    private void addNewErrorcode() {
        String itemName = txtNewErrorcde.getText();
        txtNewErrorcde.setText(null);
        if (itemName.isBlank()) {
            return;
        }
        clearInfo();
        itemErrorCode = new ItemErrorCode();
        itemErrorCode.setTest_name(itemName);
        showInfo(itemErrorCode);
        this.jList1.clearSelection();
        this.listTabel.addItem(0, itemErrorCode);
        this.jList1.setSelectedIndex(0);
    }

    private void btUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btUpdateActionPerformed
        // TODO add your handling code here:
        String itemName;
        if (itemErrorCode == null || (itemName = txtItemName.getText()).isBlank()) {
            return;
        }
        String descErrorcode = txtDescErrorcode.getText();
        String errorcode = this.txtErrorcode.getText();
        if (descErrorcode.isBlank() || errorcode.isBlank()) {
            return;
        }
        if (!itemErrorCode.toString().equalsIgnoreCase(itemName)) {
            for (ItemErrorCode errorCode : this.listTabel.getAllItem()) {
                if (errorCode != itemErrorCode && errorCode.toString().equalsIgnoreCase(itemName)) {
                    JOptionPane.showMessageDialog(null,
                            String.format("Item name: %s hash exits!", itemName));
                    return;
                }
            }
        }
        itemErrorCode.setTest_name(itemName);
        itemErrorCode.setDesc_errorcode(descErrorcode);
        itemErrorCode.setErrorcode(errorcode);
        itemErrorCode.setDesc_tooLowErrorcode(txtDescErrorcodeToolow.getText());
        itemErrorCode.setTooLowErrorcode(txtErrorcodeToolow.getText());
        itemErrorCode.setDesc_tooHighErrorcode(txtDescErrorcodeToohigh.getText());
        itemErrorCode.setTooHighErrorcode(txtErrorcodeToohigh.getText());
        JOptionPane.showMessageDialog(null, "update ok!");
    }//GEN-LAST:event_btUpdateActionPerformed

    private void btSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSaveActionPerformed
        // TODO add your handling code here:
        int rs = this.myChooser.showSaveDialog(null, String.format("%s.csv",System.currentTimeMillis()));
        if (rs == JOptionPane.OK_OPTION) {
            try {
                CSVcreater csv = new CSVcreater();
                csv.start(this.myChooser.getSelectedFile().getPath());
                for (ItemErrorCode errorCode : this.listTabel.getAllItem()) {
                    csv.put("Item", errorCode.getTest_name().toUpperCase());
                    csv.put("Name ER", errorCode.getDesc_errorcode().toUpperCase());
                    csv.put("Errorcode", errorCode.getErrorcode());
                    csv.put("Name toolow", errorCode.getDesc_tooLowErrorcode().toUpperCase());
                    csv.put("Toolow", errorCode.getTooLowErrorcode());
                    csv.put("Name toohigh", errorCode.getDesc_tooHighErrorcode().toUpperCase());
                    csv.put("toohigh", errorCode.getTooHighErrorcode());
                    csv.newLine();
                }
                csv.save();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btSaveActionPerformed

    @Override
    public void refesh() {
        if (model == null) {
            return;
        }
        this.txtDir.setText(model.getDir());
        this.listTabel.removeAllItem();
        for (ItemErrorCode errorCode : model.getErrorcodes().values()) {
            this.listTabel.addItem(errorCode);
        }
    }

    @Override
    public void update() {
        if (model == null) {
            return;
        }
        Map<String, ItemErrorCode> errorcodes = new HashMap<>();
        for (ItemErrorCode itemErrorCode : this.listTabel.getAllItem()) {
            errorcodes.put(itemErrorCode.getTest_name(), itemErrorCode);
        }
        model.setDir(txtDir.getText());
        model.setErrorcodes(errorcodes);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btSave;
    private javax.swing.JButton btUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtDescErrorcode;
    private javax.swing.JTextField txtDescErrorcodeToohigh;
    private javax.swing.JTextField txtDescErrorcodeToolow;
    private javax.swing.JTextField txtDir;
    private javax.swing.JTextField txtErrorcode;
    private javax.swing.JTextField txtErrorcodeToohigh;
    private javax.swing.JTextField txtErrorcodeToolow;
    private javax.swing.JTextField txtItemName;
    private javax.swing.JTextField txtNewErrorcde;
    // End of variables declaration//GEN-END:variables
}
