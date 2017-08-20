package com.ronda.saleassist.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ronda.saleassist.R;
import com.ronda.saleassist.activity.MainActivity;
import com.ronda.saleassist.adapter.CategoryAdapter;
import com.ronda.saleassist.adapter.SubCategoryStockAdapter;
import com.ronda.saleassist.adapter.simple2.BaseRecyclerViewAdapter;
import com.ronda.saleassist.adapter.simple2.RecyclerViewHolder;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.base.BaseFragment;
import com.ronda.saleassist.base.MyApplication;
import com.ronda.saleassist.bean.Category;
import com.ronda.saleassist.bean.SubCategory;
import com.ronda.saleassist.utils.Base64Encoder;
import com.ronda.saleassist.utils.ToastUtils;
import com.ronda.saleassist.view.RadioGroupEx;
import com.socks.library.KLog;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.loader.view.DropEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



/**
 * 称重类入库的Fragment
 * Created by lrd on 0014,2016/9/14.
 */
public class WeightFragment extends BaseFragment implements View.OnClickListener {


    private static final int SELECT_PICTURE    = 1;
    private static final int SELECT_CAMER      = 2;
    private static final int CROP_REQUEST_CODE = 3;


    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerview_category;
    private RecyclerView recyclerview_subcategory;
    private GridLayoutManager gridLayoutManager;

    private CategoryAdapter         categoryAdapter;
    private SubCategoryStockAdapter subCategoryStockAdapter;


    public static int SELECTPOSITION = 0;

    private int lastVisibleItem;

    private int pageCount = 1; //分页加载的页数
    private int pageSize = 30; //每页的大小


    private ArrayList<Category>    mListDataCategory   = new ArrayList<>(); //装载各个主分类的信息，在initCategoryData()方法中，从本地数据库中取出所有主分类的数据，并给其赋值
    private ArrayList<SubCategory> listDataSubCategory = new ArrayList<>();

