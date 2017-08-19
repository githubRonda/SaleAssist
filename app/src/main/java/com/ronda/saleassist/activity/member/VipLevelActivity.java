package com.ronda.saleassist.activity.member;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.ronda.saleassist.bean.VipLevelBean;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VipLevelActivity extends BaseActivty {


    private int levelCount = 0;
    private ImageView iv_show;  //用于显示无数据的时的背景
    private RecyclerView mLevelRecyclerView;
    private List<VipLevelBean> mLevelDatas = new ArrayList<>();
    private VipLevelAdapter mLevelAdapter;


    private String token = SPUtils.getString(AppConst.TOKEN, "");
    private String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_level);

        initToolbar("会员等级", true);

        initView();

        initEvent();

        initData();
    }


    private void initView() {
        iv_show = (ImageView) findViewById(R.id.iv_show);

        mLevelRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLevelAdapter = new VipLevelAdapter(mLevelDatas);
        mLevelRecyclerView.setAdapter(mLevelAdapter);
        mLevelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLevelRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLevelRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

    }


    private void initEvent() {
        mLevelAdapter.setOnItemLongClickListener(new BaseRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, RecyclerViewHolder holder, int position) {
                modifyVipLevelDialog(position);
            }
        });
    }


    private void initData() {
        getVipLevel();
    }


    //获取会员等级信息
    private void getVipLevel() {
        UserApi.queryVipLevel(TAG, token, shopId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        iv_show.setVisibility(View.GONE);
                        KLog.json(responseStr);
                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");

                            Toast.makeText(VipLevelActivity.this, msg, Toast.LENGTH_SHORT).show();

                            if (status == 1) {
                                JSONArray data = response.getJSONArray("data");
                                Gson gson = new Gson();
                                List<VipLevelBean> levels = gson.fromJson(data.toString(), new TypeToken<List<VipLevelBean>>() {
                                }.getType());

                                levelCount = levels.size();

                                mLevelDatas.clear();
                                mLevelDatas.addAll(levels);
                                mLevelAdapter.notifyDataSetChanged();
                            } else {
                                iv_show.setVisibility(View.VISIBLE);
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
                        iv_show.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vip_level, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                showAddVipLevelDialog();
                break;
        }
        return true;
    }


    //添加会员等级时的对话框
    private void modifyVipLevelDialog(final int position) {
        final AlertDialog alertDialogAdd = new AlertDialog.Builder(this).create();
        alertDialogAdd.show();

        alertDialogAdd.setCanceledOnTouchOutside(false);

        alertDialogAdd.getWindow().setContentView(R.layout.dialog_add_vip_level);
        alertDialogAdd.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialogAdd.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


        Button btn_confirm = (Button) alertDialogAdd.getWindow().findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) alertDialogAdd.getWindow().findViewById(R.id.btn_cancel);


        final EditText edit_name = (EditText) alertDialogAdd.getWindow().findViewById(R.id.edit_name);
        final EditText edit_intro = (EditText) alertDialogAdd.getWindow().findViewById(R.id.edit_intro);

        RecyclerView recycler_view_cost = (RecyclerView) alertDialogAdd.getWindow().findViewById(R.id.recycler_view_cost);
        final List<PreferenceBean> datas = new ArrayList<>();
        VipPreferenceListAdapter adapter = new VipPreferenceListAdapter(datas);
        recycler_view_cost.setAdapter(adapter);
        getPreferenceData(datas, adapter);
        recycler_view_cost.setLayoutManager(new LinearLayoutManager(this));
        recycler_view_cost.setItemAnimator(new DefaultItemAnimator());
        recycler_view_cost.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, RecyclerViewHolder holder, int position) {
                CheckBox checkBox = holder.getView(R.id.check_box);
                checkBox.toggle();
                datas.get(position).setChecked(checkBox.isChecked());
            }
        });

        edit_name.setText(mLevelDatas.get(position).getName());
        edit_intro.setText(mLevelDatas.get(position).getIntro());

        //选中已有的优惠项，这里还需要再次改进

