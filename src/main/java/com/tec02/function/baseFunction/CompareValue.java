/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.baseFunction;

import com.tec02.common.Common;

/**
 *
 * @author Administrator
 */
public class CompareValue {
    public static final int TOOHIGH = 3;
    public static final int TOOLOW = 2;
    public static final int FALSE = 1;
    public static final int TRUE = 0;
    
    public int checkLimitType(String result, String lowerLimit, String upperLimit) {
        Double upper = Common.cvtString2Num(upperLimit);
        Double lower = Common.cvtString2Num(lowerLimit);
        Double val = Common.cvtString2Num(result);
        if ((upper == null && lower == null) || val == null) {
            return FALSE;
        }
        if (lower == null) {
            if (aGreatThanB(val, upper)) {
                return TOOHIGH;
            }
        } else if (upper == null) {
            if (aGreatThanB(lower, val)) {
                return TOOLOW;
            }
        } else {
            if (aGreatThanB(val, upper)) {
                return TOOHIGH;
            }
            if (aGreatThanB(lower, val)) {
                return TOOLOW;
            }
        }
        return TRUE;
    }
    
    public boolean checkMatchType(String result, String lowerLimit, String upperLimit) {
        try {
            return getMatch(result, lowerLimit) || getMatch(result, upperLimit);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    
    private boolean getMatch(String result, String configSpec) {
        if (result == null) {
            return false;
        }
        result = result.trim();
        configSpec = configSpec.trim();
        if (result.equals(configSpec)) {
            return true;
        }
        String[] limits = configSpec.split("\\|");
        if (limits != null && limits.length > 0) {
            for (String spec : limits) {
                if (spec.equals(result)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    private static boolean aGreatThanB(Double a, Double b) {
        if (a == null) {
            return false;
        }
        return a.compareTo(b) >= 1;
    }
}
