package xyz.zpayh.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 文 件 名: BaseMultiAdapter
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/01/22 12:38
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间: 2016/01/23 0:51
 * 修改备注:
 */

public abstract class BaseMultiSelectAdapter extends BaseAdapter<IMultiSelectItem>
    implements MultiSelect {

    private OnItemCheckedChangeListener mOnItemCheckedChangeListener;

    @Override
    public int getLayoutRes(int index) {
        final IMultiItem data = mData.get(index);
        return data.getLayoutRes();
    }

    @Override
    public void convert(BaseViewHolder holder, IMultiSelectItem data, int index) {
        holder.setChecked(data.getCheckableViewId(),data.isChecked());
        data.convert(holder);
    }

    @Override
    public void clearSelectAll() {
        for (IMultiSelectItem selectorItem : mData) {
            selectorItem.setChecked(false);
        }
        notifyDataSetChanged();
    }

    @Override
    public void selectAll() {
        for (IMultiSelectItem selectorItem : mData) {
            selectorItem.setChecked(true);
        }
        notifyDataSetChanged();
    }

    @Override
    public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener onCheckedChangeListener) {
        mOnItemCheckedChangeListener = onCheckedChangeListener;
    }

    @Override
    public List<IMultiSelectItem> getSelectedItems() {
        List<IMultiSelectItem> selectedItems = new ArrayList<>();
        for (IMultiSelectItem selectorItem : mData) {
            if (selectorItem.isChecked()){
                selectedItems.add(selectorItem);
            }
        }
        return selectedItems;
    }

    @Override
    protected void bindData(BaseViewHolder baseViewHolder, int layoutRes) {
        baseViewHolder.setOnItemCheckedChangeListener(new OnItemCheckedChangeListener() {
            @Override
            public void onItemCheck(@NonNull View view, boolean isChecked, int adapterPosition) {
                final int id = view.getId();
                final IMultiSelectItem item = getData(adapterPosition);
                if (item != null && id == item.getCheckableViewId()){
                    item.setChecked(isChecked);
                    notifyItemChanged(adapterPosition);
                }
                if (mOnItemCheckedChangeListener != null){
                    mOnItemCheckedChangeListener.onItemCheck(view, isChecked, adapterPosition);
                }
            }
        });
        super.bindData(baseViewHolder, layoutRes);
    }
}
