package cn.jystudio.printer.network;


import android.app.Activity;
import android.net.wifi.WifiManager;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.*;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nullable;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.OutputStream;

/**
 * Created by januslo on 2018/9/22.
 */
public class RNNetworkManagerModule extends ReactContextBaseJavaModule 
    implements ActivityEventListener
{
    private boolean isRunning = false;
    private static final String TAG = "NetworkManager";
    private final ReactApplicationContext reactContext;
     private int[] PRINTER_ON_PORTS = {9100};
    private static final String EVENT_SCANNER_RESOLVED = "scannerResolved";
    private static final String EVENT_SCANNER_RUNNING = "scannerRunning";
    public static final String EVENT_DEVICE_ALREADY_PAIRED = "EVENT_DEVICE_ALREADY_PAIRED";
    public static final String EVENT_DEVICE_FOUND = "EVENT_DEVICE_FOUND";
    public static final String EVENT_DEVICE_DISCOVER_DONE = "EVENT_DEVICE_DISCOVER_DONE";
    public static final String EVENT_CONNECTION_LOST = "EVENT_CONNECTION_LOST";
    public static final String EVENT_UNABLE_CONNECT = "EVENT_UNABLE_CONNECT";
    public static final String EVENT_CONNECTED = "EVENT_CONNECTED";
    public static final String EVENT_BLUETOOTH_NOT_SUPPORT = "EVENT_BLUETOOTH_NOT_SUPPORT";


    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // public static final int MESSAGE_STATE_CHANGE = BluetoothService.MESSAGE_STATE_CHANGE;
    // public static final int MESSAGE_READ = BluetoothService.MESSAGE_READ;
    // public static final int MESSAGE_WRITE = BluetoothService.MESSAGE_WRITE;
    // public static final int MESSAGE_DEVICE_NAME = BluetoothService.MESSAGE_DEVICE_NAME;

    // public static final int MESSAGE_CONNECTION_LOST = BluetoothService.MESSAGE_CONNECTION_LOST;
    // public static final int MESSAGE_UNABLE_CONNECT = BluetoothService.MESSAGE_UNABLE_CONNECT;
    // public static final String DEVICE_NAME = BluetoothService.DEVICE_NAME;
    // public static final String TOAST = BluetoothService.TOAST;

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    private static final Map<String, Promise> promiseMap = Collections.synchronizedMap(new HashMap<String, Promise>());
    private static final String PROMISE_ENABLE_BT = "ENABLE_BT";
    private static final String PROMISE_SCAN = "SCAN";
    private static final String PROMISE_CONNECT = "CONNECT";

    private JSONArray pairedDeivce = new JSONArray();
    private JSONArray foundDevice = new JSONArray();
    // Name of the connected device
    private String mConnectedDeviceName = null;
    
    // Member object for the services
    // private BluetoothService mService = null;
    private Socket mPrinterSocket = null;

    private static final String COMMAND_LANGUAGE_ZPL = "ZPL";
    private static final String COMMAND_LANGUAGE_TSC = "TSC";
    private static final String COMMAND_LANGUAGE_ESCPOS = "ESCPOS";

    public RNNetworkManagerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.reactContext.addActivityEventListener(this);
        //this.mService = bluetoothService;
        //this.mService.addStateObserver(this);
        // Register for broadcasts when a device is discovered
        //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //this.reactContext.registerReceiver(discoverReceiver, filter);
    }

    @ReactMethod
    public void connect(String ipAddress, int port, final Promise promise) {
        try {
            Socket socket = new Socket(ipAddress, port);
            if (socket.isConnected()) {
                promise.resolve(true);
            } else {
                Log.d("NETWORK_PRINTER","Failed to connect");
            }
        } catch (Exception e) {
            e.printStackTrace();
            promise.reject("failed to connect to printer with address "+ipAddress+":"+port);
        }
    }

    @ReactMethod
    public void printLabelTest(String ipAddress, int port, String commandLanguage, final Promise promise) {
        try {
            Socket socket = new Socket(ipAddress, port);
            if (socket.isConnected()) {
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeUTF("^XA^CW1,E:ANMDS.FNT^FS^CI28^LL252^LH220,20^FT"+0+","+15+"^A1,"+25+","+25+"^FDHmm makicobs!^FS\r\n^XZ");
                dataOutputStream.flush();
                socket.close();
                promise.resolve(true);
            } else {
                Log.d("NETWORK_PRINTER","Failed to connect!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            promise.reject("failed to connect printer: " + e.getMessage());
        }
    }

    @ReactMethod
    public void printLabel(String ipAddress, int port, String commandLanguage, final ReadableMap options, final Promise promise) {
        try {
            Socket socket = new Socket(ipAddress, port);
            if (socket.isConnected()) {
                switch(commandLanguage) {
                    case COMMAND_LANGUAGE_ZPL:
                        boolean result = printInZpl(socket, options);
                        if(result) {
                            promise.resolve(null);
                        } else
                            promise.reject("COMMAND_SEND_ERROR");
                        break;
                    default:
                        socket.close();
                        promise.reject("COMMAND_LANGUAGE_NOT_SUPPORT");
                        break;
                }
            } else {
                Log.d("NETWORK_PRINTER","Failed to connect!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            promise.reject("failed to connect printer: " + e.getMessage());
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public String getName() {
        return "NetworkManager";
    }

    private void emitRNEvent(String event, @Nullable WritableArray params) {
        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(event, params);
    }

    private String ipToString(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                ((ip >> 24) & 0xFF);
    }

    private ArrayList<Integer> getAvailablePorts(String address) {
        ArrayList<Integer> ports = new ArrayList<>();
        for (int port : PRINTER_ON_PORTS) {
            if (crunchifyAddressReachable(address, port)) {
                Log.d("SUCCESS_CONNECT",address+":"+port);
                ports.add(port);
            };
        }
        return ports;
    }

    private static boolean crunchifyAddressReachable(String address, int port) {
        try {

            try (Socket crunchifySocket = new Socket()) {
                // Connects this socket to the server with a specified timeout value.
                crunchifySocket.connect(new InetSocketAddress(address, port), 100);
            }
            // Return true if connection successful
            Log.d("SUCCESS_crunchifyAddressReachable","ADA YANG SUKSES");
            return true;
        } catch (IOException exception) {
            //Log.d("ERROR_crunchifyAddressReachable","KENA ERROR");
            //exception.printStackTrace();
            return false;
        }
    }

    private boolean printInZpl(Socket connectedSocket, final ReadableMap options) {
        ZplPrinter printer = new ZplPrinter(connectedSocket,options);
        boolean result = printer.printLabel();
        return result;
    }
}
