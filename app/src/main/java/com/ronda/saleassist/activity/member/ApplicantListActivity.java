package com.ronda.saleassist.activity.member;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.divider.DividerItemDecoration;
import com.ronda.saleassist.adapter.simple.CommonAdapter;
import com.ronda.saleassist.adapter.simple.ViewHolder;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.bean.ApplyInfoBean;
import com.ronda.saleassist.bean.VipLevelBean;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Ronda(1575558177@qq.com)
 * Date: 2016/10/24
 */
public class ApplicantListActivity extends BaseActivty {

    private RecyclerView mRecyclerView;
    private ArrayList<ApplyInfoBean> mDatas = new ArrayList<>();
    private ApplicantListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private String token = SPUtils.getString(AppConst.TOKEN, "");
    private String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");

    private int curSelectLevelPosition = 0; //用于记录当同意申请时，选择的会员等级的position
    private List<VipLevelBean> spinnerLevelDatas = new ArrayList<VipLevelBean>(); //用于存储，当同意申请时，显示的会员等级的信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_list);

        initToolbar("新会员", true);

        initView();

        initEvent();

        initData();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ApplicantListAdapter(mDatas);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    private void initData() {
        //先加载会员等级信息，成功后再加载申请人的信息
        getVipLevel();
//        getApplyInfo();
    }

    private void initEvent() {
        mAdapter.setOnOperateListner(new ApplicantListAdapter.OnOperateListner() {

            @Override
            public void agree(View v, final int position) {
                final AlertDialog dialog = new AlertDialog.Builder(ApplicantListActivity.this).create();
                dialog.show();

                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setContentView(R.layout.dialog_choose_vip_level);

                Spinner spinner_vip_level = (Spinner) dialog.getWindow().findViewById(R.id.spinner_vip_level);

                Button btn_cancel = (Button) dialog.getWindow().findViewById(R.id.btn_cancel);
                Button btn_confirm = (Button) dialog.getWindow().findViewById(R.id.btn_confirm);


                MySpinnerAdapter adapter = new MySpinnerAdapter(ApplicantListActivity.this, spinnerLevelDatas, android.R.layout.simple_list_item_1);
                spinner_vip_level.setAdapter(adapter);

                spinner_vip_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        curSelectLevelPosition = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
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
                        try {
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.put("id", mDatas.get(position).getId());
                            jsonObj.put("status", 2);
                            jsonObj.put("level", spinnerLevelDatas.get(curSelectLevelPosition).getId());

                            String data = jsonObj.toString();

                            //上传信息
                            UserApi.applyProcess(TAG, token, shopId, data,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String responseStr) {
                                            try {
                                                JSONObject response = new JSONObject(responseStr);
                                                int status = response.getInt("status");
                                                String msg = response.getString("msg");
                                                Toast.makeText(ApplicantListActivity.this, msg, Toast.LENGTH_SHORT).show();


                                                if (status == 1) {
                                                    //重新加载一下数据
                                                    getApplyInfo();
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void refuse(View v, final int position) {
                final AlertDialog dialog = new AlertDialog.Builder(ApplicantListActivity.this).create();
                dialog.show();

                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setContentView(R.layout.dialog_common_two_btn_tip);

                TextView tv_title = (TextView) dialog.getWindow().findViewById(R.id.tv_title);
                TextView tv_msg = (TextView) dialog.getWindow().findViewById(R.id.tv_msg);
                Button btn_cancel = (Button) dialog.getWindow().findViewById(R.id.btn_cancel);
                Button btn_confirm = (Button) dialog.getWindow().findViewById(R.id.btn_confirm);

                tv_title.setText("提示");
                tv_msg.setText("确定要拒绝吗？");

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

                        try {
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.put("id",  mDatas.get(position).getId());
                            jsonObj.put("status", 3);
//                            jsonObj.put("level", levelId);

                            String data = jsonObj.toString();

                            //上传信息
                            UserApi.applyProcess(TAG, token, shopId, data,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String responseStr) {
                                            try {
                                                JSONObject response = new JSONObject(responseStr);
                                                int status = response.getInt("status");
                                                String msg = response.getString("msg");
                                                Toast.makeText(ApplicantListActivity.this, msg, Toast.LENGTH_SHORT).show();


                                                if (status == 1) {
                                                    //重新加载一下数据
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }

            @Override
            public void defriend(View v, final int position) {
                final AlertDialog dialog = new AlertDialog.Builder(ApplicantListActivity.this).create();
                dialog.show();

                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setContentView(R.layout.dialog_common_two_btn_tip);

                TextView tv_title = (TextView) dialog.getWindow().findViewById(R.id.tv_title);
                TextView tv_msg = (TextView) dialog.getWindow().findViewById(R.id.tv_msg);
                Button btn_cancel = (Button) dialog.getWindow().findViewById(R.id.btn_cancel);
                Button btn_confirm = (Button) dialog.getWindow().findViewById(R.id.btn_confirm);

                tv_title.setText("警告");
                tv_msg.setText("确定要加入黑名单吗？");

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

                        try {
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.put("id",  mDatas.get(position).getId());
                            jsonObj.put("status", 4);
//                            jsonObj.put("level", levelId);

                            String data = jsonObj.toString();

                            //上传信息
                            UserApi.applyProcess(TAG, token, shopId, data,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String responseStr) {
                                            try {
                                                JSONObject response = new JSONObject(responseStr);
                                                int status = response.getInt("status");
                                                String msg = response.getString("msg");
                                                Toast.makeText(ApplicantListActivity.this, msg, Toast.LENGTH_SHORT).show();


                                                if (status == 1) {
                                                    //重新加载一下数据
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }




    //获取申请会员的信息
    private void getApplyInfo() {
        UserApi.applyMember(TAG, token, shopId,
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
                                ApplyInfoBean[] beans = gson.fromJson(data.toString(), ApplyInfoBean[].class);
                                mDatas.clear();
                                mAdapter.add(beans);
                                mAdapter.notifyDataSetChanged();
                            }
                            else if (status == 0){
                                mDatas.clear();
                                mAdapter.notifyDataSetChanged();
                                Toast.makeText(ApplicantListActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ApplicantListActivity.this, R.string.resolver_error, Toast.LENGTH_SHORT).show();
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


    //获取会员等级的信息
    private void getVipLevel() {
        UserApi.queryVipLevel(TAG, token, shopId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        KLog.json(responseStr);
                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");

//                    Toast.makeText(ApplicantListActivity.this, msg, Toast.LENGTH_SHORT).show();

                            if (status == 1) {
                                JSONArray data = response.getJSONArray("data");
                                Gson gson = new Gson();
                                List<VipLevelBean> levels = gson.fromJson(data.toString(), new TypeToken<List<VipLevelBean>>() {
                                }.getType());

                                spinnerLevelDatas.clear();
                                spinnerLevelDatas.addAll(levels);
//                        adapter.notifyDataSetChanged();

                                //再加载申请人信息
                                getApplyInfo();
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

    class MySpinnerAdapter extends CommonAdapter<VipLevelBean> {

        public MySpinnerAdapter(Context context, List<VipLevelBean> datas, int layoutId) {
            super(context, datas, layoutId);//@android:id/text1
        }

        @Override
        public void convert(ViewHolder holder, VipLevelBean bean) {
            holder.setText(android.R.id.text1, bean.getName());
        }
    }
}
