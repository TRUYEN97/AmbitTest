/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.impl.common;

import com.tec02.function.AbsFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.model.FunctionConstructorModel;
import java.awt.HeadlessException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Administrator
 */
public class CheckTestConditions extends AbsFunction {

    public CheckTestConditions(FunctionConstructorModel constructorModel) {
        super(constructorModel);
    }

    @Override
    protected boolean test() {
        int holpTimes = this.config.getInteger("hold", 3);
        addLog("CONFIG", "hold when %s times failed!", holpTimes);
        int failedConsecutive = this.uICell.getTestFailedConsecutive();
        addLog("PC", "times failed: %s", failedConsecutive);
        if (failedConsecutive >= holpTimes) {
            String mess = String.format("""
                          This position has exceeded the allowable number of failed tests.\r\n
                          Vị trí này đã test fail liên tục %s lần. Cần TE xác nhận để có thể chạy tiếp!
                          """, failedConsecutive);
            String id = this.config.getString("id", "TE");
            String pw = this.config.getString("pw", "Foxconn168!!");
            if (check(id, pw, failedConsecutive)) {
                addLog("PC", "Has been confirmed by %s!", id);
                return true;
            } else {
                addLog(PC, mess);
                this.setMessage(mess);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void createDefaultConfig(FunctionConfig config) {
        config.put("hold", 3);
        config.put("id", "TE");
        config.put("pw", "Foxconn168!!");
    }

    public boolean check(String id, String pass, int failedConsecutive) {
        String mess = String.format("""
                          This position has exceeded the allowable number of failed tests.\r\n
                          Vị trí này đã test fail liên tục %s lần. Cần TE xác nhận để có thể chạy tiếp!
                          """, failedConsecutive);
        JLabel jUserName = new JLabel("User Name");
        JTextField userName = new JTextField();
        JLabel jPassword = new JLabel("Password");
        JTextField password = new JPasswordField();
        Object[] ob;
        if (!mess.isBlank()) {
            String message = String.format("<html>%s<html>",
                    mess.replaceAll("\r\n", "<br>"));
            ob = new Object[]{new JLabel(message),
                 jUserName, userName, jPassword, password};
        } else {
            ob = new Object[]{jUserName, userName, jPassword, password};
        }
        if (show(ob) == JOptionPane.OK_OPTION) {
            return userName.getText().equalsIgnoreCase(id) 
                    && password.getText().equals(pass);
        } else {
            return false;
        }
    }

    private int show(Object[] ob) throws HeadlessException {
        return JOptionPane.showConfirmDialog(null,
                ob, "Please input password!",
                JOptionPane.OK_CANCEL_OPTION);
    }

}
