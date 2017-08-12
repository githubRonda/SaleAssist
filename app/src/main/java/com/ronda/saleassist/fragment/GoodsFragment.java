package com.ronda.saleassist.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.google.gson.Gson;
import com.ronda.saleassist.R;
import com.ronda.saleassist.activity.MainActivity;
import com.ronda.saleassist.adapter.divider.DividerGridItemDecoration;
import com.ronda.saleassist.adapter.divider.DividerItemDecoration;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.api.volley.GsonUtil;
import com.ronda.saleassist.api.volley.VolleyUtil;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.BaseFragment;
import com.ronda.saleassist.base.MyApplication;
import com.ronda.saleassist.base.SPHelper;
import com.ronda.saleassist.bean.BaseBean;
import com.ronda.saleassist.bean.CartBean;
import com.ronda.saleassist.bean.Category;
import com.ronda.saleassist.bean.CategoryBean;
import com.ronda.saleassist.bean.GoodsBean;
import com.ronda.saleassist.bean.GoodsOrder;
import com.ronda.saleassist.bean.GoodsStyle;
import com.ronda.saleassist.bean.SubCategory;
import com.ronda.saleassist.dialog.GoodsStyleDialog;
import com.ronda.saleassist.engine.BarcodeScannerResolver;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.local.sqlite.table.Goods;
import com.ronda.saleassist.utils.Base64Encoder;
import com.ronda.saleassist.utils.MathCompute;
import com.ronda.saleassist.utils.PaintUtil;
import com.ronda.saleassist.utils.ToastUtils;
import com.ronda.saleassist.view.DigitKeyboardView;
import com.ronda.saleassist.view.LSpinner;
import com.ronda.saleassist.view.RadioGroupEx;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;


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

    private static final int CODE_GOODS = 0x10;
    private static final int CODE_MEMBER_DELAY_PAY = 0x20; //表示会员码，用于挂账
    private static final int CODE_MEMBER_PAY = 0x21; // 表示会员码，用于会员支付
    private static final int CODE_ALI_PAY = 0x30;
    private static final int CODE_WECHAT_PAY = 0x40;


    private static final int SELECT_PICTURE = 0; // 相册选择图片请求
    private static final int CROP_REQUEST_CODE = 1; //裁剪图片请求

    private String token = SPUtils.getString(AppConst.TOKEN, "");
    private String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");

    private static final int ONE_SCREEN_SIZE = 36; // 表示一屏的数据。要比pageSize要小

    public static int SELECT_POSITION = 0; //选中的分类项

    private int pageCount = 0; //分页加载的页码，当前页。（加载下一页成功后，会加1）。后台是从1开始。因为前台一开始是没有数据，所以初始化为0，当第一页数据加载成功之后，才为1
    private int pageSize = 40; //每页的大小

    private CategoryAdapter mCategoryAdapter;
    private GoodsAdapter mGoodsAdapter;

    private ImageView img_pic; //修改货物对话框中的View，提取出来的原因就是因为在裁剪图片的回调方法中要设置

    boolean isPicUploading = false; //是否正在上传图片
    private String imgId; // 上传图片返回的图片id


    private ItemTouchHelper mItemTouchHelper;


    private Timer timer;
    private TimerTask task;

    // handler 处理支付码和货物码
