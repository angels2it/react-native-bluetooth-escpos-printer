package cn.jystudio.printer.network.command;

import android.graphics.Bitmap;
import android.util.Log;
import cn.jystudio.printer.bluetooth.escpos.command.sdk.PrintPicture;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

public class BixolonCommand {
    private static final String DEBUG_TAG = "BixolonCommand";
    private Vector<Byte> Command = null;

    public BixolonCommand() {
        this.Command = new Vector(4096, 1024);
    }

    public BixolonCommand(int width, int height, int gap) {
        this.Command = new Vector(4096, 1024);
    }

    // public void addStartCommand(String size,String gap, String direction, String fontEncoding) {
    //     addSize(size,fontEncoding);
    //     addGap(gap,fontEncoding);
    //     addDirection(direction,fontEncoding);
    //     addCls(fontEncoding);
    // }

    public void addPrint(String fontEncoding) {
        String str = new String();
        str = "P1,1\r\n";
        addStrToCommand(str,fontEncoding);
    }

    public void addEndCommand(String fontEncoding) {
        addPrint(fontEncoding);
    }

    public void addText(String x, String y, String type, String multiplier, String text, String fontEncoding) {
        String str = new String();
        str = "T"+x+","+y+",\""+type+"\","+multiplier+",0,0,N,N,"+"\'"+text+"\'\r\n";
        addStrToCommand(str,fontEncoding);
    }

    // public void addFieldBlock(String fieldBlock,String x, String y, String type, String multiplier, String text, String fontEncoding) {
    //     String str = new String();
    //     str = "BLOCK "+x+","+y+","+fieldBlock+",\""+type+"\",0,"+multiplier+",\'"+text+"\'\r\n";
    //     addStrToCommand(str,fontEncoding);
    // }

    private void addStrToCommand(String str, String fontEncoding) {
        Log.d("ADD_STR_TO_COMMAND",str);
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                bs = str.getBytes(fontEncoding);
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
