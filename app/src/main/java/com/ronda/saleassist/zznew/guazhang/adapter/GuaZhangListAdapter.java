package com.ronda.saleassist.zznew.guazhang.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.ronda.saleassist.R;
import com.ronda.saleassist.zznew.guazhang.GuaZhangOrderListActivity;
import com.ronda.saleassist.bean.GuaZhangList;

import java.util.List;


/**
 * Created by Administrator on 2016/11/12.
 */

public class GuaZhangListAdapter extends MyBaseAdapter<GuaZhangList> {

    public GuaZhangListAdapter(Context context, List<GuaZhangList> list) {
        super(context, list);
    }

    @Override
    View getView(final GuaZhangList guaZhangList) {
        View v = inflater.inflate(R.layout.lv_guazhang_listitem, null);
        v.findViewById(R.id.ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,GuaZhangOrderListActivity.class);
                intent.putExtra("customer",guaZhangList.getCustomer());
                context.startActivity(intent);
            }
        });
//        v.findViewById(R.id.btn_jiezhang).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context,"结账",Toast.LENGTH_SHORT).show();
//            }
//        });
        TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
        tv_name.setText(guaZhangList.getMobile());
        TextView tv_account = (TextView) v.findViewById(R.id.tv_account);
        tv_account.setText("总额:"+guaZhangList.getAllcount()+"元");
        return v;
    }

}
