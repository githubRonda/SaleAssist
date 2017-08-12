package com.ronda.saleassist.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.divider.DividerItemDecoration;
import com.ronda.saleassist.adapter.simple.CommonAdapter;
import com.ronda.saleassist.adapter.simple.ViewHolder;
import com.ronda.saleassist.api.volley.GsonUtil;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.BaseFragment;
import com.ronda.saleassist.base.MyApplication;
import com.ronda.saleassist.base.dialog.DialogFactory;
import com.ronda.saleassist.bean.CartBean;
import com.ronda.saleassist.dialog.GetOrderDialog;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.local.sqlite.GreenDaoHelper;
import com.ronda.saleassist.local.sqlite.table.CartBeanOrder;
import com.ronda.saleassist.local.sqlite.table.CartBeanOrderDao;
import com.ronda.saleassist.utils.MathCompute;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    CartBeanOrderDao mCartBeanOrderDao = GreenDaoHelper.getDaoSession().getCartBeanOrderDao();//暂存订单和取单的Dao


    private CartAdapter mCartAdapter;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);//注册事件总线
        return inflater.inflate(R.layout.fragment_main_cart, container, false);
    }

    @Override
    public void init(View view) {
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this); //注销事件总线
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CartBean cartBean) {
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
    }

    @OnClick({R.id.btn_save_order, R.id.btn_get_order, R.id.btn_clear, R.id.btn_pay_cash, R.id.btn_pay_ali, R.id.btn_pay_weixin,
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
            case R.id.btn_pay_cash:
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

//        KLog.e("hangUpOrder ==> " + new Gson().toJson(mCartAdapter.getData()));

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