    private ImageView img_pic;
    private String imgPath;
    private Bitmap mBitmap;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weight, container, false);
    }

    @Override
    public void init(View view) {

        initView(view);

        initEvent();

        //展示主类别数据, 成功后在展示子类别数据
        initCategoryData();
    }

    private void initEvent() {

        //设置下拉刷新的回调事件
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //初始化子分类（第一页 30条数据）
                if (mListDataCategory.size() != 0) {
                    //initSubCategoryData(mListDataCategory.get(SELECTPOSITION).getId());
                    loadSubCategory(mListDataCategory.get(SELECTPOSITION).getId(), 1);
                    KLog.i("下拉刷新");
                } else {//当没有主分类数据时，下拉时，就应该立即隐藏掉加载动画
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        recyclerview_subcategory.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                KLog.i("onScrollStateChanged");

                //上拉加载更多
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == subCategoryStockAdapter.getItemCount()) {//数据为空的时候，也不会执行.当有数据且滑倒底部时才执行

                    loadSubCategory(mListDataCategory.get(SELECTPOSITION).getId(), pageCount + 1);

                    KLog.i("上拉加载更多, pageCount:" + pageCount);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();

                KLog.i("onScrolled， lastVisibleItem：" + lastVisibleItem);
            }
        });
    }

    private void initView(View view) {

        recyclerview_category = (RecyclerView) view.findViewById(R.id.recyclerview_category);
        recyclerview_subcategory = (RecyclerView) view.findViewById(R.id.recycler_view);


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE);//控制圆形动画的颜色

        // 调整进度条距离屏幕顶部的距离
        swipeRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        recyclerview_subcategory.setHasFixedSize(true);
        recyclerview_subcategory.setItemAnimator(new DefaultItemAnimator());
        recyclerview_category.setHasFixedSize(true);
        recyclerview_category.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerview_subcategory.clearOnScrollListeners();
    }


    private void initCategoryData() {

        setCategoryAdapter(mListDataCategory);

        //获取后台主分类数据
        UserApi.getCategoryInfo(TAG, token, shopId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        KLog.json(responseStr);
                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");
                            JSONArray arr = response.getJSONArray("data");

                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                            if (status == 1) {
                                KLog.i("主分类访问完毕：" + System.currentTimeMillis());
                                mListDataCategory.clear();
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject obj = arr.getJSONObject(i);
                                    String id = obj.getString("id");
                                    String name = obj.getString("name");
                                    String notes = obj.getString("notes");

                                    mListDataCategory.add(new Category(id, name, notes));
                                }

                                categoryAdapter.notifyDataSetChanged();

                                //再初始化第一个主分类下的子类别的数据
                                if (mListDataCategory.size() != 0) {
                                    loadSubCategory(mListDataCategory.get(SELECTPOSITION).getId(), 1);
                                }

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

    private void loadSubCategory(String categoryId, final int page) {
        if (page == 1) { //初始化
            pageCount = 1;
            listDataSubCategory.clear(); //初始化需要clear,但是加载更多时不需要
            setSubCategoryAdapter(listDataSubCategory);//主要是为了显示添加菜品这个item
        }

        //设置显示加载数据的动画
        swipeRefreshLayout.setRefreshing(true);

        //Client.cancelRequests(getActivity(), true);//关闭之前的请求，防止用户来回切换主分类时数据出错

        //UserApi.cancelAll();//写在了UserApi中
        UserApi.getGoodsInfo(TAG, token, 1, shopId, categoryId + "", pageSize, page,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        //访问后台结束时关闭加载动画
                        swipeRefreshLayout.setRefreshing(false);

                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");//当status == 9时， msg = 店铺内暂无任何货物

                            if (status == -9) {
                                Toast.makeText(getActivity(), "当前已是全部货物", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }

                            if (status == 1) {

                                KLog.i("子分类加载完毕：" + System.currentTimeMillis());
                                if (page == 1) { //初始化
                                    pageCount = 1;
                                    listDataSubCategory.clear(); //初始化需要clear,但是加载更多时不需要
                                } else {
                                    pageCount++;//加载更多请求成功，pageCount++,status = -9是店铺内暂无任何货物
                                }

                                JSONArray arr = response.getJSONArray("data"); //只有当status=1时，data才有数据。当data为空时，这条语句会抛异常
                                ArrayList<SubCategory> list = new Gson().fromJson(arr.toString(), new TypeToken<ArrayList<SubCategory>>(){}.getType());
                                listDataSubCategory.addAll(list);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            subCategoryStockAdapter.notifyDataSetChanged();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast(R.string.no_respnose);
                        //访问后台结束时关闭加载动画
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }


    private void setCategoryAdapter(final ArrayList<Category> listData) {
        categoryAdapter = new CategoryAdapter(listData);
        recyclerview_category.setAdapter(categoryAdapter);
        recyclerview_category.setLayoutManager(new LinearLayoutManager(getActivity()));
        categoryAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, RecyclerViewHolder holder, int position) {
                if (position < listData.size()) {

                    if (SELECTPOSITION == position) return;
                    if (listData.size() == 0 && shopId == "") {
                        //当shopid为空的时候，需要再次访问后台获取shopid
                        //....
                        Toast.makeText(getActivity(), "请先在网页上进行商户申请", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //点击了相关类别
                    SELECTPOSITION = position;
                    categoryAdapter.notifyDataSetChanged();//加上这句，当点击某个主分类时，背景才会有选中状态，否则的话，一直是第一个被选中
                    //展示子类别数据
                    //initSubCategoryData(mListDataCategory.get(position).getId());
                    loadSubCategory(mListDataCategory.get(position).getId(), 1);
                } else {
                    //点击了“添加分类”，弹出添加对话框
                    showAddCategoryDialog();
                }
            }
        });

        categoryAdapter.setOnItemLongClickListener(new BaseRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, RecyclerViewHolder holder, int position) {
                if (position < listData.size()) {
                    //长按了相关类别，弹出修改和删除的菜单对话框
                    showCategoryMenuDialog(position);
                }
            }
        });
//        categoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View v, int position) {
//                if (position < listData.size()) {
//
//                    if (SELECTPOSITION == position) return;
//                    if (listData.size() == 0 && App.curShopId == "") {
//                        //当shopid为空的时候，需要再次访问后台获取shopid
//                        //....
//                        Toast.makeText(getActivity(), "请先在网页上进行商户申请", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    //点击了相关类别
//                    SELECTPOSITION = position;
//                    categoryAdapter.notifyDataSetChanged();//加上这句，当点击某个主分类时，背景才会有选中状态，否则的话，一直是第一个被选中
//                    //展示子类别数据
//                    //initSubCategoryData(mListDataCategory.get(position).getId());
//                    loadSubCategory(mListDataCategory.get(position).getId(), 1);
//                } else {
//                    //点击了“添加分类”，弹出添加对话框
//                    showAddCategoryDialog();
//                }
//            }
//
//            @Override
//            public void onItemLongClick(View v, int position) {
//                if (position < listData.size()) {
//                    //长按了相关类别，弹出修改和删除的菜单对话框
//                    showCategoryMenuDialog(position);
//                } else {
//                    //长按了“添加分类”，没动作
//                }
//            }
//        });
    }

    private void setSubCategoryAdapter(final ArrayList<SubCategory> listData) {
        subCategoryStockAdapter = new SubCategoryStockAdapter(getActivity(), listData);
        recyclerview_subcategory.setAdapter(subCategoryStockAdapter);
        gridLayoutManager = new GridLayoutManager(getActivity(), 8);
        recyclerview_subcategory.setLayoutManager(gridLayoutManager);
        subCategoryStockAdapter.setOnItemClickListener(new SubCategoryStockAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final int position) {
                if (position < listDataSubCategory.size()) {

                    initRukuDialogEvent(position);

                } else {
                    //点击了“添加子类”，即“添加菜品”弹出对话框
                    showAddSubCategoryDialog();
                }
            }

            @Override
            public void onItemLongClick(View v, int position) {
//                if (position < listDataSubCategory.size()) {
//                    //长按了相关的子类
//                    showSubCategoryMenuDialog(position);
//                } else {
//                    //长按了“添加子类”，没有动作
//                }
            }
        });
    }


    private void initRukuDialogEvent(final int position) {
        final AlertDialog rukuDialog = new AlertDialog.Builder(getActivity()).create();
        rukuDialog.show();

        rukuDialog.getWindow().setContentView(R.layout.dialog_ruku_weight);
        rukuDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        rukuDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        final EditText edit_weight = (EditText) rukuDialog.getWindow().findViewById(R.id.edit_weight);
        final EditText edit_purchase_price = (EditText) rukuDialog.getWindow().findViewById(R.id.edit_purchase_price);//进价
        final EditText edit_cost_in = (EditText) rukuDialog.getWindow().findViewById(R.id.edit_cost_in);//根据重量和进价算出的总花费
        final EditText edit_selling_price1 = (EditText) rukuDialog.getWindow().findViewById(R.id.edit_selling_price1);//整数部分
        final EditText edit_selling_price2 = (EditText) rukuDialog.getWindow().findViewById(R.id.edit_selling_price2);//小数部分

        ImageButton btn_add1 = (ImageButton) rukuDialog.getWindow().findViewById(R.id.btn_add1);
        ImageButton btn_add2 = (ImageButton) rukuDialog.getWindow().findViewById(R.id.btn_add2);
        ImageButton btn_sub1 = (ImageButton) rukuDialog.getWindow().findViewById(R.id.btn_sub1);
        ImageButton btn_sub2 = (ImageButton) rukuDialog.getWindow().findViewById(R.id.btn_sub2);

        CheckBox checkBox = (CheckBox) rukuDialog.getWindow().findViewById(R.id.checkbox);

        final LinearLayout discount_group = (LinearLayout) rukuDialog.getWindow().findViewById(R.id.discount_group);

        final DropEditText dropedit_discount2 = (DropEditText) rukuDialog.getWindow().findViewById(R.id.drop_edit_discount2);
        final DropEditText dropedit_discount3 = (DropEditText) rukuDialog.getWindow().findViewById(R.id.drop_edit_discount3);


        Button btn_confirm = (Button) rukuDialog.getWindow().findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) rukuDialog.getWindow().findViewById(R.id.btn_cancel);

        List<String> list = new ArrayList<String>();
        list.add("0.95");
        list.add("0.9");
        list.add("0.85");
        list.add("0.8");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);

        dropedit_discount2.setAdapter(arrayAdapter);
        dropedit_discount3.setAdapter(arrayAdapter);


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    discount_group.setVisibility(View.VISIBLE);
                } else {
                    discount_group.setVisibility(View.INVISIBLE);
                    dropedit_discount2.setText("");
                    dropedit_discount3.setText("");
                }
            }
        });

