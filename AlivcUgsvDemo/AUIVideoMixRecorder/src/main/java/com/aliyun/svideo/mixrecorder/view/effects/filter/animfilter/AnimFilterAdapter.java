/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.mixrecorder.view.effects.filter.animfilter;

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

import com.aliyun.svideo.base.Form.I18nBean;
import com.aliyun.svideo.base.widget.CircularImageView;
import com.aliyun.svideo.record.R;
import com.aliyun.svideo.mixrecorder.util.RecordCommon;
import com.aliyun.svideosdk.common.struct.project.Source;
import com.aliyun.svideosdk.common.struct.effect.EffectFilter;
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
 * 滤镜特效adapter
 */
public class AnimFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements View.OnClickListener {

    private Context mContext;
    private OnAnimFilterItemClickListener mItemClick;
    private int mSelectedPos = -1;
    private FilterViewHolder mSelectedHolder;
    private List<String> mFilterList;

    public AnimFilterAdapter(Context context, List<String> dataList) {
        mContext = context;
        mFilterList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.alivc_recorder_item_effect_filter, parent, false);
        FilterViewHolder filterViewHolder = new FilterViewHolder(view);
        filterViewHolder.frameLayout = view.findViewById(R.id.resource_image);
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

    public void setOnItemClickListener(OnAnimFilterItemClickListener listener) {
        mItemClick = listener;
    }

    /**
     * 恢复选中的
     * @param currentEffect 当前的EffectFilter
     */
    public void setCurrentEffect(EffectFilter currentEffect) {
        if (currentEffect == null) {
            mSelectedPos = -1;
        } else {
            mSelectedPos = mFilterList.indexOf(currentEffect.getPath());
        }
    }

    private static class FilterViewHolder extends RecyclerView.ViewHolder {

        FrameLayout frameLayout;

        CircularImageView mImage;
        TextView mName;

        private FilterViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.resource_image_view);
            mName = itemView.findViewById(R.id.resource_name);
        }

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
                EffectFilter effectFilter = new EffectFilter(new Source(String.valueOf(position), mFilterList.get(position)));
                mItemClick.onItemClick(effectFilter, position);
            }
        }
    }

    public void setDataList(List<String> list) {
        if (list == null) {
            return;
        }
        mFilterList.clear();
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
        String name = "";
        I18nBean effectI18n = RecordCommon.getCurrentEffectI18n(path, "name");
        if (effectI18n == null) {
            if (LanguageUtils.isCHEN(mContext)) {
                path = path + "/config.json";
            } else {
                String pathEn = path + "/configEn.json";
                if (new File(pathEn).exists()) {
                    path = pathEn;
                } else {
                    path = path + "/config.json";
                }
            }
            StringBuilder var2 = new StringBuilder();
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

        if (LanguageUtils.isCHEN(mContext)) {
            return effectI18n.getZh_cn();
        } else {
            return effectI18n.getEn();
        }


    }
}
