/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.main.modeFlow;

import com.tec02.common.Common;
import com.tec02.common.MyObjectMapper;
import com.tec02.configuration.model.itemTest.ItemGroupDto;
import com.tec02.configuration.model.itemTest.ItemTestDto;
import com.tec02.configuration.model.itemTest.ModeDto;
import com.tec02.configuration.model.itemTest.ItemConfig;
import com.tec02.main.ErrorLog;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
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
    private String currGroupName;
    private boolean coreGroup = false;

    @NonNull
    public ModeFlow(ModeDto modeDto, ItemTestDto itemTestDto, String modeName) {
        this.modeDto = modeDto;
        this.itemTestDto = itemTestDto;
        this.modeName = modeName;
        this.currGroupName = modeDto.getGroup();
        this.itemGroupDto = getGroup(this.currGroupName);
    }

    public ItemGroupDto getItemGroupDto() {
        return itemGroupDto;
    }

    public String getModeName() {
        return modeName;
    }

    public String getAPIMode() {
        return modeDto.getApiModeName();
    }

    public int getLoop() {
        return this.modeDto.getLoop();
    }

    public Integer getBegin() {
        return itemGroupDto.getBegin();
    }

    public void reset() {
        this.itemGroupDto = getGroup(modeDto.getGroup());
        this.coreGroup = false;
    }

    public List<ItemConfig> getListItem() {
        if (itemGroupDto == null) {
            return null;
        }
        Set<String> hasGroupsScan = new HashSet<>();
        hasGroupsScan.add(this.currGroupName);
        return scanItemIn(itemGroupDto, hasGroupsScan);
    }

    public List<ItemConfig> getPassToItems() {
        ModeFlow modeFlow = new ModeFlow(modeDto, itemTestDto, modeName);
        List<ItemConfig> itemConfigs = new ArrayList<>();
        List<ItemConfig> temps;
        while ((temps = modeFlow.getListItem()) != null) {
            itemConfigs.addAll(temps);
            modeFlow.nextToPassFlow();
        }
        return itemConfigs;
    }

    private List<ItemConfig> scanItemIn(ItemGroupDto itemGroupDto, Set<String> hasGroupsScan) {
        List<ItemConfig> itemConfigs = getItemConfigs(itemGroupDto, hasGroupsScan);
        List<ItemConfig> result = new ArrayList<>();
        for (int i = 0; i < itemGroupDto.getLoop(); i++) {
            result.addAll(itemConfigs);
        }
        return result;
    }

    private List<ItemConfig> getItemConfigs(ItemGroupDto itemGroupDto, Set<String> hasGroupsScan) {
        List<ItemConfig> itemConfigs = new ArrayList<>();
        if (itemGroupDto == null) {
            return itemConfigs;
        }
        ItemConfig itemConfig;
        int modeRun = itemGroupDto.getModeRun();
        Integer begin = itemGroupDto.getBegin();
        for (String name : itemGroupDto.getItems()) {
            if (Common.isGroupItem(name)) {
                name = Common.getBaseItem(name);
                if (!hasGroupsScan.contains(name) && this.itemTestDto.getGroups().containsKey(name)) {
                    hasGroupsScan.add(name);
                    itemConfigs.addAll(scanItemIn(getGroup(name), hasGroupsScan));
                    hasGroupsScan.remove(name);
                }
            } else if (this.itemTestDto.getItems().containsKey(name)
                    && (itemConfig = MyObjectMapper.convertValue(this.itemTestDto.getItems().get(name),
                            ItemConfig.class)) != null) {
                itemConfig.setItemName(name);
                if (itemConfig.getModeRun() > modeRun) {
                    itemConfig.setModeRun(modeRun);
                }
                if (begin != null && begin > itemConfig.getBegin()) {
                    itemConfig.setBegin(begin);
                }
                itemConfigs.add(itemConfig);
            } else {
                String mess = String.format("Missing item: %s", name);
                JOptionPane.showMessageDialog(null, mess);
                ErrorLog.addError(this, mess);
            }
        }
        return itemConfigs;
    }

    public boolean nextToPassFlow() {
        String group = itemGroupDto.getPassTo();
        return setGroup(group);
    }

    public boolean nextToFailedFlow() {
        String group = this.itemGroupDto.getFailedTo();
        return setGroup(group);
    }

    public boolean setGroup(String group) {
        ItemGroupDto groupDto;
        if (group == null || (groupDto = getGroup(group)) == null) {
            this.currGroupName = null;
            this.itemGroupDto = null;
            return false;
        }
        this.currGroupName = group;
        this.itemGroupDto = groupDto;
        if (this.itemGroupDto.isCoreGroup()) {
            coreGroup = true;
        }
        return true;
    }

    public Set<String> getGroupNames() {
        return this.itemTestDto.getGroups().keySet();
    }

    public final ItemGroupDto getGroup(String groupName) {
        if (groupName == null) {
            return null;
        }
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

    public boolean isCoreGroup() {
        if (itemGroupDto == null) {
            return false;
        }
        return itemGroupDto.isCoreGroup();
    }

    public boolean hasCoreGroup() {
        return coreGroup;
    }
}
