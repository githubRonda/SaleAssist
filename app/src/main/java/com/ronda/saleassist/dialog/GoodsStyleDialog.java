package com.ronda.saleassist.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import com.ronda.saleassist.R;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.BaseDialogFragment;
import com.ronda.saleassist.base.BaseFragment;
import com.ronda.saleassist.base.SPHelper;
import com.ronda.saleassist.bean.GoodsStyle;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.local.sqlite.table.Goods;
import com.socks.library.KLog;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置分类样式的 对话框
 * <p>
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/04/06
 * Version: v1.0
 */

public class GoodsStyleDialog extends BaseDialogFragment {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.spinner_column)
    Spinner  mSpinnerColumn;
    @BindView(R.id.spinner_vertical_space)
    Spinner  mSpinnerVerticalSpace;
    @BindView(R.id.spinner_horizontal_space)
    Spinner  mSpinnerHorizontalSpace;
    @BindView(R.id.spinner_title_text_size)
    Spinner  mSpinnerTitleTextSize;
    @BindView(R.id.cb_bold)
    CheckBox mCbBold;
    @BindView(R.id.spinner_text_size)
    Spinner  mSpinnerTextSize;
    @BindView(R.id.cb_show_num)
    CheckBox mCbShowNum;
    @BindView(R.id.cb_show_price)
    CheckBox mCbShowPrice;


    private List<Integer> mListColumn;
    private List<Integer> mListHorizontalSpace;
    private List<Integer> mListVerticalSpace;
    private List<Integer> mListTitleTextSize;
    private List<Integer> mListTextSize;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_goods_style, container, false);
    }

    @Override
    public void init(View rootView) {

        mTvTitle.setText("修改商品排列");

        mSpinnerColumn.setAdapter(new ArrayAdapter<Integer>(mContext, android.R.layout.simple_list_item_1, mListColumn = Arrays.asList(new Integer[]{2, 3, 4, 5, 6, 7})));
        mSpinnerHorizontalSpace.setAdapter(new ArrayAdapter<Integer>(mContext, android.R.layout.simple_list_item_1, mListHorizontalSpace = Arrays.asList(new Integer[]{0, 2, 4, 6, 8, 10})));
        mSpinnerVerticalSpace.setAdapter(new ArrayAdapter<Integer>(mContext, android.R.layout.simple_list_item_1, mListVerticalSpace = Arrays.asList(new Integer[]{0, 2, 4, 6, 8, 10})));
        mSpinnerTitleTextSize.setAdapter(new ArrayAdapter<Integer>(mContext, android.R.layout.simple_list_item_1, mListTitleTextSize = Arrays.asList(new Integer[]{14, 16, 18, 20, 22, 24})));
        mSpinnerTextSize.setAdapter(new ArrayAdapter<Integer>(mContext, android.R.layout.simple_list_item_1, mListTextSize = Arrays.asList(new Integer[]{14, 16, 18, 20, 22, 24})));

        GoodsStyle style = SPUtils.getBean(AppConst.GOODS_STYLE, GoodsStyle.class, new GoodsStyle());

        KLog.w(new Gson().toJson(style));
        if (style != null) {
            mSpinnerColumn.setSelection(mListColumn.indexOf(style.getColumn()));
            mSpinnerHorizontalSpace.setSelection(mListHorizontalSpace.indexOf(style.getHorizontalSpace()));
            mSpinnerVerticalSpace.setSelection(mListVerticalSpace.indexOf(style.getVerticalSpace()));
            mSpinnerTitleTextSize.setSelection(mListTitleTextSize.indexOf(style.getTitleTextSize()));
            mSpinnerTextSize.setSelection(mListTextSize.indexOf(style.getTextSize()));

            mCbBold.setChecked(style.isBold());

            mCbShowNum.setChecked(style.isShowNum());
            mCbShowPrice.setChecked(style.isShowPrice());
        }
    }


    @OnClick({R.id.btn_cancel, R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_confirm:
                doConfirm();
                break;
        }
    }


    /**
     * 点击确定按钮时
     */
    private void doConfirm() {

        int column = mListColumn.get(mSpinnerColumn.getSelectedItemPosition());
        int horizontalSpace = mListHorizontalSpace.get(mSpinnerHorizontalSpace.getSelectedItemPosition());
        int verticalSpace = mListVerticalSpace.get(mSpinnerVerticalSpace.getSelectedItemPosition());
        int titleTextSize = mListTitleTextSize.get(mSpinnerTitleTextSize.getSelectedItemPosition());
        int textSize = mListTextSize.get(mSpinnerTextSize.getSelectedItemPosition());

        boolean isBold = mCbBold.isChecked();

        boolean isShowNum = mCbShowNum.isChecked();

        boolean isShowPrice = mCbShowPrice.isChecked();


        if (mCallbackListener != null) {
            GoodsStyle style = new GoodsStyle(column, horizontalSpace, verticalSpace, titleTextSize, isBold, textSize, isShowNum, isShowPrice);
            mCallbackListener.onCall(style);

            //序列化 持久化保存
            KLog.w("持久化保存： "+ new Gson().toJson(style));
            SPUtils.putBean(AppConst.GOODS_STYLE, style);
        }
        dismiss();
    }


    public static GoodsStyleDialog newInstance(String arg) {
        GoodsStyleDialog fragment = new GoodsStyleDialog();
        Bundle args = new Bundle();
        args.putString(ARGUMENT, arg);
        fragment.setArguments(args);
        return fragment;
    }


    private CallbackListener mCallbackListener;

    public GoodsStyleDialog setCallbackListener(CallbackListener callbackListener) {
        mCallbackListener = callbackListener;
        return this;
    }

    /**
     * 回调方法
     */
    public interface CallbackListener {
        void onCall(GoodsStyle style);
    }
}
