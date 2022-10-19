package com.alivc.live.beautyui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.live.beautyui.R;
import com.alivc.live.beautyui.bean.AnimojiItemBean;
import com.alivc.live.beautyui.component.CircleImageView;

import java.util.ArrayList;

public class AnimojiItemAdapter extends RecyclerView.Adapter {

    private final ArrayList<AnimojiItemBean> mItemBeans = new ArrayList<>();

    private static final String TAG = AnimojiItemAdapter.class.getSimpleName();

    private AnimojiItemListener mItemListener;

    public void setListener(AnimojiItemListener listener) {
        this.mItemListener = listener;
    }

    public void initData(ArrayList<AnimojiItemBean> itemBeans) {
        mItemBeans.addAll(itemBeans);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AnimojiItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_beauty_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AnimojiItemViewHolder) {
            final AnimojiItemBean itemBean = mItemBeans.get(position);
            AnimojiItemViewHolder viewHolder = (AnimojiItemViewHolder) holder;
            viewHolder.initData(itemBean);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mItemListener) {
                        mItemListener.onItemClicked(itemBean);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mItemBeans.size();
    }

    public interface AnimojiItemListener {
        void onItemClicked(@Nullable AnimojiItemBean itemBean);
    }

    static class AnimojiItemViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView mCircleIv;
        private TextView mTitleTv;

        public AnimojiItemViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews(itemView);
        }

        private void initViews(View view) {
            if (view == null) return;
            mCircleIv = view.findViewById(R.id.image_iv);
            mTitleTv = view.findViewById(R.id.beauty_mid_content_title_tv);
        }

        public void initData(@Nullable AnimojiItemBean bean) {
            if (null == bean) return;
            if (null != mCircleIv && bean.getId() != 0) {
                mCircleIv.setImageResource(bean.getId());
            }
            if (null != mTitleTv) {
                mTitleTv.setText(bean.getTitle());
            }
        }
    }
}
