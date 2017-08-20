package com.ronda.saleassist.api;

import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.ronda.saleassist.api.volley.GsonRequest;
import com.ronda.saleassist.api.volley.VolleyUtil;
import com.ronda.saleassist.base.MyApplication;
import com.ronda.saleassist.base.SPHelper;
import com.ronda.saleassist.base.dialog.ListDialogFragment;
import com.ronda.saleassist.bean.BaseBean;
import com.ronda.saleassist.bean.LoginBean;
import com.ronda.saleassist.utils.NetUtils;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.type;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/03/11
 * Version: v1.0
 */

public class UserApi {

    private static final String OFFICIAL_SERVER = "http://ceshi.edianlai.com";
    public static String BASE_SERVER = OFFICIAL_SERVER;


    private static final String TAG = UserApi.class.getSimpleName();


    /**
     * 1.获取短信验证码
     * 为了方便粘贴复制，就直接使用StringRequest了
     *
     * @param tag
     * @param mobile
     * @param type          1:注册， 2：登录
     * @param listener
     * @param errorListener
     */
    public static void getVerifyCode(final String tag, final String mobile, final int type, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/code_get", listener, errorListener) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", mobile);
                params.put("type", type + "");

                MapRemoveNullUtil.removeNullAndEmptyValue(params);

