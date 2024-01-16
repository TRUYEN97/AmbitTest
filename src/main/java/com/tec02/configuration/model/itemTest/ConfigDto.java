/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.model.itemTest;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Administrator
 */
@Getter
@Setter
public class ConfigDto {

    private String limit;
    private Map<String, ModeDto> modes = new HashMap<>();

    public ModeDto getMode(String name) {
        return modes.get(name);
    }
}
