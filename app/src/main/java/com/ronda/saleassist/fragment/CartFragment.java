package com.ronda.saleassist.fragment;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.divider.DividerItemDecoration;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.api.volley.GsonUtil;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.BaseFragment;
import com.ronda.saleassist.base.MyApplication;
import com.ronda.saleassist.base.dialog.DialogFactory;
import com.ronda.saleassist.bean.CartBean;
import com.ronda.saleassist.bean.CodeEvent;
import com.ronda.saleassist.bean.OrderParamData;
import com.ronda.saleassist.bean.PayEvent;
import com.ronda.saleassist.bean.WeightEvent;
import com.ronda.saleassist.dialog.GetOrderDialog;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.local.sqlite.GreenDaoHelper;
import com.ronda.saleassist.local.sqlite.table.CartBeanOrder;
import com.ronda.saleassist.local.sqlite.table.CartBeanOrderDao;
import com.ronda.saleassist.printer.PrintUtils;
import com.ronda.saleassist.printer.USBPrinter;
import com.ronda.saleassist.utils.CommonUtil;
import com.ronda.saleassist.utils.MathCompute;
import com.ronda.saleassist.utils.MusicUtil;
import com.ronda.saleassist.utils.ToastUtils;
import com.ronda.saleassist.view.ConfirmView;
import com.ronda.saleassist.view.DigitKeyboardView;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/08/04
 * Version: v1.0
 */

