/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * PanelDetail.java
 *
 * Created on Jan 24, 2022, 5:18:06 PM
 */
package com.tec02.view.subUI.FormDetail;

import com.tec02.view.managerUI.UICell;
import com.tec02.view.subUI.SubUI.AbsSubUI;
import java.util.HashMap;
import javax.swing.JPanel;

/**
 *
 * @author Administrator
 */
public class TabDetail extends JPanel {

    private final HashMap<String, AbsTabUI> tabElements;
    private final AbsSubUI boss;

    /**
     * Creates new form PanelDetail
     *
     * @param boss
     */
    public TabDetail(AbsSubUI boss) {
        this.tabElements = new HashMap<>();
        initComponents();
        this.boss = boss;
    }

    public AbsSubUI getBoss() {
        return this.boss;
    }

    public void addTabView(String title, AbsTabUI absTabUI) {
        this.tabDetail.addTab(title, absTabUI);
        this.tabElements.put(title, absTabUI);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabDetail = new javax.swing.JTabbedPane();

        setLayout(new java.awt.BorderLayout());

        tabDetail.setBackground(new java.awt.Color(0, 204, 102));
        tabDetail.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tabDetail.setName("tabDetail"); // NOI18N
        tabDetail.setOpaque(true);
        tabDetail.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabDetailStateChanged(evt);
            }
        });
        tabDetail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tabDetailKeyTyped(evt);
            }
        });
        add(tabDetail, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void tabDetailKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabDetailKeyTyped
        // TODO add your handling code here:
        for (String name : tabElements.keySet()) {
            if (tabElements.get(name).isVisible()) {
                tabElements.get(name).keyEvent(evt);
            }
        }
    }//GEN-LAST:event_tabDetailKeyTyped

    private void tabDetailStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabDetailStateChanged
        // TODO add your handling code here:
        for (String key : tabElements.keySet()) {
            if (tabElements.get(key).isVisible()) {
                tabElements.get(key).tabSelected(evt);
            }
        }
    }//GEN-LAST:event_tabDetailStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabDetail;
    // End of variables declaration//GEN-END:variables

    public void clear() {
        this.tabDetail.removeAll();
        this.tabElements.clear();
    }

    public void setUICell(UICell uiStatus) {
        for (String name : tabElements.keySet()) {
            tabElements.get(name).setUICell(uiStatus);
        }
    }

    public void startTest() {
        for (String item : tabElements.keySet()) {
            tabElements.get(item).startTest();
        }
    }

    public void endTest() {
        for (String item : tabElements.keySet()) {
            tabElements.get(item).endTest();
        }
    }
    
    public void setSelectedIndex(int index) {
        if (index < tabDetail.getTabCount()) {
            tabDetail.setSelectedIndex(index);
        }
    }

}
