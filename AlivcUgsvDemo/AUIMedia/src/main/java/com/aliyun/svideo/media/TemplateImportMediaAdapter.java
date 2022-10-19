package com.aliyun.svideo.media;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 模板导入适配器
 */
public class TemplateImportMediaAdapter extends RecyclerView.Adapter<TemplateImportMediaAdapter.TemplateImportViewHolder> {
    private HashMap<Integer, MediaInfo> mDataMap = new HashMap<>();
    private List<Long> mTemplateParams = new ArrayList<>();
    private MediaImageLoader mImageLoader;

    private int mSelectIndex = 0;

    private OnItemViewCallback mItemViewCallback;


    public TemplateImportMediaAdapter(MediaImageLoader imageLoader) {
        this.mImageLoader = imageLoader;
    }

    public void setTemplateParams(List<Long> templateParams) {
        mTemplateParams.clear();
        mTemplateParams.addAll(templateParams);
    }

    public void putData(MediaInfo info) {
        if (this.mSelectIndex == -1) {
            return;
        }
        mDataMap.put(this.mSelectIndex, info);
        int nextSelectIndex = -1;
        for (int i = 0; i < mTemplateParams.size(); i++) {
            if (mDataMap.get(i) == null) {
                nextSelectIndex = i;
                break;
            }
        }
        this.mSelectIndex = nextSelectIndex;
        if (this.mSelectIndex != -1) {
            if (mItemViewCallback != null) {
                mItemViewCallback.onDurationChange(mTemplateParams.get(this.mSelectIndex));
            }
        } else {
            if (mItemViewCallback != null) {
                List<MediaInfo> dataList = new ArrayList<>();
                for (int i = 0; i < mTemplateParams.size(); i++) {
                    dataList.add(mDataMap.get(i));
                }
                mItemViewCallback.onDurationChange(-1);
                mItemViewCallback.onFinish(dataList);
            }
        }
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mDataMap.remove(position);
    }

    @Override
    public TemplateImportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TemplateImportViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alivc_media_item_video_template_import, parent, false));
    }

    @Override
    public void onBindViewHolder(TemplateImportViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        float duration = mTemplateParams.get(position) / 1000.f;
        final MediaInfo info = mDataMap.get(position);
        if (info != null) {
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.tvDuration.setVisibility(View.GONE);
            mImageLoader.displayImage(info, holder.ivCover);
        } else {
            DecimalFormat df = new DecimalFormat("#.##");
            holder.tvDuration.setText(df.format(duration) + "S");
            holder.tvDuration.setVisibility(View.VISIBLE);
            holder.ivDelete.setVisibility(View.GONE);
            holder.ivCover.setImageResource(R.drawable.shape_rect_template_import_bg);
        }
        holder.tvIndex.setText(String.valueOf(position + 1));
        holder.ivSelect.setVisibility(position == mSelectIndex ? View.VISIBLE : View.GONE);
        holder.ivCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemViewCallback != null) {
                    if (info != null) {
                        mItemViewCallback.onItemPhotoClick(info, position);
                    } else {
                        mSelectIndex = position;
                        mItemViewCallback.onDurationChange(mTemplateParams.get(position));
                    }
                }
                notifyDataSetChanged();
            }
        });
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectIndex = position;
                if (mItemViewCallback != null) {
                    mItemViewCallback.onItemDeleteClick(info);
                    mItemViewCallback.onDurationChange(mTemplateParams.get(position));
                }
                remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTemplateParams.size();
    }

    static class TemplateImportViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivCover;
        public View ivSelect;
        public TextView tvDuration;
        public ImageView ivDelete;
        public TextView tvIndex;

        public TemplateImportViewHolder(View itemView) {
            super(itemView);
            ivCover = (ImageView) itemView.findViewById(R.id.iv_cover);
            ivSelect = itemView.findViewById(R.id.iv_select);
            tvDuration = (TextView) itemView.findViewById(R.id.tv_duration);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
            tvIndex = (TextView) itemView.findViewById(R.id.iv_index);
        }

    }

    public void setItemViewCallback(OnItemViewCallback itemViewCallback) {
        mItemViewCallback = itemViewCallback;
    }

    public interface OnItemViewCallback {
        void onItemPhotoClick(MediaInfo info, int position);

        void onItemDeleteClick(MediaInfo info);

        void onDurationChange(long currDuration);

        void onFinish(List<MediaInfo> data);
    }
}
