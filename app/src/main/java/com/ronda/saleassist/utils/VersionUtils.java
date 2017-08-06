package com.ronda.saleassist.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/08/04
 * Version: v1.0
 */

public class VersionUtils {
    /**
     * 获取VersionName
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "未知版本";
    }

    /**
     * 获取VersionCode
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
