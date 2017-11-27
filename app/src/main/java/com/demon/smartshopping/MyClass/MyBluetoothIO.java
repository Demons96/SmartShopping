package com.demon.smartshopping.MyClass;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 蓝牙发送数据给硬件的规则
 */
public class MyBluetoothIO {
    //此uuid硬件通用
    static public final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    static private final String SEND_ClEAN = "0";  //去皮清零重量
    static private final String SEND_ADD = "1";  //添加商品
    static private final String SEND_ADDC = "2"; //添加成功
    static private final String SEND_ADDD = "3"; //添加失败
    static private final String SEND_FINISH = "4"; //购物完成

    static public OutputStream os_c;  //输出流
    static public InputStream is_c;   //输入流

    static public OutputStream os_s;  //输出流
    static public InputStream is_s;   //输入流

    static public void SendClean(){
        if (os_c != null) {
            try {
                os_c.write(SEND_ClEAN.getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    static public void SendAdd(){
        if (os_c != null) {
            try {
                os_c.write(SEND_ADD.getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    static public void SendAddC(){
        if (os_c != null) {
            try {
                os_c.write(SEND_ADDC.getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    static public void SendAddD(){
        if (os_c != null) {
            try {
                os_c.write(SEND_ADDD.getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    static public void SendFinish(){
        if (os_c != null) {
            try {
                os_c.write(SEND_FINISH.getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setOs_c(final OutputStream os_){
        os_c = os_;
    }
    public static void setIs_c(final InputStream is_){
        is_c = is_;
    }

//    public static void setOs_s(final OutputStream os_){
//        os_s = os_;
//    }
//    public static void setIs_s(final InputStream is_){
//        is_s = is_;
//    }
}
