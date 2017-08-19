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

                        sb.append(new String(buf, 0, len));

                        //0xFA,0xFA,0x01,0xEA,0xEA
                        if ((startIndex = sb.indexOf("FAFA")) != -1 && (endIndex = sb.indexOf("EAEA", startIndex + 1)) != -1) {

                            String receivedCmd = sb.substring(startIndex, endIndex + 4);
                            sb.delete(startIndex, endIndex + 4);

                            if (receivedCmd.length() == 10) {
                                int data = Integer.parseInt(receivedCmd.substring(4, 6));

                                //玛德~~， 这个指令判断真蛋疼， 小白也是的，定义之初就不能定义好一点吗
                                switch (data) {
                                    case 1: //累清

                                        break;
                                    case 2: //取消

                                        break;
                                    case 3://时间

                                        break;
                                    case 4://补单

                                        break;
                                    case 5://折扣

                                        break;
                                    case 6://7

                                        break;
                                    case 7://8

                                        break;
                                    case 8://9

                                        break;
                                    case 9://取消

                                        break;
                                    case 10://4

                                        break;
                                    case 11://5

                                        break;
                                    case 12://6

                                        break;
                                    case 13://确定

                                        break;
                                    case 14://1

                                        break;
                                    case 15://2

                                        break;
                                    case 16://3

                                        break;
                                    case 17://代码

                                        break;
                                    case 18://0

                                        break;
                                    case 19://00

                                        break;
                                    case 20://.

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
