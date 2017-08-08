package com.ronda.saleassist.fragment;

import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.divider.DividerGridItemDecoration;
import com.ronda.saleassist.adapter.divider.DividerItemDecoration;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.api.volley.GsonUtil;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.BaseFragment;
import com.ronda.saleassist.bean.Category;
import com.ronda.saleassist.bean.CategoryBean;
import com.ronda.saleassist.bean.GoodsBean;
import com.ronda.saleassist.bean.SubCategory;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.utils.PaintUtil;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import butterknife.BindView;

import static android.media.CamcorderProfile.get;


/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/08/04
 * Version: v1.0
 */

public class GoodsFragment extends BaseFragment implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.btn_edit_order)
    Button mBtnEditOrder;
    @BindView(R.id.btn_refresh)
    Button mBtnRefresh;
    @BindView(R.id.img_arrow_left)
    ImageButton mImgArrowLeft;
    @BindView(R.id.rv_category)
    RecyclerView mRvCategory;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.btn_add)
    Button mBtnAdd;
    @BindView(R.id.btn_scan_down)
    Button mBtnScanDown;
    @BindView(R.id.btn_scan_up)
    Button mBtnScanUp;
    @BindView(R.id.fragment_category)
    LinearLayout mFragmentCategory;


    public static int SELECT_POSITION = 0; //选中的分类项

    private int pageCount = 1; //分页加载的页码
    private int pageSize = 10; //每页的大小

    private CategoryAdapter mCategoryAdapter;
    private GoodsAdapter mGoodsAdapter;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_goods, container, false);
    }

    @Override
    public void init(View view) {

        initCategoryView();

        initGoodsView();
    }

    private void initCategoryView() {
        mRvCategory.setHasFixedSize(true);
        mRvCategory.setLayoutManager(new LinearLayoutManager(mContext));
        mCategoryAdapter = new CategoryAdapter();
        mRvCategory.setAdapter(mCategoryAdapter);
        mRvCategory.setItemAnimator(new DefaultItemAnimator());
        mRvCategory.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mCategoryAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM); //设置新条目加载进来的动画方式

        View categoryFooterView = LayoutInflater.from(mContext).inflate(R.layout.item_category, (ViewGroup) mRecyclerView.getParent(), false);
        ((TextView) categoryFooterView.findViewById(R.id.text_category)).setText("添加分类");
        mCategoryAdapter.addFooterView(categoryFooterView);

        categoryFooterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast("category --> footerView");
            }
        });

        loadCategoryData();

        mCategoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ToastUtils.showToast("position: " + position);
                SELECT_POSITION = position;
                mCategoryAdapter.notifyDataSetChanged();
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

    private void initGoodsView() {
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 6));

        mGoodsAdapter = new GoodsAdapter();
        mRvCategory.setItemAnimator(new DefaultItemAnimator());
        mRvCategory.addItemDecoration(new DividerGridItemDecoration(mContext));

        mRecyclerView.setAdapter(mGoodsAdapter);

        mGoodsAdapter.setOnLoadMoreListener(this, mRecyclerView);

        // 最后一个添加项
        View goodsFooterView = LayoutInflater.from(mContext).inflate(R.layout.item_subcategory, (ViewGroup) mRecyclerView.getParent(), false);
        ((TextView) goodsFooterView.findViewById(R.id.text_subcategory)).setText("添加菜品");
        ((ImageView) goodsFooterView.findViewById(R.id.img_subcategory)).setImageResource(R.drawable.img_add_subcategory);
        goodsFooterView.findViewById(R.id.img_discount).setVisibility(View.GONE);

        mGoodsAdapter.addFooterView(goodsFooterView, -1, LinearLayout.VERTICAL);
