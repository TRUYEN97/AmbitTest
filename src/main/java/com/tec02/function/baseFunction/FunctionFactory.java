/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function.baseFunction;

import com.tec02.common.Logger;
import com.tec02.function.AbsFunction;
import com.tec02.function.impl.common.CheckTime;
import com.tec02.function.impl.runin.PowerSwitchFunc;
import com.tec02.function.impl.common.CheckDutCompare;
import com.tec02.function.impl.common.CheckProduct;
import com.tec02.function.impl.common.CheckTestConditions;
import com.tec02.function.impl.common.DeltaValue;
import com.tec02.function.impl.common.DutTelnet;
import com.tec02.function.impl.common.Dutping;
import com.tec02.function.impl.common.GetSfisAndCompareSpec;
import com.tec02.function.impl.common.SfisFunction;
import com.tec02.function.impl.common.TelnetReadUntilKey;
import com.tec02.function.impl.common.TempCPU;
import com.tec02.function.impl.common.VirFunction;
import com.tec02.function.impl.common.uploadLog.customerAPI.UpAPI;
import com.tec02.function.impl.common.uploadLog.ftp.UpLogFTP;
import com.tec02.function.impl.common.uploadLog.mydas.Mydas;
import com.tec02.function.impl.runin.EmmcBadBlock;
import com.tec02.function.impl.runin.EmmcSpeed;
import com.tec02.function.impl.runin.EmmcWriteRead;
import com.tec02.function.impl.runin.RebootSoft;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;

/**
 *
 * @author Administrator
 */
public class FunctionFactory {

    private final Map<String, Class<? extends AbsFunction>> functions;
    private final Logger logger;
    private static volatile FunctionFactory instance;

    private FunctionFactory() {
        this.functions = new HashMap<>();
        this.logger = new Logger("Log/FunctionManagement");
        init();
    }

    public static FunctionFactory getInstance() {
        FunctionFactory ins = FunctionFactory.instance;
        if (ins == null) {
            synchronized (FunctionFactory.class) {
                ins = FunctionFactory.instance;
                if (ins == null) {
                    FunctionFactory.instance = ins = new FunctionFactory();
                }
            }
        }
        return ins;
    }

    private void init() {
        addFunctionClass(CheckTime.class);
        addFunctionClass(PowerSwitchFunc.class);
        addFunctionClass(CheckTestConditions.class);
        addFunctionClass(Dutping.class);
        addFunctionClass( DutTelnet.class);
        addFunctionClass( CheckDutCompare.class);
        addFunctionClass( RebootSoft.class);
        addFunctionClass( EmmcWriteRead.class);
        addFunctionClass( EmmcSpeed.class);
        addFunctionClass(TelnetReadUntilKey.class);
        addFunctionClass(EmmcBadBlock.class);
        addFunctionClass(SfisFunction.class);
        addFunctionClass(CheckProduct.class);
        addFunctionClass(TempCPU.class);
        addFunctionClass(GetSfisAndCompareSpec.class);
        addFunctionClass(VirFunction.class);
        addFunctionClass(DeltaValue.class);
        addFunctionClass(UpAPI.class);
        addFunctionClass(Mydas.class);
        addFunctionClass(UpLogFTP.class);
    }

    @NonNull
    public void addFunctionClass(Class<? extends AbsFunction> functionClass) {
        String name = functionClass.getSimpleName();
        this.functions.put(name.toLowerCase(), functionClass);
        this.logger.addLog("add: %s", name);
    }

    public FunctionConfig getDefaultConfigOfFunction(String functionName) {
        AbsFunction absFunction = getFunction(functionName);
        if (absFunction == null) {
            return null;
        }
        return absFunction.getDefaultConfig();
    }

    public Set<String> getAllFunctionName() {
        return this.functions.keySet();
    }

    public Map<String, Class<? extends AbsFunction>> getFunctions() {
        return functions;
    }
    
    public AbsFunction getFunction(String functionName) {
        if (functionName == null || !this.functions.containsKey(functionName = functionName.toLowerCase())) {
            return null;
        }
        try {
            return this.functions.get(functionName).getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
            this.logger.addLog("Error: %s", ex.getLocalizedMessage());
            return null;
        }
    }

}
