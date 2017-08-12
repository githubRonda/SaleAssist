package com.ronda.saleassist.api;

import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
     * 8. 货物入库(配合图片上传一起使用)
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
        goodsEdit(tag,token, 1, shopid, categoryId, goodId, name, price, photoId, discount2, discount3, intro, method, unit, listener, errorListener);
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

                KLog.w(tag + "--> " + new Gson().toJson(params));
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
     private static final String UPLOAD_ORDER = "/market/api/order_info_upload";
     */

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
     * /market/api/alipay_location_info_upload
     */


}