//
//        goodsFooterView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ToastUtils.showToast("Goods --> footerView");
//            }
//        });

    }

    //===================后台相关===================


    //--------OnRefreshListener-------------
    @Override
    public void onRefresh() {
        ToastUtils.showToast("onRefresh");
        KLog.i("onRefresh");
    }

    //--------OnLoadMoreListener--------------
    @Override
    public void onLoadMoreRequested() {
        ToastUtils.showToast("onLoadMoreRequested");
        KLog.i("onLoadMoreRequested");
    }

    /**
     * 获取后台分类数据
     */
    private void loadCategoryData() {

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
                        if (mCategoryAdapter.getData().size() != 0) {

                            //loadSubCategory(mListDataCategory.get(SELECTPOSITION).getId(), 1);

                            pageCount = 1;
                            loadGoodsData(mCategoryAdapter.getData().get(SELECT_POSITION).getId(), pageCount); //page 从1 开始
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


    private void loadGoodsData(String categoryId, int page) {

        String token = SPUtils.getString(AppConst.TOKEN, "");
        String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");
        UserApi.getGoodsInfo(TAG, token, 1, shopId, categoryId, pageSize, page, // 1为id升序 2为id降序 3为价格升序 4为价格降序
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        KLog.json(response);

                        GoodsBean bean = GsonUtil.getGson().fromJson(response, GoodsBean.class);

                        if (bean.getStatus() == -9) { //status = -9是店铺内暂无任何货物
                            ToastUtils.showToast("当前已是全部货物");
                        } else if (bean.getStatus() == 1) {

                            mGoodsAdapter.setNewData(bean.getData());

                        } else {
                            ToastUtils.showToast(bean.getMsg());
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

        public CategoryAdapter() {
            super(R.layout.item_category);
        }

        @Override
        protected void convert(BaseViewHolder holder, Category item) {
            holder.setText(R.id.text_category, item.getName());

            if (SELECT_POSITION == holder.getAdapterPosition()) {
                holder.setBackgroundRes(R.id.text_category, R.color.menu_background_selcted);
            } else {
                holder.setBackgroundRes(R.id.text_category, R.color.menu_background_normal);
            }
        }
    }


    /**
     * 货物的适配器
     */
    static class GoodsAdapter extends BaseQuickAdapter<SubCategory, BaseViewHolder> {


        public GoodsAdapter() {
            super(R.layout.item_subcategory);
        }


        @Override
        protected void convert(BaseViewHolder holder, SubCategory item) {
            KLog.i("adapter Position: "+ holder.getAdapterPosition());
            //最后一项（添加）
//            if (holder.getAdapterPosition() == getData().size() ) {
//                holder.setText(R.id.text_subcategory, "添加菜品");
//                holder.setImageResource(R.id.img_subcategory, R.drawable.img_add_subcategory);
//                holder.setVisible(R.id.img_discount, false);
//            } else {
                holder.setText(R.id.text_subcategory, item.getName() + " " + item.getPrice() + item.getUnit());
                Glide.with(mContext)
                        .load(UserApi.BASE_SERVER + item.getPicurl())
                        .into((ImageView) holder.getView(R.id.img_subcategory));

                if ("1".equals(item.getMethod())) {
                    holder.setVisible(R.id.img_num, true);
                    PaintUtil.paintCircle((ImageView) holder.getView(R.id.img_num));
                } else {
                    holder.setVisible(R.id.img_num, false);
                }

                String discount = item.getDiscount2().trim();//discount2为公共折扣（要显示在主界面上的）
                //判断是否是有效的折扣（不为0，不为1，不为空字符串）
                if (discount.equals("0") || discount.equals("1") || discount.isEmpty()) {
                    holder.setVisible(R.id.img_discount, false);
                } else {
                    holder.setVisible(R.id.img_discount, true); //必须要首先设置显示，要不然item复用是有的会显示不出来

                    if (Double.parseDouble(discount) > 0 && Double.parseDouble(discount) < 1) {
                        String s = String.valueOf(Double.parseDouble(discount)); //先把discount转成double类型再转成String类型，是为了去除小数部分最后一位是0的情况

                        PaintUtil.paintText(s.substring(s.indexOf(".") + 1, s.length()) + "折", (ImageView) holder.getView(R.id.img_discount));
                    }
                }

                if (item.isMoving()) {
                    holder.setBackgroundRes(R.id.ll_content, R.drawable.bg_item_dash);
                } else {
                    holder.setBackgroundColor(R.id.ll_content, Color.TRANSPARENT);
                }
//            }



        }
    }


}
