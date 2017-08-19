package com.ronda.saleassist.activity.member;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.BaseRecyclerViewAdapter;
import com.ronda.saleassist.adapter.RecyclerViewHolder;
import com.ronda.saleassist.adapter.divider.DividerItemDecoration;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.bean.PreferenceBean;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.utils.ToastUtils;
import com.ronda.saleassist.view.DigitKeyboardView;
import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * author: Ronda(1575558177@qq.com)
 * Date: 2016/11/17
 * Version: v1.0
 */

public class VipPreferenceActivity extends BaseActivty {

    private int curPreferencePosition = 0; //用于记录新增优惠项时当前下拉列表的position

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private List<PreferenceBean> mDatas = new ArrayList<>();
    private VipPreferenceAdapter mAdapter;

    private String token = SPUtils.getString(AppConst.TOKEN, "");
    private String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_preference);

        initToolbar("会员优惠项", true);

        initView();

        initEvent();

        loadPreferenceData();
    }

    private void initView() {

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));
        mSwipeRefreshLayout.setRefreshing(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new VipPreferenceAdapter(mDatas);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    private void initEvent() {

        // 下拉刷新事件
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPreferenceData();
            }
        });

        // RecyclerView的item的右侧的删除事件
        mAdapter.setOnDeleteBtnClickListener(new VipPreferenceAdapter.OnDeleteBtnClickListener() {

            @Override
            public void onDeleteBtnClick(View itemView, RecyclerView.ViewHolder holder, final int position) {
                final AlertDialog dialog = new AlertDialog.Builder(VipPreferenceActivity.this).create();
                dialog.show();


                dialog.getWindow().setContentView(R.layout.dialog_common_two_btn_tip);

                TextView tv_title = (TextView) dialog.getWindow().findViewById(R.id.tv_title);
                TextView tv_msg = (TextView) dialog.getWindow().findViewById(R.id.tv_msg);
                Button btn_cancel = (Button) dialog.getWindow().findViewById(R.id.btn_cancel);
                Button btn_confirm = (Button) dialog.getWindow().findViewById(R.id.btn_confirm);

                tv_title.setText("警告");
                tv_msg.setText("确定删除这条记录吗？");
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        UserApi.deletePreference(TAG, token, shopId, mDatas.get(position).getId(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String responseStr) {
                                        KLog.json(responseStr);
                                        try {
                                            JSONObject response = new JSONObject(responseStr);
                                            int status = response.getInt("status");
                                            String msg = response.getString("msg");
                                            if (status == 1) {
                                                //删除成功时，重新加载一下
                                                loadPreferenceData();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
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
                });
            }
        });

        //RecyclerView的item的长按事件
        mAdapter.setOnItemLongClickListener(new BaseRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, RecyclerViewHolder holder, int position) {
                showPreferenceDialog(position);
            }
        });
    }

    // 访问后台，加载优惠项数据
    private void loadPreferenceData() {
        UserApi.getPreference(TAG, token, shopId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        KLog.json(responseStr);
                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");

                            if (status == 1) {
                                JSONArray data = response.getJSONArray("data");
                                Gson gson = new Gson();
                                List<PreferenceBean> preferences = gson.fromJson(data.toString(), new TypeToken<List<PreferenceBean>>() {
                                }.getType());

                                mDatas.clear();
                                mDatas.addAll(preferences);
                                mAdapter.notifyDataSetChanged();
                            }
                            else if (status == 0){
                                mDatas.clear();
                                mAdapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast(R.string.no_respnose);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vip_preference, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                showPreferenceDialog(-1);//当position为-1的时候，表示新增；其他时候表示修改
                break;
        }
        return true;
    }

    // 用于添加和修改时显示的对话框: 当表示添加的时候穿-1，修改的时候传item的position
    private void showPreferenceDialog(final int position) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();

        dialog.getWindow().setContentView(R.layout.dialog_add_preference);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        Button btn_cancel = (Button) dialog.getWindow().findViewById(R.id.btn_cancel);
        Button btn_confirm = (Button) dialog.findViewById(R.id.btn_confirm);
        final DigitKeyboardView keyboard = (DigitKeyboardView) dialog.getWindow().findViewById(R.id.ll_keyboard);
        Spinner spinner_preference_kind = (Spinner) dialog.getWindow().findViewById(R.id.spinner_preference_kind);

        //优惠项名称
        final EditText et_name = (EditText) dialog.getWindow().findViewById(R.id.et_name);

        //优惠种类1
        final LinearLayout ll_preference1 = (LinearLayout) dialog.getWindow().findViewById(R.id.ll_preference1);
        final EditText et_preference1_1 = (EditText) dialog.getWindow().findViewById(R.id.et_preference1_1);
        final EditText et_preference1_2 = (EditText) dialog.getWindow().findViewById(R.id.et_preference1_2);

        //优惠种类2
        final LinearLayout ll_preference2 = (LinearLayout) dialog.getWindow().findViewById(R.id.ll_preference2);
        final EditText et_preference2 = (EditText) dialog.getWindow().findViewById(R.id.et_preference2);

        //优惠种类3
        final LinearLayout ll_preference3 = (LinearLayout) dialog.getWindow().findViewById(R.id.ll_preference3);
        final EditText et_preference3 = (EditText) dialog.getWindow().findViewById(R.id.et_preference3);

        //优惠种类4
        final LinearLayout ll_preference4 = (LinearLayout) dialog.getWindow().findViewById(R.id.ll_preference4);
        final EditText et_preference4_1 = (EditText) dialog.getWindow().findViewById(R.id.et_preference4_1);
        final EditText et_preference4_2 = (EditText) dialog.getWindow().findViewById(R.id.et_preference4_2);

        String[] spinnerKindDatas = {"满XX减XX元", "满XX送积分", "设置折扣", "充值XX送XX"};
        spinner_preference_kind.setAdapter(new ArrayAdapter<String>(VipPreferenceActivity.this, android.R.layout.simple_list_item_1, spinnerKindDatas));

        if (position != -1) { //表示修改,此时需要给View赋值
            spinner_preference_kind.setSelection(Integer.parseInt(mDatas.get(position).getCosttype()) - 1);//costType：1,2,3； 而Selection是从0开始的
            et_name.setText(mDatas.get(position).getName());
            et_preference1_1.setText(mDatas.get(position).getMin());
            et_preference1_2.setText(mDatas.get(position).getCommission());
            et_preference2.setText(mDatas.get(position).getScore());
            et_preference3.setText(mDatas.get(position).getCost());
        }

        //下拉列表的事件
        spinner_preference_kind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curPreferencePosition = position;

                switch (position) {
                    case 0:
                        ll_preference1.setVisibility(View.VISIBLE);
                        ll_preference2.setVisibility(View.GONE);
                        ll_preference3.setVisibility(View.GONE);
                        ll_preference4.setVisibility(View.GONE);
                        et_preference2.setText("");
                        et_preference3.setText("");
                        et_preference4_1.setText("");
                        et_preference4_2.setText("");
                        break;
                    case 1:
                        ll_preference2.setVisibility(View.VISIBLE);
                        ll_preference1.setVisibility(View.GONE);
                        ll_preference3.setVisibility(View.GONE);
                        ll_preference4.setVisibility(View.GONE);
                        et_preference1_1.setText("");
                        et_preference1_2.setText("");
                        et_preference3.setText("");
                        et_preference4_1.setText("");
                        et_preference4_2.setText("");
                        break;
                    case 2:
                        ll_preference3.setVisibility(View.VISIBLE);
                        ll_preference1.setVisibility(View.GONE);
                        ll_preference2.setVisibility(View.GONE);
                        ll_preference4.setVisibility(View.GONE);
                        et_preference1_1.setText("");
                        et_preference1_2.setText("");
                        et_preference2.setText("");
                        et_preference4_1.setText("");
                        et_preference4_2.setText("");
                        break;
                    case 3:
                        ll_preference4.setVisibility(View.VISIBLE);
                        ll_preference1.setVisibility(View.GONE);
                        ll_preference2.setVisibility(View.GONE);
                        ll_preference3.setVisibility(View.GONE);
                        et_preference1_1.setText("");
                        et_preference1_2.setText("");
                        et_preference2.setText("");
                        et_preference3.setText("");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        keyboard.bindEditTextViews(getWindow(), et_preference1_1, et_preference1_2, et_preference2,et_preference3,et_preference4_1,et_preference4_2);


        et_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    keyboard.setVisibility(View.GONE);
                }
                else{
                    keyboard.setVisibility(View.VISIBLE);
                    hideKeyboard(VipPreferenceActivity.this, v);
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString().trim();
                String min = et_preference1_1.getText().toString();
                if (3 == curPreferencePosition) {
                    min = et_preference4_1.getText().toString();

                    KLog.w("position=3-->min:" + min);
                }
                String commision = et_preference1_2.getText().toString();
                String score = et_preference2.getText().toString();
                String cost = et_preference3.getText().toString();
                String gift = et_preference4_2.getText().toString();


                // 名称不能为空校验
                if (name.isEmpty()){
                    Toast.makeText(VipPreferenceActivity.this, "优惠项名称不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                //上传优惠：要先进行表单校验
                switch (curPreferencePosition) {
                    case 0://上传第一种优惠

                        if (min.isEmpty() || commision.isEmpty()) {
                            Toast.makeText(VipPreferenceActivity.this, "内容不能为空！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        uploadPreference(name, "1", min, commision, score, cost, gift, position);
                        break;

                    case 1://上传第二种优惠

                        if (score.isEmpty()) {
                            Toast.makeText(VipPreferenceActivity.this, "内容不能为空！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        uploadPreference(name, "2", min, commision, score, cost, gift, position);
                        break;

                    case 2://上传第三种优惠

                        if (cost.isEmpty() || Double.parseDouble(cost) > 1 || Double.parseDouble(cost) <= 0) {
                            Toast.makeText(VipPreferenceActivity.this, "内容输入不合法！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        uploadPreference(name, "3", min, commision, score, cost, gift, position);
                        break;
                    case 3://上传第四种优惠
                        if (min.isEmpty() || gift.isEmpty()) {
                            KLog.i(min + ", gift:" + gift);
                            Toast.makeText(VipPreferenceActivity.this, "内容不能为空！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        uploadPreference(name, "4", min, commision, score, cost, gift, position);
                        break;
                }

                dialog.dismiss();
            }
        });
    }

    public static void hideKeyboard(Activity activity, View viewToHide) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(viewToHide.getWindowToken(), 0);
    }

    // 上传优惠项数据。用于优惠项的新增和修改
    private void uploadPreference(String name, String costtype, String min, String commision, String score, String cost, String gift, int position) {
        String type = "";
        String id = "";

        if (position == -1) {//新增
            type = "2";
            id = "";
        } else {//修改
            type = "3";
            id = mDatas.get(position).getId();
        }

        UserApi.addAndModifyPreference(TAG, token, shopId, type, name, costtype, min, commision, score, cost, id, gift,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        KLog.json(responseStr);
                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");
                            Toast.makeText(VipPreferenceActivity.this, msg, Toast.LENGTH_SHORT).show();

                            //重新加载数据
                            loadPreferenceData();
                        } catch (JSONException e) {
                            e.printStackTrace();
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