//    private CodeHandler handler = new CodeHandler();//用于处理各种条码的处理器

    private static String payQrcode;
    private static String goodsCode;
    private static String memberCode;

    private BarcodeScannerResolver mBarcodeScannerResolver; //扫码监听

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_goods, container, false);
    }

    @Override
    public void init(View view) {

        initCategoryView();

        initGoodsView();

        initGoodsEvent();

        initDragGoodsEvent();
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
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(mContext));

        mGoodsAdapter = new GoodsAdapter();
        mGoodsAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mGoodsAdapter.setFooterViewAsFlow(true);

        // 最后一个添加项
        View goodsFooterView = LayoutInflater.from(mContext).inflate(R.layout.item_goods_add, (ViewGroup) mRecyclerView.getParent(), false);
        mGoodsAdapter.addFooterView(goodsFooterView);

        //设置适配器
        mRecyclerView.setAdapter(mGoodsAdapter);


        //添加货物事件
        goodsFooterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddSubCategoryDialog();
            }
        });
    }

    private void initGoodsEvent() {
        mGoodsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mGoodsAdapter.getData().get(position).getPrice().isEmpty() || 0 == Double.parseDouble(mGoodsAdapter.getData().get(position).getPrice())) {
                    ToastUtils.showToast("单价为0，不能添加至货篮！");
                    return;
                }

                // TODO: 2017/8/12/0012 这里省略掉确认对话框
                SubCategory goods = mGoodsAdapter.getData().get(position);

                String weight;
                if ("1".equals(goods.getMethod())) { //计件类 ： 不需要获取秤端重量
                    weight = "1";
                } else { //称重类， 需要货物秤端的重量
                    //weight = CommonUtil.getWeight(mBluetooth.weightStr) / 1000.0;//单位是kg
                    weight = new DecimalFormat("0.00").format(Math.random() * 10); // 模拟一个随机值
                }

                String discount = "1";
                if (goods.getDiscount2Enable()) {
                    discount = goods.getDiscount2();
                }

                CartBean cartBean = new CartBean(goods.getGoods(), goods.getName(), goods.getPrice(), weight, goods.getPicurl(),
                        discount, goods.getCategory().getId(), goods.getBargoods(), goods.getMethod(), goods.getUnit());

                EventBus.getDefault().post(cartBean);
            }
        });

        mGoodsAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                if (mCategoryAdapter.getData().size() == 0) {
                    ToastUtils.showToast("请先创建分类");
                } else {
                    showSubCategoryMenuDialog(position);
                }
                return true;
            }
        });
    }

    /**
     * 拖拽事件
     */
    public void initDragGoodsEvent() {
        ItemDragAndSwipeCallback mItemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mGoodsAdapter);
        mItemTouchHelper = new ItemTouchHelper(mItemDragAndSwipeCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        //mGoodsAdapter.setOnItemDragListener(listener);//不需要监听拖拽事件
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
                            ToastUtils.showToast("图片没找到");
                            return;
                        }
                        cursor.moveToFirst();
                        imgPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        cursor.close();

                    } else {
                        imgPath = uri.getPath();

                    }

                    Uri uri1 = Uri.fromFile(new File(imgPath));
                    Log.i("size", "1:" + new File(imgPath).length());

                    //裁剪图片
                    startImageZoom(uri1);
                } else {
                    ToastUtils.showToast("图片没找到");
                    return;
                }
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
            //设置图片
            img_pic.setImageBitmap(bm);

            Log.i("size", "2：" + bm.getByteCount());

            uploadPic(Base64Encoder.bitmapToBase64(bm), 4); // 4表示商品图片
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


    @OnClick({R.id.img_arrow_left, R.id.btn_edit_order, R.id.btn_add, R.id.btn_scan_down, R.id.btn_scan_up})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_arrow_left:
                ((MainActivity) getActivity()).openDrawer();
                break;
            case R.id.btn_edit_order:
                doEditOrder();
                break;
            case R.id.btn_add:
                showAddDialog();
                break;
            case R.id.btn_scan_down:
                doGoodsSurface();
                break;
            case R.id.btn_scan_up:
                break;
        }
    }

    /**
     * 点击修改顺序
     */
    private void doEditOrder() {
        if ("排序".equals(mBtnEditOrder.getText())) {
            mBtnEditOrder.setText("完成");
            for (SubCategory item : mGoodsAdapter.getData()) {
                item.setMoving(true);
            }
            mGoodsAdapter.notifyDataSetChanged();

            //清空点击，长按，下拉刷新事件的影响
            mGoodsAdapter.setOnItemClickListener(null);
            mGoodsAdapter.setOnItemLongClickListener(null);
            mSwipeRefreshLayout.setEnabled(false);
            mGoodsAdapter.enableDragItem(mItemTouchHelper);//启用拖拽
        } else {
            mBtnEditOrder.setText("排序");
            for (SubCategory item : mGoodsAdapter.getData()) {
                item.setMoving(false);
            }
            mGoodsAdapter.notifyDataSetChanged();

            mGoodsAdapter.disableDragItem();//停用拖拽
            //恢复事件
            mSwipeRefreshLayout.setEnabled(true);
            initGoodsEvent();

            //更新货物顺序
            updateGoodsOrder();
        }
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


    /**
     * 显示添加子分类的对话框
     */
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

        final CheckBox checkBox = (CheckBox) alertDialogAdd.getWindow().findViewById(R.id.checkbox);
        final LinearLayout discount_group = (LinearLayout) alertDialogAdd.getWindow().findViewById(R.id.discount_group);
        final LSpinner<String> dropedit_discount2 = (LSpinner<String>) alertDialogAdd.getWindow().findViewById(R.id.drop_edit_discount2);
        final LSpinner<String> dropedit_discount3 = (LSpinner<String>) alertDialogAdd.getWindow().findViewById(R.id.drop_edit_discount3);


        List<String> dropData = Arrays.asList("0.95", "0.9", "0.85", "0.8");
        dropedit_discount2.setData(dropData);
        dropedit_discount3.setData(dropData);


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    discount_group.setVisibility(View.VISIBLE);
                } else {
                    discount_group.setVisibility(View.INVISIBLE);
                    dropedit_discount2.setSelection(0);
                    dropedit_discount3.setSelection(0);
                }
            }
        });

        img_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //只能从相册中选择图片
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "选择图片"), SELECT_PICTURE);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgId = null;//置空
                alertDialogAdd.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //只要点击了某个主类别后再点击相应的子类添加按钮，那么这里不会出错
                String categoryId = mCategoryAdapter.getData().get(SELECT_POSITION).getId();
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

                if (isPicUploading) {
                    Toast.makeText(getActivity(), "正在上传图片，请稍等", Toast.LENGTH_SHORT).show();
                    return;
                }

