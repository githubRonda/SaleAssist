package com.ronda.saleassist.dialog;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.divider.DividerItemDecoration;
import com.ronda.saleassist.base.BaseDialogFragment;
import com.ronda.saleassist.bean.CartBean;
import com.ronda.saleassist.bean.SubCategory;
import com.ronda.saleassist.local.sqlite.GreenDaoHelper;
import com.ronda.saleassist.local.sqlite.table.SimpleGoodsBean;
import com.ronda.saleassist.local.sqlite.table.SimpleGoodsBeanDao;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

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

public class FuzzyQueryGoodsDialog extends BaseDialogFragment {


    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.et_fuzzy_query)
    EditText mEtFuzzyQuery;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private int mCurSelectPosition = -1;

    private MyAdapter mAdapter;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fuzzy_query_goods, container, false);
    }

    @Override
    public void init(View rootView) {

        mTvTitle.setText("快速查询");

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new MyAdapter();

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mCurSelectPosition != position) {
                    mCurSelectPosition = position;
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        mEtFuzzyQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框为空的时候，暂时定为清空列表数据
                if (TextUtils.isEmpty(s)) {
                    mAdapter.clearData();
                    return;
                }
                fuzzyQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public void appendText(final String price) {
        if (mEtFuzzyQuery == null ){
            return;
        }

        mEtFuzzyQuery.post(new Runnable() {
            @Override
            public void run() {
                mEtFuzzyQuery.append(price);
            }
        });
    }


    @OnClick({R.id.btn_cancel, R.id.btn_confirm, R.id.btn_fuzzy_query})
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

        if (mCurSelectPosition != -1 && mAdapter.getData().size() > 0) {

            // TODO: 2017/8/19/0019 调用查询指定货物的接口，返回成功，则使用EventBus向货篮添加数据
            //EventBus.getDefault().post(new CartBean());
        }

        //dismiss();
    }

    public static FuzzyQueryGoodsDialog newInstance(String arg) {
        FuzzyQueryGoodsDialog fragment = new FuzzyQueryGoodsDialog();
        Bundle args = new Bundle();
        args.putString(ARGUMENT, arg);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 模糊查询
     */
    private void fuzzyQuery(String price) {

        KLog.d("fuzzyQuery: " + price);

//        SimpleGoodsBeanDao dao = GreenDaoHelper.getDaoSession().getSimpleGoodsBeanDao();
//        List<SimpleGoodsBean> list = dao.queryBuilder().where(SimpleGoodsBeanDao.Properties.Price.like("1.13%")).list();
//        System.out.println(list);


//        GoodsApi.fuzzyQueryGoods(SPHelper.getLoginToken(), SPHelper.getCurShopId(), fuzzyMsg, "", new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                KLog.json(response);
//
//                QueryTypeAndGoodsBean bean = GsonUtil.getGson().fromJson(response, QueryTypeAndGoodsBean.class);
//
//                if (1 == bean.getStatus() && bean.getData().getGoods() != null) {
//                    mCurSelectPosition = 0;
//                    mAdapter.setData(bean.getData().getGoods());
//                    mRecyclerView.setAdapter(mAdapter);
//                } else {
//                    Toast.makeText(mContext, bean.getMsg(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(mContext, R.string.no_respnose, Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    private CallbackListener mCallbackListener;

    public FuzzyQueryGoodsDialog setCallbackListener(CallbackListener callbackListener) {
        mCallbackListener = callbackListener;
        return this;
    }

    /**
     * 回调方法
     */
    public interface CallbackListener {
        void onCall(SubCategory subCategory);
    }

    class MyAdapter extends BaseQuickAdapter<SubCategory, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_list_goods);
        }

        @Override
        protected void convert(BaseViewHolder holder, SubCategory item) {
            holder.setText(R.id.tv_name, item.getName());
            holder.setText(R.id.tv_price, item.getPrice());

            if (mCurSelectPosition == holder.getAdapterPosition()) {
                holder.itemView.setBackgroundResource(R.drawable.bg_table_row_select);
            } else {
                holder.itemView.setBackgroundDrawable(null);
            }
        }
    }
}