public class CartFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.txt_total)
    TextView mTxtTotal;

    @BindView(R.id.btn_get_order)
    Button mBtnGetOrder;

    @BindView(R.id.btn_save_order)
    Button mBtnSaveOrder;
    @BindView(R.id.btn_clear)
    Button mBtnClear;
    @BindView(R.id.btn_pay_cash)
    Button mBtnPayCash;
    @BindView(R.id.btn_pay_ali)
    Button mBtnPayAli;
    @BindView(R.id.btn_pay_weixin)
    Button mBtnPayWeixin;
    @BindView(R.id.btn_scan_vip_code)
    Button mBtnScanVipCode;
    @BindView(R.id.btn_pay_delay)
    Button mBtnPayDelay;
    @BindView(R.id.btn_vip_pay)
    Button mBtnVipPay;
    @BindView(R.id.btn_recharge)
    Button mBtnRecharge;


    private WindowManager mWM;
    private View mLeftView;
    private TextView mTvName, mTvWeight, mTvPrice, mTvTotalCount, mTvTotal;
    private int mLeftViewWidth;

    private static final String PAY_CASH = "cash";
    private static final String PAY_ALI = "alipay";
    private static final String PAY_WECHAT = "wechatpay";
    private static final String PAY_LATER = "laterpay";
    private static final String PAY_VIP = "vippay";

    private static final int VIP_CODE = 1;
    private static final int ALIPAY_CODE = 2;
    private static final int WECHATPAY_CODE = 3;

    private int mCurCodeType = 0; // 结账时表示是哪种条码类型 （挂账(会员码), 支付宝，微信  都是18位）

    private String token = SPUtils.getString(AppConst.TOKEN, "");
    private String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");

    CartBeanOrderDao mCartBeanOrderDao = GreenDaoHelper.getDaoSession().getCartBeanOrderDao();//暂存订单和取单的Dao


    private CartAdapter mCartAdapter;
    private Dialog payProgressDialog;
    private ConfirmView mDialogConfirmView;
    private TextView mDialogMessage;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);//注册事件总线
        return inflater.inflate(R.layout.fragment_main_cart, container, false);
    }

    @Override
    public void init(View view) {
        mWM = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        mBtnPayAli.setEnabled(SPUtils.getBoolean(AppConst.SUPPORT_ALIPAY, true));
        mBtnPayWeixin.setEnabled(SPUtils.getBoolean(AppConst.SUPPORT_WECHATPAY, true));

        mBtnGetOrder.setText("取单(" + mCartBeanOrderDao.count() + ")");

        mCartAdapter = new CartAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mCartAdapter);

        mCartAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                showDeleteOneDialog(position);
            }
        });

        initLeftView();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this); //注销事件总线

        if (mLeftView != null) {
            mWM.removeView(mLeftView);
            mLeftView = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventAddGoods(CartBean cartBean) {
        KLog.i(cartBean.toString());

        if (mCartAdapter.getData().contains(cartBean)) {
            int pos = mCartAdapter.getData().indexOf(cartBean);

            String weight = MathCompute.add(mCartAdapter.getData().get(pos).getWeight(), cartBean.getWeight());
            cartBean.setWeight(weight);//数量（重量相加）, 并且还要变换位置
            mCartAdapter.remove(pos);
        }

        mCartAdapter.addData(0, cartBean);
        mCartAdapter.notifyItemChanged(1); // 去掉上一个的背景色。直接使用 notifyDataSetChanged(); 没有动画效果

        updateTotalView(); //添加至货篮时更新总计

        // 更新LeftView 浮框中的数据(重量由另一个事件总线控制)
        mTvName.setText(cartBean.getName());
        mTvPrice.setText(cartBean.getDiscountPrice());
        mTvTotal.setText(cartBean.getDiscountCost());

        //3s钟后，由小计变为总计
        mHandler.removeCallbacks(mTotalRunanble);
        mHandler.postDelayed(mTotalRunanble, 3000);
    }

    private Runnable mTotalRunanble = new Runnable() {
        @Override
        public void run() {
            mTvTotal.setText(getCurTotalCost());
        }
    };

    //各种结算方式，使用EventBus的原因在于，物理按键也可以进行结算，这样便于统一管理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventPay(final PayEvent payEvent) {

        KLog.e("payMethod ------> " + payEvent.getPayMethod());

        switch (payEvent.getPayMethod()) {
            case 1: //现金
                uploadOrder(PAY_CASH, "1", "", "", "", "", ""); //上传订单 现金
                break;

            case 2://支付宝
                if (SPUtils.getBoolean(AppConst.SUPPORT_ALIPAY, false)) {
                    ToastUtils.showToast("商家未开通支付宝当面付");
                    return;
                }
                mCurCodeType = ALIPAY_CODE;
                showPayProcessDialog(ConfirmView.State.Progressing, "正在扫码...");

//                mHandler.sendEmptyMessage(payEvent.getPayMethod());


                break;
            case 3://微信
                if (SPUtils.getBoolean(AppConst.SUPPORT_ALIPAY, false)) {
                    ToastUtils.showToast("商家未开通微信支付");
                    return;
                }
                mHandler.sendEmptyMessage(payEvent.getPayMethod());
                break;
            case 4: //挂账

                break;
            case 5://会员支付
//                mHandler.sendEmptyMessage(payEvent.getPayMethod());
                break;
        }
    }

    //获取条码：会员码，支付宝码， 微信码.
    //获取到条码之后，根据当前的结算方式标记，上传订单数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventGetCode(CodeEvent codeEvent) {

        String code = codeEvent.getCode();

        switch (mCurCodeType){
            case ALIPAY_CODE:
                uploadOrder(PAY_ALI, "1", code, "", "", "", "");//上传订单 支付宝

                mCurCodeType= 0; // 重置为0
                break;
            case WECHATPAY_CODE:

                mCurCodeType= 0;
                break;
            case VIP_CODE:

                mCurCodeType= 0;
                break;
        }
    }


    /**
     * 结算时进度对话框
     *
     * @param state 三种状态
     * @param msg   显示信息
     */
    private void showPayProcessDialog(ConfirmView.State state, String msg) {
        if (payProgressDialog == null) {
            payProgressDialog = new AlertDialog.Builder(getActivity()).create();
            payProgressDialog.setCancelable(false);
            payProgressDialog.getWindow().setContentView(R.layout.dialog_pay_process);

            mDialogConfirmView = (ConfirmView) payProgressDialog.getWindow().findViewById(R.id.confirm_view);
            mDialogMessage = (TextView) payProgressDialog.getWindow().findViewById(R.id.tv_message);
            Button btnConfirm = (Button) payProgressDialog.getWindow().findViewById(R.id.btn_confirm);

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    payProgressDialog.dismiss();
                }
            });
        }
        if (!payProgressDialog.isShowing()) {
            payProgressDialog.show();
        }

        mDialogConfirmView.animatedWithState(state);

        mDialogMessage.setText(msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventGetWeight(final WeightEvent weightEvent) {
        String weight = weightEvent.getWeight();

        mTvWeight.setText(weight);

        try {
            if (Double.parseDouble(weight) == 0) {
                hideLeftView();
            } else {
                showLeftView();
            }
        } catch (Exception e) {
            hideLeftView();
        }
        //KLog.i("MainActivity: weightEvent --> " + weightEvent.getWeight());
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1: //现金结算
                    uploadOrder(PAY_CASH, "1", "", "", "", "", ""); //上传订单 现金 秤端
                    break;
                /*case 2: // 挂账
                    // 进行次数判断，当下位机连续按键多次的时候，只有第一次有效，可以避免出现多个对话框。
                    // 这种情况对于上位机来说是不存在的，因为点击按钮之后，对话框就会出来，此时按钮就是点击不了了。
                    if (payProgressDialog == null || (payProgressDialog != null && !payProgressDialog.isShowing())) {
//                        btnPayGuaZhang.performClick();
                        initMemberInfoAndOperateFrom(OPERATE_FROM_BALANCE);
                        showScanCodeDialog(CODE_MEMBER_DELAY_PAY);// 挂账 秤端
                    }
                    break;
                case 2:
                    downToScanGoodsCode();
                    break;
                case 3: //支付宝
                    if (payProgressDialog == null || (payProgressDialog != null && !payProgressDialog.isShowing())) {
                        initMemberInfoAndOperateFrom(OPERATE_FROM_BALANCE);
                        showScanCodeDialog(CODE_ALI_PAY); //支付宝 秤端
                    }
                    break;
                case 5:
                    if (payProgressDialog == null || (payProgressDialog != null && !payProgressDialog.isShowing())) {
                        initMemberInfoAndOperateFrom(OPERATE_FROM_BALANCE);
                        showScanCodeDialog(CODE_MEMBER_PAY); //会员支付 秤端
                    }
                    break;

                case 11: // 秤端添加额外项（按累计、加、减键）
                    CartBean c = (CartBean) msg.obj;
                    // 深拷贝
                    CartBean cartBean = new CartBean();
                    cartBean.setDate(System.currentTimeMillis());
//                    cartBean.setName("额外项");
//                    cartBean.setId("1000");
                    cartBean.setName(c.getName());
                    cartBean.setId(c.getId());
                    cartBean.setPrice(c.getPrice());
                    cartBean.setWeight(c.getWeight());
                    cartBean.setDiscount("1");

                    if (mCartDatas.contains(cartBean)) {
                        int pos = mCartDatas.indexOf(cartBean);

                        String weight = PreciseCompute.add(mCartDatas.get(pos).getWeight(), cartBean.getWeight()) + "";
                        cartBean.setWeight(weight);//数量（重量相加）, 并且还要变换位置
                        mCartDatas.remove(pos);
                    }

                    mCartDatas.add(0, cartBean);
                    mCartAdapter.notifyDataSetChanged();

                    Toast.makeText(MyApplication.getInstance(), "已添加至货篮", Toast.LENGTH_SHORT).show();

                    //主要下行累计总价数据
                    mBluetooth.downPriceAndTotal(++count, 0, getCurCartTotal()); //注意这里传的单价是0
                    updateTotalView();
                    break;
                case 1001: //1000以上用于信息提示（即语音提示）
//                    SuperUtil.playMusic(getActivity(), R.raw.amount_already_add);
                    MusicUtil.playMusic(getActivity(), R.raw.amount_already_add);
                    break;*/
            }

        }
    };

    @OnClick({R.id.btn_save_order, R.id.btn_get_order, R.id.btn_clear, R.id.btn_add, R.id.btn_pay_cash, R.id.btn_pay_ali, R.id.btn_pay_weixin,
            R.id.btn_scan_vip_code, R.id.btn_pay_delay, R.id.btn_vip_pay, R.id.btn_recharge})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save_order: //暂存订单
                saveOrder();
                break;
            case R.id.btn_get_order: // 取单
                getOrder();
                break;
            case R.id.btn_clear: //清空货篮
                showClearCartConfirmDialog();
                break;
            case R.id.btn_add:
                showAddDialog();
                break;
            case R.id.btn_pay_cash:
                EventBus.getDefault().post(new PayEvent(1, "现金支付 "));
                break;
            case R.id.btn_pay_ali:
                break;
            case R.id.btn_pay_weixin:
                break;
            case R.id.btn_scan_vip_code:
                break;
            case R.id.btn_pay_delay:
                break;
            case R.id.btn_vip_pay:
                break;
            case R.id.btn_recharge:
                break;
        }
    }


    /**
     * 更新总计View
     */
    private void updateTotalView() {
        // 更新总计
        mTxtTotal.setText(getCurTotalCost());

    }

    /**
     * 获取当前货篮中的总额
     *
     * @return
     */
    public String getCurTotalCost() {

        String total;

        BigDecimal b = new BigDecimal("0.0");
        for (CartBean bean : mCartAdapter.getData()) {
            b = b.add(new BigDecimal(bean.getDiscountCost()));
        }

//        b.setScale(2, BigDecimal.ROUND_HALF_UP);


        //进制处理
        int type = SPUtils.getInt(AppConst.CALCULATE_TYPE, AppConst.TYPE_TOTAL_5);// 默认总额逢5进
        if (type == AppConst.TYPE_TOTAL_1) {
            total = MathCompute.roundUp_scale1(b); //精确到角而不是分， 总额逢1进
        } else if (type == AppConst.TYPE_TOTAL_5) {
            total = MathCompute.roundHalfUp_scale1(b);
        } else {
            total = b.toString();
        }

        return total;
    }

    //清空货篮
    private void clearCart() {
        mCartAdapter.clearData();
        updateTotalView();//清空更新
    }

    /**
     * 显示删除货篮中某一记录的对话框
     *
     * @param position
     */
    private void showDeleteOneDialog(final int position) {
        final AlertDialog alertDialogDelete = new AlertDialog.Builder(getActivity()).create();
        alertDialogDelete.show();
        alertDialogDelete.getWindow().setContentView(R.layout.dialog_delete_category);
        alertDialogDelete.setCanceledOnTouchOutside(false);

        TextView msg = (TextView) alertDialogDelete.findViewById(R.id.tv_msg);
        Button btn_confirm = (Button) alertDialogDelete.getWindow().findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) alertDialogDelete.getWindow().findViewById(R.id.btn_cancel);

        msg.setText("确定删除\"" + mCartAdapter.getData().get(position).getName() + "\"吗？");

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogDelete.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCartAdapter.remove(position);
                mCartAdapter.notifyDataSetChanged();