//        checkBox.setChecked(false);//必须要写在监听方法后面


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.btn_add1) {
                    String value = edit_selling_price1.getText().toString().trim();
                    if (value == null || value.isEmpty()) {
                        value = "0";
                    }
                    int value_int = Integer.parseInt(value);
                    value_int++;
                    edit_selling_price1.setText(value_int + "");
                } else if (id == R.id.btn_sub1) {
                    String value = edit_selling_price1.getText().toString().trim();
                    if (value == null || value.isEmpty()) {
                        value = "0";
                    }
                    int value_int = Integer.parseInt(value);
                    --value_int;
                    if (value_int >= 0) {
                        edit_selling_price1.setText(value_int + "");
                    }
                } else if (id == R.id.btn_add2) {
                    String value = edit_selling_price2.getText().toString().trim();
                    if (value == null || value.isEmpty()) {
                        value = "0";
                    }
                    int value_int = Integer.parseInt(value);
                    value_int = value_int + 10;
                    if (value_int >= 99) {
                        value_int = 0;
                    }
                    edit_selling_price2.setText(value_int + "");
                } else if (id == R.id.btn_sub2) {
                    String value = edit_selling_price2.getText().toString().trim();
                    if (value == null || value.isEmpty()) {
                        value = "0";
                    }
                    int value_int = Integer.parseInt(value);
                    value_int = value_int - 10;
                    if (value_int < 0) {
                        value_int = 90;
                    }
                    edit_selling_price2.setText(value_int + "");
                }
            }
        };

        btn_add1.setOnClickListener(listener);
        btn_sub1.setOnClickListener(listener);
        btn_add2.setOnClickListener(listener);
        btn_sub2.setOnClickListener(listener);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String weight_t = edit_weight.getText().toString().trim();
                    double weight_value = weight_t.length() > 0 ? Double.parseDouble(weight_t.toString()) : 0;
                    String price_t = edit_purchase_price.getText().toString().trim();
                    double price_value = price_t.length() > 0 ? Double.parseDouble(price_t.toString()) : 0;

                    edit_cost_in.setText(String.format("%.2f", weight_value * price_value));//保留两位小数

                    //设置默认售价(把售价的初始值设为进价)
                    String sell_price = price_t;
                    String[] strArr = sell_price.split("\\.");//正则中"."有特殊意义，需要转义
                    if (strArr.length == 1) {
                        if (strArr[0].isEmpty())
                            edit_selling_price1.setText("0");
                        else
                            edit_selling_price1.setText(strArr[0]);
                        edit_selling_price2.setText("0");
                    } else if (strArr.length == 2) {
                        edit_selling_price1.setText(strArr[0]);
                        edit_selling_price2.setText(strArr[1].length() > 2 ? strArr[1].substring(0, 2) : strArr[1]);
                    }


                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "输入有误", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        edit_weight.addTextChangedListener(textWatcher);
        edit_purchase_price.addTextChangedListener(textWatcher);

        if (listDataSubCategory.get(position).getDiscount2Enable()) {
            checkBox.setChecked(true);

            dropedit_discount2.setText(listDataSubCategory.get(position).getDiscount2());
            dropedit_discount3.setText(listDataSubCategory.get(position).getDiscount3());

        }


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rukuDialog.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String goodId_t = listDataSubCategory.get(position).getGoods() + "";
                String stock_t = edit_weight.getText().toString().trim();
                String cost_t = edit_purchase_price.getText().toString().trim(); //进价
                String selling_price_t = edit_selling_price1.getText().toString() + "." + edit_selling_price2.getText().toString();//售价
                String cost_in_t = edit_cost_in.getText().toString();
