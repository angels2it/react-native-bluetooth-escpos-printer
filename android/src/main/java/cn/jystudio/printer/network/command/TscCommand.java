package cn.jystudio.printer.network.command;

import android.graphics.Bitmap;
import android.util.Log;
import cn.jystudio.printer.bluetooth.escpos.command.sdk.PrintPicture;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

public class TscCommand {
    private static final String DEBUG_TAG = "TSCCommand";
    private Vector<Byte> Command = null;

    public TscCommand() {
        this.Command = new Vector(4096, 1024);
    }

    public TscCommand(int width, int height, int gap) {
        this.Command = new Vector(4096, 1024);
    }

    public void addStartCommand(String size,String gap, String direction) {
        addSize(size);
        addGap(gap);
        addDirection(direction);
        addCls();
    }
    public void addSize(String size) {
        String str = new String();
        str = size+"\r\n";
        addStrToCommand(str);
    }
    public void addGap(String gap) {
        String str = new String();
        str = gap+"\r\n";
        addStrToCommand(str);
    }
    public void addDirection(String direction) {
        String str = new String();
        str = direction+"\r\n";
        addStrToCommand(str);
    }
    public void addCls() {
        String str = new String();
        str = "CLS\r\n";
        addStrToCommand(str);
    }

    public void addPrint() {
        String str = new String();
        str = "PRINT 1,1\r\n";
        addStrToCommand(str);
    }

    public void addEndCommand() {
        addPrint();
    }

    public void addText(String x, String y, String type, String text) {
        String str = new String();
        str = "TEXT "+x+","+y+",\""+type+"\",0,1,1,0,"+"\""+text+"\"\r\n";
        addStrToCommand(str);
    }

    public void addFieldBlock(String fieldBlock,String x, String y, String type, String text) {
        String str = new String();
        str = "BLOCK "+x+","+y+","+fieldBlock+",\""+type+"\",0,1,1,\""+text+"\"\r\n";
        addStrToCommand(str);
    }

    private void addStrToCommand(String str) {
        Log.d("ADD_STR_TO_COMMAND",str);
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                bs = str.getBytes("GB2312");
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
