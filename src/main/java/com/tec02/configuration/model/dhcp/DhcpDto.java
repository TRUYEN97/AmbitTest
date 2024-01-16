/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.model.dhcp;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Administrator
 */
@Getter
@Setter
public class DhcpDto{
    private boolean on = false;
    private String netIP = "172.16.0.255";
    private int macLength = 17;
    private int leaseTime = 604800;
}
