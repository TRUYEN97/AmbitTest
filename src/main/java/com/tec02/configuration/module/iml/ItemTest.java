/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module.iml;

import com.alibaba.fastjson.JSONObject;
import com.tec02.common.MyObjectMapper;
import com.tec02.communication.Communicate.Impl.Cmd.Cmd;
import com.tec02.configuration.model.itemTest.ItemLimit;
import com.tec02.configuration.model.itemTest.ItemTestDto;
import com.tec02.configuration.module.AbsModule;
import com.tec02.configuration.module.view.ItemTest.ItemTestPanel;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class ItemTest extends AbsModule<ItemTestDto, ItemTestDto> {

    private final Cmd cmd;

    public ItemTest(ItemTestDto itemTestDto) {
        super(itemTestDto, itemTestDto, new ItemTestPanel());
        this.cmd = new Cmd();
    }
    

    @Override
    public void execute() {
        String limitCmd = model.getConfig().getLimit();
        if (limitCmd == null || limitCmd.isBlank()) {
            return;
        }
        if (!cmd.insertCommand(limitCmd)) {
            throw new RuntimeException(String.format("run limit command failure!\r\n %s", cmd.readAll()));
        }
        String responce = cmd.readAll();
        System.out.println(responce);
        if (responce == null
                || !responce.trim().endsWith("200")
                || !responce.contains("{")
                || !responce.contains("}")) {
            throw new RuntimeException("get limit failure!");
        }
        String limit = responce.substring(responce.indexOf("{"),
                responce.lastIndexOf("}") + 1);
        JSONObject limits = JSONObject.parseObject(limit).getJSONObject("limits");
        Map<String, ItemLimit> limitDtos = new HashMap<>();
        for (String key : limits.keySet()) {
            limitDtos.put(key, MyObjectMapper.convertValue(limits.get(key), ItemLimit.class));
        }
        model.setLimits(limitDtos);
    }

}
