package com.ronda.saleassist.activity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ronda.saleassist.R;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.AppManager;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.base.MyApplication;
import com.ronda.saleassist.bean.CartBean;
import com.ronda.saleassist.bean.WeightEvent;
import com.ronda.saleassist.engine.BarcodeScannerResolver;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.printer.USBPrinter;
import com.ronda.saleassist.utils.MusicUtil;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivty implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout   mDrawerLayout;

    private String token  = SPUtils.getString(AppConst.TOKEN, "");
    private String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");

    private ActionBarDrawerToggle mDrawerToggle;

    private BarcodeScannerResolver mBarcodeScannerResolver; //扫码解析器

    private WindowManager mWM;
    private View          mToastView;
    private TextView      mTvWeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initEvent();

        startScanListen();

        USBPrinter.getInstance().initPrinter(this);

        mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

        EventBus.getDefault().register(this);//注册事件总线
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDrawerLayout.removeDrawerListener(mDrawerToggle);

        removeScanListen();

        USBPrinter.getInstance().destroyPrinter();

        EventBus.getDefault().unregister(this);
    }

    private void initEvent() {
        // 侧滑菜单
        initDrawerLayoutEvent();
    }

    @Override
    public void onBackPressed() {
        twiceBack2Exit();
    }


    /**
     * 侧滑菜单事件
     */
    private void initDrawerLayoutEvent() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mNavView.setNavigationItemSelectedListener(this);

        LinearLayout llNavHeader = ((LinearLayout) mNavView.getHeaderView(0));
        llNavHeader.findViewById(R.id.ib_arrow_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(GravityCompat.END);
            }
        });
    }


    // 展开侧边栏，在GoodsFragment中调用
    public void openDrawer() {
        if (!mDrawerLayout.isDrawerOpen(mNavView)) {
            mDrawerLayout.openDrawer(mNavView);
        }
    }

    // 收起侧边栏
    public void closeDrawer() {
        if (mDrawerLayout.isDrawerOpen(mNavView)) {
            mDrawerLayout.closeDrawer(mNavView);
        }
    }

    //================OnNavigationItemSelectedListener====================
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.ll_sell_manage:
//                jump(SellManageActivity.class);
//                break;
//            case R.id.ll_stock_manage:
//                jump(StockManageActivity.class);
//                break;
//            case R.id.ll_member_manage:
//                jump(ManageVipActivity.class);
//                break;
            case R.id.ll_setting:
                jump(SettingActivity.class);
                break;
//            case R.id.ll_shop_apply:
//                jump(ShopApplyActivity.class);
//                break;
//            case R.id.ll_manage:
//                jump(EmpManageActivity.class);
//                break;
//            case R.id.ll_my_shop:
//                jump(MyShopActivity.class);
//                break;
//            case R.id.ll_update_price:
//                jump(UpdatePriceActivity.class);
//                break;
//            case R.id.ll_order_manage:
//                jump(OrderListActivity.class);
//                break;
//            case R.id.ll_guazhang_manage:
//                jump(GuaZhangListActivity.class);
//                break;

        }
        mDrawerLayout.closeDrawer(GravityCompat.END);//外层的DrawerLayout
        //mDrawerLayout.closeDrawer(mNavView); // 这样也是可以的
        return false;
    }


    /**
     * 开始扫码监听
     */
    public void startScanListen() {
        mBarcodeScannerResolver = new BarcodeScannerResolver();
        mBarcodeScannerResolver.setScanSuccessListener(new BarcodeScannerResolver.OnScanSuccessListener() {
            @Override
            public void onScanSuccess(String barcode) {
                //条码有8位和13位的
                if (barcode.length() == 8 || barcode.length() == 13) {
                    //TODO 处理条码 （获取对应货物信息，添加至货篮）

                    if (AppManager.getInstance().currentActivity() != MainActivity.this) {
                        return;
                    }
                    getGoodsInfoByCode(barcode);
                }
                ToastUtils.showToast("barcode: " + barcode);
            }
        });
    }

    /**
     * 移除扫码监听
     */
    public void removeScanListen() {
        mBarcodeScannerResolver.removeScanSuccessListener();
        mBarcodeScannerResolver = null;
    }

    /**
     * Activity截获按键事件.发给 BarcodeScannerResolver
     * dispatchKeyEvent() 和 onKeyDown() 方法均可
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        mBarcodeScannerResolver.resolveKeyEvent(event);
        return super.dispatchKeyEvent(event);
    }

    /**
     * 按两次退出
     */
    private long mExitTime;

    private void twiceBack2Exit() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    /**
     * 自定义归属地浮窗显示
     */
    private void showToast(String text) {
        if (mToastView == null) {
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.format = PixelFormat.TRANSLUCENT;
            params.windowAnimations = android.R.style.Animation_Toast;
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
//            params.type = WindowManager.LayoutParams.TYPE_PHONE;
            params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            params.gravity = Gravity.LEFT + Gravity.TOP;
            params.setTitle("Toast");
//            params.x = 10;
//            params.y = 50;

            mToastView = LayoutInflater.from(this).inflate(R.layout.toast_address, null);
            mTvWeight = (TextView) mToastView.findViewById(R.id.tv_weight);

            mWM.addView(mToastView, params);
        }
        mTvWeight.setText(text);
    }

    private void hideToast() {
        if (mToastView != null) {
            mWM.removeViewImmediate(mToastView);
            mToastView = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final WeightEvent weightEvent) {
        String weight = weightEvent.getWeight();
        try {
            if (Double.parseDouble(weight) == 0) {
                hideToast();
            } else {
                showToast(weight);
            }
        } catch (Exception e) {
            hideToast();
        }
        //KLog.i("MainActivity: weightEvent --> " + weightEvent.getWeight());
    }


    //========================后台服务器相关=====================================

    /**
     * 根据条码获取货物
     *
     * @param goodsCode
     */
    public void getGoodsInfoByCode(String goodsCode) {
        UserApi.bargoodsInfoAndCreate(TAG, token, shopId, goodsCode,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        KLog.json(responseStr);

                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");
                            if (status == 1) {
                                JSONObject data = response.getJSONObject("data");
                                String name = data.getString("name");
                                String barcode = data.getString("barcode");

                                String price = data.getString("price");
                                String goodid = data.getString("goodid");
                                String discount = data.getString("discount1");

                                CartBean cartBean = new CartBean();
                                cartBean.setCategory("1111");//条码类是一个特殊的类，后台固定为1111
                                cartBean.setId(goodid);
                                cartBean.setName(name);
                                cartBean.setPrice(price);
                                cartBean.setWeight("1");
                                cartBean.setDiscount(discount);
                                cartBean.setImgPath("");//后台没有返回图片路径，这里暂定设为空
                                cartBean.setBarcode(barcode);

                                EventBus.getDefault().post(cartBean);
                            } else if (status == -20) {
                                Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();

                                //SuperUtil.playMusic(getContext(), R.raw.no_stock1);
                                MusicUtil.playMusic(getApplicationContext(), R.raw.no_stock1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showToast("数据解析有误");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast(R.string.no_respnose);
                    }
                });
    }

}
