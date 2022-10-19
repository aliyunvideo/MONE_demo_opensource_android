package com.aliyun.aio.avbaseui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.aio.avbaseui.widget.AVActionBar;
import com.aliyun.aio.avtheme.AVBaseThemeActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class AVBaseListActivity extends AVBaseThemeActivity {

    private AVActionBar mAVActionBar;
    private RecyclerView mRecyclerView;
    private AVListAdapter mAVListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.av_activity_list_layout);
        initBaseView();
        initBaseData();
    }

    private void initBaseView() {
        mAVActionBar = findViewById(R.id.av_base_title);
        mRecyclerView = findViewById(R.id.av_base_main_recyclerView);
        mAVListAdapter = new AVListAdapter(this);
        mRecyclerView.setAdapter(mAVListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAVActionBar.getLeftImageView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initBaseData() {
        mAVActionBar.getTitleView().setText(getTitleResId());
        if (showBackBtn()) {
            mAVActionBar.showLeftView();
        } else {
            mAVActionBar.hideLeftView();
        }
        mAVListAdapter.setData(createListData());
    }

    /**
     * 标题的resourceID， 不要直接使用文本，要适配多语言
     * @return
     */
    public abstract int getTitleResId();

    /**
     * 是否显示返回按钮
     * @return
     */
    public abstract boolean showBackBtn();
    public abstract List<ListModel> createListData();
    public abstract void onListItemClick(ListModel model);

    public static class ListModel {
        public int index;
        public int drawableResId;
        public String title;
        public String desc;

        public ListModel(int index, int drawableResId, String title, String desc) {
            this.index = index;
            this.drawableResId = drawableResId;
            this.title = title;
            this.desc = desc;
        }
    }

    private static class AVListAdapter extends RecyclerView.Adapter<AVListHolder> {

        private List<ListModel> mData = new ArrayList<>();
        private WeakReference<AVBaseListActivity> mAVBaseListActivityRef;

        public AVListAdapter(AVBaseListActivity activity) {
            mAVBaseListActivityRef = new WeakReference<>(activity);
        }

        public void setData(List<ListModel> data) {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }

        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public AVListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.av_base_list_item, parent, false);
            AVListHolder avListHolder = new AVListHolder(itemView);
            avListHolder.mImageView = itemView.findViewById(R.id.av_list_item_image);
            avListHolder.mTitle = itemView.findViewById(R.id.av_list_item_title);
            avListHolder.mDesc = itemView.findViewById(R.id.av_list_item_desc);
            return avListHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull AVListHolder holder, int position) {
            ListModel listModel = mData.get(position);
            if (listModel == null) {
                return;
            }
            holder.mImageView.setImageResource(listModel.drawableResId);
            holder.mTitle.setText(listModel.title);
            if(!TextUtils.isEmpty(listModel.desc)) {
                holder.mDesc.setText(listModel.desc);
                holder.mDesc.setVisibility(View.VISIBLE);
            } else {
                holder.mDesc.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(view -> {
                if (mAVBaseListActivityRef.get() != null) {
                    mAVBaseListActivityRef.get().onListItemClick(mData.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private static class AVListHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTitle;
        TextView mDesc;
        public AVListHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
