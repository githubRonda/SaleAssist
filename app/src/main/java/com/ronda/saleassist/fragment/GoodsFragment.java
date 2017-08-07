package com.ronda.saleassist.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.divider.DividerItemDecoration;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.api.volley.GsonUtil;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.BaseFragment;
import com.ronda.saleassist.bean.Category;
import com.ronda.saleassist.bean.CategoryBean;
import com.ronda.saleassist.bean.LoginBean;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.R.id.list;
import static android.media.CamcorderProfile.get;
import static com.ronda.saleassist.R.drawable.error;


/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/08/04
 * Version: v1.0
 */

public class GoodsFragment extends BaseFragment {

    @BindView(R.id.btn_edit_order)
    Button             mBtnEditOrder;
    @BindView(R.id.btn_refresh)
    Button             mBtnRefresh;
    @BindView(R.id.img_arrow_left)
    ImageButton        mImgArrowLeft;
    @BindView(R.id.rv_category)
    RecyclerView       mRvCategory;
    @BindView(R.id.recycler_view)
    RecyclerView       mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.btn_add)
    Button             mBtnAdd;
    @BindView(R.id.btn_scan_down)
    Button             mBtnScanDown;
    @BindView(R.id.btn_scan_up)
    Button             mBtnScanUp;
    @BindView(R.id.fragment_category)
    LinearLayout       mFragmentCategory;


    private CategoryAdapter mCategoryAdapter;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_goods, container, false);
    }

    @Override
    public void init(View view) {

        mRvCategory.setHasFixedSize(true);
        mRvCategory.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCategoryAdapter = new CategoryAdapter();
        mRvCategory.setAdapter(mCategoryAdapter);
        mRvCategory.setItemAnimator(new DefaultItemAnimator());
        mRvCategory.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mCategoryAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM); //设置新条目加载进来的动画方式

        View categoryFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.item_category, (ViewGroup) mRecyclerView.getParent(), false);
        ((TextView) categoryFooterView.findViewById(R.id.text_category)).setText("添加分类");
        mCategoryAdapter.addFooterView(categoryFooterView);

        categoryFooterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast("category --> footerView");
            }
        });

        initCategoryData();

        mCategoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ToastUtils.showToast("position: " + position);
            }
        });

        mCategoryAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                ToastUtils.showToast("long press position: " + position);
                return false;
            }
        });
    }

    //===================后台相关===================

    /**
     * 获取后台分类数据
     */
    private void initCategoryData() {

        String token = SPUtils.getString(AppConst.TOKEN, "");
        String shopid = SPUtils.getString(AppConst.CUR_SHOP_ID, "");

        UserApi.getCategoryInfo(TAG, token, shopid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CategoryBean categoryBean = GsonUtil.getGson().fromJson(response, CategoryBean.class);
                        if (categoryBean.getStatus() != 1) {
                            ToastUtils.showToast(categoryBean.getMsg());
                            return;
                        }

                        mCategoryAdapter.setNewData(categoryBean.getData());


                        //再初始化第一个主分类下的子类别的数据
                        if (mCategoryAdapter.getData().size()!= 0) {

                            //loadSubCategory(mListDataCategory.get(SELECTPOSITION).getId(), 1);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast(error.getMessage());
                    }
                });
    }


    /**
     * 分类的适配器
     */
    static class CategoryAdapter extends BaseQuickAdapter<Category, BaseViewHolder> {
        public static int SELECT_POSITION = 0;


        public CategoryAdapter() {
            super(R.layout.item_category);
        }

        @Override
        protected void convert(BaseViewHolder holder, Category item) {
            holder.setText(R.id.text_category, item.getName());

            if (SELECT_POSITION == holder.getAdapterPosition()){
                holder.setBackgroundRes(R.id.text_category, R.color.menu_background_selcted);
            }
            else{
                holder.setBackgroundRes(R.id.text_category, R.color.menu_background_normal);
            }
        }
    }


    /**
     * 货物的适配器
     */
    static class GoodsAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


        public GoodsAdapter() {
            super(R.layout.item_category);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {

        }
    }


}
