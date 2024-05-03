/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.tec02.configuration.module.view.Socket;

import com.tec02.common.MyConst;
import com.tec02.configuration.model.socket.SocketClientDto;
import com.tec02.configuration.model.socket.SocketDto;
import com.tec02.configuration.module.view.AbsHasTabPanel;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class SocketPanel extends AbsHasTabPanel<SocketDto, ServerInfoPanel> {

    /**
     * Creates new form SocketPanal
     */
    public SocketPanel() {
        super("Socket");
        initComponents();
        this.pn.add(pnMain);
    }

    @Override
    protected ServerInfoPanel createTabPanel(String name) {
        SocketClientDto clientDto = model.getClients().get(name);
        if (clientDto == null) {
            clientDto = new SocketClientDto();
            model.getClients().put(name, clientDto);
        }
        return new ServerInfoPanel(name, clientDto);
    }

    @Override
    public void refesh() {
        var socketModel = model.getServer();
        cbFlag.setSelected(socketModel.isFlag());
        spPort.setValue(socketModel.getPort());
        clear();
        var clients = model.getClients();
        if (!clients.containsKey(MyConst.AE_SERVER_NAME)) {
            clients.put(MyConst.AE_SERVER_NAME, new SocketClientDto());
        }
        for (String name : clients.keySet()) {
            addTab(name);
        }
    }

    @Override
    public void update() {
        var socketModel = model.getServer();
        socketModel.setFlag(cbFlag.isSelected());
        socketModel.setPort((int) spPort.getValue());
        Map<String, SocketClientDto> clients = new HashMap<>();
        SocketPanel.this.model.setClients(clients);
        for (ServerInfoPanel tab : getTabPanel().getTabElements()) {
            tab.update();
            clients.put(tab.getTabName(), tab.getModel());
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
        cbFlag = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        spPort = new javax.swing.JSpinner();
        pn = new javax.swing.JPanel();

        setBackground(new java.awt.Color(102, 255, 204));

        jPanel1.setBackground(new java.awt.Color(0, 204, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Server", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 14))); // NOI18N

        cbFlag.setText("On");

        jLabel2.setText("Port server");

        spPort.setModel(new javax.swing.SpinnerNumberModel(8686, 0, null, 1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(spPort, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cbFlag)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(cbFlag)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spPort))
                .addContainerGap())
        );

        pn.setBackground(new java.awt.Color(0, 204, 204));
        pn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pn.setLayout(new javax.swing.BoxLayout(pn, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 69, Short.MAX_VALUE))
                    .addComponent(pn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbFlag;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel pn;
    private javax.swing.JSpinner spPort;
    // End of variables declaration//GEN-END:variables


    @Override
    public SocketDto getModel() {
        return model;
    }

    @Override
    protected void deleteTabFromModel(String tabName) {
        this.model.getClients().remove(tabName);
    }
}
