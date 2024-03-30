/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.main;

import com.tec02.main.modeFlow.ModeFlow;
import com.tec02.configuration.controller.ConfigurationManagement;
import com.tec02.configuration.model.itemTest.ItemTestDto;
import com.tec02.configuration.model.itemTest.ModeDto;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import lombok.NonNull;

/**
 *
 * @author Administrator
 */
public final class ModeManagement {
    
    private static volatile ModeManagement instance;
    private final ConfigurationManagement configurationManagement;
    private ModeDto modeDto;
    private ItemTestDto itemTestDto;
    private String currentModeName;
    private JComboBox<String> comboBox;
    
    private ModeManagement() {
        this.configurationManagement = ConfigurationManagement.getInstance();
        this.itemTestDto = this.configurationManagement.getItemTestConfig().getModel();
        var modeNames = this.itemTestDto.getConfig().getModes();
        if (modeNames != null && !modeNames.isEmpty()) {
            setModeName(modeNames.get(0).getModeName());
        }
    }
    
    public static ModeManagement getInsatace() {
        ModeManagement ins = ModeManagement.instance;
        if (ins == null) {
            synchronized (ModeManagement.class) {
                ins = ModeManagement.instance;
                if (ins == null) {
                    ModeManagement.instance = ins = new ModeManagement();
                }
            }
        }
        return ins;
    }
    
    public List<String> getModeNames() {
        return itemTestDto.getConfig().getModes().stream().map((t) -> {
            return t.getModeName();
        }).collect(Collectors.toList());
    }
    
    public boolean setModeName(String modeName) {
        ItemTestDto dto = this.configurationManagement.getItemTestConfig().getModel();
        if (dto == null) {
            return false;
        }
        ModeDto mDto = dto.getConfig().getMode(modeName);
        String pw = mDto.getPassword();
        if (pw != null && !pw.isBlank()) {
            JPasswordField passwordField = new JPasswordField();
            JOptionPane.showMessageDialog(null, passwordField,
                    String.format("%s - password", modeName), JOptionPane.PLAIN_MESSAGE);
            String input = String.valueOf(passwordField.getPassword());
            if (input == null || input.isBlank() || !input.equals(pw)) {
                return false;
            }
        }
        this.currentModeName = modeName;
        this.itemTestDto = dto;
        this.modeDto = dto.getConfig().getMode(modeName);
        return true;
    }
    
    public String getCurrentModeName() {
        return currentModeName;
    }
    
    public synchronized ModeFlow getModeFlow() {
        if (modeDto == null) {
            return null;
        }
        return new ModeFlow(modeDto, itemTestDto, currentModeName);
    }
    
    @NonNull
    public void setComboBox(JComboBox<String> cbbModeTest) {
        this.comboBox = cbbModeTest;
        this.comboBox.removeAllItems();
        for (String modeName : getModeNames()) {
            this.comboBox.addItem(modeName);
        }
        if (isChosseModeOk()) {
            this.comboBox.addItemListener((java.awt.event.ItemEvent evt) -> {
                if (evt.getStateChange() == ItemEvent.SELECTED) {
                    Object itemName = evt.getItem();
                    if (itemName == null || !setModeName(itemName.toString())) {
                        this.comboBox.setSelectedItem(currentModeName);
                    }
                }
            });
        } else {
            System.exit(0);
        }
    }
    
    private boolean isChosseModeOk() {
        for (String modeName : getModeNames()) {
            this.comboBox.setSelectedItem(modeName);
            if (setModeName(modeName)) {
                return true;
            }
        }
        return false;
    }
}
