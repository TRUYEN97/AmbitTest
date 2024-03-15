/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.model.itemTest;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Administrator
 */
@Getter
@Setter
public class ModeDto {

    private String password;
    private String modeName;
    private String apiModeName;
    private Integer loop = 1;
    private String group;

    public Integer getLoop() {
        if (loop == null || loop < 1) {
            return 1;
        }
        return loop;
    }

}
