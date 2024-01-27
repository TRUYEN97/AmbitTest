/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.tec02.main;

import com.tec02.view.managerUI.DrawBoardUI;
import com.tec02.configuration.controller.ConfigurationManagement;
import com.tec02.view.Gui;
import com.tec02.view.managerUI.UICellManagement;
import java.io.FileNotFoundException;
import javax.swing.JOptionPane;

/**
 *
 * @author Administrator
 */
public class AmbitUI {

    private final DrawBoardUI drawBoardUI;
    private final ConfigurationManagement configurationManagement;
    private final Gui gui;
    

    public AmbitUI() {
        this.drawBoardUI = new DrawBoardUI();
        this.configurationManagement = ConfigurationManagement.getInstance();
        this.gui = Gui.getInstance();
        UICellManagement.getInstance().setGui(this.gui);
    }

    private void run() throws FileNotFoundException {
        this.configurationManagement.setFile("config.json");
        this.configurationManagement.init();
        this.configurationManagement.execute();
        this.drawBoardUI.setDrawPanel(this.gui.getDrawPanel());
        this.drawBoardUI.draw();
        this.gui.display();
    }

    public static void main(String[] args) {
        try {
            AmbitUI ambitUI = new AmbitUI();
            ambitUI.run();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getLocalizedMessage());
            System.exit(0);
        }
    }
}
