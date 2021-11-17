package cn.jystudio.bluetooth.zpl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import cn.jystudio.bluetooth.BluetoothService;
import cn.jystudio.bluetooth.BluetoothServiceStateObserver;
import com.facebook.react.bridge.*;

import java.util.Map;
import java.util.Vector;

/**
 * Created by kape on 2021/11/17.
 */
public class RNBluetoothZplPrinterModule extends ReactContextBaseJavaModule
implements BluetoothServiceStateObserver{
    private static final String TAG="BluetoothZplPrinter";
    private BluetoothService mService;

    public RNBluetoothZplPrinterModule(ReactApplicationContext reactContext,BluetoothService bluetoothService) {
        super(reactContext);
        this.mService = bluetoothService;
        this.mService.addStateObserver(this);
    }

    @Override
    public String getName() {
        return "BluetoothZplPrinter";
    }

    @ReactMethod
    public void printLabel(final ReadableMap options, final Promise promise) {
        for (int i = 0;texts!=null&& i < texts.size(); i++) {
            ReadableMap text = texts.getMap(i);
            String t = text.getString("text");            

            try {
                byte[] temp = t.getBytes("UTF-8");
                String temStr = new String(temp, encoding);
                t = new String(temStr.getBytes("UTF-8"), "UTF-8");//打印的文字
            } catch (Exception e) {
                promise.reject("INVALID_TEXT", e);
                return;
            }
            
            zpl.addText(x, y, fonttype/*字体类型*/,
                rotation/*旋转角度*/, xscal/*横向放大*/, yscal/*纵向放大*/, t);
        }
        if(sendDataByte(tosend)){
            promise.resolve(null);
        }else{
            promise.reject("COMMAND_SEND_ERROR");
        }
        
    }

    private boolean sendDataByte(byte[] data) {
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            return false;
        }
        mService.write(data);
        return true;
    }

    @Override
    public void onBluetoothServiceStateChanged(int state, Map<String, Object> boundle) {

    }
}
