package com.ronda.saleassist.zznew.guazhang;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ronda.saleassist.R;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.bean.Allinfo;
import com.ronda.saleassist.bean.Good;
import com.ronda.saleassist.bean.GuaZhangOrderDetail;
import com.socks.library.KLog;

public class GuaZhangOrderDetailActivity extends BaseActivty {

    private GuaZhangOrderDetail guaZhangOrderDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gua_zhang_detail);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText("订单详情");

        guaZhangOrderDetail = (GuaZhangOrderDetail) getIntent().getSerializableExtra("guaZhangOrderDetail");


        KLog.json(guaZhangOrderDetail.toString());

        if(guaZhangOrderDetail!=null){
//            Toast.makeText(this,guaZhangOrderDetail.getNo(),Toast.LENGTH_LONG).show();
            initView();
        }
    }

    private void initView() {
        LinearLayout ll_shopcut = (LinearLayout) findViewById(R.id.ll_shopcut);
        LinearLayout ll_shopcut_item = (LinearLayout) findViewById(R.id.ll_shopcut_item);
        ll_shopcut_item.setVisibility(View.GONE);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) ll_shopcut_item.getLayoutParams();
        TextView tv_goodsname = (TextView) findViewById(R.id.tv_goodsname);
        LinearLayout.LayoutParams namelp = (LinearLayout.LayoutParams) tv_goodsname.getLayoutParams();
        LinearLayout.LayoutParams singlepricelp = (LinearLayout.LayoutParams) findViewById(R.id.tv_goodssingleprice).getLayoutParams();
        LinearLayout.LayoutParams countlp = (LinearLayout.LayoutParams) findViewById(R.id.tv_goodscount).getLayoutParams();
        LinearLayout.LayoutParams pricelp = (LinearLayout.LayoutParams) findViewById(R.id.tv_goodsprice).getLayoutParams();
        pricelp.setMargins(0, 10, 0, 10);
        if (guaZhangOrderDetail != null) {
            for (int i = 0; i < guaZhangOrderDetail.getAllinfo().size(); i++) {
                Good good = guaZhangOrderDetail.getAllinfo().get(i).getGoodinfo();
                Allinfo info = guaZhangOrderDetail.getAllinfo().get(i);
                LinearLayout ll = new LinearLayout(GuaZhangOrderDetailActivity.this);

                TextView name = new TextView(GuaZhangOrderDetailActivity.this);
                namelp.gravity = Gravity.LEFT;
                name.setLayoutParams(namelp);
                name.setTextColor(Color.BLACK);
                name.setText(good.getName());
                name.setPadding(30, 0, 0, 0);

                TextView singleprice = new TextView(GuaZhangOrderDetailActivity.this);
                singleprice.setLayoutParams(singlepricelp);
                singleprice.setText("¥" + info.getPrice());
                singleprice.setGravity(Gravity.CENTER);

                TextView count = new TextView(GuaZhangOrderDetailActivity.this);
                count.setLayoutParams(countlp);
                count.setText("x" + guaZhangOrderDetail.getAllinfo().get(i).getNumber());
                count.setGravity(Gravity.CENTER);

                TextView price = new TextView(GuaZhangOrderDetailActivity.this);
                price.setLayoutParams(pricelp);
                price.setTextColor(Color.BLACK);
                price.setText("¥" + guaZhangOrderDetail.getAllinfo().get(i).getOrderprice());
                price.setGravity(Gravity.RIGHT);
                price.setPadding(0, 0, 30, 0);
                ll.addView(name);
                ll.addView(singleprice);
                ll.addView(count);
                ll.addView(price);

                ll.setLayoutParams(lp);

                ll_shopcut.addView(ll);
            }
            TextView tv_total_price = (TextView) findViewById(R.id.tv_total_price);
            tv_total_price.setText(guaZhangOrderDetail.getAllinfo().get(0).getTotal_price());
        }
    }
}
