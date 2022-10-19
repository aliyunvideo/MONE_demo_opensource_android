/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.recorder.views.effects.filter.animfilter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.svideo.base.Form.I18nBean;
import com.aliyun.ugsv.common.utils.LanguageUtils;
import com.aliyun.ugsv.common.utils.image.AbstractImageLoaderTarget;
import com.aliyun.ugsv.common.utils.image.ImageLoaderImpl;
import com.aliyun.svideo.recorder.R;
import com.aliyun.svideo.recorder.utils.RecordCommon;
import com.aliyun.svideosdk.common.struct.effect.EffectFilter;
import com.aliyun.svideosdk.common.struct.project.Source;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * 特效 Adapter
 */
public class AUISpecialEffectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private OnSpecialEffectItemClickListener mItemClick;
    private int mSelectedPos = -1;
    private FilterViewHolder mSelectedHolder;
    private List<String> mFilterList;

    public AUISpecialEffectAdapter(Context context, List<String> dataList) {
        mContext = context;
        mFilterList = dataList;
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

        if (mSelectedPos > mFilterList.size() || mSelectedPos < 0) {
            mSelectedPos = 0;
        }

        updateItem(filterViewHolder, mSelectedPos == position);

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

    public void setOnItemClickListener(OnSpecialEffectItemClickListener listener) {
        mItemClick = listener;
    }

    /**
     * 恢复选中的
     *
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
        View mMask;
        ImageView mImage;
        TextView mName;

        private FilterViewHolder(View itemView) {
            super(itemView);
            mMask = itemView.findViewById(R.id.ugsv_recorder_filter_mask);
            mImage = itemView.findViewById(R.id.ugsv_recorder_filter_icon);
            mName = itemView.findViewById(R.id.ugsv_recorder_filter_name);
        }
    }

    @Override
    public void onClick(View view) {
        if (mItemClick != null) {
            FilterViewHolder viewHolder = (FilterViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            if (mSelectedPos != position) {
                if (mSelectedHolder != null) {
                    updateItem(mSelectedHolder, false);
                }
                mSelectedPos = position;
                updateItem(viewHolder, true);
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

    public void setSelectedPosition(int position) {
        mSelectedPos = position;
    }

    /**
     * 获取滤镜名称 适配系统语言/中文或其他
     *
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

        if (LanguageUtils.isCHEN(mContext)) {
            return effectI18n.getZh_cn();
        } else {
            return effectI18n.getEn();
        }
    }
}
