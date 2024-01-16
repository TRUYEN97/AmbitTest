/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module;

import javax.swing.JPanel;

/**
 *
 * @author Administrator
 * @param <M>
 */
public abstract class AbsModuleView<M> extends JPanel implements IRefeshAndUpdate{
    
    protected M model;

    public void setModel(M model) {
        this.model = model;
    }
  
}
