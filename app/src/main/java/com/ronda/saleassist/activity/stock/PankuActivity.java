package com.ronda.saleassist.activity.stock;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.ronda.saleassist.R;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.fragment.PankuBargoodsFragment;
import com.ronda.saleassist.fragment.PankuWeightFragment;


public class PankuActivity extends BaseActivty implements View.OnClickListener {

    private static final int WEIGHT_FRAGMENT_TYPE = 1;
    private static final int CODE_FRAGMENT_TYPE = 2;

    private int currentFragmentType = 1;

    private Button btnWeightKind, btnCodeKind;

    private PankuWeightFragment pankuWeightFragment;
    private PankuBargoodsFragment pankuBargoodsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panku);

        initToolbar("盘库", true);

        btnWeightKind = (Button) findViewById(R.id.btn_weight_kind);
        btnCodeKind = (Button) findViewById(R.id.btn_code_kind);

        btnWeightKind.setOnClickListener(this);
        btnCodeKind.setOnClickListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState != null){
            int type = savedInstanceState.getInt("lastFragmentType");
            pankuWeightFragment = (PankuWeightFragment) fragmentManager.findFragmentByTag("weight");
            pankuBargoodsFragment = (PankuBargoodsFragment) fragmentManager.findFragmentByTag("code");

            if (type > 0){
                loadFragment(type);
            }
        }
        else {
            loadFragment(WEIGHT_FRAGMENT_TYPE);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("lastFragmentType", currentFragmentType);
    }

    private void loadFragment(int type) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (type == WEIGHT_FRAGMENT_TYPE){
            if (pankuWeightFragment == null){
                pankuWeightFragment = new PankuWeightFragment();
                transaction.add(R.id.fl_content, pankuWeightFragment, "weight");
            }
            else {
                transaction.show(pankuWeightFragment);
            }

            if (pankuBargoodsFragment != null){
                transaction.hide(pankuBargoodsFragment);
            }
            currentFragmentType = WEIGHT_FRAGMENT_TYPE;
        }
        else if (type == CODE_FRAGMENT_TYPE){
            if (pankuBargoodsFragment == null){
                pankuBargoodsFragment = new PankuBargoodsFragment();
                transaction.add(R.id.fl_content, pankuBargoodsFragment, "code");
            }
            else {
                transaction.show(pankuBargoodsFragment);
            }

            if (pankuWeightFragment != null){
                transaction.hide(pankuWeightFragment);
            }
            currentFragmentType = CODE_FRAGMENT_TYPE;
        }

        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_weight_kind:
                //btnWeightKind.setTextColor(getColor(R.color.colorPrimary));
                btnWeightKind.setTextColor(Color.parseColor("#eb4f38"));
                btnCodeKind.setTextColor(Color.WHITE);
                btnWeightKind.setBackgroundResource(R.drawable.btn_pink_left);
                btnCodeKind.setBackgroundResource(R.drawable.btn_trans_right);
                switchFragment(WEIGHT_FRAGMENT_TYPE);
                break;
            case R.id.btn_code_kind:
                btnWeightKind.setTextColor(Color.WHITE);
                //btnCodeKind.setTextColor(getColor(R.color.colorPrimary));
                btnCodeKind.setTextColor(Color.parseColor("#eb4f38"));
                btnWeightKind.setBackgroundResource(R.drawable.btn_trans_left);
                btnCodeKind.setBackgroundResource(R.drawable.btn_pink_right);
                switchFragment(CODE_FRAGMENT_TYPE);
                break;
        }
    }

    /**
     * 切换至(加载)对应类型的Fragment
     * @param type
     */
    private void switchFragment(int type) {
        switch (type){
            case WEIGHT_FRAGMENT_TYPE:
                loadFragment(WEIGHT_FRAGMENT_TYPE);
                break;
            case CODE_FRAGMENT_TYPE:
                loadFragment(CODE_FRAGMENT_TYPE);
                break;
        }
    }
}
