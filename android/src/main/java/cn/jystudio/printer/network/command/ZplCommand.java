package cn.jystudio.printer.network.command;

import android.graphics.Bitmap;
import android.util.Log;
import cn.jystudio.printer.bluetooth.escpos.command.sdk.PrintPicture;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

public class ZplCommand {
    private static final String DEBUG_TAG = "ZPLCommand";
    private Vector<Byte> Command = null;

    public ZplCommand() {
        this.Command = new Vector(4096, 1024);
    }

    public ZplCommand(int width, int height, int gap) {
        this.Command = new Vector(4096, 1024);
    }

    public void addStartCommand() {
        String str = new String();
        str = "^XA\r\n"+"^CW1,E:ANMDS.FNT^FS\r\n^CI28";
        addStrToCommand(str);
    }

    public void addLabelPositioning(String labelLength, String startX, String startY){
        String str = new String();
        str = "^LL"+labelLength+"\r\n"+"^LH"+startX+","+startY+"\r\n";
        addStrToCommand(str);
    }

    public void addEndCommand() {
        String str = new String();
        str = "^XZ\r\n";
        addStrToCommand(str);
    }

    public void addText(String fontSize, String x, String y, String text) {
        String str = new String();
        str = "^FT"+x+","+y+"^A1,"+fontSize+","+fontSize+"^FD"+text+"^FS\r\n";
        
        addStrToCommand(str);
    }

    public void addFieldBlock(String fieldBlock) {
        String str = new String();
        str = "^FB"+fieldBlock+"\r\n";
        addStrToCommand(str);
    }

    private void addStrToCommand(String str) {
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                bs = str.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < bs.length; i++) {
                this.Command.add(Byte.valueOf(bs[i]));
            }
        }
    }

    public Vector<Byte> getCommand() {
        return this.Command;
    }

}
