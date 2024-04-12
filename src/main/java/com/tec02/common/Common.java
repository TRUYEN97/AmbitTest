/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.common;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author Administrator
 */
public class Common {

    public static String getBaseItem(String itemName) {
        if (itemName != null) {
            if (itemName.matches(ITEM_NUMBER_REGEX)) {
                return itemName.substring(0, itemName.lastIndexOf("_"));
            } else if (isGroupItem(itemName)) {
                itemName = itemName.replaceAll("\\*G\\-", "");
            }
        }
        return itemName;
    }
    
    

    public static Double cvtString2Num(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean isGroupItem(String name) {
        return name.startsWith("*G-");
    }

    public static String createGroupCodeName(String groupName) {
        return String.format("*G-%s", groupName);
    }

    public static Integer getOrderNumberItem(String itemName) {
        if (itemName != null && itemName.matches(ITEM_NUMBER_REGEX)) {
            return Integer.valueOf(itemName.substring(itemName.lastIndexOf("_") + 1).trim());
        }
        return null;
    }

    public static final String ITEM_NUMBER_REGEX = ".+_[0-9]+$";

    public static Integer checkBeginNumber(String itemName, Integer begin) {
        if (begin == null) {
            begin = -1;
        }
        Integer number = getOrderNumberItem(itemName);
        if (number != null && number > begin) {
            return number;
        }
        return begin;
    }

    public static String findGroup(String line, String regex) {
        if (line == null || regex == null) {
            return null;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static List<String> findGroups(String line, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        return matcher.results().map((t) -> t.group()).collect(Collectors.toList());
    }

    public static String subString(String line, String startkey, String endkey) {
        if (!stringAvailable(startkey) && !stringAvailable(endkey)) {
            return line.trim();
        } else if (stringAvailable(startkey) && line.contains(startkey)
                && !stringAvailable(endkey)) {
            int index = line.indexOf(startkey) + startkey.length();
            return line.substring(index).trim();
        } else if (stringAvailable(endkey) && line.contains(endkey)
                && !stringAvailable(startkey)) {
            int index = line.indexOf(endkey);
            return line.substring(0, index).trim();
        } else if (stringAvailable(startkey) && line.contains(startkey)
                && stringAvailable(endkey) && line.contains(endkey)) {
            int start = line.indexOf(startkey) + startkey.length();
            int end = line.lastIndexOf(endkey);
            return line.substring(start, end).trim();
        }
        return null;
    }

    public static boolean stringAvailable(String str) {
        return str != null && !str.isBlank();
    }
}
