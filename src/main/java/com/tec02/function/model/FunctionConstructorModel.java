/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.model;

import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.baseFunction.FunctionLogger;
import com.tec02.view.managerUI.UICell;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Administrator
 */
@Setter
@Getter
@Builder
public class FunctionConstructorModel {
    private UICell uICell;
    private Model model;
    private FunctionConfig config;
    private FunctionLogger logger;
    private Integer begin;
    private String limitName;
    private String configName;
}
