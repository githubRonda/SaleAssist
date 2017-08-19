package com.ronda.saleassist.serialport;

import android.content.Intent;
import android.util.Log;

import com.ronda.saleassist.base.MyApplication;
import com.ronda.saleassist.bean.PayEvent;
import com.ronda.saleassist.bean.PriceEvent;
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


    private SerialPort mSerialPort;
    private InputStream mInputStream;
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

    private PriceEvent priceEvent = new PriceEvent();

    class ReadThread extends Thread {

        private StringBuilder sb = new StringBuilder();
        private byte[] buf = new byte[1024];

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

                        sb.append(temp);

                        //0xFA,0xFA,0x01,0xEA,0xEA
                        if ((startIndex = sb.indexOf("fafa")) != -1 && (endIndex = sb.indexOf("eaea", startIndex + 1)) != -1) {

                            String receivedCmd = sb.substring(startIndex, endIndex + 4);

                            KLog.d("cmd: " + receivedCmd);

                            sb.delete(startIndex, endIndex + 4);

                            if (receivedCmd.length() == 10) {
                                int data = Integer.parseInt(receivedCmd.substring(4, 6));

                                //玛德~~， 这个指令判断真蛋疼， 小白也是的，定义之初就不能定义好一点吗
                                switch (data) {
                                    case 1: //折扣

                                        break;
                                    case 2: //7
                                    case 3://8
                                    case 4://9
                                        priceEvent.setPrice((data + 5) + "");
                                        EventBus.getDefault().post(priceEvent);
                                        break;
                                    case 5://钱箱
                                        Intent intent = new Intent("com.android.yf_pull_money_locker");
                                        MyApplication.getInstance().sendBroadcast(intent);
                                        break;
                                    case 6://数量

                                        break;
                                    case 7://4
                                    case 8://5
                                    case 9://6
                                        priceEvent.setPrice((data - 3) + "");
                                        EventBus.getDefault().post(priceEvent);
                                        break;
                                    case 10://结算
                                        EventBus.getDefault().post(new PayEvent("cash", "现金支付 "));
                                        break;
                                    case 11://锁定

                                        break;
                                    case 12://1
                                    case 13://2
                                    case 14://3
                                        priceEvent.setPrice((data - 11) + "");
                                        EventBus.getDefault().post(priceEvent);
                                        break;
                                    case 15://找钱

                                        break;
                                    case 16://代码

                                        break;
                                    case 17://0
                                        priceEvent.setPrice("0");
                                        EventBus.getDefault().post(priceEvent);
                                        break;
                                    case 18://00
                                        priceEvent.setPrice("00");
                                        EventBus.getDefault().post(priceEvent);
                                        break;
                                    case 19://.
                                        priceEvent.setPrice(".");
                                        EventBus.getDefault().post(priceEvent);
                                        break;
                                    case 20://打印

                                        break;

                                }
                            }
                            //sb.setLength(0);
                        }
                    }
                    //Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
