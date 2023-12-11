/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.mixrecorder.view.effects.filter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aliyun.svideo.record.R;
import com.aliyun.svideo.base.widget.CircularImageView;
import com.aliyun.svideosdk.common.struct.project.Source;
import com.aliyun.ugsv.common.utils.LanguageUtils;
import com.aliyun.ugsv.common.utils.image.AbstractImageLoaderTarget;
import com.aliyun.ugsv.common.utils.image.ImageLoaderImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * 滤镜adapter
 * @author xlx
 */
public class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements View.OnClickListener {

    private static final String TAG = "FilterAdapter";
    private Context mContext;
    private OnFilterItemClickListener mItemClick;
    private int mSelectedPos = -1;
    private FilterViewHolder mSelectedHolder;
    private List<String> mFilterList;

    public FilterAdapter(Context context, List<String> dataList) {
        this.mContext = context;

        this.mFilterList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.alivc_recorder_item_effect_filter, parent, false);
        FilterViewHolder filterViewHolder = new FilterViewHolder(view);
        filterViewHolder.frameLayout = (FrameLayout)view.findViewById(R.id.resource_image);
        return filterViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final FilterViewHolder filterViewHolder = (FilterViewHolder)holder;
        String name = mContext.getString(R.string.alivc_recorder_dialog_filter_none);
        String path = mFilterList.get(position);
        if (path == null || "".equals(path)) {
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.alivc_svideo_effect_none);

            filterViewHolder.mImage.setImageDrawable(drawable);
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

        if (mSelectedPos > mFilterList.size()) {
            mSelectedPos = 0;
        }

        if (mSelectedPos == position) {
            filterViewHolder.mImage.setSelected(true);
            mSelectedHolder = filterViewHolder;
        } else {
            filterViewHolder.mImage.setSelected(false);
        }
        filterViewHolder.mName.setText(name);
        filterViewHolder.itemView.setTag(holder);
        filterViewHolder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }



    private static class FilterViewHolder extends RecyclerView.ViewHolder {

        FrameLayout frameLayout;

        CircularImageView mImage;
        TextView mName;

        public FilterViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.resource_image_view);
            mName = (TextView)itemView.findViewById(R.id.resource_name);
        }

    }

    public void setOnItemClickListener(OnFilterItemClickListener listener) {
        mItemClick = listener;
    }


    @Override
    public void onClick(View view) {
        if (mItemClick != null) {
            FilterViewHolder viewHolder = (FilterViewHolder)view.getTag();
            int position = viewHolder.getAdapterPosition();
            if (mSelectedPos != position) {
                if (mSelectedHolder != null && mSelectedHolder.mImage != null) {
                    mSelectedHolder.mImage.setSelected(false);
                }
                viewHolder.mImage.setSelected(true);
                mSelectedPos = position;
                mSelectedHolder = viewHolder;

                AUIEffectInfo effectInfo = new AUIEffectInfo();
//                effectInfo.type = UIEditorPage.FILTER_EFFECT;
                effectInfo.setPath(mFilterList.get(position));
                Source source = new Source(mFilterList.get(position));
                source.setId(String.valueOf(position));
                effectInfo.setSource(source);
                effectInfo.id = position;
                mItemClick.onItemClick(effectInfo, position);
            }
        }
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
                var2.append((char)var7);
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
