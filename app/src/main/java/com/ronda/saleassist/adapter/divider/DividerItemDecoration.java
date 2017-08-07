package com.ronda.saleassist.adapter.divider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * User: Ronda(1575558177@qq.com)
 * Date: 2016/10/24
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    private Drawable mDivider;
    private int mOrientation;
    public DividerItemDecoration(Context context, int orientation) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);//调用结束后务必调用recycle()方法，否则这次的设定会对下次的使用造成影响
        mDivider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
    }
    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }
    /*
    这个onDraw()方法每次滑动的时候都会执行一次
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }
    private void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();//Android中的getWidth()是包括内边距的。
//        final int childCount = parent.getChildCount();//这个childCount，就是当前屏幕中显示的item的个数
        final int childCount = parent.getChildCount()-1;//这个childCount，就是当前屏幕中显示的item的个数
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            //RecyclerView v = new RecyclerView(parent.getContext());
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;//这里的bottomMargin因为没有设置，所以默认为0
            final int bottom = top + mDivider.getIntrinsicHeight();// getIntrinsicHeight()值为1
            Log.d("TAG","top:"+top+", getTop:"+child.getTop());//100 0
            Log.d("TAG","bottom:"+bottom+", getBottom:"+child.getBottom()); //101 100
            //drawable的setBounds()这个方法表示drawable将在被绘制在canvas的哪个矩形区域内，这个四参数分别表示距离原点左上右下的距离
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
    private void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicWidth();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();//获取当前绘制的ItemView的position
        int childCount = parent.getAdapter().getItemCount(); //这个获取的是Adapter中的ItemView的总个数
        //parent.getChildCount(); //注意：这个获取的是RecyclerView中显示的ItemView的个数（即屏幕上我们能够看到的ItemView的个数），并不是总数
        System.out.println("itemPosition："+ itemPosition+", count:"+parent.getChildCount());
        if (mOrientation == VERTICAL_LIST) {
            if (childCount == itemPosition + 1) { //如果是最后一项，则不显示底部分割线
                outRect.set(0, 0, 0, 0);
            } else {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            }
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }
}