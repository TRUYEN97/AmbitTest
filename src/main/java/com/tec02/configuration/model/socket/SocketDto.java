/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.model.socket;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Administrator
 */
@Getter
@Setter
public class SocketDto {

    private SocketServerDto server = new SocketClientDto();
    private Map<String, SocketClientDto> clients = new HashMap<>();

    public SocketClientDto getClient(String name) {
        if(name == null){
            return null;
        }
        return this.clients.get(name);
    }
}