//                downCurCartTotal();
                updateTotalView(); //删除指定货物时更新total

                alertDialogDelete.dismiss();
            }
        });
    }


    //显示清空货篮时的确认对话框
    private void showClearCartConfirmDialog() {
        if (mCartAdapter.getData().size() == 0) {
            Toast.makeText(MyApplication.getInstance(), "货篮为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.show();

        dialog.getWindow().setContentView(R.layout.dialog_clear_cart);
        Button btn_confirm = (Button) dialog.getWindow().findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) dialog.getWindow().findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                //清空货篮
                clearCart();
//                downCurCartTotal();
            }
        });
    }


    //暂存订单
    private void saveOrder() {

        if (mCartAdapter.getData().isEmpty()) {
            Toast.makeText(mContext, "没有点单记录，请录入", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            CartBeanOrder order = new CartBeanOrder(null, date, getCurTotalCost(), new Gson().toJson(mCartAdapter.getData())); // 把数据序列化存储
            mCartBeanOrderDao.insert(order);
            ToastUtils.showToast("存单成功");

            long count = mCartBeanOrderDao.queryBuilder().count();
            mBtnGetOrder.setText("取单(" + count + ")");

            mCartAdapter.clearData(); // 挂单成功清除货篮数据
            updateTotalView();
        } catch (Exception e) {
            e.printStackTrace();

            ToastUtils.showToast("存单失败");
        }
    }


    /**
     * 取单 按钮调用
     */
    public void getOrder() {

        if (!mCartAdapter.getData().isEmpty()) {//货篮中有数据时
            mDialogFactory.showConfirmDialog("提示", "当前有销售数据可能没有保存，是否放弃当前数据并取单？", false, 2, new DialogFactory.DialogActionListener() {
                @Override
                public void onDialogClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            mCartAdapter.clearData();
                            showGetOrderDialog();

                            break;
                        case DialogInterface.BUTTON_NEGATIVE: // 什么都不做
                            break;
                    }
                }
            });
        } else {
            showGetOrderDialog();
        }
    }

    /**
     * 显示取单的对话框
     */
    private void showGetOrderDialog() {
        GetOrderDialog.newInstance("get_order")
                .setCallbackListener(new GetOrderDialog.CallbackListener() {
                    @Override
                    public void onCall(String cartbeans) {
                        List<CartBean> list = GsonUtil.getGson().fromJson(cartbeans, new TypeToken<List<CartBean>>() {
                        }.getType());

                        mCartAdapter.setNewData(list);

                        mBtnGetOrder.setText("取单(" + mCartBeanOrderDao.count() + ")");
                        updateTotalView(); // 取单更新总计
                    }
                })
                .show(getActivity().getSupportFragmentManager(), "get_order");
    }

    /**
     * 添加额外项
     */
    private void showAddDialog() {

        final AlertDialog dialogAddition = new AlertDialog.Builder(getActivity()).create();
        dialogAddition.show();
        dialogAddition.getWindow().setContentView(R.layout.dialog_addition);

        DigitKeyboardView keyboardView = (DigitKeyboardView) dialogAddition.findViewById(R.id.digit_keyboard_view);
        Button btn_confirm = (Button) dialogAddition.getWindow().findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) dialogAddition.getWindow().findViewById(R.id.btn_cancel);
        final EditText edit_addition = (EditText) dialogAddition.getWindow().findViewById(R.id.edit_addition);

        keyboardView.bindEditTextViews(getActivity().getWindow(), edit_addition);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddition.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addtion = edit_addition.getText().toString().trim();

                if (addtion.isEmpty()) {
                    Toast.makeText(getActivity(), "金额不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialogAddition.dismiss();

                CartBean cartBean = new CartBean("1000", "额外项", addtion, "1", "", "1", "", "", "0", "");
                cartBean.setDate(System.currentTimeMillis());
                EventBus.getDefault().post(cartBean);
            }
        });
    }


    /**
     * 自定义浮窗显示
     */
    private void initLeftView() {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = android.R.style.Animation_Toast;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.gravity = Gravity.LEFT + Gravity.CENTER_VERTICAL;
        params.setTitle("Toast");

        mLeftView = LayoutInflater.from(mContext).inflate(R.layout.toast_left_view, null);
        mTvName = (TextView) mLeftView.findViewById(R.id.tv_name);
        mTvWeight = (TextView) mLeftView.findViewById(R.id.tv_weight);
        mTvPrice = (TextView) mLeftView.findViewById(R.id.tv_price);
        mTvTotal = (TextView) mLeftView.findViewById(R.id.tv_subtotal_total);
        mTvTotalCount = (TextView) mLeftView.findViewById(R.id.tv_total_count);

        mLeftView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mLeftViewWidth = mLeftView.getMeasuredWidth();
        mWM.addView(mLeftView, params);
    }


    public void showLeftView() {

        if (mLeftView.getTranslationX() >= 0) { //完全展开
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(mLeftView, "translationX", mLeftView.getTranslationX(), 0f);
        animator.setDuration(800);
        animator.start();
    }

    public void hideLeftView() {
        if (mLeftView.getTranslationX() <= -mLeftViewWidth) { //完全收缩
            return;
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(mLeftView, "translationX", mLeftView.getTranslationX(), -mLeftViewWidth);
        animator.setDuration(800);
        animator.start();
    }


    //======================================服务器相关=================================================


    // 真正上传订单前一步的统一方法：集中构建上传订单的data参数数据和打印小票数据
    private void uploadOrder(String method, String customer, String paycode, String costprice, String extcode, String extmethod, String extpay) {

        //1. 先判断货篮是否为空，和蓝牙是否连接
        if (mCartAdapter.getData().size() == 0) {
            ToastUtils.showToast("货篮为空");
            return;
        }

        //2. 构建订单上传接口中的data参数(json数组的字符串)
        List<OrderParamData> list = new ArrayList<OrderParamData>();
        for (CartBean cartbean : mCartAdapter.getData()) {
            //注意：cost参数：表示折扣。orderprice参数：重量*单价*折扣
            list.add(new OrderParamData(cartbean.getWeight(), cartbean.getId(), cartbean.getPrice(), cartbean.getDiscount(), cartbean.getDiscountCost()));
        }
        String param_data = new Gson().toJson(list);

        switch (method) {
            case PAY_CASH:
                //打印
                USBPrinter.getInstance().print(PrintUtils.generateBillData(mCartAdapter.getData(), getCurTotalCost(), "现金"));
                //上传
                doUploadOrder(param_data, getCurTotalCost(), method, customer, "", "", "", "", "");
                //清空数据
                clearCart();
                break;
            case PAY_ALI:
                //打印
                USBPrinter.getInstance().print(PrintUtils.generateBillData(mCartAdapter.getData(), getCurTotalCost(), "支付宝"));
                showPayProcessDialog(ConfirmView.State.Progressing, "正在支付，请稍后...");
                doUploadOrder( param_data, getCurTotalCost(), method, customer, paycode, costprice, extmethod, extcode, extpay);
                break;
//            case PAY_WECHAT:
//                hexData = CommonUtil.getDFFFHexStr(shopName, operator, dateStr, timeStr, mCartDatas, total, "微信", total);
//                setPayProcessView(FLAG_PAY_PROCESSING, STATUS_UPLOADING, "正在支付，请稍后...");// 正在支付时，状态码时随便设的
//                String t = total;
//                if (!costprice.isEmpty()) {
//                    t = costprice;
//                }
//                doUploadOrder(App.curShopId, param_data, total, method, customer, paycode, t, extmethod, extcode, extpay, hexData); //微信支付比较特殊，非会员支付的时候也要传costprice,不过这时就等于 total
//                break;
//            case PAY_LATER:
//                hexData = CommonUtil.getDFFFHexStr(shopName, operator, dateStr, timeStr, mCartDatas, total, "挂账", total);
//                setPayProcessView(FLAG_PAY_PROCESSING, STATUS_UPLOADING, "正在挂账，请稍后...");// 正在挂账时，状态码时随便设的
//                doUploadOrder(App.curShopId, param_data, total, method, customer, paycode, costprice, extmethod, extcode, extpay, hexData);
//                break;
//            case PAY_VIP://会员支付。获取会员信息后，若余额充足时，直接就走这个接口。若余额不足时，若选择支付宝支付，在按到支付码后继续走此接口；若选择现金支付，则继续走此接口
//                hexData = CommonUtil.getDFFFHexStr(shopName, operator, dateStr, timeStr, mCartDatas, total, "会员支付", total);
//
//                setPayProcessView(FLAG_PAY_PROCESSING, STATUS_UPLOADING, "正在支付，请稍后...");// 正在支付时，状态码时随便设的
//                //uploadOrder_vip(memberInfo.getUserid(), "", costTotal, "", memberInfo.getExtcode(), "");
//                doUploadOrder(App.curShopId, param_data, total, method, customer, paycode, costprice, extmethod, extcode, extpay, hexData);
//                break;
        }
    }

    //上传订单的统一方法接口，用于集中对结果处理和payProcessDialog状态设置
    private void doUploadOrder(String data, String total, final String method, String customer, String paycode,
                               String costprice, String extmethod, String extcode, String extpay) {
        UserApi.uploadOrder(TAG, token, shopId, data, total, paycode, method, customer, costprice, extmethod, extcode, extpay,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        KLog.json(responseStr);
                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");
                            String sub_msg = response.getString("sub_msg");

                            if (status > 0) {
                                Toast.makeText(getActivity(), msg + sub_msg, Toast.LENGTH_SHORT).show();

                                String data = response.getString("data");//订单号

                                switch (method) {
                                    case PAY_WECHAT:
                                    case PAY_ALI:
                                        showPayProcessDialog(ConfirmView.State.Success, "支付成功");
                                        closePayProcessDialogDelay();
                                        MusicUtil.playMusic(mContext, R.raw.pay_successful);

                                        break;
//                                    case PAY_LATER:
//                                        setPayProcessView(FLAG_PAY_SUCCESS, 1, "挂账成功", hexData);
//                                        //playMusic(context, R.raw.laterpay_successful);
//                                        MusicUtil.playMusic(context, R.raw.laterpay_successful);
//                                        break;
//                                    case PAY_VIP:
//                                        setPayProcessView(FLAG_PAY_SUCCESS, 1, "会员支付成功", hexData);
//                                        //playMusic(context, R.raw.pay_successful);
//                                        MusicUtil.playMusic(context, R.raw.pay_successful);
//                                        break;
                                }

                                clearCart();//清空货篮
//
////                        if (operateFrom != OPERATE_FROM_BALANCE) {
////                            downCurCartTotal(); //此时货篮中的总额为空，相当于下行清空指令
////                        }
//                                downCurCartTotal();
//                                //延迟关闭对话框
//                                delayClosePayConfirmDialog(); //实际上就是使用 performClick() 来模拟点击确定按钮
//                            } else {
//                                setPayProcessView(FLAG_ONLINE_PAY_FAIL, status, msg);//支付失败
//                                //playMusic(context, R.raw.pay_failed);
//                                MusicUtil.playMusic(context, R.raw.pay_failed);
                            }
                            else{
                                showPayProcessDialog(ConfirmView.State.Fail, "支付失败: "+ sub_msg);
                                closePayProcessDialogDelay();

                                MusicUtil.playMusic(mContext, R.raw.pay_failed);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MyApplication.getInstance(), "数据解析有误", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast("网络繁忙，支付失败！");

                        closePayProcessDialogDelay();
                    }
                });
    }

    /**
     * 立即关闭
     */
    private void closePayProcessDialogImmediate() {
        if (payProgressDialog == null || !payProgressDialog.isShowing()){
            return;
        }

        payProgressDialog.dismiss();
    }


    /**
     * 延迟关闭
     */
    private void closePayProcessDialogDelay() {
        if (payProgressDialog == null || !payProgressDialog.isShowing()){
            return;
        }

        mHandler.removeCallbacks(mCloseDialogRunnable);
        mHandler.postDelayed(mCloseDialogRunnable, 3000);
    }

    private Runnable mCloseDialogRunnable = new Runnable() {
        @Override
        public void run() {
            if (payProgressDialog == null || !payProgressDialog.isShowing()){
                return;
            }
            payProgressDialog.dismiss();
        }
    };

    //======================================内部类=================================================
    class CartAdapter extends BaseQuickAdapter<CartBean, BaseViewHolder> {

        public CartAdapter() {
            super(R.layout.list_item_cart);
        }

        @Override
        protected void convert(BaseViewHolder holder, CartBean item) {
            holder.setText(R.id.text_greens_name, item.getName());
            holder.setText(R.id.text_greens_price, item.getDiscountPrice());
            holder.setText(R.id.text_greens_weight, item.getWeight());

            holder.itemView.setBackgroundColor(Color.WHITE);
            if (holder.getLayoutPosition() == 0) {
                holder.itemView.setBackgroundColor(Color.GREEN);
            }


            holder.addOnClickListener(R.id.img_del); //添加事件，在Adapter中实现 BaseQuickAdapter.OnItemChildClickListener 是无效的，只能通过mAdapter.setOnItemChildClickListener()
        }
    }
}
