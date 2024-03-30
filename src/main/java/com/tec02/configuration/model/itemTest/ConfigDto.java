/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.model.itemTest;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Administrator
 */
@Getter
@Setter
public class ConfigDto {

    private String limitCmd;
    private String limitDir;
    private List<ModeDto> modes = new ArrayList<>();

    public ModeDto getMode(String name) {
        return getMode(name, null);
    }
    
    public ModeDto getMode(String name, ModeDto modeDtoDefault) {
        for (ModeDto mode : modes) {
            if (mode != null && mode.getModeName().equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return modeDtoDefault;
    }
}
