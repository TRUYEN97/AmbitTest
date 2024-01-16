/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tec02.view.subUI.FormDetail;

import com.tec02.view.subUI.AbsUI;


/**
 *
 * @author Administrator
 */
public abstract class AbsTabUI extends AbsUI {

    public AbsTabUI(String name, int timeUpdate) {
        super(name, timeUpdate);
    }

    public abstract void keyEvent(java.awt.event.KeyEvent evt);
}
