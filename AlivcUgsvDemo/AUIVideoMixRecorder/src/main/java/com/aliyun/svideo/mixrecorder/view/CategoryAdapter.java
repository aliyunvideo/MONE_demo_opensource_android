/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.mixrecorder.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.svideo.base.Form.ResourceForm;
import com.aliyun.svideo.base.widget.CircularImageView;
import com.aliyun.svideo.mixrecorder.view.effects.filter.EffectInfo;
import com.aliyun.svideo.record.R;
import com.aliyun.ugsv.common.utils.LanguageUtils;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private Context mContext;
    private OnItemClickListener mItemClick;
    private ArrayList<ResourceForm> data = new ArrayList<>();
    private OnMoreClickListener mMoreClickListener;
    private int mSelectedPosition = 0;
    private static final int VIEW_TYPE_SELECTED = 1;
    private static final int VIEW_TYPE_UNSELECTED = 2;

    public CategoryAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(ArrayList<ResourceForm> data) {
        if (data == null) {
            return;
        }
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.alivc_recorder_item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CategoryViewHolder categoryViewHolder = (CategoryViewHolder)holder;
        ResourceForm form = data.get(position);
        int viewType = getItemViewType(position);
        if (form.isMore()) {
            categoryViewHolder.mName.setVisibility(View.GONE);
            categoryViewHolder.mImage.setVisibility(View.VISIBLE);
            categoryViewHolder.mImage.setImageResource(R.mipmap.aliyun_svideo_more);
        } else {
            categoryViewHolder.mImage.setVisibility(View.GONE);
            categoryViewHolder.mName.setVisibility(View.VISIBLE);
            String name = form.getName();
            if (!LanguageUtils.isCHEN(mContext) && form.getNameEn() != null) {
                name = form.getNameEn();
            }
            categoryViewHolder.mName.setText(name);
        }
        categoryViewHolder.itemView.setTag(holder);
        categoryViewHolder.itemView.setOnClickListener(this);
        switch (viewType) {
        case VIEW_TYPE_SELECTED:
            categoryViewHolder.itemView.setSelected(true);
            break;
        case VIEW_TYPE_UNSELECTED:
            categoryViewHolder.itemView.setSelected(false);
            break;
        default:
            break;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void selectPosition(int categoryIndex) {
        int lasPos = mSelectedPosition;
        mSelectedPosition = categoryIndex;
        notifyItemChanged(mSelectedPosition);
        notifyItemChanged(lasPos);
    }

    private static class CategoryViewHolder extends RecyclerView.ViewHolder {

        CircularImageView mImage;
        TextView mName;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.category_image_source);
            mName = itemView.findViewById(R.id.tv_category_name_source);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClick = listener;
    }

    @Override
    public void onClick(View view) {
        CategoryViewHolder viewHolder = (CategoryViewHolder)view.getTag();
        int position = viewHolder.getAdapterPosition();
        ResourceForm form = data.get(position);
        if (form.isMore()) {
            if (mMoreClickListener != null) {
                mMoreClickListener.onMoreClick();
            }
        } else {
            if (mItemClick != null) {
                EffectInfo effectInfo = new EffectInfo();
                effectInfo.isCategory = true;
                int lastPos = mSelectedPosition;
                mSelectedPosition = viewHolder.getAdapterPosition();
                mItemClick.onItemClick(effectInfo, viewHolder.getAdapterPosition());
                notifyItemChanged(lastPos);
                notifyItemChanged(mSelectedPosition);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mSelectedPosition) {
            return VIEW_TYPE_SELECTED;
        } else {
            return VIEW_TYPE_UNSELECTED;
        }
    }

    public void setMoreClickListener(OnMoreClickListener moreClickListener) {
        mMoreClickListener = moreClickListener;
    }

    public interface OnMoreClickListener {
        void onMoreClick();
    }

    public interface OnItemClickListener {
        boolean onItemClick(EffectInfo effectInfo, int index);
    }
}
