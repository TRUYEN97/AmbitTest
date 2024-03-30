/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.tec02.view.subUI.FormDetail.TabItem.ShowLog;

import com.tec02.common.MyConst;
import com.tec02.function.IFunctionModel;
import com.tec02.view.managerUI.UICell;
import java.awt.event.MouseEvent;
import java.util.Queue;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Administrator
 */
public class ShowLog extends javax.swing.JFrame {

    /**
     * Creates new form ItemLog
     */
    private final UICell uICell;
    private Queue<String> queueLog;
    private Thread thread;
    private final IFunctionModel functionModel;

    public ShowLog(IFunctionModel functionModel, UICell uICell) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ShowLog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        initComponents();
        this.uICell = uICell;
        this.functionModel = functionModel;
        DefaultCaret caret = (DefaultCaret) txtLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    private void runtheard() {
        if (this.thread == null || !this.thread.isAlive()) {
            this.thread = new Thread(() -> {
                while (true) {
                    if (queueLog == null) {
                        return;
                    }
                    while (!queueLog.isEmpty()) {
                        ShowLog.this.txtLog.append(queueLog.poll());
                    }
                    if (!ShowLog.this.functionModel.isTesting()) {
                        ShowLog.this.txtLog.setText(this.functionModel.getLogger().getLog());
                        stopTimer();
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                    }
                }
            });
            this.thread.start();
        }
    }

    public void setlog(String data) {
        this.txtLog.setText(data);
    }

    public void appenLog(String data) {
        this.txtLog.append(data);
    }

    public void showLog() {
        String index = uICell.getName();
        var model = this.functionModel.getModel();
        var dataCell = uICell.getDataCell();
        String sn = dataCell.get(MyConst.MODEL.SN, "");
        String mlbsn = dataCell.get(MyConst.MODEL.MLBSN, "");
        String mac = dataCell.get(MyConst.MODEL.MAC, "");
        String title = String.format("%s %s %s %s %s", index, model.getTest_name(),
                sn == null ? "" : sn, mlbsn == null ? "" : mlbsn, mac == null ? "" : mac);
        this.setTitle(title);
        setVisible(true);
        if (this.functionModel.isTesting()) {
            this.queueLog = this.functionModel.getLogger().getTestQueue();
            runtheard();
        } else {
            this.txtLog.setText(this.functionModel.getLogger().getLog());
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

        jScrollPane2 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        txtLog.setEditable(false);
        txtLog.setColumns(20);
        txtLog.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtLog.setRows(5);
        txtLog.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtLogMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(txtLog);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        stopTimer();
        this.dispose();
    }//GEN-LAST:event_formWindowClosed

    private void txtLogMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtLogMouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON3 && evt.getClickCount() > 1) {
            if (this.txtLog.getDocument().getLength() > 0) {
                txtLog.setCaretPosition(this.txtLog.getDocument().getLength());
            }
            DefaultCaret caret = (DefaultCaret) txtLog.getCaret();
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        }
    }//GEN-LAST:event_txtLogMouseClicked

    /**
     * @param args the command line arguments
     */
    public void stopTimer() {
        if (thread == null || !thread.isAlive()) {
            return;
        }
        this.thread.stop();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea txtLog;
    // End of variables declaration//GEN-END:variables

}
