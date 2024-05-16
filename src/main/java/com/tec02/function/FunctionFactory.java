/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.function;

import com.tec02.common.Logger;
import com.tec02.function.baseFunction.AnalysisBase;
import com.tec02.function.baseFunction.BaseFunction;
import com.tec02.function.baseFunction.FileBaseFunction;
import com.tec02.function.baseFunction.FunctionConfig;
import com.tec02.function.impl.common.controller.AutoClickOnScreen;
import com.tec02.function.impl.common.reader.CheckComport;
import com.tec02.function.impl.common.CheckTime;
import com.tec02.function.impl.runin.PowerSwitchFunc;
import com.tec02.function.impl.common.reader.CheckDutCompare;
import com.tec02.function.impl.common.reader.CheckDutUsb;
import com.tec02.function.impl.common.reader.CheckKingTonIC;
import com.tec02.function.impl.common.CheckProduct;
import com.tec02.function.impl.common.CheckTestConditions;
import com.tec02.function.impl.common.DeltaValue;
import com.tec02.function.impl.common.DutTelnet;
import com.tec02.function.impl.common.Dutping;
import com.tec02.function.impl.common.FindMacBLE;
import com.tec02.function.impl.common.controller.FixtureAction;
import com.tec02.function.impl.common.GetSfisAndCompareSpec;
import com.tec02.function.impl.common.GoldenFile;
import com.tec02.function.impl.common.reader.IperfSpeedTest;
import com.tec02.function.impl.common.LedTest;
import com.tec02.function.impl.common.NetworkControlAndPing;
import com.tec02.function.impl.common.controller.NetworkAdapterControl;
import com.tec02.function.impl.common.controller.ResetButton;
import com.tec02.function.impl.common.SaveLocalLog;
import com.tec02.function.impl.common.SendCommandInFileAndPing;
import com.tec02.function.impl.common.SfisFunction;
import com.tec02.function.impl.common.reader.TelnetReadUntilKey;
import com.tec02.function.impl.common.reader.TempCPU;
import com.tec02.function.impl.common.reader.UserspaceSpeedtestSpeed;
import com.tec02.function.impl.common.VirFunction;
import com.tec02.function.impl.common.uploadLog.CreateJsonApi;
import com.tec02.function.impl.common.uploadLog.CreateTxt;
import com.tec02.function.impl.common.uploadLog.ZipFile;
import com.tec02.function.impl.common.uploadLog.customerAPI.UpAPI;
import com.tec02.function.impl.common.uploadLog.ftp.UpFTP;
import com.tec02.function.impl.common.uploadLog.ftp.UpLogFTP;
import com.tec02.function.impl.common.uploadLog.mydas.Mydas;
import com.tec02.function.impl.common.uploadLog.teAPI.UpTeAPI;
import com.tec02.function.impl.mblt.OpenShort;
import com.tec02.function.impl.mblt.Usbside;
import com.tec02.function.impl.mblt.UbootidleTestFixture;
import com.tec02.function.impl.common.ValueSubItem;
import com.tec02.function.impl.common.ValueWithSpecSubItem;
import com.tec02.function.impl.common.controller.ResetButtonCCT;
import com.tec02.function.impl.common.reader.GetMD5;
import com.tec02.function.impl.common.reader.PhyItem;
import com.tec02.function.impl.common.reader.ReadComport;
import com.tec02.function.impl.mblt.SetENV;
import com.tec02.function.impl.mblt.UsbsideCheckReboot;
import com.tec02.function.impl.mblt.voltageTest.impl.ThermalShutdown;
import com.tec02.function.impl.mblt.voltageTest.impl.VoltageTest;
import com.tec02.function.impl.runin.EmmcBadBlock;
import com.tec02.function.impl.runin.EmmcSpeed;
import com.tec02.function.impl.runin.EmmcWriteRead;
import com.tec02.function.impl.runin.RebootSoft;
import com.tec02.function.impl.sft.LedBootup;
import com.tec02.function.model.FunctionConstructorModel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import lombok.NonNull;

/**
 *
 * @author Administrator
 */
public class FunctionFactory {
    
    private final Map<String, Class<? extends AbsFunction>> functions;
    private final Map<String, Class<? extends AbsBaseFunction>> baseFunctions;
    private final Logger logger;
    private static volatile FunctionFactory instance;
    
