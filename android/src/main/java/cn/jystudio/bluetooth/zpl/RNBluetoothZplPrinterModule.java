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
        ZplCommand zpl = new ZplCommand();
        ReadableArray texts = options.hasKey("text")? options.getArray("text"):null;
        int totalOrderQty = options.hasKey("totalOrderQuantity")? options.getInt("totalOrderQuantity"):0;
        int labelLength = 230;
        int additionalLabelLengthPerItem = 5;
        labelLength = labelLength + additionalLabelLengthPerItem * totalOrderQty;
        Integer dataLabelLength = new Integer(labelLength);
        zpl.addStartCommand(dataLabelLength.toString());
        for (int i = 0;texts!=null&& i < texts.size(); i++) {
            ReadableMap text = texts.getMap(i);
            String t = text.getString("text");
            String fontType = text.getString("fontType");
            String fontSize = text.getString("fontSize");
            String x = text.getString("x");
            String y = text.getString("y");
            String fieldBlock = text.hasKey("fieldBlock") ? text.getString("fieldBlock") : "";
            if (fieldBlock!="") {
                zpl.addFieldBlock(fieldBlock);
            }
            try {
                byte[] temp = t.getBytes("UTF-8");
                String temStr = new String(temp, "UTF-8");
                t = new String(temStr.getBytes("UTF-8"), "UTF-8");//打印的文字
                Log.d("ZPLPrinter",t);
            } catch (Exception e) {
                promise.reject("INVALID_TEXT", e);
                return;
            }
            
            zpl.addText(fontType,fontSize,x,y,t);
        }
        zpl.addEndCommand();
        Vector<Byte> bytes = zpl.getCommand();
        byte[] tosend = new byte[bytes.size()];
        for(int i=0;i<bytes.size();i++){
            tosend[i]= bytes.get(i);
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
        mService.writeV2(data);
        return true;
    }

    @Override
    public void onBluetoothServiceStateChanged(int state, Map<String, Object> boundle) {

    }
}
