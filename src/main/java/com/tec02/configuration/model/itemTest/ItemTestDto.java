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
@Setter
@Getter
public class ItemTestDto {

    private ConfigDto config = new ConfigDto();
    private Map<String, ItemGroupDto> groups = new HashMap<>();
    private Map<String, ItemConfig> items = new HashMap<>();
    private Map<String, ItemLimit> limits = new HashMap<>();
    
}
