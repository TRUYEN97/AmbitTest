/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.main.modeFlow;

import com.tec02.configuration.model.itemTest.ItemGroupDto;
import com.tec02.configuration.model.itemTest.ItemTestDto;
import com.tec02.configuration.model.itemTest.ModeDto;
import com.tec02.configuration.model.itemTest.ItemConfig;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

/**
 *
 * @author Administrator
 */
public class ModeFlow {

    private final ModeDto modeDto;
    private final ItemTestDto itemTestDto;
    private final String modeName;
    private ItemGroupDto itemGroupDto;

    @NonNull
    public ModeFlow(ModeDto modeDto, ItemTestDto itemTestDto, String modeName) {
        this.modeDto = modeDto;
        this.itemTestDto = itemTestDto;
        this.modeName = modeName;
        this.itemGroupDto = getGroup(modeDto.getGroup());
    }

    public String getModeName() {
        return modeName;
    }

    public String getAPIMode() {
        return modeDto.getMode();
    }

    public int getLoop() {
        return this.modeDto.getLoop();
    }

    public Integer getBegin() {
        return itemGroupDto.getBegin();
    }

    public void reset() {
        this.itemGroupDto = getGroup(modeDto.getGroup());
    }

    public List<ItemConfig> getListItem() {
        if (itemGroupDto == null) {
            return null;
        }
        List<ItemConfig> itemConfigs = new ArrayList<>();
        ItemConfig itemConfig;
        for (String itemName : this.itemGroupDto.getItems()) {
            if ((itemConfig = (ItemConfig) this.itemTestDto.getItems().get(itemName)) != null) {
                itemConfig.setTest_name(itemName);
                itemConfigs.add(itemConfig);
            }
        }
        return itemConfigs;
    }

    public boolean nextToPassFlow() {
        String group = itemGroupDto.getPassTo();
        ItemGroupDto groupDto;
        if (group == null || (groupDto = getGroup(group)) == null) {
            itemGroupDto = null;
            return false;
        }
        itemGroupDto = groupDto;
        return true;
    }

    public boolean nextToFailedFlow() {
        String group = itemGroupDto.getFailedTo();
        ItemGroupDto groupDto;
        if (group == null || (groupDto = getGroup(group)) == null) {
            itemGroupDto = null;
            return false;
        }
        itemGroupDto = groupDto;
        return true;
    }

    public final ItemGroupDto getGroup(String groupName) {
        return this.itemTestDto.getGroups().get(groupName);
    }

    public Color getFailColor() {
        List<Integer> rgb = this.itemGroupDto.getFailColor();
        return new Color(rgb.get(0), rgb.get(1), rgb.get(2));
    }

    public Color getTestColor() {
        List<Integer> rgb = this.itemGroupDto.getTestColor();
        return new Color(rgb.get(0), rgb.get(1), rgb.get(2));
    }
}
