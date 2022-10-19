/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.recorder.views.effects.filter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.ugsv.common.utils.LanguageUtils;
import com.aliyun.ugsv.common.utils.image.AbstractImageLoaderTarget;
import com.aliyun.ugsv.common.utils.image.ImageLoaderImpl;
import com.aliyun.svideo.recorder.R;
import com.aliyun.svideosdk.common.struct.project.Source;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * 滤镜 Adapter
 */
public class AUIFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private OnFilterItemClickListener mItemClickListener;
    private int mSelectedPos = -1;
    private FilterViewHolder mSelectedHolder;
    private List<String> mFilterList;

    public AUIFilterAdapter(Context context, List<String> dataList) {
        this.mContext = context;
        this.mFilterList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ugsv_recorder_chooser_filter_item, parent, false);
        FilterViewHolder filterViewHolder = new FilterViewHolder(view);
        return filterViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final FilterViewHolder filterViewHolder = (FilterViewHolder) holder;
        String name;
        String path = mFilterList.get(position);
        if (path == null || "".equals(path)) {
            name = mContext.getString(R.string.ugsv_recorder_panel_effect_none);
            filterViewHolder.mImage.setImageResource(R.drawable.ic_ugsv_recorder_chooser_clear);
        } else {
            name = getFilterName(path);
            if (filterViewHolder != null) {
                new ImageLoaderImpl().loadImage(mContext, path + "/icon.png")
                        .into(filterViewHolder.mImage, new AbstractImageLoaderTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource) {
                                filterViewHolder.mImage.setImageDrawable(resource);
                            }
                        });
            }
        }
        filterViewHolder.mName.setText(name);

        if (mSelectedPos > mFilterList.size()) {
            mSelectedPos = 0;
        }

        updateItem(filterViewHolder, position == mSelectedPos);

        filterViewHolder.itemView.setTag(holder);
        filterViewHolder.itemView.setOnClickListener(this);
    }

    private void updateItem(final FilterViewHolder filterViewHolder, boolean isSelected) {
        if (isSelected) {
            filterViewHolder.mMask.setVisibility(View.VISIBLE);
            filterViewHolder.mName.setTextColor(mContext.getResources().getColor(R.color.text_ultraweak));
            filterViewHolder.mName.setBackgroundColor(mContext.getResources().getColor(R.color.fill_weak));
            mSelectedHolder = filterViewHolder;
        } else {
            filterViewHolder.mMask.setVisibility(View.GONE);
            filterViewHolder.mName.setTextColor(mContext.getResources().getColor(R.color.text_strong));
            filterViewHolder.mName.setBackgroundColor(mContext.getResources().getColor(R.color.fill_medium));
        }
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }

    private static class FilterViewHolder extends RecyclerView.ViewHolder {
        View mMask;
        ImageView mImage;
        TextView mName;

        public FilterViewHolder(View itemView) {
            super(itemView);
            mMask = itemView.findViewById(R.id.ugsv_recorder_filter_mask);
            mImage = itemView.findViewById(R.id.ugsv_recorder_filter_icon);
            mName = itemView.findViewById(R.id.ugsv_recorder_filter_name);
        }
    }

    @Override
    public void onClick(View view) {
        if (mItemClickListener != null) {
            FilterViewHolder viewHolder = (FilterViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            if (mSelectedPos != position) {
                if (mSelectedHolder != null) {
                    updateItem(mSelectedHolder, false);
                }
                mSelectedPos = position;
                updateItem(viewHolder, true);

                AUIEffectInfo effectInfo = new AUIEffectInfo();
                effectInfo.setPath(mFilterList.get(position));
                Source source = new Source(mFilterList.get(position));
                source.setId(String.valueOf(position));
                effectInfo.setSource(source);
                effectInfo.id = position;
                mItemClickListener.onItemClick(effectInfo, position);
            }
        }
    }

    public void setOnItemClickListener(OnFilterItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setDataList(List<String> list) {
        mFilterList.clear();
        mFilterList.add(null);
        mFilterList.addAll(list);
    }

    public void setSelectedPos(int position) {
        mSelectedPos = position;
    }

    /**
     * 获取滤镜名称 适配系统语言/中文或其他
     *
     * @param path 滤镜文件目录
     * @return name
     */
    private String getFilterName(String path) {
        if (LanguageUtils.isCHEN(mContext)) {
            path = path + "/config.json";
        } else {
            path = path + "/configEn.json";
        }
        String name = "";
        StringBuffer var2 = new StringBuffer();
        File var3 = new File(path);

        try {
            FileReader var4 = new FileReader(var3);

            int var7;
            while ((var7 = var4.read()) != -1) {
                var2.append((char) var7);
            }

            var4.close();
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        try {
            JSONObject var4 = new JSONObject(var2.toString());
            name = var4.optString("name");
        } catch (JSONException var5) {
            var5.printStackTrace();
        }
        return name;
    }
}
