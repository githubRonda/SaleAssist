package com.ronda.saleassist.activity.sale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.ronda.saleassist.R;
import com.ronda.saleassist.base.BaseActivty;

/**
 * 销售管理界面
 *
 * 点击员工管理中的员工，也可以跳到这个界面.这个是用于查询该员工下的销售记录。用empBundle.size()>0来识别。
 * empBundle 包含了员工的 uid 和 nick
 * 这个界面比较特殊，即可以由右侧菜单直接进入，也可以在员工管理中点击员工项进入。这个界面是一个汇集口。
 * 但是要注意：由这个界面调到下面的其他界面时，传入 Intent 的 Bundle 不能为null, 但是 size() 可以为0，表示老板的报表，size()>0表示员工的报表
 * 员工界面的uid等信息，传到这个管理界面，然后再由这个管理界面分别传到销售记录界面和销售统计界面中
 */
public class SellManageActivity extends BaseActivty implements View.OnClickListener {


    private LinearLayout llRecordCount, llStatistics;

    private String uid; // 表示员工的uid, 若uid为null,则表示调用店老板的报表接口，否则调用员工的报表接口

    private Bundle empBundle;// 表示员工的bundle, 包含uid，nick, 若bundle为null,则表示调用店老板的报表接口，否则调用员工的报表接

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_manager);

        empBundle = getIntent().getExtras();

        String title  = "销售管理";
        if (null != empBundle && empBundle.size()>0){
            title = empBundle.getString("nick")+"员工-销售管理";
        }
        else {
            empBundle = new Bundle(); //这里当empBundle为null 的时候，必须要创建一个空的Bundle，否则 在调用 Intent.putExtras(empBundle)时运行会空指针异常
        }
        initToolbar(title, true);


        llRecordCount = (LinearLayout) findViewById(R.id.ll_record_count);
        llStatistics = (LinearLayout) findViewById(R.id.ll_record_statistics);

        llRecordCount.setOnClickListener(this);
        llStatistics.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_record_count:
//                KLog.e("empBundle : "+ empBundle + "  "+(empBundle == null)+" ,size"+empBundle.size());
                startActivity(new Intent(this, BillListActivity.class).putExtras(empBundle));
                break;
            case R.id.ll_record_statistics:
                startActivity(new Intent(this, CategoryStatisticActivity.class).putExtras(empBundle));
                break;
        }
    }
}
