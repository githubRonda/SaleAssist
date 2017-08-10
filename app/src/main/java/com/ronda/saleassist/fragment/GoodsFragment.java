package com.ronda.saleassist.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.gson.Gson;
import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.divider.DividerGridItemDecoration;
import com.ronda.saleassist.adapter.divider.DividerItemDecoration;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.api.volley.GsonUtil;
import com.ronda.saleassist.api.volley.VolleyUtil;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.BaseFragment;
import com.ronda.saleassist.base.SPHelper;
import com.ronda.saleassist.bean.BaseBean;
import com.ronda.saleassist.bean.Category;
import com.ronda.saleassist.bean.CategoryBean;
import com.ronda.saleassist.bean.GoodsBean;
import com.ronda.saleassist.bean.SubCategory;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.utils.PaintUtil;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

import static android.media.CamcorderProfile.get;
import static com.ronda.saleassist.fragment.GoodsFragment.SELECT_POSITION;


/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/08/04
 * Version: v1.0
 */

public class GoodsFragment extends BaseFragment implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

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

    private String token  = SPUtils.getString(AppConst.TOKEN, "");
    private String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");

    private static final int ONE_SCREEN_SIZE = 36; // 表示一屏的数据。要比pageSize要小

    public static int SELECT_POSITION = 0; //选中的分类项

    private int pageCount = 0; //分页加载的页码，当前页。（加载下一页成功后，会加1）。后台是从1开始。因为前台一开始是没有数据，所以初始化为0，当第一页数据加载成功之后，才为1
    private int pageSize  = 40; //每页的大小

    private CategoryAdapter mCategoryAdapter;
    private GoodsAdapter    mGoodsAdapter;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_goods, container, false);
    }

    @Override
    public void init(View view) {

        initCategoryView();

        initGoodsView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VolleyUtil.getInstance().cancelPendingRequests(TAG);
    }

    private void initCategoryView() {
        mRvCategory.setHasFixedSize(true);
        mRvCategory.setLayoutManager(new LinearLayoutManager(mContext));
        mRvCategory.setItemAnimator(new DefaultItemAnimator());
        mRvCategory.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mCategoryAdapter = new CategoryAdapter();
        mCategoryAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM); //设置新条目加载进来的动画方式
        mRvCategory.setAdapter(mCategoryAdapter);

        // 初始化加载分类数据
        loadCategoryData();

        View categoryFooterView = LayoutInflater.from(mContext).inflate(R.layout.item_category, (ViewGroup) mRecyclerView.getParent(), false);
        ((TextView) categoryFooterView.findViewById(R.id.text_category)).setText("添加分类");
        mCategoryAdapter.addFooterView(categoryFooterView);

        categoryFooterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击了“添加分类”，弹出添加对话框
                showAddCategoryDialog();
            }
        });


        mCategoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ToastUtils.showToast("position: " + position);

                if (SELECT_POSITION == position) {
                    return;
                }

                SELECT_POSITION = position;
                mCategoryAdapter.notifyDataSetChanged();//改变背景

                //切换类别，请求第一页数据
                mSwipeRefreshLayout.setRefreshing(true);
                loadGoodsData(mCategoryAdapter.getData().get(SELECT_POSITION).getId(), 1);
            }
        });

        mCategoryAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                //长按了相关类别，弹出修改和删除的菜单对话框
                showCategoryMenuDialog(position);
                return true;
            }
        });
    }

    private void initGoodsView() {

        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 6));
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(mContext));

        mGoodsAdapter = new GoodsAdapter();
        mGoodsAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mGoodsAdapter.setFooterViewAsFlow(true);

        // 最后一个添加项
        View goodsFooterView = LayoutInflater.from(mContext).inflate(R.layout.item_goods_add, (ViewGroup) mRecyclerView.getParent(), false);
        mGoodsAdapter.addFooterView(goodsFooterView);

        mRecyclerView.setAdapter(mGoodsAdapter);

        goodsFooterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast("Goods --> footerView");
            }
        });

    }

    /**
     * 显示添加主分类的对话框
     */
    private void showAddCategoryDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.show();

        dialog.getWindow().setContentView(R.layout.dialog_add_category);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        Button btn_confirm = (Button) dialog.getWindow().findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) dialog.getWindow().findViewById(R.id.btn_cancel);

        final EditText edit_name = (EditText) dialog.getWindow().findViewById(R.id.edit_name);
        final EditText edit_notes = (EditText) dialog.getWindow().findViewById(R.id.edit_notes);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edit_name.getText().toString().trim();
                String notes = edit_notes.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.showToast("类名不可为空");
                    return;
                }


                dialog.dismiss();


                //更新后台数据库，并且当更新后台数据成功时，还会再次初始化主分类的数据
                addCategory(name, notes);
            }
        });
    }

    /**
     * 显示修改和删除主分类的对话框
     */
    private void showCategoryMenuDialog(final int position) {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_menu_category);
        //alertDialogDelete.setCanceledOnTouchOutside(false);

        Button btn_modify = (Button) dialog.getWindow().findViewById(R.id.btn_modify);
        Button btn_delete = (Button) dialog.getWindow().findViewById(R.id.btn_delete);

        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModifyCategoryDialog(position);
                dialog.dismiss();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteCategoryDialog(position);
                dialog.dismiss();
            }
        });
    }

    /**
     * 显示修改主分类的对话框
     */
    private void showModifyCategoryDialog(final int position) {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();

        //由于是自定义的Dialog，所以需要自定义一个编辑框才可以弹出输入法
        dialog.setView(new EditText(getActivity()));

        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_modify_category); //重用布局文件

        Button btn_confirm = (Button) dialog.getWindow().findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) dialog.getWindow().findViewById(R.id.btn_cancel);

        final EditText edit_name = (EditText) dialog.getWindow().findViewById(R.id.edit_name);
        final EditText edit_notes = (EditText) dialog.getWindow().findViewById(R.id.edit_notes);

        edit_name.setText(mCategoryAdapter.getData().get(position).getName());
        edit_notes.setText(mCategoryAdapter.getData().get(position).getNotes());

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edit_name.getText().toString().trim();
                String notes = edit_notes.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    ToastUtils.showToast("类名不可为空");
                    return;
                }

                String id = mCategoryAdapter.getData().get(position).getId();
                //更新后台数据库和前端界面
                modifyCategory(id, name, notes, position);

                dialog.dismiss();
            }
        });
    }

    /**
     * 显示删除主分类的对话框
     *
     * @param position 主分类的position，删除的时候根据position获取到主分类的id然后删除
     */
    private void showDeleteCategoryDialog(final int position) {
        final AlertDialog alertDialogDelete = new AlertDialog.Builder(getActivity()).create();
        alertDialogDelete.show();
        alertDialogDelete.getWindow().setContentView(R.layout.dialog_delete_category);

        TextView tv_msg = (TextView) alertDialogDelete.getWindow().findViewById(R.id.tv_msg);
        tv_msg.setText("确定删除 \"" + mCategoryAdapter.getData().get(position).getName() + "\" 这条记录吗？");

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
                alertDialogDelete.dismiss();

                //请求更新后台数据库，并且请求成功后，直接更新界面
                deleteCategory(mCategoryAdapter.getData().get(position).getId(), position); //从数据库中删除指定Id的主分类数据
            }
        });
    }


    //===================后台相关===================

    //--------OnRefreshListener-------------
    @Override
    public void onRefresh() {
        KLog.i("onRefresh");

        mGoodsAdapter.setEnableLoadMore(false);//禁用加载更多

        loadGoodsData(mCategoryAdapter.getData().get(SELECT_POSITION).getId(), 1);//onRefresh() 回调。下拉刷新，要加载第一页数据
    }

    //--------OnLoadMoreListener--------------
    //初始化和下拉刷新都会调用这个方法（本质就是LoadingView的绘制）
    @Override
    public void onLoadMoreRequested() {
        KLog.i("onLoadMoreRequested");

        mSwipeRefreshLayout.setEnabled(false);//禁用下拉刷新

        // 既包括初始化第一页数据，也包括后续的下拉加载更多事件。后台页码是从1开始，前台pageCount初始为0
        loadGoodsData(mCategoryAdapter.getData().get(SELECT_POSITION).getId(), pageCount + 1); //onLoadMoreRequested 回调
    }

    /**
     * 加载分类数据
     */
    private void loadCategoryData() {

        //加载分类数据时,要禁用货物的下拉刷新功能, 等加载成功之后恢复
        mSwipeRefreshLayout.setEnabled(false);

        UserApi.getCategoryInfo(TAG, token, shopId,
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
                            mSwipeRefreshLayout.setEnabled(true);

                            // TODO: 17-8-10 个人看法：分类一旦修改重新加载之后, SELECT_POSITION 和 pageCount 都要恢复初始值
                            SELECT_POSITION = 0;
                            pageCount = 0;
                            loadGoodsData(mCategoryAdapter.getData().get(SELECT_POSITION).getId(), pageCount + 1);// 分类数据加载完毕后，加载货物数据
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
     * 加载货物数据
     *
     * @param categoryId 分类id
     * @param page       要加载数据的页码
     */
    private void loadGoodsData(String categoryId, final int page) {

        UserApi.getGoodsInfo(TAG, token, 1, shopId, categoryId, pageSize, page, // 1为id升序 2为id降序 3为价格升序 4为价格降序
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        KLog.json(response);
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            int status = jsonObject.getInt("status");
//                            String msg = jsonObject.getString("msg");
//
//                            if (status == 1) {
//                                List<SubCategory> bean = GsonUtil.getGson().fromJson(jsonObject.getString("data"), new TypeToken<List<SubCategory>>() {
//                                }.getType());
//                                //货物数据加载成功之后，pageCount才正确表示当前页码
//                                pageCount = page;
//
//                                if (pageCount == 1) { // 表示初始化数据
//                                    mGoodsAdapter.setNewData(bean);
//                                } else if (pageCount > 1) { // 加载更多数据
//                                    mGoodsAdapter.addData(bean);
//                                }
//                            } else if (status == -9) { //status = -9是店铺内暂无任何货物
//                                ToastUtils.showToast("当前已是全部货物");
//                            } else {
//                                ToastUtils.showToast(msg);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }

                        GoodsBean bean = GsonUtil.getGson().fromJson(response, GoodsBean.class);

                        if (bean.getStatus() == -9) { //status = -9是店铺内暂无任何货物
                            ToastUtils.showToast("当前已是全部货物");
                            mGoodsAdapter.loadMoreEnd();
                        } else if (bean.getStatus() == 1) {
                            //货物数据加载成功之后，pageCount才正确表示当前页码
                            pageCount = page;

                            if (pageCount == 1) { // 表示初始化数据
                                mGoodsAdapter.setNewData(bean.getData());
                            } else if (pageCount > 1) { // 加载更多数据
                                mGoodsAdapter.addData(bean.getData());
                            }

                            mGoodsAdapter.loadMoreComplete();
                        } else {
                            ToastUtils.showToast(bean.getMsg());
                        }

                        //若小于1屏的数据，则不显示LoadingView，此时也不能加载更多。（pageSize一定要大于1屏的数据个数）
                        if (mGoodsAdapter.getData().size() < ONE_SCREEN_SIZE) {
                            mGoodsAdapter.loadMoreEnd(true); //true is gone,false is visible.
                        }

                        mGoodsAdapter.setEnableLoadMore(true);//启用加载更多
                        mSwipeRefreshLayout.setEnabled(true);//启用下拉刷新
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast(error.getMessage());

                        mGoodsAdapter.loadMoreFail();
                        mGoodsAdapter.setEnableLoadMore(true);//启用加载更多
                        mSwipeRefreshLayout.setEnabled(true);//启用下拉刷新
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }

        );

    }

    /**
     * 添加主分类
     *
     * @param name  主分类的名字
     * @param notes 主分类的备注
     */
    private void addCategory(String name, String notes) {

        UserApi.addCategory(TAG, token, shopId, name, notes,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        BaseBean bean = GsonUtil.getGson().fromJson(response, BaseBean.class);

                        if (bean.getStatus() != 1) {
                            ToastUtils.showToast(bean.getMsg());
                            return;
                        }

                        //若添加成功, 则需要再次加载数据
                        loadCategoryData();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast("添加失败");
                    }
                }

        );
    }

    /**
     * 修改主分类
     */
    private void modifyCategory(final String categoryId, final String name, final String notes, final int position) {

        UserApi.updateCategory(TAG, token, shopId, name, notes, categoryId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        KLog.json(response);

                        BaseBean bean = GsonUtil.getGson().fromJson(response, BaseBean.class);

                        if (bean.getStatus() != 1) {
                            ToastUtils.showToast(bean.getMsg());
                            return;
                        }

                        //对于修改就直接本地更新
                        mCategoryAdapter.getData().set(position, new Category(categoryId, name, notes));
                        mCategoryAdapter.notifyItemChanged(position);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast("修改失败");
                    }
                });
    }

    /**
     * 删除相应的主分类
     */
    private void deleteCategory(String categoryId, final int position) {

        UserApi.deleteCategory(TAG, token, shopId, categoryId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        BaseBean bean = GsonUtil.getGson().fromJson(response, BaseBean.class);

                        if (bean.getStatus() != 1) {
                            ToastUtils.showToast(bean.getMsg());
                            return;
                        }

                        //本地更新界面数据
                        mCategoryAdapter.getData().remove(position);
                        mCategoryAdapter.notifyItemRemoved(position);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast("删除失败");
                    }
                });
    }

    //==========内部类============

    /**
     * 分类的适配器
     */
    class CategoryAdapter extends BaseQuickAdapter<Category, BaseViewHolder> {

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
    class GoodsAdapter extends BaseQuickAdapter<SubCategory, BaseViewHolder> {


        public GoodsAdapter() {
            super(R.layout.item_subcategory);
        }


        @Override
        protected void convert(BaseViewHolder holder, SubCategory item) {
            KLog.i("adapter Position: " + holder.getAdapterPosition());
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
        }
    }
}
