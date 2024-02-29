/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tec02.view.subUI.FormDetail;

import com.tec02.view.subUI.AbsUI;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author Administrator
 */
public abstract class AbsTabUI extends AbsUI {
    protected static final int CTRL_S = 19;
    protected static final int CTRL_D = 4;
    protected static final int CTRL_Q = 17;
    protected static final int CTRL_R = 18;
    protected static final int CTRL_W = 23;

    public AbsTabUI(String name, int timeUpdate) {
        super(name, timeUpdate);
    }

    public abstract void keyEvent(java.awt.event.KeyEvent evt);

    public abstract void tabSelected(ChangeEvent evt);
}
