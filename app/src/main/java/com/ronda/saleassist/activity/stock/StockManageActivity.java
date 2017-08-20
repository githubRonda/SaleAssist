package com.ronda.saleassist.activity.stock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.ronda.saleassist.R;
import com.ronda.saleassist.base.BaseActivty;

public class StockManageActivity extends BaseActivty implements View.OnClickListener {

    private LinearLayout llRuku, llPanku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_manage);

        initToolbar("库存管理", true);

        llRuku = (LinearLayout) findViewById(R.id.ll_ruku);
        llPanku = (LinearLayout) findViewById(R.id.ll_panku);

        llRuku.setOnClickListener(this);
        llPanku.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.ll_ruku) {
            Intent intent = new Intent(this, StockActivity.class);
            startActivity(intent);
        } else if (id == R.id.ll_panku) {
            Intent intent = new Intent(this, PankuActivity.class);
            startActivity(intent);
        }
    }
}