//                if (imgId == null) {
//                    Toast.makeText(getActivity(), "请为菜品选择一张图片", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                String discount2 = checkBox.isChecked() ? dropedit_discount2.getSelectedItem() : "";
                String discount3 = checkBox.isChecked() ? dropedit_discount2.getSelectedItem() : "";
                String notes = edit_notes.getText().toString().trim();

                String unit = ((RadioButton) alertDialogAdd.getWindow().findViewById(radio_group_ex.getCheckedRadioButtonId())).getText().toString(); //单位，默认是元/kg
                String method = ""; //0表示称重类，而1表示按份计算
                if (R.id.rbtn_kg == radio_group_ex.getCheckedRadioButtonId()) {
                    method = "0";
                } else {
                    method = "1";
                }

                addSubCategory(categoryId, name, price, notes, "", discount2, discount3, method, unit);//上传图片返回的id存在于App.imgId。库存stock这里暂定为0

                alertDialogAdd.dismiss();
            }
        });
    }

    /**
     * 显示修改和删除货物的对话框
     *
     * @param position position of item
     */
    private void showSubCategoryMenuDialog(final int position) {
        final AlertDialog alertDialogDelete = new AlertDialog.Builder(getActivity()).create();
        alertDialogDelete.show();
        alertDialogDelete.getWindow().setContentView(R.layout.dialog_menu_category);//重用布局文件

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

    /**
     * 显示修改子分类（菜品）的对话框
     *
     * @param position position of item
     */
    private void showModifySubCategoryDialog(final int position) {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.show();

        dialog.getWindow().setContentView(R.layout.dialog_modify_subcategory);  //重用布局文件
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        Button btn_confirm = (Button) dialog.getWindow().findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) dialog.getWindow().findViewById(R.id.btn_cancel);

        img_pic = (ImageView) dialog.getWindow().findViewById(R.id.img_pic);

        final EditText edit_name = (EditText) dialog.getWindow().findViewById(R.id.edit_name);
        final EditText edit_price = (EditText) dialog.getWindow().findViewById(R.id.edit_price);
        final EditText edit_notes = (EditText) dialog.getWindow().findViewById(R.id.edit_notes);

        final RadioGroupEx radio_group_ex = (RadioGroupEx) dialog.getWindow().findViewById(R.id.radio_group_ex);

        final CheckBox checkBox = (CheckBox) dialog.getWindow().findViewById(R.id.checkbox);
        final LinearLayout discount_group = (LinearLayout) dialog.getWindow().findViewById(R.id.discount_group);
        final LSpinner<String> dropedit_discount2 = (LSpinner<String>) dialog.getWindow().findViewById(R.id.drop_edit_discount2);
        final LSpinner<String> dropedit_discount3 = (LSpinner<String>) dialog.getWindow().findViewById(R.id.drop_edit_discount3);

        final LSpinner<Category> spinner_category = (LSpinner<Category>) dialog.getWindow().findViewById(R.id.spinner_cotegory);

        if ("0".equals(mGoodsAdapter.getData().get(position).getMethod())) {
            radio_group_ex.check(R.id.rbtn_kg);
        } else {
            for (int i = 0; i < radio_group_ex.getChildCount(); i++) {
                if (((RadioButton) radio_group_ex.getChildAt(i)).getText().equals(mGoodsAdapter.getData().get(position).getUnit())) {
                    ((RadioButton) radio_group_ex.getChildAt(i)).setChecked(true);
                    break;
                }
            }
        }

        List<String> dropData = Arrays.asList("0.95", "0.9", "0.85", "0.8");
        dropedit_discount2.setData(dropData);
        dropedit_discount3.setData(dropData);


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    discount_group.setVisibility(View.VISIBLE);
                } else {
                    discount_group.setVisibility(View.GONE);
                    dropedit_discount2.setSelection(0);
                    dropedit_discount3.setSelection(0);
                }
            }
        });

        edit_name.setText(mGoodsAdapter.getData().get(position).getName());
        edit_price.setText(mGoodsAdapter.getData().get(position).getPrice());
        edit_notes.setText(mGoodsAdapter.getData().get(position).getIntro());


        if (mGoodsAdapter.getData().get(position).getDiscount2Enable()) {
            checkBox.setChecked(true);
            int index1 = dropData.indexOf(mGoodsAdapter.getData().get(position).getDiscount2());
            int index2 = dropData.indexOf(mGoodsAdapter.getData().get(position).getDiscount3());
            dropedit_discount2.setSelection(index1 == -1 ? 0 : index1);
            dropedit_discount3.setSelection(index2 == -1 ? 0 : index2);
        }

        spinner_category.setData(mCategoryAdapter.getData());

        Glide.with(getActivity())
                .load(UserApi.BASE_SERVER + mGoodsAdapter.getData().get(position).getPicurl())
                .into(img_pic);

        img_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //只能从相册中选择图片
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "选择图片"), SELECT_PICTURE);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                imgId = null;//置空
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = edit_name.getText().toString().trim();
                if (name.isEmpty()) {
                    ToastUtils.showToast("菜品名不可为空");
                    return;
                }
                String price = edit_price.getText().toString().trim();
                if (price.isEmpty()) {
                    ToastUtils.showToast("菜品价格不可为空");
                    return;
                }
                if (isPicUploading) {
                    Toast.makeText(getActivity(), "正在上传图片，请稍等", Toast.LENGTH_SHORT).show();
                    return;
                }


                String discount2 = checkBox.isChecked() ? dropedit_discount2.getSelectedItem() : "";
                String discount3 = checkBox.isChecked() ? dropedit_discount2.getSelectedItem() : "";
                String notes = edit_notes.getText().toString().trim();

                String categoryId = spinner_category.getSelectedItem().getId();

                String unit = ((RadioButton) dialog.getWindow().findViewById(radio_group_ex.getCheckedRadioButtonId())).getText().toString(); //单位，默认是元/kg
                String method = ""; //0表示称重类，而1表示按份计算
                if (R.id.rbtn_kg == radio_group_ex.getCheckedRadioButtonId()) {
                    method = "0";
                } else {
                    method = "1";
                }

                //修改后台数据
                modifySubCategory(mGoodsAdapter.getData().get(position).getGoods(), categoryId, name, price, discount2, discount3, notes, method, unit);

                dialog.dismiss();
            }
        });
    }

    /**
     * 显示删除货物的对话框
     */
    private void showDeleteSubCategoryDialog(final int position) {
        final AlertDialog alertDialogDelete = new AlertDialog.Builder(getActivity()).create();
        alertDialogDelete.show();
        alertDialogDelete.getWindow().setContentView(R.layout.dialog_delete_subcategory);

        TextView tvDescription = (TextView) alertDialogDelete.getWindow().findViewById(R.id.tv_description);
        tvDescription.setText("确定删除 \"" + mGoodsAdapter.getData().get(position).getName() + "\" 这条记录吗？");

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
                deleteSubCategory(mGoodsAdapter.getData().get(position).getGoods(), position);
            }
        });
    }


    /**
     * 添加额外项
     */
    private void showAddDialog() {

        final AlertDialog dialogAddition = new AlertDialog.Builder(getActivity()).create();
        dialogAddition.show();
        dialogAddition.getWindow().setContentView(R.layout.dialog_addition);

        DigitKeyboardView keyboardView = (DigitKeyboardView) dialogAddition.findViewById(R.id.digit_keyboard_view);
        Button btn_confirm = (Button) dialogAddition.getWindow().findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) dialogAddition.getWindow().findViewById(R.id.btn_cancel);
        final EditText edit_addition = (EditText) dialogAddition.getWindow().findViewById(R.id.edit_addition);

        keyboardView.bindEditTextViews(getActivity().getWindow(), edit_addition);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddition.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addtion = edit_addition.getText().toString().trim();

                if (addtion.isEmpty()) {
                    Toast.makeText(getActivity(), "金额不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialogAddition.dismiss();

                CartBean cartBean = new CartBean("1000", "额外项", addtion, "1", "", "1", "", "", "0", "");
                cartBean.setDate(System.currentTimeMillis());
                EventBus.getDefault().post(cartBean);
            }
        });
    }


    /**
     * 商品排列样式
     */
    private void doGoodsSurface() {
        GoodsStyleDialog.newInstance("goods_style")
                .setCallbackListener(new GoodsStyleDialog.CallbackListener() {
                    @Override
                    public void onCall(GoodsStyle style) {
                        mGoodsAdapter.setStyle(style);
                    }
                })
                .show(getActivity().getSupportFragmentManager(), "goods_style");
    }

    //===================后台相关===================

    //--------OnRefreshListener-------------
    @Override
    public void onRefresh() {
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


    /**
     * 添加子分类
     */
    private void addSubCategory(String categoryId, String name, String price, String notes, String cost, String discount2, String discount3, String method, String unit) {

        UserApi.goodsUpload(TAG, token, shopId, name, categoryId, imgId, notes, price, cost, discount2, discount3, method, unit,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        BaseBean bean = GsonUtil.getGson().fromJson(response, BaseBean.class);

                        if (bean.getStatus() != 1) {
                            ToastUtils.showToast(bean.getMsg());
                            return;
                        }

                        //当请求成功时，再次初始化子类别数据
                        loadGoodsData(mCategoryAdapter.getData().get(SELECT_POSITION).getId(), 1);
                        imgId = null;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast("添加失败");
                        imgId = null;
                    }
                });
    }

    /**
     * 修改货物，并且再次获取后台子类别数据来更新前端数据
     */
    private void modifySubCategory(String id, String categoryId, String name, String price, String discount2, String discount3, String notes, String method, String unit) {

        UserApi.updateGood(TAG, token, shopId, categoryId, id, name, price, imgId, discount2, discount3, notes, method, unit,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //KLog.json(response);

                        BaseBean bean = GsonUtil.getGson().fromJson(response, BaseBean.class);

                        if (bean.getStatus() == 1) {
                            //当修改后台数据成功时，再次获取后台数据，更新前端货物数据
                            loadGoodsData(mCategoryAdapter.getData().get(SELECT_POSITION).getId(), 1);
                        }

                        imgId = null;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast("修改失败");
                        imgId = null;
                    }
                });
    }

    /**
     * 删除相应的货物
     *
     * @param subCategoryId 货物的id，供数据库删除
     * @param position      货物的position，供adapter删除
     */
    private void deleteSubCategory(String subCategoryId, final int position) {
        String categoryId = mGoodsAdapter.getData().get(position).getCategory().getId();

        UserApi.deleteGood(TAG, token, shopId, categoryId, subCategoryId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        BaseBean bean = GsonUtil.getGson().fromJson(response, BaseBean.class);

                        if (bean.getStatus() != 1) {
                            ToastUtils.showToast(bean.getMsg());
                            return;
                        }

                        //当发送删除请求成功后，直接更新
                        mGoodsAdapter.remove(position);
                        mGoodsAdapter.notifyItemRemoved(position);//有动画效果
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast("删除失败");
                    }
                });
    }


    /**
     * 上传图片
     */
    private void uploadPic(String pic, int type) {
        //上传图片 商品图片 4 65536=64K
        isPicUploading = true;
        UserApi.uploadPhoto(TAG, token, shopId, pic, type,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        //KLog.json(responseStr);
                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");
                            ToastUtils.showToast(msg);

                            if (status == 1) {
                                isPicUploading = false;//说明图片已经上传完毕
                                imgId = response.getString("data");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast("上传失败");
                    }
                });
    }


    /**
     * 上传货物顺序
     */
    private void updateGoodsOrder() {

        List<GoodsOrder> list = new ArrayList<>();

        List<SubCategory> goodsData = mGoodsAdapter.getData();
        for (int i = 0; i < goodsData.size(); i++) {
            list.add(new GoodsOrder(goodsData.get(i).getGoods(), i));
        }

        String data = new Gson().toJson(list);

        UserApi.updateGoodsOrder(TAG, token, shopId, data, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        BaseBean bean = GsonUtil.getGson().fromJson(response, BaseBean.class);

                        if (bean.getStatus() != 1) {
                            ToastUtils.showToast(bean.getMsg());
                            return;
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
     * 不间断的检测 mBluetooth 中的静态变量 payQrcode goodsCode memberCode 是否为空，不为空的话，取出其内容，且重置为空
     *
     * //@param codeType 标识当前检测的条码时是支付码，还是货物码，或者会员码（当codeType是会员码时，用于标识该会员码用于支付还是挂账）
     */
//    public void startTimer(final int codeType) {
//        payQrcode = null;//先置空这个变量
//        goodsCode = null;//先置空。
//        memberCode = null;
//
//        timer = new Timer();
//        switch (codeType) {
//            case CODE_GOODS:  //货物码
//                startScanListen();
//                task = new TimerTask() {
//                    @Override
//                    public void run() {
//                        if (!TextUtils.isEmpty(goodsCode)) {
//                            Message msg = new Message();
//                            msg.what = CODE_GOODS; // 表示货物条码
//                            msg.obj = new String(goodsCode);
//
//                            KLog.i("检测到商品条码：" + msg.obj);
//
//                            handler.sendMessage(msg);
//                            goodsCode = null;
//                        }
//                    }
//                };
//                break;
//
//            case CODE_MEMBER_DELAY_PAY:
//            case CODE_MEMBER_PAY:
//                task = new TimerTask() {
//                    @Override
//                    public void run() {
//                        if (mBluetooth.memberCode != null && !mBluetooth.memberCode.isEmpty()) {
//                            stopTimer();
//                            Message msg = new Message();
//                            msg.what = codeType; // 表示会员条码,用于挂账和会员支付
//                            msg.obj = new String(mBluetooth.memberCode);
//
//                            KLog.i("检测到会员码数据：" + msg.obj);
//
//                            handler.sendMessage(msg);
//
//                            mBluetooth.memberCode = null;
//                        }
//                    }
//                };
//
//                break;
//
//            case CODE_ALI_PAY:
//                task = new TimerTask() {
//                    @Override
//                    public void run() {//获取支付宝支付码
//                        if (mBluetooth.payQrcode != null && !mBluetooth.payQrcode.isEmpty()) {
//                            stopTimer();
//                            Message msg = new Message();
//                            msg.what = CODE_ALI_PAY; //支付宝标志
//                            msg.obj = mBluetooth.payQrcode;
//                            handler.sendMessage(msg);
//                            mBluetooth.payQrcode = null; //重置为null
//                        }
//                    }
//                };
//                break;
//            case CODE_WECHAT_PAY:
//                task = new TimerTask() {
//                    @Override
//                    public void run() {//获取微信支付码
//                        if (mBluetooth.payQrcode != null && !mBluetooth.payQrcode.isEmpty()) {
//                            stopTimer();
//                            Message msg = new Message();
//                            msg.what = CODE_WECHAT_PAY; //微信标志
//                            msg.obj = mBluetooth.payQrcode;
//                            handler.sendMessage(msg);
//                            mBluetooth.payQrcode = null; //重置为null
//                        }
//                    }
//                };
//                break;
//        }
//
//        timer.schedule(task, 0, 800);//启动定时器，延迟为1s，循环间隔为1s
//    }

    public void stopTimer() {
        if (timer != null) {
            task.cancel();
            timer.cancel();
            task = null;
            timer = null;
        }
    }

    /**
     * 开始扫码监听
     */
    public void startScanListen() {
        mBarcodeScannerResolver = new BarcodeScannerResolver();
        mBarcodeScannerResolver.setScanSuccessListener(new BarcodeScannerResolver.OnScanSuccessListener() {
            @Override
            public void onScanSuccess(String barcode) {
                //TODO 显示扫描内容
                //条码有8位和13位的
                if (barcode.length() == 8 || barcode.length() == 13){
                    Log.w(TAG, "barcode: " + barcode);
                }
               ToastUtils.showToast("barcode: " + barcode);
            }
        });
    }

    /**
     * 移除扫码监听
     */
    public void removeScanListen() {
        mBarcodeScannerResolver.removeScanSuccessListener();
        mBarcodeScannerResolver = null;
    }


    //=================================内部类======================================

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
    class GoodsAdapter extends BaseItemDraggableAdapter<SubCategory, BaseViewHolder> {

        private GoodsStyle mStyle;


        public GoodsAdapter() {
            super(R.layout.item_subcategory, null);

            // 从 SharedPreferences 中获取存储的样式设置信息
            mStyle = SPUtils.getBean(AppConst.GOODS_STYLE, GoodsStyle.class, new GoodsStyle());

            // 先在这里改变布局，然后在 convert 中改变 ItemView 样式
            if (mRecyclerView != null && mStyle != null) {
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mStyle.getColumn()));
            }
        }


        @Override
        protected void convert(BaseViewHolder holder, SubCategory item) {

            // 设置间距
            if (mStyle != null) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(mStyle.getHorizontalSpace(), mStyle.getVerticalSpace(), mStyle.getHorizontalSpace(), mStyle.getVerticalSpace());
                holder.itemView.setLayoutParams(params);



                // 设置正文字体
                ((TextView) holder.getView(R.id.tv_plu)).setTextSize(mStyle.getTextSize());
                ((TextView) holder.getView(R.id.tv_price)).setTextSize(mStyle.getTextSize());

                //设置编码和价格显示与否
//                if (mStyle.isShowNum()) {
//                    holder.getView(R.id.tv_plu).setVisibility(View.VISIBLE);
//                } else {
//                    holder.getView(R.id.tv_plu).setVisibility(View.INVISIBLE);
//                }
                if (mStyle.isShowPrice()) {
                    holder.setVisible(R.id.tv_price, true);
                } else {
                    holder.setVisible(R.id.tv_price, false);
                }

                // 设置标题字体
                ((TextView) holder.getView(R.id.tv_name)).setTextSize(mStyle.getTitleTextSize());

                // 设置标题是否为粗体
                ((TextView) holder.getView(R.id.tv_name)).getPaint().setFakeBoldText(mStyle.isBold());
            }


            //holder.setText(R.id.tv_plu, item.getNumber());
            holder.setText(R.id.tv_name, item.getName());
            holder.setText(R.id.tv_price, item.getPrice() + item.getUnit());



//            holder.setText(R.id.text_subcategory, item.getName() + " " + item.getPrice() + item.getUnit());
//            Glide.with(mContext)
//                    .load(UserApi.BASE_SERVER + item.getPicurl())
//                    .into((ImageView) holder.getView(R.id.img_subcategory));

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


        public void setStyle(GoodsStyle style) {
            mStyle = style;
            // 分两步：1.改变RecyclerView的布局；2.改变ItemView的样式
            if (mRecyclerView != null) {
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mStyle.getColumn()));
            }
            notifyDataSetChanged();
        }
    }


    //用于处理各种条码的处理器
