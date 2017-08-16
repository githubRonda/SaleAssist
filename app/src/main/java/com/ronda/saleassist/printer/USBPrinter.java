package com.ronda.saleassist.printer;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import java.util.HashMap;

/**
 * USB打印机
 */

public class USBPrinter {

    private static final String ACTION_USB_PERMISSION = "com.usb.printer.USB_PERMISSION";

    private static USBPrinter mInstance;

    private Context mContext;
    private UsbDevice mUsbDevice;
    private PendingIntent mPermissionIntent;
    private UsbManager mUsbManager;
    private UsbDeviceConnection mUsbDeviceConnection;

    private final BroadcastReceiver mUsbDeviceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbDevice != null) {
                            //call method to set up device communication

                            if (usbDevice.getVendorId() == 4070) {
                                mUsbDevice = usbDevice;
                            }


                            KLog.w("UsbDevice --> VendorId: " + usbDevice.getVendorId());
                        }
                    } else {
                        //Toast.makeText(context, "Permission denied for device " + usbDevice, Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "Permission denied for device ", Toast.LENGTH_SHORT).show();
                    }

                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                if (mUsbDevice != null) {
                    Toast.makeText(context, "Device closed", Toast.LENGTH_SHORT).show();
                    if (mUsbDeviceConnection != null) {
                        mUsbDeviceConnection.close();
                    }
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action)) {
                KLog.i("ACTION_USB_ACCESSORY_ATTACHED");
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                KLog.i("ACTION_USB_DEVICE_ATTACHED");
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                KLog.d("device: " + device.getVendorId());
                mUsbManager.requestPermission(device, mPermissionIntent);
            }
        }
    };

    private USBPrinter() {

    }

    public static USBPrinter getInstance() {
        if (mInstance == null) {
            mInstance = new USBPrinter();
        }
        return mInstance;
    }

    /**
     * 初始化打印机，需要与destroy对应
     *
     * @param context 上下文
     */
    public static void initPrinter(Context context) {
        getInstance().init(context);
    }

    /**
     * 销毁打印机持有的对象
     */
    public static void destroyPrinter() {
        getInstance().destroy();
    }

    private void init(Context context) {
        mContext = context;
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);

        mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);

        // 过滤两个Action的过滤器
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        mContext.registerReceiver(mUsbDeviceReceiver, filter);

        // 列出所有的USB设备，并且都请求获取USB权限
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        for (UsbDevice device : deviceList.values()) {
            mUsbManager.requestPermission(device, mPermissionIntent);

            int vendorId = device.getVendorId();
            int productId = device.getProductId();
            int deviceClass = device.getDeviceClass();
            int deviceSubclass = device.getDeviceSubclass();
            int deviceProtocol = device.getDeviceProtocol();
            String deviceName = device.getDeviceName();
            KLog.i("vendorId: " + vendorId + ", productId: " + productId + ", deviceClass: " + deviceClass + ", deviceSubclass: "
                    + deviceSubclass + ", deviceProtocol: " + deviceProtocol + ", deviceName: " + deviceName);

            System.out.println(device);

        }


    }

    private void destroy() {
        mContext.unregisterReceiver(mUsbDeviceReceiver);

        if (mUsbDeviceConnection != null) {
            mUsbDeviceConnection.close();
            mUsbDeviceConnection = null;
        }

        mContext = null;
        mUsbManager = null;
    }

    /**
     * 打印方法
     *
     * @param msg
     */
    public void print(byte[] msg) {
        if (mUsbDevice == null) {
            ToastUtils.showToast("未连接打印机");
            return;
        }
        final byte[] printData = msg;
        if (mUsbDevice != null) {

            UsbInterface usbInterface = mUsbDevice.getInterface(0);
            for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                final UsbEndpoint ep = usbInterface.getEndpoint(i);
                if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                        mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
                        if (mUsbDeviceConnection != null) {
//                            Toast.makeText(mContext, "Device connected", Toast.LENGTH_SHORT).show();
                            mUsbDeviceConnection.claimInterface(usbInterface, true);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        int b = mUsbDeviceConnection.bulkTransfer(ep, printData, printData.length, 100000);
                                        Log.i("Return Status", "b-->" + b);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();

                            //mUsbDeviceConnection.releaseInterface(usbInterface);
                            break;
                        }
                    }
                }
            }
        } else {
            Toast.makeText(mContext, "No available USB print device", Toast.LENGTH_SHORT).show();
        }
    }
}
