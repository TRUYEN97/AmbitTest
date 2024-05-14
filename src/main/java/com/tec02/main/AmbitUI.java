/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.tec02.main;

import com.tec02.FileService.FileService;
import com.tec02.FileService.MD5;
import com.tec02.view.managerUI.DrawBoardUI;
import com.tec02.configuration.controller.ConfigurationManagement;
import com.tec02.view.Gui;
import com.tec02.view.ShowConfigEdit;
import com.tec02.view.managerUI.UICellManagement;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.JOptionPane;

/**
 *
 * @author Administrator
 */
public class AmbitUI {

    public AmbitUI(String name, String version) {
        Core core = Core.getInstance();
        core.setAppName(name);
        core.setSoftwareVsersion(version);
    }

    private void run() throws FileNotFoundException {
        ConfigurationManagement configurationManagement = ConfigurationManagement.getInstance();
        configurationManagement.setFile("config.json");
        configurationManagement.init();
        configurationManagement.execute();
        Gui gui = Gui.getInstance();
        UICellManagement.getInstance().setGui(gui);
        DrawBoardUI drawBoardUI = new DrawBoardUI();
        drawBoardUI.setDrawPanel(gui.getDrawPanel());
        drawBoardUI.draw();
        gui.display();
    }

    public static void main(String[] args) {
        try {
            String version = "1.0.0";
            String name = "AmbitUI";
            if (args.length >= 1) {
                version = args[0].trim();
            }
            if (args.length >= 2) {
                name = args[1].trim();
            }
            if (version.equalsIgnoreCase("config")) {
                ShowConfigEdit showConfigEdit = new ShowConfigEdit();
                showConfigEdit.display();
            } else {
                AmbitUI ambitUI = new AmbitUI(name, version);
                ambitUI.run();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getLocalizedMessage());
            System.exit(0);
        }
    }
}
