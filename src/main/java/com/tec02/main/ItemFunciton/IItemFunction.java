/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tec02.main.ItemFunciton;

import com.tec02.function.AbsFunction;

/**
 *
 * @author Administrator
 */
public interface IItemFunction {

    AbsFunction getFunction(String functionName, String itemName, String limitName, Integer begin);

    AbsFunction getFunction(String functionName, String itemName, Integer begin);
    
    AbsFunction getFunction(String functionName, String itemName, String limitName);

    AbsFunction getFunction(String functionName, String itemName);
}