//                String discount2 = edit_discount2.getText().toString().trim();
//                String discount3 = edit_discount3.getText().toString().trim();

                if (stock_t.isEmpty()) {
                    Toast.makeText(getActivity(), "重量不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cost_t.isEmpty()) {
                    Toast.makeText(getActivity(), "进价不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Double.parseDouble(selling_price_t) == 0) {
                    Toast.makeText(getActivity(), "售价不能为0", Toast.LENGTH_SHORT).show();
                    return;
                }

                String discount2 = dropedit_discount2.getText().toString();
                String discount3 = dropedit_discount3.getText().toString();


                UserApi.updateGood_ruku(TAG, token, shopId, goodId_t, stock_t, cost_t, selling_price_t, cost_in_t, discount2, discount3,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String responseStr) {
                                KLog.json(responseStr);
                                try {
                                    JSONObject response = new JSONObject(responseStr);
                                    int status = response.getInt("status");
                                    String msg = response.getString("msg");
                                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                                    if (status == 1) {
                                        initCategoryData();
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

                //关闭对话框
                rukuDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    private void showAddCategoryDialog() {
        final AlertDialog alertDialogAdd = new AlertDialog.Builder(getActivity()).create();

        //由于是自定义的Dialog，所以需要自定义一个编辑框才可以弹出输入法
        alertDialogAdd.setView(new EditText(getActivity()));

        alertDialogAdd.show();
        alertDialogAdd.getWindow().setContentView(R.layout.dialog_add_category);

        alertDialogAdd.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        Button btn_confirm = (Button) alertDialogAdd.getWindow().findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) alertDialogAdd.getWindow().findViewById(R.id.btn_cancel);

        final EditText edit_name = (EditText) alertDialogAdd.getWindow().findViewById(R.id.edit_name);
        final EditText edit_notes = (EditText) alertDialogAdd.getWindow().findViewById(R.id.edit_notes);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAdd.dismiss();
                //关闭软键盘
                //alertDialogAdd.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edit_name.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(getActivity(), "类名不可为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String notes = edit_notes.getText().toString().trim();
                //更新后台数据库，并且当更新后台数据成功时，还会再次初始化主分类的数据
                addCategory(name, notes);

                alertDialogAdd.dismiss();
                //关闭软键盘
                //alertDialogAdd.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        });
    }

    private void showAddSubCategoryDialog() {
        final AlertDialog alertDialogAdd = new AlertDialog.Builder(getActivity()).create();

        alertDialogAdd.show();
        alertDialogAdd.getWindow().setContentView(R.layout.dialog_add_subcategory);

        alertDialogAdd.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialogAdd.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        Button btn_confirm = (Button) alertDialogAdd.getWindow().findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) alertDialogAdd.getWindow().findViewById(R.id.btn_cancel);

        img_pic = (ImageView) alertDialogAdd.getWindow().findViewById(R.id.img_pic);

        final EditText edit_name = (EditText) alertDialogAdd.getWindow().findViewById(R.id.edit_name);
        final EditText edit_price = (EditText) alertDialogAdd.getWindow().findViewById(R.id.edit_price);
        final EditText edit_notes = (EditText) alertDialogAdd.getWindow().findViewById(R.id.edit_notes);

        final RadioGroupEx radio_group_ex = (RadioGroupEx) alertDialogAdd.getWindow().findViewById(R.id.radio_group_ex);

//        final EditText edit_discount2 = (EditText) alertDialogAdd.getWindow().findViewById(R.id.edit_discount2);
//        final EditText edit_discount3 = (EditText) alertDialogAdd.getWindow().findViewById(R.id.edit_discount3);

        CheckBox checkBox = (CheckBox) alertDialogAdd.getWindow().findViewById(R.id.checkbox);
        final LinearLayout discount_group = (LinearLayout) alertDialogAdd.getWindow().findViewById(R.id.discount_group);
        final DropEditText dropedit_discount2 = (DropEditText) alertDialogAdd.getWindow().findViewById(R.id.drop_edit_discount2);
        final DropEditText dropedit_discount3 = (DropEditText) alertDialogAdd.getWindow().findViewById(R.id.drop_edit_discount3);

        List<String> list = new ArrayList<String>();
        list.add("0.95");
        list.add("0.9");
        list.add("0.85");
        list.add("0.8");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);

        dropedit_discount2.setAdapter(arrayAdapter);
        dropedit_discount3.setAdapter(arrayAdapter);


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    discount_group.setVisibility(View.VISIBLE);
                } else {
                    discount_group.setVisibility(View.GONE);
                    dropedit_discount2.setText("");
                    dropedit_discount3.setText("");
                }
            }
        });

        img_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence[] items = {"相册", "相机"};
                new AlertDialog.Builder(getActivity())
                        .setTitle("选择图片来源")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) { //相册选择图片
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    intent.setType("image/*");
                                    startActivityForResult(Intent.createChooser(intent, "选择图片"), SELECT_PICTURE);
                                } else { //拍照选择图片

                                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//判断有没有sd卡
                                        Toast.makeText(getActivity(), "没有sd卡", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    //先把图片给存下来然后在获取这个路径
                                    Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                                    //getActivity().startActivityForResult(intent, MainActivity.SELECT_CAMER);
                                    startActivityForResult(intent, SELECT_CAMER);
                                }
                            }
                        })
                        .create().show();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAdd.dismiss();
                //关闭软键盘 (好像没有用)
                //alertDialogAdd.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //只要点击了某个主类别后再点击相应的子类添加按钮，那么这里不会出错
                String categoryId = mListDataCategory.get(SELECTPOSITION).getId();
                String name = edit_name.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(getActivity(), "菜品名不可为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String price = edit_price.getText().toString().trim();
                if (price.isEmpty()) {
                    Toast.makeText(getActivity(), "菜品价格不可为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mBitmap == null) {
                    Toast.makeText(getActivity(), "请为菜品选择一张图片", Toast.LENGTH_SHORT).show();
                    return;
                }


                String discount2 = dropedit_discount2.getText().toString().trim();
                String discount3 = dropedit_discount3.getText().toString().trim();
                String notes = edit_notes.getText().toString().trim();

                String unit = ((RadioButton)alertDialogAdd.getWindow().findViewById(radio_group_ex.getCheckedRadioButtonId())).getText().toString(); //单位，默认是元/kg
                String method  = ""; //0表示称重类，而1表示按份计算
                if (R.id.rbtn_kg == radio_group_ex.getCheckedRadioButtonId()){
                    method = "0";
                }
                else {
                    method = "1";
                }

                addSubCategory(categoryId, name, price, notes, "", discount2, discount3, method, unit);//上传图片返回的id存在于App.imgId。库存stock这里暂定为0


                mBitmap = null;//重置mBitmap

                alertDialogAdd.dismiss();
            }
        });
    }

    private void showCategoryMenuDialog(final int position) {
        final AlertDialog alertDialogDelete = new AlertDialog.Builder(getActivity()).create();
        alertDialogDelete.show();
        alertDialogDelete.getWindow().setContentView(R.layout.dialog_menu_category);
        //alertDialogDelete.setCanceledOnTouchOutside(false);

        Button btn_modify = (Button) alertDialogDelete.getWindow().findViewById(R.id.btn_modify);
        Button btn_delete = (Button) alertDialogDelete.getWindow().findViewById(R.id.btn_delete);

        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModifyCategoryDialog(position);
                alertDialogDelete.dismiss();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteCategoryDialog(position);
                alertDialogDelete.dismiss();
            }
        });
    }

    private void showSubCategoryMenuDialog(final int position) {
        final AlertDialog alertDialogDelete = new AlertDialog.Builder(getActivity()).create();
        alertDialogDelete.show();
        alertDialogDelete.getWindow().setContentView(R.layout.dialog_menu_category);//重用布局文件
        //alertDialogDelete.setCanceledOnTouchOutside(false);

        Button btn_modify = (Button) alertDialogDelete.getWindow().findViewById(R.id.btn_modify);
        Button btn_delete = (Button) alertDialogDelete.getWindow().findViewById(R.id.btn_delete);

        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModifySubCategoryDialog(position);
                alertDialogDelete.dismiss();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteSubCategoryDialog(position);
                alertDialogDelete.dismiss();
            }
        });
    }

    private void showModifyCategoryDialog(final int position) {
        final AlertDialog alertDialogAdd = new AlertDialog.Builder(getActivity()).create();

        //由于是自定义的Dialog，所以需要自定义一个编辑框才可以弹出输入法
        alertDialogAdd.setView(new EditText(getActivity()));

        alertDialogAdd.show();
        alertDialogAdd.getWindow().setContentView(R.layout.dialog_modify_category); //重用布局文件

        alertDialogAdd.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        Button btn_confirm = (Button) alertDialogAdd.getWindow().findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) alertDialogAdd.getWindow().findViewById(R.id.btn_cancel);

        final EditText edit_name = (EditText) alertDialogAdd.getWindow().findViewById(R.id.edit_name);
        final EditText edit_notes = (EditText) alertDialogAdd.getWindow().findViewById(R.id.edit_notes);

        edit_name.setText(mListDataCategory.get(position).getName());
        edit_notes.setText(mListDataCategory.get(position).getNotes());

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAdd.dismiss();
                //关闭软键盘
                //alertDialogAdd.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edit_name.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(getActivity(), "类名不可为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String notes = edit_notes.getText().toString().trim();
                String id = mListDataCategory.get(position).getId();
                //更新后台数据库和前端界面
                modifyCategory(id, name, notes, position);
                //initCategoryData();
                //更新界面数据,直接更新 mListDataCategory 集合，不再重新调用 initCategotyData()，这种方法无法保证两个异步请求谁先执行完
                //mListDataCategory.set(position, new Category(id, name, notes));
                //categoryAdapter.notifyDataSetChanged();

                alertDialogAdd.dismiss();
            }
        });
    }

    private int dialog_modify_spinner_position;

    private void showModifySubCategoryDialog(final int position) {
        final AlertDialog alertDialogModify = new AlertDialog.Builder(getActivity()).create();

        //由于是自定义的Dialog，所以需要自定义一个编辑框才可以弹出输入法
        alertDialogModify.setView(new EditText(getActivity()));

        alertDialogModify.show();
        alertDialogModify.getWindow().setContentView(R.layout.dialog_modify_subcategory);  //重用布局文件

        alertDialogModify.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        Button btn_confirm = (Button) alertDialogModify.getWindow().findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) alertDialogModify.getWindow().findViewById(R.id.btn_cancel);

        img_pic = (ImageView) alertDialogModify.getWindow().findViewById(R.id.img_pic);

        final EditText edit_name = (EditText) alertDialogModify.getWindow().findViewById(R.id.edit_name);
        final EditText edit_price = (EditText) alertDialogModify.getWindow().findViewById(R.id.edit_price);
        final EditText edit_notes = (EditText) alertDialogModify.getWindow().findViewById(R.id.edit_notes);

        final RadioGroupEx radio_group_ex = (RadioGroupEx) alertDialogModify.getWindow().findViewById(R.id.radio_group_ex);

        Spinner spinner_category = (Spinner) alertDialogModify.getWindow().findViewById(R.id.spinner_cotegory);

        final EditText edit_discount2 = (EditText) alertDialogModify.getWindow().findViewById(R.id.edit_discount2);
        final EditText edit_discount3 = (EditText) alertDialogModify.getWindow().findViewById(R.id.edit_discount3);


        if ("0".equals(listDataSubCategory.get(position).getMethod())) {
            radio_group_ex.check(R.id.rbtn_kg);
        } else {
            for (int i = 0; i < radio_group_ex.getChildCount(); i++) {
                if (((RadioButton) radio_group_ex.getChildAt(i)).getText().equals(listDataSubCategory.get(position).getUnit())) {
                    ((RadioButton) radio_group_ex.getChildAt(i)).setChecked(true);
                    break;
                }
            }
            listDataSubCategory.get(position).getUnit();
        }

        edit_name.setText(listDataSubCategory.get(position).getName());
        edit_price.setText(listDataSubCategory.get(position).getPrice());
        edit_discount2.setText(listDataSubCategory.get(position).getDiscount2());
        edit_discount3.setText(listDataSubCategory.get(position).getDiscount3());
        edit_notes.setText(listDataSubCategory.get(position).getIntro());


        List<String> list = new ArrayList<String>();
        for (int i = 0; i < mListDataCategory.size(); i++) {
            list.add(mListDataCategory.get(i).getName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
        spinner_category.setAdapter(arrayAdapter);
        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dialog_modify_spinner_position = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_category.setSelection(SELECTPOSITION);


        imgPath = listDataSubCategory.get(position).getPicurl();
        //下面这两种加载图片的都行
        Glide.with(getActivity())
                .load(UserApi.BASE_SERVER + imgPath)
                .into(img_pic);

//        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
//        img_pic.setImageBitmap(bitmap);

        img_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                getActivity().startActivityForResult(intent, MainActivity.REQUEST_IMAGE);
                */
                CharSequence[] items = {"相册", "相机"};
                new AlertDialog.Builder(getActivity())
                        .setTitle("选择图片来源")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) { //相册选择图片
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    intent.setType("image/*");
                                    getActivity().startActivityForResult(Intent.createChooser(intent, "选择图片"), SELECT_PICTURE);
                                } else { //拍照选择图片

                                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//判断有没有sd卡
                                        Toast.makeText(getActivity(), "没有sd卡", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    //先把图片给存下来然后在获取这个路径
                                    Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                                    getActivity().startActivityForResult(intent, SELECT_CAMER);
                                }
                            }
                        })
                        .create().show();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogModify.dismiss();
                //关闭软键盘
                //alertDialogModify.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = edit_name.getText().toString().trim();
                String discount2 = edit_discount2.getText().toString().trim();
                String discount3 = edit_discount3.getText().toString().trim();
                String notes = edit_notes.getText().toString().trim();


                if (name.isEmpty()) {
                    Toast.makeText(getActivity(), "菜品名不可为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String price = edit_price.getText().toString().trim();
                if (price.isEmpty()) {
                    Toast.makeText(getActivity(), "菜品价格不可为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                /*if ( mBitmap == null) {
                    Toast.makeText(getActivity(), "请为菜品选择一张图片", Toast.LENGTH_SHORT).show();
                    return;
                }*/

//                if (!App.isUploadPicOver) {
//                    Toast.makeText(getActivity(), "正在上传图片，请稍等", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                String categoryId = mListDataCategory.get(dialog_modify_spinner_position).getId();

                String unit = ((RadioButton) alertDialogModify.getWindow().findViewById(radio_group_ex.getCheckedRadioButtonId())).getText().toString(); //单位，默认是元/kg
                String method = ""; //0表示称重类，而1表示按份计算
                if (R.id.rbtn_kg == radio_group_ex.getCheckedRadioButtonId()) {
                    method = "0";
                } else {
                    method = "1";
                }

                //修改后台数据
                modifySubCategory(listDataSubCategory.get(position).getGoods(), categoryId, name, price, discount2, discount3, notes, method, unit);


                mBitmap = null;//重置

                alertDialogModify.dismiss();
                //关闭软键盘
                //alertDialogModify.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        });
    }

    private void showDeleteCategoryDialog(final int position) {
        final AlertDialog alertDialogDelete = new AlertDialog.Builder(getActivity()).create();
        alertDialogDelete.show();
        alertDialogDelete.getWindow().setContentView(R.layout.dialog_delete_category);

        TextView tvDescription = (TextView) alertDialogDelete.getWindow().findViewById(R.id.tv_msg);
        tvDescription.setText("确定删除 \"" + mListDataCategory.get(position).getName() + "\" 这条记录吗？");

        alertDialogDelete.setCanceledOnTouchOutside(false);

        Button btn_confirm = (Button) alertDialogDelete.getWindow().findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) alertDialogDelete.getWindow().findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialogDelete.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求更新后台数据库，并且请求成功后，直接使用 mListDataCategory 更新界面
                deleteCategory(mListDataCategory.get(position).getId(), position); //从数据库中删除指定Id的主分类数据

                alertDialogDelete.dismiss();
            }
        });
    }

    private void showDeleteSubCategoryDialog(final int position) {
        final AlertDialog alertDialogDelete = new AlertDialog.Builder(getActivity()).create();
        alertDialogDelete.show();
        alertDialogDelete.getWindow().setContentView(R.layout.dialog_delete_subcategory);

        TextView tvDescription = (TextView) alertDialogDelete.getWindow().findViewById(R.id.tv_description);
        tvDescription.setText("确定删除 \"" + listDataSubCategory.get(position).getName() + "\" 这条记录吗？");

        alertDialogDelete.setCanceledOnTouchOutside(false);

        Button btn_confirm = (Button) alertDialogDelete.getWindow().findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) alertDialogDelete.getWindow().findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogDelete.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSubCategory(listDataSubCategory.get(position).getGoods(), position);
                alertDialogDelete.dismiss();
            }
        });
    }


    private void addCategory(String name, String notes) {
        //向后台请求添加主分类
        UserApi.addCategory(TAG, token, shopId, name, notes,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        KLog.json(responseStr);
                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                            if (status == 1) {
                                initCategoryData();
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


    private void addSubCategory(String categoryId, String name, String price, String notes, String cost, String discount2, String discount3, String method, String unit) {

        KLog.i("添加子分类");

        UserApi.goodsUpload(TAG, token, shopId, name, categoryId + "", "", notes, price, cost, discount2, discount3, method, unit,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        KLog.json(responseStr);

                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");

                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                            if (status == 1) {

                                //当请求成功时，再次初始化子类别数据
                                //initSubCategoryData(mListDataCategory.get(SELECTPOSITION).getId());
                                loadSubCategory(mListDataCategory.get(SELECTPOSITION).getId(), 1);

                                //App.imgId = "";//重置
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


    private void modifyCategory(final String categoryId, final String name, final String notes, final int position) {
        UserApi.updateCategory(TAG, token, shopId, name, notes, categoryId + "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        KLog.json(responseStr);

                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                            if (status == 1) {
                                //更新界面数据,直接更新 mListDataCategory 集合
                                mListDataCategory.set(position, new Category(categoryId, name, notes));
                                //categoryAdapter.notifyDataSetChanged();
                                categoryAdapter.notifyItemChanged(position);
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

    private void modifySubCategory(String id, String categoryId, String name, String price, String discount2, String discount3, String notes, String method, String unit) {

        UserApi.updateGood(TAG, token, shopId, categoryId + "", id + "", name, price, "", discount2, discount3, notes, method, unit,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        KLog.json(responseStr);
                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");

                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                            if (status == 1) {
                                //当修改后台数据成功时，再次获取后台数据，更新前端子类别数据
                                //initSubCategoryData(mListDataCategory.get(SELECTPOSITION).getId());
                                loadSubCategory(mListDataCategory.get(SELECTPOSITION).getId(), 1);
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


    private void deleteCategory(String categoryId, final int position) {

        UserApi.deleteCategory(TAG, token, shopId, categoryId + "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        KLog.json(responseStr);
                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                            if (status == 1) {
                                //更新界面数据
                                mListDataCategory.remove(position);
                                //categoryAdapter.notifyDataSetChanged();
                                categoryAdapter.notifyItemRemoved(position);
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


    private void deleteSubCategory(String subCategoryId, final int position) {

        String categoryId = listDataSubCategory.get(SELECTPOSITION).getCategory().getId() + "";

        UserApi.deleteGood(TAG, token, shopId, categoryId, subCategoryId + "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        KLog.json(responseStr);

                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");

                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                            if (status == 1) {
                                //当发送删除请求成功后，直接更新 listDataSubCategory 来更新前端界面
                                listDataSubCategory.remove(position);
                                subCategoryStockAdapter.notifyItemRemoved(position);//有动画效果
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 从相册中选择图片
        if (requestCode == SELECT_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                //使用该办法解决大部分手机获取图片的问题
                String imgPath;
                if (data != null) {
                    Uri uri = data.getData();
                    if (!TextUtils.isEmpty(uri.getAuthority())) {
                        Cursor cursor = getActivity().getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                        if (null == cursor) {
                            Toast.makeText(getActivity(), "图片没找到", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        cursor.moveToFirst();
                        imgPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        cursor.close();

                        KLog.i("图片选择结果：" + imgPath);
                        //EventBus.getDefault().post(new SubCategory(imgPath));
                    } else {
                        imgPath = uri.getPath();

                        KLog.i("图片选择结果：" + imgPath);
                        //EventBus.getDefault().post(new SubCategory(imgPath));
                    }

                    Uri uri1 = Uri.fromFile(new File(imgPath));
                    Log.i("size", "1:" + new File(imgPath).length());

                    //裁剪图片
                    startImageZoom(uri1);

                    //Log.i("isEqual",(uri==uri1)+"");

                } else {
                    Toast.makeText(getActivity(), "图片没找到", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } else if (requestCode == SELECT_CAMER) {
            if (resultCode == Activity.RESULT_OK) {
                String imgPath = Environment.getExternalStorageDirectory() + "/image.jpg";

                Uri uri1 = Uri.fromFile(new File(imgPath));
                Log.i("size", "1：" + new File(imgPath).length());

                //裁剪图片
                startImageZoom(uri1);

                //Log.i("isEqual",(uri==uri1)+"");
            }
        } else if (requestCode == CROP_REQUEST_CODE) {//裁剪图片的Intent请求
            if (data == null) {
                return;
            }//剪裁后的图片
            Bundle extras = data.getExtras();
            if (extras == null) {
                return;
            }
            Bitmap bm = extras.getParcelable("data");
            //ShowImageView(bm);
            //EventBus.getDefault().post(bm);
            mBitmap = bm;
            img_pic.setImageBitmap(mBitmap);

            Log.i("size", "2：" + bm.getByteCount());

            //上传图片 商品图片 4 65536=64K
            UserApi.uploadPhoto(TAG, token, shopId, Base64Encoder.bitmapToBase64(bm), 4,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String responseStr) {
                            try {
                                JSONObject response = new JSONObject(responseStr);
                                int status = response.getInt("status");
                                String msg = response.getString("msg");
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

//                                if (status == 1) {
//                                    App.imgId = response.getString("data");
//                                    App.isUploadPicOver = true; //说明图片已经上传完毕
//                                }

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


    /**
     * 剪裁图片
     *
     * @param uri
     */
    private void startImageZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 128);
        intent.putExtra("outputY", 128);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }
}
