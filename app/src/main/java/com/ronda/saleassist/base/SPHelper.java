package com.ronda.saleassist.base;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SharedPreferences 的帮助类
 * <p>
 * Author: Ronda(1575558177@qq.com)
 * Date: 2016/11/25
 * Version: v1.0
 */

public class SPHelper {

    private static SharedPreferences        preferences = MyApplication.getInstance().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
    private static SharedPreferences.Editor editor      = preferences.edit();

    /********************** value --> String ***********************/

    /**
     * 用户名（手机号）
     */
    private static final String LOGIN_MOBILE   = "login_mobile";
    private static final String defLoginMobile = "";

    /**
     * token
     */
    private static final String LOGIN_TOKEN   = "login_token";
    private static final String defLoginToken = "";

    /**
     * 昵称
     */
    private static final String LOGIN_NICKNAME   = "login_nickname";
    private static final String defLoginNickname = "";

    /**
     * userId
     */
    private static final String LOGIN_USER_ID  = "login_user_id";
    private static final String defLoginUserId = "";

    /**
     * 所有店铺的List集合
     */
    private static final String LOGIN_SHOP_LIST  = "login_shop_list";
    private static final String defLoginShopList = null;

    /**
     * 蓝牙mac地址
     */
    private static final String MAIN_BLUETOOTH_ADDR  = "main_bluetooth_addr";
    private static final String defMainBluetoothAddr = "";



    /********************** value --> boolean ***********************/
    /**
     * 程序启动时是否自动检查更新
     */
    private static final String  SETTING_AUTO_CHECK  = "setting_auto_check";
    private static final boolean defSettingAutoCheck = true;


    /**
     * 用于购买时是否显示提示折扣对话框
     */
    private static final String  SETTING_DISCOUNT_DIALOG = "setting_discount_dialog";
    private static final boolean defDiscountDialog       = false;

    /**
     * 用于完成支付时，是否默认选中打印小票的checkbox
     */
    private static final String  SETTING_PRINT_BILL  = "setting_print_bill";
    private static final boolean defSettingPrintBill = true;

    /**
     * 当有新订单时，是否默认打印小票
     */
    private static final String  SETTING_PRINT_NEW_ORDER = "setting_print_new_order";
    private static final boolean defSettingPrintNewOrder = true;



    /*************************** 对外提供更简单的方法 **********************************/

    /**
     * 登录成功时，持久化保存的一些信息
     */
    public static void setLoginMobile(String mobile) {
        putString(LOGIN_MOBILE, mobile);
    }

    public static String getLoginMobile() {
        return getString(LOGIN_MOBILE, defLoginMobile);
    }

    public static void setLoginToken(String token) {
        editor.putString(LOGIN_TOKEN, token);
        editor.apply();
    }

    public static String getLoginToken() {
        return preferences.getString(LOGIN_TOKEN, defLoginToken);
    }

    public static void setLoginNickname(String nickname) {
        putString(LOGIN_NICKNAME, nickname);
    }

    public static String getLoginNickname() {
        return getString(LOGIN_NICKNAME, defLoginNickname);
    }


    public static void setLoginUserId(String userId){
        putString(LOGIN_USER_ID, userId);
    }

    public static String getLoginUserId(){
        return getString(LOGIN_USER_ID, defLoginToken);
    }


    public static <T> void setLoginShopList(List<T> datalist) {
        putList(LOGIN_SHOP_LIST, datalist);
    }

    public static <T> List<T> getLoginShopList(Class<T[]> clazz) {
        return getList(LOGIN_SHOP_LIST, defLoginShopList, clazz);
    }

    public static <T> void setLoginInfo(String mobile, String token, String nickname, String userId, List<T> datalist) {
        putString(LOGIN_MOBILE, mobile);
        putString(LOGIN_TOKEN, token);
        putString(LOGIN_NICKNAME, nickname);
        putString(LOGIN_USER_ID, userId);
        putList(LOGIN_SHOP_LIST, datalist);
    }


    /**
     * 主界面中，持久化保存的一些信息
     */
    public static void setMainBluetoothAddr (String bluetoothAddr){
        putString(MAIN_BLUETOOTH_ADDR, bluetoothAddr);
    }

    public static String getMainBluetoothAddr(){
        return getString(MAIN_BLUETOOTH_ADDR, defMainBluetoothAddr);
    }


    /**
     * 在设置中需持久化保存的一些信息
     */
    public static void setSettingAutoCheck(boolean autoCheck){
        putBoolean(SETTING_AUTO_CHECK, autoCheck);
    }

    public static boolean getSettingAutoCheck(){
        return getBoolean(SETTING_AUTO_CHECK, defSettingAutoCheck);
    }

    public static void setSettingDiscountDialog(boolean discountDialog){
        putBoolean(SETTING_DISCOUNT_DIALOG, discountDialog);
    }

    public static boolean getSettingDiscountDialog(){
        return getBoolean(SETTING_DISCOUNT_DIALOG, defDiscountDialog);
    }

    public static void setSettingPrintBill(boolean printBill){
        putBoolean(SETTING_PRINT_BILL, printBill);
    }

    public static boolean getSettingPrintBill(){
        return getBoolean(SETTING_PRINT_BILL, defSettingPrintBill);
    }

    public static void setSettingPrintNewOrder(boolean isPrint){
        putBoolean(SETTING_PRINT_NEW_ORDER, isPrint);
    }

    public static boolean getSettingPrintNewOrder(){
        return getBoolean(SETTING_PRINT_NEW_ORDER, defSettingPrintNewOrder);
    }


    /************************** 保存和读取基本类型的数据 *****************************/

    /**
     * 保存值为 String 类型的数据
     *
     * @param key
     * @param value
     */
    private static void putString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    private static String getString(String key, String defValue) {
        return preferences.getString(key, defValue);
    }

    /**
     * 保存值为 boolean 类型的数据
     *
     * @param key
     * @param value
     */
    private static void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    private static boolean getBoolean(String key, boolean defvalue) {
        return preferences.getBoolean(key, defvalue);
    }


    /**
     * 保存和读取List<JavaBean>
     * 本质上就是：借助Gson把List集合转成Json字符串存到SharedPreference中;在取的时候，先取出该Json字符串，然后再转成List<JavaBean>
     */
    private static <T> void putList(String tag, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        editor.putString(tag, strJson);
        editor.apply();
    }

    /**
     * @param tag          key
     * @param defaultValue 默认值
     * @param clazz        javaBean数组的字节码。eg:Student[].class
     * @return
     */
    private static <T> List<T> getList(String tag, String defaultValue, Class<T[]> clazz) {
        List<T> datalist = new ArrayList<T>();
        String jsonStr = preferences.getString(tag, defaultValue);
        if (null == jsonStr) {
            return datalist;
        }

        T[] arr = new Gson().fromJson(jsonStr, clazz);
        return Arrays.asList(arr);
    }

    /**
     * 清除SharedPreference
     */
    public static void clearSharedPreference(){
        editor.clear();
        editor.apply();
    }
}
