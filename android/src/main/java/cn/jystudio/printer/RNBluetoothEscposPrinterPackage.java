package cn.jystudio.printer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.jystudio.printer.bluetooth.BluetoothService;
import cn.jystudio.printer.bluetooth.RNBluetoothManagerModule;
import cn.jystudio.printer.bluetooth.escpos.RNBluetoothEscposPrinterModule;
import cn.jystudio.printer.bluetooth.tsc.RNBluetoothTscPrinterModule;
import cn.jystudio.printer.bluetooth.zpl.RNBluetoothZplPrinterModule;
import cn.jystudio.printer.network.RNNetworkManagerModule;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.JavaScriptModule;

public class RNBluetoothEscposPrinterPackage implements ReactPackage {
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        BluetoothService service = new BluetoothService(reactContext);
        return Arrays.<NativeModule>asList(
            new RNBluetoothManagerModule(reactContext, service),
            new RNBluetoothEscposPrinterModule(reactContext, service),
            new RNBluetoothTscPrinterModule(reactContext, service),
            new RNBluetoothZplPrinterModule(reactContext, service),
            new RNNetworkManagerModule(reactContext)
        );
    }

    // Deprecated from RN 0.47
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
}