package com.ronda.saleassist.activity.member;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ronda.saleassist.R;
import com.ronda.saleassist.base.BaseActivty;


/**
 * 会员管理目录
 *
 * User: Ronda(1575558177@qq.com)
 * Date: 2016/10/24
 */
public class ManageVipActivity extends BaseActivty implements View.OnClickListener {

    private static final int REQUSET_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_manage);

        initToolbar("会员管理", true);

        initView();
        initEvent();
    }

    private void initView() {}

    private void initEvent() {

        findViewById(R.id.ll_new_member).setOnClickListener(this); //待审核会员
        findViewById(R.id.ll_all_member).setOnClickListener(this); //所有会员的查询与管理
        findViewById(R.id.ll_vip_level).setOnClickListener(this); //会员等级设置
        findViewById(R.id.ll_vip_preference).setOnClickListener(this); //会员优惠项设置
        findViewById(R.id.ll_vip_extra_cost).setOnClickListener(this); //会员配送费设置
        findViewById(R.id.ll_push_ad).setOnClickListener(this); //推送优惠广告等信息

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_new_member:
                startActivity(new Intent(this, ApplicantListActivity.class));
                break;
            case R.id.ll_all_member:
                startActivity(new Intent(this, VipListActivity.class));
                break;
            case R.id.ll_vip_level:
                startActivity(new Intent(this, VipLevelActivity.class));
                break;
            case R.id.ll_vip_preference:
                startActivity(new Intent(this, VipPreferenceActivity.class));
                break;
            case R.id.ll_vip_extra_cost:
                startActivity(new Intent(this, PeiSongCostActivity.class));
                break;
            case R.id.ll_push_ad:
                startActivity(new Intent(this, PushAdActivity.class));
                break;

        }
    }
}
