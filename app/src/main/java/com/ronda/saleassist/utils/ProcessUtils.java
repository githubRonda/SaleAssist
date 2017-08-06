package com.ronda.saleassist.utils;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/08/04
 * Version: v1.0
 * <p>
 * 进程相关的工具类
 */

public class ProcessUtils {
    /**
     * 获取当前进程的名称，即包名
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }
}
