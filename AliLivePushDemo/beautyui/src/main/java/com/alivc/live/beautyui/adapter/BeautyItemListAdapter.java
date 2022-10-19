package com.alivc.live.beautyui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.live.beautyui.R;
import com.alivc.live.beautyui.bean.BeautyItemBean;
import com.alivc.live.beautyui.component.CircleImageView;
import com.alivc.live.beautyui.listener.BeautyItemListener;

import java.util.ArrayList;

/**
 * 美颜界面Item适配器
 */
public class BeautyItemListAdapter extends RecyclerView.Adapter {

    private static final String TAG = BeautyItemListAdapter.class.getSimpleName();

    private Context mContext;
    private BeautyItemListener mBeautyItemListener;

    private final ArrayList<BeautyItemBean> itemBeans = new ArrayList<BeautyItemBean>();
    private final ArrayList<BeautyItemBean> defaultItemBeans = new ArrayList<BeautyItemBean>();

    private BeautyItemBean mCurrentSelectedItemBean;
    private boolean canMultiSelect;

    private BeautyItemListAdapter() {
    }

    public BeautyItemListAdapter(Context context) {
        mContext = context;
    }

    public void setCanMultiSelect(boolean canMultiSelect) {
        this.canMultiSelect = canMultiSelect;
    }

    public void setItemBeans(ArrayList<BeautyItemBean> itemBeans) {
        this.itemBeans.addAll(itemBeans);

        for (BeautyItemBean itemBean : itemBeans) {
            this.defaultItemBeans.add(itemBean.clone());
        }

        notifyDataSetChanged();
    }

    public void setBeautyItemListener(BeautyItemListener listener) {
        mBeautyItemListener = listener;
    }

    public void resetAllItemViews() {

        for (int i = 0; i < itemBeans.size(); ++i) {
            BeautyItemBean itemBean = itemBeans.get(i);
            BeautyItemBean defaultItemBean = getDefaultItemBean(i);

            if (defaultItemBean != null && !itemBean.equals(defaultItemBean)) {
                BeautyItemBean newItemBean = defaultItemBean.clone();
                itemBeans.set(i, newItemBean);

                BeautyItemBean.BeautyItemType itemType = newItemBean.getItemType();
                // only seek bar value & switch value should be callback and reset.
                if (itemType == BeautyItemBean.BeautyItemType.SEEK_BAR || itemType == BeautyItemBean.BeautyItemType.SWITCH) {
                    mBeautyItemListener.onItemDataChanged(newItemBean);
                }

            }
        }

        notifyDataSetChanged();
    }

    @Nullable
    private BeautyItemBean getDefaultItemBean(int index) {
        if (index < defaultItemBeans.size()) {
            return defaultItemBeans.get(index);
        }
        return null;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BeautyItemBean itemBean = itemBeans.get(viewType);
        if (itemBean != null && itemBean.getItemType() == BeautyItemBean.BeautyItemType.DIVIDER) {
            return new BeautyItemDividerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_beauty_item_divider, parent, false));
        } else {
            return new BeautyItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_beauty_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BeautyItemViewHolder) {
            final BeautyItemBean itemBean = itemBeans.get(position);

            BeautyItemViewHolder viewHolder = (BeautyItemViewHolder) holder;
            viewHolder.initData(mContext, itemBean);

            final BeautyItemBean.BeautyItemType itemType = itemBean.getItemType();
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemType != BeautyItemBean.BeautyItemType.NONE && itemType != BeautyItemBean.BeautyItemType.DIVIDER) {
                        // When item list can't be multi selected,
                        // we should disable the status of current selected item.
                        if (!canMultiSelect && mCurrentSelectedItemBean != null && mCurrentSelectedItemBean != itemBean) {
                            mCurrentSelectedItemBean.setSelected(false);
                            // Callback the unselected item data back.
                            if (mBeautyItemListener != null) {
                                mBeautyItemListener.onItemDataChanged(mCurrentSelectedItemBean);
                            }
                            mCurrentSelectedItemBean = null;
                        }

                        itemBean.setSelected(!itemBean.isSelected());
                        mCurrentSelectedItemBean = itemBean;

                        if (mBeautyItemListener != null) {
                            mBeautyItemListener.onItemClicked(itemBean);
                        }

                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemBeans.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class BeautyItemViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView mCircleIv;
        private TextView mTitleTv;

        public BeautyItemViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews(itemView);
        }

        private void initViews(View view) {
            if (view == null) return;
            mCircleIv = view.findViewById(R.id.image_iv);
            mTitleTv = view.findViewById(R.id.beauty_mid_content_title_tv);
        }

        public void initData(@Nullable Context context, @Nullable BeautyItemBean beautyItemBean) {

            if (context == null || beautyItemBean == null) return;

            // Only when the itemType is switch or seekbar, it will display whether selected or not.
            BeautyItemBean.BeautyItemType itemType = beautyItemBean.getItemType();
            boolean showSelectedStatus = (itemType == BeautyItemBean.BeautyItemType.SWITCH || itemType == BeautyItemBean.BeautyItemType.SEEK_BAR);
            boolean isSelected = showSelectedStatus && beautyItemBean.isSelected();

            if (mCircleIv != null) {
                if (beautyItemBean.getImageResId() != 0) {
                    int imageResId = isSelected ? beautyItemBean.getEnableImageResId() : beautyItemBean.getImageResId();
                    mCircleIv.setImageResource(imageResId);
                } else {
                    mCircleIv.setCircleBackgroundColor(context.getColor(R.color.color_bg_beauty_type_default));
                }
            }

            if (mTitleTv != null) {
                mTitleTv.setText(beautyItemBean.getTitle());
                mTitleTv.setSelected(isSelected);
            }

        }
    }

    static class BeautyItemDividerViewHolder extends RecyclerView.ViewHolder {
        public BeautyItemDividerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
