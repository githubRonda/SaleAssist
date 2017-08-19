package com.ronda.saleassist.zznew;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.ronda.saleassist.R;


public class RefreshListView extends ListView implements AbsListView.OnScrollListener {

    private LayoutInflater inflater;
    private View footer;
    private Context context;
    private int lastVisibleItem;
    private int totalCount;
    private boolean isLoading;
    private LoadData loadData;

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context,attrs);
        this.context = context;
        initFoot(context);
        setOnScrollListener(this);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initFoot(Context context) {
        inflater = LayoutInflater.from(context);
        footer = inflater.inflate(R.layout.lv_foot_refresh, null);
        footer.setVisibility(View.GONE);
        addFooterView(footer);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (totalCount == lastVisibleItem && scrollState == SCROLL_STATE_IDLE) {
            if (!isLoading) {
                isLoading = true;
                footer.setVisibility(View.VISIBLE);
            }
            loadData.onLoading();
        }
    }

    public void loadCompleted(){
        isLoading=false;
        footer.setVisibility(View.GONE);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.totalCount = totalItemCount;
        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
    }

    public void setInterface(LoadData loadData){
        this.loadData=loadData;
    }

    public interface LoadData {

        void onLoading();
    }

}
