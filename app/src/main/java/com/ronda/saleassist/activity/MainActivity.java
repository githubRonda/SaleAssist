package com.ronda.saleassist.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ronda.saleassist.R;
import com.ronda.saleassist.base.BaseActivty;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivty implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout   mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initEvent();
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
//            case R.id.ll_setting:
//                jump(SettingActivity.class);
//                break;
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


}
