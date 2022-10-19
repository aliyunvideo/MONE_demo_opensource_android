package com.aliyun.player.alivcplayerexpand.view.more;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.auiplayerserver.bean.VideoInfo;
import com.aliyun.player.alivcplayerexpand.R;

import java.util.List;

public class SelectSeriesView extends FrameLayout {

    private SelectSeriesAdapter mSelectSeriesAdapter;

    public SelectSeriesView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SelectSeriesView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SelectSeriesView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
       View view = LayoutInflater.from(context).inflate(R.layout.alivc_dialog_select_series, this, true);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        mSelectSeriesAdapter = new SelectSeriesAdapter();
        recyclerView.setAdapter(mSelectSeriesAdapter);
    }

    private OnVideoItemClickListener mOnVideoItemClickListener;
    public void setOnItemClickListener(OnVideoItemClickListener onItemClickListener) {
        mOnVideoItemClickListener = onItemClickListener;
    }

    public void setSeriesData(int position, List<VideoInfo> list) {
        if (mSelectSeriesAdapter != null){
            mSelectSeriesAdapter.setmOnVideoItemClickListener(new OnVideoItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (mOnVideoItemClickListener != null){
                        mOnVideoItemClickListener.onItemClick(position);
                    }
                }
            });
            mSelectSeriesAdapter.update(list,position);
        }
    }


    public interface OnVideoItemClickListener {
        void onItemClick(int position);
    }

}