    private FunctionFactory() {
        this.functions = new HashMap<>();
        this.baseFunctions = new HashMap<>();
        this.logger = new Logger("Log/FunctionManagement");
        initBaseFunction();
        initFunction();
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
    
    private void initBaseFunction() {
        addBaseFunctionClass(CreateJsonApi.class);
        addBaseFunctionClass(AnalysisBase.class);
        addBaseFunctionClass(BaseFunction.class);
        addBaseFunctionClass(CreateTxt.class);
        addBaseFunctionClass(FileBaseFunction.class);
        addBaseFunctionClass(UpFTP.class);
        addBaseFunctionClass(ZipFile.class);
    }
    
    private void initFunction() {
        addFunctionClass(CheckTime.class);
        addFunctionClass(PowerSwitchFunc.class);
        addFunctionClass(CheckTestConditions.class);
        addFunctionClass(Dutping.class);
        addFunctionClass(DutTelnet.class);
        addFunctionClass(CheckDutCompare.class);
        addFunctionClass(RebootSoft.class);
        addFunctionClass(EmmcWriteRead.class);
        addFunctionClass(EmmcSpeed.class);
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
        addFunctionClass(GoldenFile.class);
        addFunctionClass(OpenShort.class);
        addFunctionClass(FixtureAction.class);
        addFunctionClass(Usbside.class);
        addFunctionClass(UpTeAPI.class);
        addFunctionClass(SaveLocalLog.class);
        addFunctionClass(ThermalShutdown.class);
        addFunctionClass(UbootidleTestFixture.class);
        addFunctionClass(VoltageTest.class);
        addFunctionClass(ValueSubItem.class);
        addFunctionClass(CheckDutUsb.class);
        addFunctionClass(ValueSubItem.class);
        addFunctionClass(UserspaceSpeedtestSpeed.class);
        addFunctionClass(LedTest.class);
        addFunctionClass(SetENV.class);
        addFunctionClass(ResetButton.class);
        addFunctionClass(ResetButtonCCT.class);
        addFunctionClass(IperfSpeedTest.class);
        addFunctionClass(CheckKingTonIC.class);
        addFunctionClass(LedBootup.class);
        addFunctionClass(CheckComport.class);
        addFunctionClass(FindMacBLE.class);
        addFunctionClass(UsbsideCheckReboot.class);
        addFunctionClass(NetworkAdapterControl.class);
        addFunctionClass(AutoClickOnScreen.class);
        addFunctionClass(PhyItem.class);
        addFunctionClass(NetworkControlAndPing.class);
        addFunctionClass(ValueWithSpecSubItem.class);
        addFunctionClass(GetMD5.class);
        addFunctionClass(ReadComport.class);
        addFunctionClass(SendCommandInFileAndPing.class);
    }
    
    @NonNull
    public void addFunctionClass(Class<? extends AbsFunction> functionClass) {
        String name = functionClass.getSimpleName();
        this.functions.put(name.toLowerCase(), functionClass);
        this.logger.addLog("add func: %s", name);
    }
    
    @NonNull
    public void addBaseFunctionClass(Class<? extends AbsBaseFunction> baseFunctionClass) {
        String name = baseFunctionClass.getSimpleName();
        this.baseFunctions.put(name.toLowerCase(), baseFunctionClass);
        this.logger.addLog("add base func: %s", name);
    }
    
    public FunctionConfig getDefaultConfigOfFunction(String functionName) {
        AbsFunction absFunction = getFunction(functionName, null);
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
    
    public synchronized AbsFunction getFunction(String functionName, FunctionConstructorModel constructorModdel) {
        if (functionName == null
                || !this.functions.containsKey(functionName.toLowerCase())) {
            JOptionPane.showMessageDialog(null,
                    String.format("Func: \"%s\", function not exists!",
                            functionName));
            return null;
        }
        try {
            return this.functions.get(functionName.toLowerCase())
                    .getConstructor(FunctionConstructorModel.class)
                    .newInstance(constructorModdel);
        } catch (Exception ex) {
            ex.printStackTrace();
            this.logger.addLog("Error: %s", ex.getLocalizedMessage());
            return null;
        }
    }
    
    public AbsBaseFunction getBaseFunction(String functionName, FunctionConstructorModel constructorModel) {
        if (functionName == null
                || !this.baseFunctions.containsKey(functionName = functionName.toLowerCase())) {
            return null;
        }
        try {
            return this.baseFunctions.get(functionName).getConstructor(FunctionConstructorModel.class).newInstance(constructorModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            this.logger.addLog("Error: %s", ex.getLocalizedMessage());
            return null;
        }
    }
    
}
