package com.ronda.saleassist.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ronda.saleassist.R;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.local.sqlite.GreenDaoHelper;
import com.ronda.saleassist.module.home.CartFragment;
import com.ronda.saleassist.module.home.HomeFragment;
import com.ronda.saleassist.module.home.MineFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivty {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout   mDrawerLayout;

    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;

    @BindView(R.id.rg)
    RadioGroup mRg;

    private ActionBarDrawerToggle mDrawerToggle;

    private long mExitTime;
    private List<Fragment> mFragments;
    private Fragment       mCurFragment;

    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initToolbar(R.string.app_name, false);

        initButtomTab();

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
        GreenDaoHelper.getDaoSession().getCartDao().deleteAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                break;
            case R.id.share:

                break;
            case R.id.setting:

                break;
        }
        return true;
    }

    /**
     * 添加Fragment，并显示第一个Fragment和RadioButton
     */
    private void initButtomTab() {
        mFragments = new ArrayList<>();

        mFragments.add(HomeFragment.newInstance("arg1"));
        mFragments.add(CartFragment.newInstance("arg2"));
        mFragments.add(MineFragment.newInstance("arg3"));
        //mFragments.add(com.ronda.paicaibao.module.home.TestFragment.newInstance("arg4"));

        mCurFragment = mFragments.get(position);
        replaceFragment(mCurFragment);
        // 其实先设置事件的话，再选中，此时会触发该事件；反之则不会。
        // 所以会有两种思路：1.先设置RadioGroup事件，再设置选中；2.先设置View(第一个Fragment和第一个RadioButton要选中)，再设置事件
        ((RadioButton) mRg.getChildAt(position)).setChecked(true);
    }

    private void initEvent() {
        // 侧滑菜单
        initDrawerLayoutEvent();

        // tab切换
        initTabChangeEvent();
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
        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        item.setChecked(true);
                        break;

                }
                mDrawerLayout.closeDrawer(GravityCompat.START);//外层的DrawerLayout
                return false;
            }
        });
        ((LinearLayout) mNavView.getHeaderView(0)).getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Pic", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 底部tab切换事件
     */
    private void initTabChangeEvent() {
        mRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                if (!radioButton.isChecked()) {
                    return;
                }

                switch (checkedId) {
                    case R.id.rb_home:
                        setToolbarTitle("shop1");
                        position = 0;
                        break;
                    case R.id.rb_cart:
                        setToolbarTitle("shop2");
                        position = 1;
                        break;
                    case R.id.rb_me:
                        setToolbarTitle("shop3");
                        position = 2;
                        break;
                }
                Fragment to = mFragments.get(position);
                showFragment(mCurFragment, to);
                mCurFragment = to;
            }

        });
    }

    private void showFragment(Fragment from, Fragment to) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        if (!to.isAdded()) {    // 先判断是否被add过
            transaction.hide(from).add(R.id.fragment_container, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
    }

    /**
     * 按两次退出
     */
    private void twiceBack2Exit() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
