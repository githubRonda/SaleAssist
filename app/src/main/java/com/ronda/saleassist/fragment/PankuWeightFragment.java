package com.ronda.saleassist.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.PankuAdapter;
import com.ronda.saleassist.adapter.simple.ViewHolder;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.api.volley.VolleyUtil;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.BaseFragment;
import com.ronda.saleassist.bean.PankuBean;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lrd on 0030,2016/9/30.
 */
public class PankuWeightFragment extends BaseFragment implements View.OnClickListener {

    private ListView listView;
    private Button   btnShowAll, btnClear, btnToNextDay, btnSelectAll;

    private ArrayList<PankuBean> mDatas = new ArrayList<>();//上半部的称重类的数据
    private PankuAdapter mAdapter; //上半部的的适配器

    private ArrayList<String> noStockDatas = new ArrayList<>(); //下半部的ListView的数据
    private ArrayAdapter<String> noStockAdapter;  // 下半部的ListView的适配器

    private ArrayList<PankuBean> bargoodsDatas = new ArrayList<>();

    private boolean isSelectAll = true;

    private ListView listviewNoStock;


    private String token  = SPUtils.getString(AppConst.TOKEN, "");
    private String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_panku_weight, container, false);
    }

    @Override
    public void init(View view) {

        listView = (ListView) view.findViewById(R.id.list_view);
        listviewNoStock = (ListView) view.findViewById(R.id.listview_no_stock);

        btnShowAll = (Button) view.findViewById(R.id.btn_show_all);
        btnClear = (Button) view.findViewById(R.id.btn_clear);
        btnToNextDay = (Button) view.findViewById(R.id.btn_tonext);
        btnSelectAll = (Button) view.findViewById(R.id.btn_select_all);


        mAdapter = new PankuAdapter(getActivity(), mDatas, R.layout.item_panku);
        noStockAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, noStockDatas);

        listView.setAdapter(mAdapter);

        initDatas();

        btnShowAll.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnToNextDay.setOnClickListener(this);
        btnSelectAll.setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder holder = (ViewHolder) view.getTag();
                CheckBox checkBox = holder.getView(R.id.checkbox);
                checkBox.toggle();
                //mAdapter.getCheckedMap().put(position, checkBox.isChecked());
                mDatas.get(position).setChecked(checkBox.isChecked());
                System.out.println(mDatas.get(position).isChecked());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_show_all) {
            listviewNoStock.setAdapter(noStockAdapter);
        } else if (id == R.id.btn_clear) {
            manageStock(2);//type = 2 清空库存
        } else if (id == R.id.btn_select_all) {
            if (isSelectAll) {
                for (int i = 0; i < mDatas.size(); i++) {
                    mDatas.get(i).setChecked(true);
                }
                btnSelectAll.setText("取消");
            } else {
                for (int i = 0; i < mDatas.size(); i++) {
                    mDatas.get(i).setChecked(false);
                }
                btnSelectAll.setText("全选");
            }
            mAdapter.notifyDataSetChanged();

            isSelectAll = !isSelectAll;
        } else if (id == R.id.btn_tonext) {
            manageStock(1);//type = 1 留到下一天
        }
    }

    public ArrayList<PankuBean> getBargoodsDatas() {
        return bargoodsDatas;
    }

    private void manageStock(int type) {

        String manageData = "";
        JSONArray jsonArray = new JSONArray();

        try {
            for (PankuBean bean : mDatas) {
                if (bean.isChecked()) {
                    JSONObject json = new JSONObject();
                    json.put("gid", bean.getGid());
                    json.put("checkstatus", bean.getCheckstatus());

                    jsonArray.put(json);
                }
            }

            KLog.json(jsonArray.toString());
            manageData = jsonArray.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        UserApi.manageStock(TAG, token, shopId, manageData, type, "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        KLog.json(responseStr);

                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");

                            if (status == 1) {
                                initDatas();
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


    private void initDatas() {
        UserApi.getStockInfo(TAG, token, shopId, "1", "1",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        KLog.json(responseStr);

                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");

                            if (status == 1) {

                                mDatas.clear();
                                noStockDatas.clear();

                                JSONObject data = response.getJSONObject("data");
                                JSONArray lastgoods = data.getJSONArray("lastgoods");
                                JSONArray noStockGoods = data.getJSONArray("nostockgoods");


                                for (int i = 0; i < lastgoods.length(); i++) {
                                    String gid = lastgoods.getJSONObject(i).getString("gid");
                                    String name = lastgoods.getJSONObject(i).getString("name");
                                    String isBar = lastgoods.getJSONObject(i).getString("isbar");
                                    int check = lastgoods.getJSONObject(i).getInt("check");
                                    String checkstatus = lastgoods.getJSONObject(i).getString("checkstatus");
                                    String percent = lastgoods.getJSONObject(i).getString("percent");
                                    String stock = lastgoods.getJSONObject(i).getString("stock");
                                    String intime = lastgoods.getJSONObject(i).getString("intime");

                                    if (isBar == null || isBar.isEmpty() || isBar.equals("0")) {
                                        mDatas.add(new PankuBean(gid, name, isBar, check, checkstatus, percent, stock, intime));
                                    } else {
                                        bargoodsDatas.add(new PankuBean(gid, name, isBar, check, checkstatus, percent, stock, intime));

                                    }

                                }

                                for (int j = 0; j < noStockGoods.length(); j++) {
                                    String isBar = noStockGoods.getJSONObject(j).getString("isbar");
                                    if (isBar == null || isBar.isEmpty() || isBar.equals("0")) {
                                        noStockDatas.add(noStockGoods.getJSONObject(j).getString("name"));
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mAdapter.notifyDataSetChanged();
                        noStockAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast(R.string.no_respnose);
                    }
                });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            mAdapter.notifyDataSetChanged();
            noStockAdapter.notifyDataSetChanged();
        }
    }
}
