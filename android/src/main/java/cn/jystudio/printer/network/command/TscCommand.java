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

    public void addSetCounter(String fontEncoding, String remainder){
        String str = new String();
        str = "SET COUNTER @0 +1\r\n@0=\""+remainder+"\"\r\n";
        addStrToCommand(str,fontEncoding);
    }
    public void addStartCommand(String size,String gap, String direction, String fontEncoding, String remainder) {
        addSetCounter(fontEncoding, remainder);
        addSize(size,fontEncoding);
        addGap(gap,fontEncoding);
        addDirection(direction,fontEncoding);
        addCls(fontEncoding);
    }
    public void addSize(String size, String fontEncoding) {
        String str = new String();
        str = size+"\r\n";
        addStrToCommand(str,fontEncoding);
    }
    public void addGap(String gap, String fontEncoding) {
        String str = new String();
        str = gap+"\r\n";
        addStrToCommand(str,fontEncoding);
    }
    public void addDirection(String direction, String fontEncoding) {
        String str = new String();
        str = direction+"\r\n";
        addStrToCommand(str,fontEncoding);
    }
    public void addCls(String fontEncoding) {
        String str = new String();
        str = "CLS\r\n";
        addStrToCommand(str,fontEncoding);
    }

    public void addPrint(String fontEncoding, String totalRep) {
        String str = new String();
        str = "PRINT "+totalRep+"\r\n";
        addStrToCommand(str,fontEncoding);
    }

    public void addDelay(String fontEncoding) {
        String str = new String();
        str = "DELAY 5000\r\n";
        addStrToCommand(str,fontEncoding);
    }

    public void addEndCommand(String fontEncoding, String totalRep) {
        addPrint(fontEncoding,totalRep);
        //addDelay(fontEncoding);
    }

    public void addText(String x, String y, String type, String multiplier, String text, String fontEncoding, String useCounter) {
        String str = new String();
        if(useCounter.equals("N")){
            str = "TEXT "+x+","+y+",\""+type+"\",0,"+multiplier+",0,"+"\""+text+"\"\r\n";
        } else {
            str = "TEXT "+x+","+y+",\""+type+"\",0,"+multiplier+",0,"+"@0+\""+text+"\"\r\n";
        }
        addStrToCommand(str,fontEncoding);
    }

    public void addFieldBlock(String fieldBlock,String x, String y, String type, String multiplier, String text, String fontEncoding) {
        String str = new String();
        str = "BLOCK "+x+","+y+","+fieldBlock+",\""+type+"\",0,"+multiplier+",\""+text+"\"\r\n";
        addStrToCommand(str,fontEncoding);
    }

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
