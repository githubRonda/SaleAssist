package com.ronda.saleassist.activity.member;

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
import android.widget.Button;
import android.widget.EditText;
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
import com.ronda.saleassist.bean.PeisongCost;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.utils.ToastUtils;
import com.ronda.saleassist.view.DigitKeyboardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 配送费设置
 */
public class PeiSongCostActivity extends BaseActivty {

    private String token = SPUtils.getString(AppConst.TOKEN, "");
    private String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerView;
    private List<PeisongCost> mDatas = new ArrayList<>();
    private PeisongCostAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pei_song_cost);

        initToolbar("配送费", true);

        initView();

        initEvent();

        loadData();
    }


    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));
        mSwipeRefreshLayout.setRefreshing(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new PeisongCostAdapter(mDatas);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    private void initEvent() {

        //下拉刷新事件
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        //设置删除按钮的点击事件
        mAdapter.setOnDeleteBtnClickListener(new PeisongCostAdapter.OnDeleteBtnClickListener() {
            @Override
            public void onDeleteBtnClick(View itemView, RecyclerView.ViewHolder holder, int position) {
                showDeleteConfirmDialog(position);
            }
        });

        // 长按修改
        mAdapter.setOnItemLongClickListener(new BaseRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, RecyclerViewHolder holder, int position) {
                showUpdateDialog(position);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vip_peisong_cost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                showAddDialog();
                break;
        }
        return true;
    }

    /*************** 显示新增，修改， 删除对话框 ***************************/

    /**
     * 显示新增配送费的对话框
     */
    private void showAddDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();


        dialog.getWindow().setContentView(R.layout.dialog_peisong_cost);

        TextView txt_title = (TextView) dialog.getWindow().findViewById(R.id.txt_title);

        final EditText edit_min = (EditText) dialog.getWindow().findViewById(R.id.edit_extra_min);
        final EditText edit_cost = (EditText) dialog.getWindow().findViewById(R.id.edit_extra_cost);
        DigitKeyboardView keyboard = (DigitKeyboardView) dialog.getWindow().findViewById(R.id.ll_keyboard);

        Button btn_cancel = (Button) dialog.getWindow().findViewById(R.id.btn_cancel);
        Button btn_confirm = (Button) dialog.getWindow().findViewById(R.id.btn_confirm);

        txt_title.setText("新建配送费");

        keyboard.bindEditTextViews(getWindow(), edit_min, edit_cost);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String min = edit_min.getText().toString().trim();
                String cost = edit_cost.getText().toString().trim();
                if (min.isEmpty()) {
                    edit_min.setError("不能为空！");
                    return;
                }
                if (cost.isEmpty()) {
                    edit_cost.setError("不能为空！");
                    return;
                }

                addExtraCost(min, cost);

                dialog.dismiss();
            }
        });

    }

    // 显示修改配送费时的对话框
    private void showUpdateDialog(final int position) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();


        dialog.getWindow().setContentView(R.layout.dialog_peisong_cost);

        TextView txt_title = (TextView) dialog.getWindow().findViewById(R.id.txt_title);

        final EditText edit_min = (EditText) dialog.getWindow().findViewById(R.id.edit_extra_min);
        final EditText edit_cost = (EditText) dialog.getWindow().findViewById(R.id.edit_extra_cost);
        DigitKeyboardView keyboard = (DigitKeyboardView) dialog.getWindow().findViewById(R.id.ll_keyboard);

        Button btn_cancel = (Button) dialog.getWindow().findViewById(R.id.btn_cancel);
        Button btn_confirm = (Button) dialog.getWindow().findViewById(R.id.btn_confirm);


        txt_title.setText("修改配送费");
        edit_min.setText(mDatas.get(position).getMin());
        edit_cost.setText(mDatas.get(position).getCost());

        keyboard.bindEditTextViews(getWindow(), edit_min, edit_cost);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String min = edit_min.getText().toString().trim();
                String cost = edit_cost.getText().toString().trim();
                if (min.isEmpty()) {
                    edit_min.setError("不能为空！");
                    return;
                }
                if (cost.isEmpty()) {
                    edit_cost.setError("不能为空！");
                    return;
                }

                updateExtraCost(mDatas.get(position).getId(), min, cost);

                dialog.dismiss();
            }
        });

    }

    // 显示删除配送费的确认对话框
    private void showDeleteConfirmDialog(final int position) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
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
                deleteExtraCost(mDatas.get(position).getId());
            }
        });


    }


    /*********** 访问后台接口部分 ****************/

    /**
     * 获取配送费信息
     */
    private void loadData() {
        UserApi.getExtraCost(TAG, token, shopId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        mSwipeRefreshLayout.setRefreshing(false);

                        try {
                            JSONObject response = new JSONObject(responseStr);
                            JSONArray data = response.getJSONArray("data");

                            List<PeisongCost> list = new Gson().fromJson(data.toString(), new TypeToken<List<PeisongCost>>() {
                            }.getType());
                            mDatas.clear();
                            mDatas.addAll(list);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            mAdapter.notifyDataSetChanged();
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

    //访问后台，新增配送费
    private void addExtraCost(String min, String cost) {
        UserApi.addExtraCost(TAG, token, shopId, min, cost,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");

                            if (status == 1) {
                                loadData();
                            }
                            Toast.makeText(PeiSongCostActivity.this, msg, Toast.LENGTH_SHORT).show();
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


    //访问后台，修改配送费
    private void updateExtraCost(String id, String min, String cost) {
        UserApi.updateExtraCost(TAG, token, shopId, id, min, cost,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");

                            if (status == 1) {
                                loadData();
                            }
                            Toast.makeText(PeiSongCostActivity.this, msg, Toast.LENGTH_SHORT).show();
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

    //访问后台，删除指定配送费
    private void deleteExtraCost(String id) {
        UserApi.deleteExtraCost(TAG, token, shopId, id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");

                            if (status == 1) {
                                loadData();
                            }
                            Toast.makeText(PeiSongCostActivity.this, msg, Toast.LENGTH_SHORT).show();
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

    /**
     * 适配器，用于显示本类中的配送费列表
     */
    public static class PeisongCostAdapter extends BaseRecyclerViewAdapter<PeisongCost> {

        private OnDeleteBtnClickListener mOnDeleteBtnClickListener;

        public PeisongCostAdapter(List<PeisongCost> list) {
            super(list, R.layout.list_item_peisong_cost);
        }

        @Override
        public void bindDataToItemView(final RecyclerViewHolder holder, final int position) {
            holder.setText(R.id.txt_peisong_info, "消费满" + mDatas.get(position).getMin() + "元， 配送费为" + mDatas.get(position).getCost() + "元");

            if (mOnDeleteBtnClickListener != null) {
                holder.setOnClickListener(R.id.img_del, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnDeleteBtnClickListener.onDeleteBtnClick(v, holder, position);
                    }
                });
            }
        }


        public void setOnDeleteBtnClickListener(OnDeleteBtnClickListener onDeleteBtnClickListener) {
            mOnDeleteBtnClickListener = onDeleteBtnClickListener;
        }

        public interface OnDeleteBtnClickListener {
            void onDeleteBtnClick(View itemView, RecyclerView.ViewHolder holder, int position);
        }
    }
}
