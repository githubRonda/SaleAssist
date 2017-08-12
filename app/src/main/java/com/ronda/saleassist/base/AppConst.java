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


}
