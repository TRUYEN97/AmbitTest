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
public class ItemGroupDto {

    private int modeRun = 1;
    private List<Integer> failColor = List.of(255, 0, 0);
    private List<Integer> testColor = List.of(255, 255, 0);
    private String passTo;
    private String failedTo;
    private List<String> items = new ArrayList<>();
    private Integer begin;
    private int loop = 1;
    private int step = 1;
    private boolean coreGroup = true;

    public int getModeRun() {
        if (modeRun < 1) {
            modeRun = 1;
        } else if (modeRun > 3) {
            modeRun = 3;
        }
        return modeRun;
    }

    public List<Integer> getFailColor() {
        if (failColor == null) {
            return List.of(255, 0, 0);
        }
        int r = 255;
        int g = 0;
        int b = 0;
        if (!failColor.isEmpty()) {
            r = failColor.get(0);
            if (failColor.size() > 1) {
                g = failColor.get(1);
                if (failColor.size() > 2) {
                    b = failColor.get(2);
                }
            }
        }
        return List.of(r, g, b);
    }

    public List<Integer> getTestColor() {
        if (testColor == null) {
            return List.of(255, 255, 0);
        }
        int r = 255;
        int g = 255;
        int b = 0;
        if (!testColor.isEmpty()) {
            r = testColor.get(0);
            if (testColor.size() > 1) {
                g = testColor.get(1);
                if (testColor.size() > 2) {
                    b = testColor.get(2);
                }
            }
        }
        return List.of(r, g, b);
    }

}