//    class CodeHandler extends Handler {
//
//        // 扫码卖货时，关闭秤端扫码，用于延迟关闭，为handler 服务
//        Runnable mShutScanRunnable = new Runnable() {
//            @Override
//            public void run() {
//                stopTimer();
//            }
//        };
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case CODE_ALI_PAY: //接收到支付宝条码。这里有两种情况：1.单纯的支付宝付款；2.会员支付余额不足时,接着支付宝支付
//                    if (payConfirmDialog != null && payConfirmDialog.isShowing()) {
//                        String paycode = msg.obj.toString();
//
//                        if (memberInfo == null) { // 普通的支付宝支付（非会员支付）
//                            //非会员支付时，customer可为空，这里习惯设为1
//                            uploadOrder(PAY_ALI, "1", paycode, "", "", "", "");//上传订单 支付宝
//
//                        } else {//会员余额不足时， 接着用支付宝支付
//                            // 获取会员优惠折扣信息
//                            List<PreferenceBean> list = memberInfo.getCosts();
//                            String discount = "1";
//                            for (int i = 0; i < list.size(); i++) {
//                                if ("3".equals(list.get(i).getCosttype())) { //表示折扣
//                                    discount = list.get(i).getCost();
//                                }
//                            }
//                            String costTotal = PreciseCompute.mul(getTotal() + "", discount); //原价*会员优惠折扣
//                            String extpay = PreciseCompute.roundHalfUp_scale2(PreciseCompute.sub(costTotal, memberInfo.getMoney() + "")); //补差价，保留两位小数，四舍五入
//
//                            uploadOrder(PAY_VIP, memberInfo.getUserid(), paycode, costTotal, memberInfo.getExtcode(), PAY_ALI, extpay); // 上传订单 会员支付 余额不足 支付宝支付
//                        }
//                    }
//                    break;
//
//                case CODE_WECHAT_PAY:
//                    if (payConfirmDialog != null && payConfirmDialog.isShowing()) {
//                        String paycode = msg.obj.toString();
//
//                        if (memberInfo == null) { // 普通的微信支付（非会员支付）
//                            //非会员支付时，customer可为空，这里习惯设为1
//                            uploadOrder(PAY_WECHAT, "1", paycode, "", "", "", "");//上传订单 微信
//                            Toast.makeText(getActivity(), "weixin_pay:" + paycode, Toast.LENGTH_SHORT).show();
//                            System.out.println("weixin_pay:" + paycode);
//                        } else {//会员余额不足时， 接着用微信支付
//                            // 获取会员优惠折扣信息
//                            List<PreferenceBean> list = memberInfo.getCosts();
//                            String discount = "1";
//                            for (int i = 0; i < list.size(); i++) {
//                                if ("3".equals(list.get(i).getCosttype())) { //表示折扣
//                                    discount = list.get(i).getCost();
//                                }
//                            }
//                            String costTotal = PreciseCompute.mul(getTotal() + "", discount); //原价*会员优惠折扣
//                            String extpay = PreciseCompute.roundHalfUp_scale2(PreciseCompute.sub(costTotal, memberInfo.getMoney() + "")); //补差价，保留两位小数，四舍五入
//
//                            uploadOrder(PAY_VIP, memberInfo.getUserid(), paycode, costTotal, memberInfo.getExtcode(), PAY_WECHAT, extpay); // 上传订单 会员支付 余额不足 支付宝支付
//                        }
//
//                    }
//                    break;
//                case CODE_GOODS: //接收到货物条码的数据时
//                    String goodsCode = msg.obj.toString();
//                    Toast.makeText(MyApplication.getInstance(), "条码值为：" + goodsCode, Toast.LENGTH_SHORT).show();
//
//                    ((CategoryFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_category)).getGoodsInfoByCode(goodsCode);
//
//                    //延迟6s 关闭定时器。（应该是从最后一次开始计算，然后6s之后才关闭）
//                    handler.removeCallbacks(mShutScanRunnable);// 先移除 runnable
//                    handler.postDelayed(mShutScanRunnable, 12000);
//                    break;
//                case CODE_MEMBER_DELAY_PAY: // 接收到会员条码的数据时,做挂账处理
//                case CODE_MEMBER_PAY: // 接收到会员条码的数据时,做挂账处理和会员支付处理
//                    String memberCode = msg.obj.toString();
//                    Toast.makeText(MyApplication.getInstance(), "会员条码值为：" + memberCode, Toast.LENGTH_SHORT).show();
//                    getMemberInfoByCode(memberCode, msg.what);
//                    break;
//            }
//
//        }
//    }
}