                KLog.w(tag + "--> " + new Gson().toJson(params));
                return params;
            }
        };

        VolleyUtil.getInstance().addToRequestQueue(request, tag);
    }


    /**
     * 2.用户登录（密码验证码登录和token登录）
     * 使用了GsonRequest
     *
     * @param tag           请求标识. 便于撤销的时候使用
     * @param mobile        手机号
     * @param password      密码
     * @param code          短信验证码
     * @param token         登录token
     * @param listener
     * @param errorListener
     */
    public static void login(final String tag, final String mobile, final String password, final String code, final String token, Response.Listener<LoginBean> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        GsonRequest<LoginBean> gsonRequest = new GsonRequest<LoginBean>(Request.Method.POST, OFFICIAL_SERVER + "/market/api/login", LoginBean.class, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", mobile); //RequestParams的add和put最主要的区别：add()方法可以给同一个键添加多个值，而put()方法键名若相同的话，后面的会覆盖前面的
                params.put("password", password);
                params.put("code", code);
                params.put("token", token);

                MapRemoveNullUtil.removeNullAndEmptyValue(params);

                KLog.w(tag + "--> " + new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(gsonRequest, tag);

    }


    /**
     * 3.注册
     *
     * @param tag
     * @param mobile
     * @param password
     * @param nickname
     * @param code
     * @param listener
     * @param errorListener
     */
    public static void regist(final String tag, final String mobile, final String password, final String nickname, final String code, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/reg", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", mobile);
                params.put("password", password);
                params.put("nickname", nickname);
                params.put("code", code);

                KLog.w(tag + "--> " + new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

    /**
     * 4. 用户登出
     */
    public static void logout(String tag, final String token, final String mobile, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/logout", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("mobile", mobile);
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

    /**
     * 5. 图片上传
     *
     * @param tag
     * @param photo         图片文件（base64编码）
     * @param type          图片类型（暂定： 1头像 2身份证照片 3营业执照 4商品图片 5企业logo）
     * @param shopid
     * @param listener
     * @param errorListener
     */
    public static void uploadPhoto(String tag, final String token, final String shopid, final String photo, final int type, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/photo_upload", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("photo", photo);
                params.put("type", type + "");
                params.put("shopid", shopid);// 上传企业Logo时必须要加上shopid参数
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

    /**
     * 6. 商户申请
     */
    public static void shopApply(String tag, final String token, final String mobile, final String shopname, final String province, final String city, final String address, final String identification, final String lincense, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_apply", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("mobile", mobile);
                params.put("shopname", shopname);
                params.put("province", province);
                params.put("city", city);
                params.put("address", address);
                params.put("identification", identification);
                params.put("lincense", lincense);
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

    /**
     * 7. 商户店铺信息获取
     */

    public static void getShopInfo(String tag, final String token, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_info", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

    /**
     * 8. 货物入库 添加商品 (配合图片上传一起使用)
     *
     * @param shopid   店铺id
     * @param name     货物名称
     * @param category 货物种类id（可空）
     * @param photo    货物图片id（图片上传成功返回的id）
     * @param intro    货物简介
     * @param price    货物价格
     */
    public static void goodsUpload(String tag, final String token, final String shopid, final String name, final String category, final String photo, final String intro, final String price,
                                   final String cost, final String discount2, final String discount3, final String method, final String unit,
                                   Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_goods_upload", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("name", name);
                params.put("category", category);
                params.put("photo", photo);
                params.put("intro", intro);
                params.put("price", price);

                params.put("cost", cost); //成本价，可空
//                params.put("discount1", discount1);//折扣,均可空
                params.put("discount2", discount2);
                params.put("discount3", discount3);
                params.put("method", method);
                params.put("unit", unit);
                params.put("stock", "0");//库存, 新建一个菜是，库存为0

                MapRemoveNullUtil.removeNullAndEmptyValue(params);

                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

    /**
     * 9. 店铺货物信息修改 之 修改顺序
     * /market/api/shop_goods_edit
     */
    //修改货物顺序
    public static void updateGoodsOrder(String tag, final String token, final String shopid, final String data, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_goods_edit", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("type", "6");
                params.put("data", data);

                KLog.e(new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

    //修改菜品
    public static void updateGood(String tag, String token, String shopid, String categoryId, String goodId, String name, String price, String photoId,
                                  String discount2, String discount3, String intro, String method, String unit, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        goodsEdit(tag, token, 1, shopid, categoryId, goodId, name, price, photoId, discount2, discount3, intro, method, unit, listener, errorListener);
    }


    //删除菜品
    public static void deleteGood(String tag, String token, String shopid, String categoryId, String goodId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        goodsEdit(tag, token, 2, shopid, categoryId, goodId, "", "", "", "", "", "", "", "", listener, errorListener);
    }

    /**
     * 店铺货物信息修改
     * <p/>
     * //@param token 令牌
     *
     * @param type     类型 1为修改 2为删除 3为入库 6为修改顺序
     * @param shopid   店铺id
     * @param category 货物种类id
     * @param goods    货物id（店铺货物信息货物（10接口）后返回的id）
     * @param name     货物名称
     * @param price    货物价格
     * @param photo    货物图片id（先进行图片上传（4接口），上传成功返回的id）
     * @param intro    货物简介
     */

    /*Volley版*/
    private static void goodsEdit(final String tag, final String token, final int type, final String shopid, final String category, final String goods, final String name, final String price,
                                  final String photo, final String discount2, final String discount3, final String intro,
                                  final String method, final String unit, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_goods_edit", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("type", type + "");
                params.put("shopid", shopid);
                params.put("goods", goods);
                params.put("name", name);
                params.put("category", category);
                params.put("photo", photo);
                params.put("intro", intro);
                params.put("price", price);
                params.put("discount2", discount2);
                params.put("discount3", discount3);

                params.put("method", method);
                params.put("unit", unit);

                MapRemoveNullUtil.removeNullAndEmptyValue(params);

                KLog.w(tag + "--> " + new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

    //这个是货物入库的接口，而且和上面的updateGood()访问的是同一个地址，只不过参数不同而已
    public static void updateGood_ruku(String tag, final String token, final String shopid,
                                       final String goodId, final String stock, final String cost, final String price, final String orderprice, final String discount2, final String discount3,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_goods_edit", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("type", 3 + ""); //3为入库
                params.put("goods", goodId);

                params.put("stock", stock);
                params.put("cost", cost); //成本价
                params.put("price", price);
                params.put("orderprice", orderprice); //总价值（可空，空值时接口自动计算）
                params.put("discount2", discount2);
                params.put("discount3", discount3);
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }


    /**
     * 10. 店铺货物信息获取
     * 排序类型 1为id升序 2为id降序 3为价格升序 4为价格降序
     * /market/api/shop_goods_info
     */

    public static void getGoodsInfo(final String tag, final String token, final int type, final String shopid, final String category, final int count, final int page, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_goods_info", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("type", type + "");
                params.put("shopid", shopid);
                params.put("category", category);
                params.put("all", "1");
                params.put("count", count + "");
                params.put("page", page + "");

                KLog.w(tag + "--> " + new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

    /**
     * 11. 店铺货物种类信息（增删改）
     */
    public static void addCategory(String tag, String token, String shopid, String name, String notes, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        operateCategory(tag, token, 1, shopid, name, notes, "", listener, errorListener);
    }

    public static void updateCategory(String tag, String token, String shopid, String name, String notes, String id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        operateCategory(tag, token, 2, shopid, name, notes, id, listener, errorListener);
    }

    public static void deleteCategory(String tag, String token, String shopid, String id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        operateCategory(tag, token, 3, shopid, "", "", id, listener, errorListener);
    }

    /**
     * 店铺货物种类信息接口（增删改）
     * <p>
     * //@param token   令牌
     *
     * @param type   排序类型 1为新增 2为修改某条货物种类 3为删除某条货物种类
     * @param shopid 店铺id
     * @param name   种类名称
     * @param notes  种类说明
     * @param id     种类id（修改和删除时需上传）
     */
    private static void operateCategory(String tag, final String token, final int type, final String shopid, final String name, final String notes, final String id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_goods_category_upload", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("type", type + "");
                params.put("shopid", shopid);//100001
                params.put("name", name);
                params.put("notes", notes);
                params.put("id", id);

                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

    /**
     * 12. 货物种类信息接口
     */
    public static void getCategoryInfo(String tag, final String token, final String shopid, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_category_info", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

    /**
     * 13. 订单上传
     */
    /**
     * 订单上传
     * <p>
     * //@param token  令牌
     *
     * @param shopid 商店id
     * @param data   订单数据（json数组格式字符串）
     *               number  该货物数量（重量）
     *               goodId  货物id
     */
    public static void uploadOrder(String tag, String token, String shopid, String data, String total, String paycode, String method, String customer,
                                   Response.Listener<String> listener, Response.ErrorListener errorListener) {
        uploadOrder(tag, token, shopid, data, total, paycode, method, customer, "", "", "", "", listener, errorListener);
    }

    public static void uploadOrder(final String tag, final String token, final String shopid, final String data, final String total, final String paycode,
                                   final String method, final String customer,
                                   final String costprice, final String extmethod, final String extcode, final String extpay,
                                   Response.Listener<String> listener, Response.ErrorListener errorListener) {
        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        VolleyUtil.getInstance().cancelPendingRequests(tag);//防止出现多次重复请求

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/order_info_upload", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);//100001
                params.put("total", total);
                params.put("paycode", paycode);
                params.put("method", method);
                params.put("customer", customer);//买家id
                params.put("data", data); //json数组格式的字符串

                params.put("costprice", costprice); //会员优惠后的总额
                params.put("extmethod", extmethod);
                params.put("extcode", extcode);
                params.put("extpay", extpay);

                /*
                costprice	:	优惠后的总价
                extmethod:	额外支付方式（目前参数：’alipay’,’cash’） 会员账户余额不足时
                extcode	:	会员支付验证码（扫码时获得）
                extpay	:	额外支付金额  会员账户余额不足时

                */

                KLog.w(tag + "--> " + new Gson().toJson(params));
                return params;
            }
        };
        // 防止框架内部多次请求
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

    /**
     * 14. 订单列表
     * /market/api/order_info_list
     */

    /**
     * /market/api/order_member_info_list
     */

    /**
     * 订单详细信息
     * /market/api/order_info_detail
     */

    /**
     * /market/api/order_member_info_detail
     */

    /**
     * 店铺收入信息概览
     * /market/api/order_survey
     */

    /**
     * /market/api/order_member_survey
     */

    /**
     * 店铺收入信息分布数据接口
     * /market/api/order_info_goods
     */

    /**
     * /market/api/order_member_info_goods
     */


    /**
     * 上传定位信息
     *
     * @param shopid       商店id
     * @param province     省份
     * @param city         城市
     * @param districtcode 区县编码
     * @param address      详细地址
     * @param longitude    经度
     * @param latitude     纬度
     */

    public static void uploadLocation(String tag, final String token, final String shopid, final String province, final String city, final String districtcode, final String address, final double longitude, final double latitude,
                                      Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/alipay_location_info_upload", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("province", province);
                params.put("city", city);
                params.put("districtcode", districtcode);
                params.put("address", address);
                params.put("longitude", longitude + "");
                params.put("latitude", latitude + "");
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }


    /**
     * 条码货物扫码信息获取接口：
     */
    public static void bargoodsInfoAndCreate(String tag, final String token, final String shopid, final String barcode, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_bargoods_infoandcreate", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("barcode", barcode);
                return params;
            }

        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }


    //店铺会员支付扫码接口 根据会员码获取会员信息
    public static void getMemberInfoByCode(String tag, final String token, final String shopid, final String qrcode, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_user_barcode", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("qrcode", qrcode);
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }


    /**
     * 修改密码
     * （当不传入新密码时，系统将以手机短信的形式通知用户新的随机密码）
     *
     * @param password
     * @param newpassword
     */

    public static void resetPassword(String tag, final String token, final String shopid, final String password, final String newpassword,
                                     Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/password_reset", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("password", password);
                params.put("newpassword", newpassword);
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

    /**
     * 用户反馈
     *
     * @param user
     * @param method
     * @param content
     */

    public static void uploadSuggestion(String tag, final String user, final String method, final String content,
                                        Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/suggestion", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user", user);
                params.put("method", method);
                params.put("content", content);
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }


    //买家申请VIP处理接口
    public static void applyProcess(String tag, final String token, final String shopid, final String data,
                                    Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_user_apply_notify", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("data", data);//json字符串，(id(申请时的), status(2,3,4), level(会员等级))
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }


    //买家申请店铺VIP轮询接口
    public static void applyMember(String tag, final String token, final String shopid,
                                   Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_user_apply_check", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

    //本店会员等级的设置
    public static void queryVipLevel(String tag, final String token, final String shopid,
                                     Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_vipuser_level", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("type", "1"); // 查询
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }



    public static void addVipLevel(String tag, final String token, final String shopid,final int orderno, final String name, final String intro, final String cost,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_vipuser_level", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("type", "2"); // 新增
                params.put("orderno", orderno + "");
                params.put("name", name);
                params.put("intro", intro);
                params.put("cost", cost);

                KLog.w(new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

    public static void updateVipLevel(String tag, final String token, final String shopid,final String id, final String name, final String intro, final String cost,
                                   Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_vipuser_level", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("type", "3"); // 修改
                params.put("id", id);
                params.put("name", name);
                params.put("intro", intro);
                params.put("cost", cost);

                KLog.w(new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

    //本店会员优惠项添加
    public static void addAndModifyPreference(String tag, final String token, final String shopid,final String type, final String name, final String costtype, final String min, final String commission, final String score, final String cost, final String id, final String gift,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_vipuser_cost_list", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("type", type); //2:新增优惠项; 3:修改优惠项
                params.put("name", name);
                params.put("id", id);
                params.put("costtype", costtype);
                params.put("min", min);
                params.put("commission", commission);
                params.put("score", score);
                params.put("cost", cost);
                params.put("gift", gift);

                KLog.w(new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }



    //获取现有优惠项
    public static void getPreference(String tag, final String token, final String shopid,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_vipuser_cost_list", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("type", "1"); //查询现有优惠项

                KLog.w(new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }


    //删除优惠项
    public static void deletePreference(String tag, final String token, final String shopid,final String id,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_vipuser_cost_list", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("type", "4"); //删除
                params.put("id", id);

                KLog.w(new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }


    //获取所有会员信息
    public static void getAllMember(String tag, final String token, final String shopid,final int from, final int size,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_vipuser_manage", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("type", "1");
                params.put("from", from + "");
                params.put("size", size + "");

                KLog.w(new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }


    public static void modifyMemberLevel(String tag, final String token, final String shopid,final String id, final String level, final String bill,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_vipuser_manage", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("type", "2");
                params.put("id", id);
                params.put("level", level);
                params.put("bill", bill);

                KLog.w(new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }


    // 店铺会员充值接口
    public static void rechargeMember(String tag, final String token, final String shopid, final String qrcode, final String userid, final String money, final String extcode,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_user_recharge", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("type", "3"); // 1:查询会员信息（扫码）2: 会员扫码充值 3:直接充值
                params.put("qrcode", qrcode);
                params.put("userid", userid);
                params.put("money", money);
                params.put("extcode", extcode);

                KLog.w(new Gson().toJson(params));
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }


    //新增配送费
    public static void getExtraCost(String tag, final String token, final String shopid,
                                    Response.Listener<String> listener, Response.ErrorListener errorListener) {
        doExtraCost(tag, token, shopid,"1", "", "", "", listener, errorListener);
    }

    public static void addExtraCost(String tag, final String token, final String shopid,String min, String cost,
                                    Response.Listener<String> listener, Response.ErrorListener errorListener) {
        doExtraCost(tag, token, shopid,"2", min, cost, "", listener, errorListener);
    }

    public static void updateExtraCost(String tag, final String token, final String shopid,String id, String min, String cost,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {
        doExtraCost(tag, token, shopid,"3", min, cost, id, listener, errorListener);
    }

    public static void deleteExtraCost(String tag, final String token, final String shopid,String id,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {
        doExtraCost(tag, token, shopid, "4", "", "", id, listener, errorListener);
    }

    // 配送费增删改查
    public static void doExtraCost(String tag, final String token, final String shopid, final String type, final String min, final String cost, final String id,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_distribution", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("type", type);
                params.put("min", min);
                params.put("cost", cost);
                params.put("id", id);
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

    //店铺消息推送•优惠、广告信息接口
    public static void pushAd(String tag, final String token, final String shopid,final String title, final String content, final String begintime, final String endtime,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_ad", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("type", "1");
                params.put("title", title);
                params.put("content", content);
                params.put("begintime", begintime);
                params.put("endtime", endtime);
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }


    //获取已推送的广告

    public static void getPushAd(String tag, final String token, final String shopid,final int from, final int size, final String ing,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_ad", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("type", "2");
                params.put("from", from + ""); //分页启示，默认为0
                params.put("size", size + "");  //分页每页数据量，默认为10
                params.put("ing", ing);//是否筛选正在进行中的优惠活动，是传1，不是则不用传输出参数：

                KLog.w(new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }





    /**
     * 获取订单列表
     * <p>
     * //@param token 令牌
     *
     * @param shopid  商店id
     * @param count   每页数据量（默认20）
     * @param page    页码（默认1）
     */
    public static void getOrderList(String tag, final String token, final String shopid, final int count, final int page,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/order_info_list", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("count", count + "");
                params.put("page", page + "");

                KLog.w(new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }




    // 用于员工的报表，operator表示员工的uid
    public static void getMemberOrderList(String tag, final String token, final String shopid,final String operator, final int count, final int page,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/order_member_info_list", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("operator", operator);
                params.put("count", count + "");
                params.put("page", page + "");

                KLog.w(new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }



    /**
     * 订单详细信息
     * <p>
     * //@param token 令牌
     *
     * @param no      订单编号
     */

    public static void getOrderDetail(String tag, final String token, final String shopid,final String no,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/order_info_detail", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("no", no);

                KLog.w(new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }




    /**
     * 获取折线图的数据
     *
     * @param shopid   商店id
     * @param day      天数（默认当天）
     * @param goodid   货物id（可空，填写获取7日销量数据-折线图使用）
     * @param category 货物种类id（可空，填写获取7日销量数据-折线图使用
     */
    public static void getOrderInfoGoods(String tag, final String token, final String shopid,final String day, final String goodid, final String category, final String endtime,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/order_info_goods", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("day", day);
                params.put("goodid", goodid);
                params.put("category", category); //可空
                params.put("endtime", endtime); //可空

                KLog.w(new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }


    public static void getMemberOrderInfoGoods(String tag, final String token, final String shopid,final String operator, final String day, final String goodid, final String category, final String endtime,
                                               Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/order_member_info_goods", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("operator", operator);
                params.put("day", day);
                params.put("goodid", goodid);
                params.put("category", category); //可空
                params.put("endtime", endtime); //可空

                KLog.w(new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }


    /**
     * 店铺收入信息概览
     *
     * @param shopid  商店id
     * @param day     天数（默认当天）
     */

    public static void getOrderSurvey(String tag, final String token, final String shopid,final String day,
                                      Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/order_survey", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("day", day);

                KLog.w(new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }


    public static void getMemberOrderSurvey(String tag, final String token, final String shopid,final String operator, final String day,
                                       Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/order_member_survey", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("operator", operator);
                params.put("day", day);

                KLog.w(new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }



    /**
     * 某种货物在时间段内的交易列表
     *
     * @param shopid
     * @param day     距离今天的天数（统计天数，不传endtime时）
     * @param goodid
     * @param endtime 统计截止时间（可空，不传时默认结束为当天，格式Y-m-d）
     * @param from    起始数据页码（可空，不传时默认0）
     * @param size    数据每页条数（可空，不传时默认10）
     */
    public static void getGoodsDetialInTime(String tag, final String token, final String shopid, final String day, final String goodid, final String endtime, final String from, final String size,
                                            Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/order_info_good_detail", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("day", day);
                params.put("goodid", goodid);
                params.put("endtime", endtime);
                params.put("from", from);
                params.put("size", size);

                KLog.w(new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }



    /**
     * 26. 盘库操作处理接口：
     *
     * @param data
     * @param type
     * @param method
     */
    public static void manageStock(String tag, final String token, final String shopid, final String data, final int type, final String method,
                                   Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_stock_manage", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("data", data);
                params.put("type", type + "");  //操作类型1预留至下一次盘库，2清空库存，3撤销操作
                params.put("method", method); //type=3即撤销操作时传入字符reset（可空）
                return params;
            }
        };

        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

    public static void getStockInfo(String tag, final String token, final String shopid, final String percent, final String bar,
                                    Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_stock_info_get", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("percent", percent); //对库存在某个百分比下的货物进行盘库处理（例：传0.05，即5%)
                params.put("bar", bar); //默认不对条码货物进行盘库，如需进行此参数传1（可空）
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }


    /**
     * 24 条码货物入库接口：
     *
     * @param barcode
     * @param number
     * @param new_t
     * @param goods
     * @param name
     * @param stock
     * @param cost
     * @param price
     * @param discount2
     * @param discount3
     * @param orderprice
     */

    public static void bargoodsInSet(String tag, final String token, final String shopid,
                                     final String barcode, final String number, final String new_t, final String goods, final String name, final String stock, final String cost,
                                     final String price, final String discount2, final String discount3, final String orderprice,
                                     Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + "/market/api/shop_bargoods_inset", listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("shopid", shopid);
                params.put("barcode", barcode);
                params.put("number", number);//入库数量
                params.put("new", new_t); //填1（22接口返回status=-20时）
                params.put("goods", goods); //货物id（22接口正常返回data中）
                params.put("name", name); //货物名称（new时必填，同时会修改货物表名称）
                params.put("stock", stock); //库存
                params.put("cost", cost); //成本，这个应该是进价进价aaaabcde
                params.put("price", price); // 售价
                params.put("discount2", discount2); //折扣
                params.put("discount3", discount3);
                params.put("orderprice", orderprice); //总价值（可空，空时接口进行计算）
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

}
