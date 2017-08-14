package com.ronda.saleassist.serialport;

import android.util.Log;

import com.ronda.saleassist.bean.WeightEvent;
import com.ronda.saleassist.utils.CommonUtil;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/02/27
 * Version: v1.0
 */

public class CmdSerialPort {


    private SerialPort   mSerialPort;
    private InputStream  mInputStream;
    private OutputStream mOutputStream;

    private ReadThread mReadThread;

    private boolean isActive;
    // private boolean isStop = false;


    public CmdSerialPort(String path) {


        try {
            // mSerialPort = new SerialPort(new File("/dev/ttyS1"), 9600, 0);
            mSerialPort = new SerialPort(new File(path), 9600, 0); // 创建 SerialPort

            // 获取输入输出流，这样就可以对串口进行数据的读写了
            mInputStream = mSerialPort.getInputStream();
            mOutputStream = mSerialPort.getOutputStream();

//            isStop = false;
            mReadThread = new ReadThread();
            mReadThread.start();

            isActive = true;
        } catch (IOException e) {
            e.printStackTrace();

            isActive = false;
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void write(byte[] data) {
        try {
            mOutputStream.write(data);

            Log.w("TAG", "write data: " + new String(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSerial() {
        isActive = false;

        if (mReadThread != null) {
            mReadThread.interrupt();
            mReadThread = null;
        }

        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }

        try {
            if (mInputStream != null) {
                mInputStream.close();
                mInputStream = null;
            }
            if (mOutputStream != null) {
                mOutputStream.close();
                mOutputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ReadThread extends Thread {

        private StringBuilder sb  = new StringBuilder();
        private byte[]        buf = new byte[1024];

        DecimalFormat format = new DecimalFormat("#0.000");

        private int startIndex, endIndex;

        @Override
        public void run() {

            while (isActive) {

                try {
                    int len = mInputStream.read(buf);

                    if (len != -1) {
                        byte[] buf_data = Arrays.copyOf(buf, len);
                        String temp = CommonUtil.bytesToHexString(buf_data);

                        Log.d("zhiling", "指令数据: " + temp);
//                        sb.append(new String(buf, 0, len));
//                        Log.i("TAG", sb.toString());
//                        if ((startIndex = sb.indexOf(" ")) != -1 && (endIndex = sb.indexOf(" ", startIndex + 1)) != -1) {
//                            String weight = sb.substring(startIndex + 1, endIndex);
//                            double weight_d = Double.parseDouble(weight);
//                            String str = format.format(weight_d);
//
//                            KLog.d("str: " + str);
//                            EventBus.getDefault().post(new WeightEvent(str));
//                            sb.setLength(0);
//                        }
//
//                        // TODO: 2017/8/8/0008  你可在这里在对sb做过长清空处理
//                        if (sb.length()>20){
//                            sb.setLength(0);
//                        }


//                        if ((startIndex = sb.indexOf("=")) != -1 && (endIndex = sb.indexOf("=", startIndex + 1)) != -1) {
//                            // 两个等号之间有13位
//                            if ((endIndex - startIndex) < 9) { // 说明是错误数据
//                                sb.setLength(0);
//                            } else {
//                                String data = sb.substring(startIndex + 1, endIndex); // 第一位是等号
//                                String weight = new StringBuilder(data.substring(0, 7)).reverse().toString();
//                                double weight_d = Double.parseDouble(weight);// 去掉两端的0字符
//
//                                //weightStr = ((int) (weight_d * 1000)) + ""; // 去掉小数点，转成int。用于兼容之前版本（用蓝牙获取的是整型）
//
//                                String str = format.format(weight_d); // 保留3位小数。用于实时显示重量数据
//
//                                //KLog.w("str = " + str);
//                                EventBus.getDefault().post(new WeightEvent(str));
////                            mHandler.obtainMessage(0, str).sendToTarget();
//                                //EventBus.getDefault().post(new WeightEvent()); // 不用EventBus或广播的原因就是我担心频繁的创建对象会消耗大量的资源
//                                sb.setLength(0);
//                            }
//                        }
                    }
                    Thread.sleep(200);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
