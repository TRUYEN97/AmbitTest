/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.baseFunction;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tec02.FileService.FileService;
import com.tec02.FileService.Zip;
import com.tec02.common.MyObjectMapper;
import com.tec02.function.AbsBaseFunction;
import com.tec02.function.model.FunctionConstructorModel;
import java.io.File;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class FileBaseFunction extends AbsBaseFunction {

    private final FileService fileService;
    private final Zip zip;

    public FileBaseFunction(FunctionConstructorModel constructorModel) {
        super(constructorModel);
        fileService = new FileService();
        zip = new Zip();
    }

    public boolean saveJson(JSONObject data, String path) {
        String json = data.toJSONString();
        try {
            json = MyObjectMapper.prettyPrintJsonUsingDefaultPrettyPrinter(json);
        } catch (JsonProcessingException ex) {
            addLog(ERROR, ex.getLocalizedMessage());
        }
        return saveTxt(json, path);
    }

    public String createStringPath(String prefix, String fileName, String suffix) {
        List<String> elementDir = this.config.getJsonList(prefix);
        addLog(CONFIG, "prefix: %s", elementDir);
        List<String> elementNames = this.config.getJsonList(fileName);
        addLog(CONFIG, "File name: %s", elementNames);
        String suffer = this.config.getString(suffix);
        addLog(CONFIG, "suffix: %s", suffer);
        String path = String.format("%s/%s.%s", createName(elementDir),
                createName(elementNames), suffer);
        addLog(PC, "Path: %s", path);
        return path;
    }

    public boolean saveTxt(String data, String path) {
        return saveFile(path, data);
    }

    public boolean saveFile(String dirPath, String nameFile, String data) {
        String filePath = String.format("%s/%s", dirPath, nameFile);
        return saveFile(filePath, data);
    }

    public boolean saveFile(String filePath, String data) {
        if (fileService.saveFile(filePath, data)) {
            addLog("PC", String.format("Save data in: %s ok", filePath));
            return true;
        }
        addLog("PC", String.format("Save data in: %s failed!", filePath));
        return false;
    }

    public boolean saveZip(String zipPath, String filePath) {
        addLog("PC", String.format("%s -> %s", filePath, zipPath));
        if (zip.zipFile(zipPath, new File(filePath))) {
            addLog("PC", String.format("Save data in: %s ok", zipPath));
            return true;
        }
        addLog("PC", String.format("Save data in: %s failed!", zipPath));
        return false;
    }

    public boolean saveZip(String dirPath, String zipName, String fileName) {
        String zipPath = String.format("%s\\%s", dirPath, zipName);
        String filePath = String.format("%s\\%s", dirPath, fileName);
        addLog("PC", String.format("%s -> %s", filePath, zipPath));
        if (zip.zipFile(zipPath, new File(filePath))) {
            addLog("PC", String.format("Save data in: %s ok", zipPath));
            return true;
        }
        addLog("PC", String.format("Save data in: %s failed!", zipPath));
        return false;
    }

    public String createName(List<String> elementName) {
        StringBuilder pathName = new StringBuilder();
        addLog(CONFIG, "Name elements: %s", elementName);
        for (String elem : elementName) {
            if (!pathName.isEmpty()) {
                pathName.append("_");
            }
            elem = elem.trim();
            String value;
            if ((value = dataCell.getString(elem)) == null || elem.matches("^\\{\\w+\\}$")) {
                elem = elem.replaceAll("\\{|\\}", "");
                pathName.append(elem);
            } else {
                value = value.replace('\\', '-');
                value = value.replace('/', '-');
                value = value.replace(' ', '-');
                value = value.replace(':', '-');
                pathName.append(value);
            }
        }
        return pathName.toString();
    }

    public String createDir(List<String> elementDir) {
        StringBuilder pathName = new StringBuilder();
        addLog(CONFIG, "Dir elements: %s", elementDir);
        for (String elem : elementDir) {
            if (!pathName.isEmpty()) {
                pathName.append("/");
            }
            String value = dataCell.getString(elem);
            if (value == null) {
                pathName.append(elem);
            } else {
                value = value.replace('\\', '-');
                value = value.replace('/', '-');
                value = value.replace(' ', '-');
                value = value.replace(':', '-');
                pathName.append(value);
            }
        }
        return pathName.toString();
    }

    public String createDefaultStringPath(boolean pass) {
        String fileNameKey = pass ? "LocalName" : "LocalNameFail";
        return createStringPath("LocalPrefix", fileNameKey, "LocalSuffix");
    }

}