//        for (int i = 0; i < datas.size(); i++) {
//            for (int j = 0; j < mLevelDatas.size(); j++) {
//                if (datas.get(i).getId().equals(mLevelDatas.get(j).getId())) {
//                    datas.get(i).setChecked(true);
//                }
//            }
//        }
//        adapter.notifyDataSetChanged();

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAdd.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 先校验，再提交至后台
                String name = edit_name.getText().toString().trim();
                String intro = edit_intro.getText().toString().trim();
                String costIds = ""; //选中的优惠项的id
                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).isChecked()) {
                        costIds += datas.get(i).getId() + ",";
                    }
                }
                if (name.isEmpty()) {
                    Toast.makeText(VipLevelActivity.this, "名称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (costIds.length() == 0) {
                    Toast.makeText(VipLevelActivity.this, "未选中优惠项", Toast.LENGTH_SHORT).show();
                    return;
                }
                costIds = costIds.substring(0, costIds.length() - 1);//去掉最后一个逗号

                UserApi.updateVipLevel(TAG, token, shopId, mLevelDatas.get(position).getId(), name, intro, costIds,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String responseStr) {
                                KLog.json(responseStr);
                                try {
                                    JSONObject response = new JSONObject(responseStr);
                                    int stauts = response.getInt("status");
                                    String msg = response.getString("msg");
                                    Toast.makeText(VipLevelActivity.this, msg, Toast.LENGTH_SHORT).show();

                                    if (stauts == 1) {
                                        initData();
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

                alertDialogAdd.dismiss();
            }
        });
    }


    //添加会员等级时的对话框
    private void showAddVipLevelDialog() {
        final AlertDialog alertDialogAdd = new AlertDialog.Builder(this).create();
        alertDialogAdd.show();

        alertDialogAdd.setCanceledOnTouchOutside(false);

        alertDialogAdd.getWindow().setContentView(R.layout.dialog_add_vip_level);
        alertDialogAdd.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialogAdd.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


        Button btn_confirm = (Button) alertDialogAdd.getWindow().findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) alertDialogAdd.getWindow().findViewById(R.id.btn_cancel);


        final EditText edit_name = (EditText) alertDialogAdd.getWindow().findViewById(R.id.edit_name);
        final EditText edit_intro = (EditText) alertDialogAdd.getWindow().findViewById(R.id.edit_intro);

        RecyclerView recycler_view_cost = (RecyclerView) alertDialogAdd.getWindow().findViewById(R.id.recycler_view_cost);
        final List<PreferenceBean> datas = new ArrayList<>();
        VipPreferenceListAdapter adapter = new VipPreferenceListAdapter(datas);
        recycler_view_cost.setAdapter(adapter);
        getPreferenceData(datas, adapter);
        recycler_view_cost.setLayoutManager(new LinearLayoutManager(this));
        recycler_view_cost.setItemAnimator(new DefaultItemAnimator());
        recycler_view_cost.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, RecyclerViewHolder holder, int position) {
                CheckBox checkBox = holder.getView(R.id.check_box);
                checkBox.toggle();
                datas.get(position).setChecked(checkBox.isChecked());
            }
        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAdd.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 先校验，再提交至后台
                String name = edit_name.getText().toString().trim();
                String intro = edit_intro.getText().toString().trim();
                String costIds = ""; //选中的优惠项的id
                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).isChecked()) {
                        costIds += datas.get(i).getId() + ",";
                    }
                }
                if (name.isEmpty()) {
                    Toast.makeText(VipLevelActivity.this, "名称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (costIds.length() == 0) {
                    Toast.makeText(VipLevelActivity.this, "未选中优惠项", Toast.LENGTH_SHORT).show();
                    return;
                }
                costIds = costIds.substring(0, costIds.length() - 1);//去掉最后一个逗号

                UserApi.addVipLevel(TAG, token, shopId, ++levelCount, name, intro, costIds,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String responseStr) {
                                KLog.json(responseStr);
                                try {
                                    JSONObject response = new JSONObject(responseStr);
                                    int stauts = response.getInt("status");
                                    String msg = response.getString("msg");
                                    Toast.makeText(VipLevelActivity.this, msg, Toast.LENGTH_SHORT).show();

                                    if (stauts == 1) {
                                        initData();
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


                alertDialogAdd.dismiss();
            }
        });
    }

    // 访问后台，获取优惠项数据，然后装进 datas中
    private void getPreferenceData(final List<PreferenceBean> datas, final VipPreferenceListAdapter adapter) {
        UserApi.getPreference(TAG, token, shopId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
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

                                datas.clear();
                                datas.addAll(preferences);
                                adapter.notifyDataSetChanged();
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

    // 添加会员等级时对话框中的列表适配器
    class VipPreferenceListAdapter extends BaseRecyclerViewAdapter<PreferenceBean> {

        public VipPreferenceListAdapter(List<PreferenceBean> list) {
            super(list, R.layout.list_item_vip_level_preference);
        }

        @Override
        public void bindDataToItemView(RecyclerViewHolder holder, int position) {
            holder.setText(R.id.tv_name, mDatas.get(position).getName());
            holder.setChecked(R.id.check_box, mDatas.get(position).isChecked());
        }
    }


    //显示所有会员等级的适配器
    public class VipLevelAdapter extends BaseRecyclerViewAdapter<VipLevelBean> {

        public VipLevelAdapter(List list) {
            super(list, R.layout.list_item_vip_level);
        }

        @Override
        public void bindDataToItemView(RecyclerViewHolder holder, int position) {
            holder.setText(R.id.tv_level_name, mDatas.get(position).getName());

            PreferenceBean[] beans = mDatas.get(position).getAllcost();
            if (beans != null) {
                //提取之歌会员等级中的优惠项信息
                String preference = "";
                for (int i = 0; i < beans.length; i++) {
                    if (beans[i] != null) {
                        if (i == 0) {
                            preference += beans[i].getName();
                        } else {
                            preference += "," + beans[i].getName();
                        }
                    }
                }
                holder.setText(R.id.tv_preference, preference);
            }

        }
    }

}
