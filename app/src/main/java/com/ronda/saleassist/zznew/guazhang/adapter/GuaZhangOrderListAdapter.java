package com.ronda.saleassist.zznew.guazhang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.TextView;

import com.ronda.saleassist.R;
import com.ronda.saleassist.zznew.guazhang.GuaZhangOrderDetailActivity;
import com.ronda.saleassist.bean.GuaZhangOrderDetail;

import java.util.List;


/**
 * Created by Administrator on 2016/11/12.
 */

public class GuaZhangOrderListAdapter extends MyBaseAdapter<GuaZhangOrderDetail> {

    public GuaZhangOrderListAdapter(Context context, List<GuaZhangOrderDetail> list) {
        super(context, list);
    }

    @Override
    View getView(final GuaZhangOrderDetail guaZhangOrderDetail) {
        View view = inflater.inflate(R.layout.lv_guazhang_orderlist_item,null);
        view.findViewById(R.id.ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context,"点击了订单详情",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context,GuaZhangOrderDetailActivity.class);
                intent.putExtra("guaZhangOrderDetail",guaZhangOrderDetail);
                context.startActivity(intent);

            }
        });
        TextView tv_no= (TextView) view.findViewById(R.id.tv_no);
        tv_no.setText(guaZhangOrderDetail.getNo());
        TextView tv_total= (TextView) view.findViewById(R.id.tv_total);
        tv_total.setText("总价:"+guaZhangOrderDetail.getAllinfo().get(0).getTotal_price()+"元");
        final AppCompatCheckBox acb = (AppCompatCheckBox) view.findViewById(R.id.acb);
        if(guaZhangOrderDetail.isChecked()){
            acb.setChecked(true);
        }else{
            acb.setChecked(false);
        }
        acb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check!=null) {
                    if (acb.isChecked()) {
                        check.unCheck(guaZhangOrderDetail);
                    } else {
                        check.onCheck(guaZhangOrderDetail);
                    }
                }
            }
        });


        return view;
    }
    public interface OnCheck{
        void onCheck(GuaZhangOrderDetail guaZhangOrderDetail);
        void unCheck(GuaZhangOrderDetail guaZhangOrderDetail);
    }
    private OnCheck check;
    public void setCheck(OnCheck check){
        this.check = check;
    }
}
