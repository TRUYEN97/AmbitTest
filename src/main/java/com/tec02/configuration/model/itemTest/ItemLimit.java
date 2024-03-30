/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.model.itemTest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Administrator
 */
@Getter
@Setter
@NoArgsConstructor
public class ItemLimit {
    protected String limit_type = "BOOL";
    protected int required = -1;
    protected String error_code = "";
    protected String test_name = "";
    protected String lower_limit = "";
    protected String upper_limit = "";
    protected String units = ""; 

    @Override
    public String toString() {
        return test_name;
    }
    
}
