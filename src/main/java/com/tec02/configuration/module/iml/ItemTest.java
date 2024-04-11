/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module.iml;

import com.alibaba.fastjson2.JSONObject;
import com.tec02.FileService.FileService;
import com.tec02.common.MyObjectMapper;
import com.tec02.communication.Communicate.Impl.Cmd.Cmd;
import com.tec02.configuration.model.itemTest.ItemLimit;
import com.tec02.configuration.model.itemTest.ItemTestDto;
import com.tec02.configuration.module.AbsModule;
import com.tec02.configuration.module.view.ItemTest.ItemTestPanel;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author Administrator
 */
public class ItemTest extends AbsModule<ItemTestDto, ItemTestDto, ItemTestPanel> {

    private final Cmd cmd;

    public ItemTest(ItemTestDto itemTestDto) {
        super(itemTestDto, itemTestDto, new ItemTestPanel());
        this.cmd = new Cmd();
    }

    @Override
    public void execute() {
        String limit = null;
        String limitCmd = model.getConfig().getLimitCmd();
        String limitDir = model.getConfig().getLimitDir();
        if (limitCmd != null && !limitCmd.isBlank()) {
            if (!cmd.insertCommand(limitCmd)) {
                throw new RuntimeException(String.format("Run limits command failure!\r\n %s", cmd.readAll()));
            }
            String responce = cmd.readAll();
            System.out.println(responce);
            if (responce == null
                    || !responce.trim().endsWith("200")
                    || !responce.contains("{")
                    || !responce.contains("}")) {
                throw new RuntimeException("Get limits failure!");
            }
            limit = responce.substring(responce.indexOf("{"),
                    responce.lastIndexOf("}") + 1);
        } else if (limitDir != null && !limitDir.isBlank()) {
            File file = new File(limitDir);
            if (!file.exists()) {
                throw new RuntimeException("Get limits file not found! " + file);
            }
            FileService fileService = new FileService();
            limit = fileService.readFile(file);
        } else {
            return;
        }
        try {
            JSONObject limits = JSONObject.parseObject(limit).getJSONObject("limits");
            Map<String, ItemLimit> limitDtos = new HashMap<>();
            for (String key : limits.keySet()) {
                limitDtos.put(key, MyObjectMapper.convertValue(limits.get(key), ItemLimit.class));
            }
            model.setLimits(limitDtos);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

}
