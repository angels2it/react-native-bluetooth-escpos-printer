package cn.jystudio.printer.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import cn.jystudio.printer.network.command.ZplCommand;
import com.facebook.react.bridge.*;

import java.util.Map;
import java.util.Vector;
import java.net.Socket;
import java.io.OutputStream;
import java.io.IOException;

/**
 * Created by kape on 2021/11/17.
 */
public class ZplPrinter {
    private Socket mmSocket;
    private final ReadableMap options;
    public ZplPrinter(Socket connectedSocket, final ReadableMap options) {
        this.mmSocket = connectedSocket;
        this.options = options;
    }

    public boolean printLabel() {
        ZplCommand zpl = new ZplCommand();
        ReadableArray texts = options.hasKey("text")? options.getArray("text"):null;
        zpl.addStartCommand();
        zpl.addLabelPositioning("252","210","20");
        for (int i = 0;texts!=null&& i < texts.size(); i++) {
            ReadableMap text = texts.getMap(i);
            String t = text.getString("text");
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
                //promise.reject("INVALID_TEXT", e);
                //mmSocket.close();
                return false;
            }
            
            zpl.addText(fontSize,x,y,t);
        }
        zpl.addEndCommand();
        Vector<Byte> bytes = zpl.getCommand();
        byte[] tosend = new byte[bytes.size()];
        for(int i=0;i<bytes.size();i++){
            tosend[i]= bytes.get(i);
        }
        try{
            if(sendDataByte(tosend)){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        
        
    }

    private boolean sendDataByte(byte[] data) {
        try {
            if(mmSocket.isConnected()) {
                OutputStream outputStream = mmSocket.getOutputStream();
                outputStream.write(data);
                outputStream.flush();
                mmSocket.close();
                return true;
            } else {
                return false;
            }
        } catch(IOException e) {
            return false;
        }
        
        
    }
}
