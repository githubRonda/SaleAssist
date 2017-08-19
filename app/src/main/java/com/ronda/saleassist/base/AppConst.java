package com.ronda.saleassist.base;

import com.ronda.saleassist.local.preference.SPUtils;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/07/25
 * Version: v1.0
 * <p>
 * App 中定义的一些常量
 */

public interface AppConst {

    //todo======================SharedPreferences相关==================================

    /**
     * 登录时的token
     */
    String TOKEN = "token";


    /**
     * 登录时手机号
     */
    String MOBILE = "mobile";

    /**
     * 昵称
     */
    String NICK_NAME = "nick_name";

    /**
     * 用户id
     */
    String USER_ID = "user_id";

    /**
     * 用户对应的店铺列表
     */
    String LOGIN_SHOP_LIST = "login_shop_list";

    /**
     * 当前店铺的id
     */
    String CUR_SHOP_ID = "cur_shop_id";

    /**
     * 当前店铺名称
     */
    String CUR_SHOP_NAME = "cur_shop_name";

    /**
     * 是否开通支付宝
     */
    String SUPPORT_ALIPAY = "support_alipay";

    /**
     * 是否开通微信支付
     */
    String SUPPORT_WECHATPAY = "support_wechatpay";


    /**
     * 结算时的进制方式
     */
    String CALCULATE_TYPE = "calculatetype";
    int TYPE_PER_1 = 1; // 每次称量时逢1进
    int TYPE_PER_5 = 2; // 每次称量时逢5进
    int TYPE_TOTAL_1 = 3; // 总额逢1进
    int TYPE_TOTAL_5 = 4;


    /**
     * 商品排列样式
     */

    String GOODS_STYLE = "goods_style";

    /**
     * 重量串口
     */
    String WEIGHT_SERIAL_PORT = "weight_serial_port";
    /**
     * 交互指令串口
     */
    String CMD_SERIAL_PORT = "cmd_serial_port";

    /**
     * 设置中是否显示主界面中的折扣对话框
     */
    String SHOW_DISCOUNT_DIALOG = "show_discount_dialog";

    /**
     * 结算时是否打印小票
     */
    String PRINT_BILL = "print_bill";

    /**
     * 新订单时是否打印小票
     */
    String PRINT_NEW_ORDER = "print_new_order";

    /**
     * 是否自动更新
     */
    String AUTO_UPGRADE = "auto_upgrade";
}
