package com.aliyun.svideo.template.sample.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.svideosdk.template.model.AliyunAETemplateAsset;
import com.aliyun.ugsv.common.utils.image.ImageLoaderImpl;
import com.aliyun.svideo.template.sample.R;
import com.aliyun.svideosdk.template.model.AliyunAETemplateAssetMedia;
import com.aliyun.svideosdk.template.model.AliyunAETemplateAssetText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 模板编辑配器
 */
public class TemplateEditorAdapter extends RecyclerView.Adapter<TemplateEditorAdapter.TemplateEditorViewHolder> implements View.OnClickListener {
    private List<AliyunAETemplateAsset> mData = new ArrayList<>();
    private OnItemClickCallback mOnItemClickCallback;
    private String mSelectedGroup;

    @Override
    public TemplateEditorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TemplateEditorViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alivc_editor_template_editor_item, parent, false));
    }

    public void setData(List<AliyunAETemplateAsset> data) {
        this.mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    public List<AliyunAETemplateAsset> getData() {
        return mData;
    }

    @Override
    public void onBindViewHolder(final TemplateEditorViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final AliyunAETemplateAsset item = mData.get(position);
        if (item instanceof AliyunAETemplateAssetMedia) {
            holder.mediaContainer.setVisibility(View.VISIBLE);
            holder.textContainer.setVisibility(View.GONE);
            new ImageLoaderImpl().loadImage(holder.mediaCover.getContext(), ((AliyunAETemplateAssetMedia) item).getReplacePath()).into(holder.mediaCover);
            DecimalFormat df = new DecimalFormat("#.##");
            holder.duration.setText(df.format(item.getTimelineOut() - item.getTimelineIn()) + "S");
            holder.duration.setVisibility(View.VISIBLE);
            if (item.getKey().equals(mSelectedGroup)) {
                holder.mediaEdit.setVisibility(View.VISIBLE);
            } else {
                holder.mediaEdit.setVisibility(View.GONE);
            }
        } else {
            holder.mediaContainer.setVisibility(View.GONE);
            holder.textContainer.setVisibility(View.VISIBLE);
            holder.textCover.setText(((AliyunAETemplateAssetText) item).getReplaceText());
            if (item.getKey().equals(mSelectedGroup)) {
                holder.textEdit.setVisibility(View.VISIBLE);
            } else {
                holder.textEdit.setVisibility(View.GONE);
            }
        }
        holder.layoutItem.setTag(holder);
        holder.layoutItem.setOnClickListener(this);
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.layout_item) {
            TemplateEditorViewHolder holder = (TemplateEditorViewHolder) v.getTag();
            final int position = holder.getAdapterPosition();
            AliyunAETemplateAsset item = mData.get(position);
            if (item.getKey().equals(mSelectedGroup)) {
                if (mOnItemClickCallback != null) {
                    mOnItemClickCallback.onEdit(item, v);
                }
            } else {
                mSelectedGroup = item.getKey();
                if (mOnItemClickCallback != null) {
                    mOnItemClickCallback.onSelect(item, v);
                }
                notifyDataSetChanged();
            }
        }
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.mOnItemClickCallback = onItemClickCallback;
    }

    static class TemplateEditorViewHolder extends RecyclerView.ViewHolder {
        public View mediaContainer;
        public ImageView mediaCover;
        public TextView duration;
        public TextView mediaEdit;

        public View textContainer;
        public TextView textCover;
        public TextView textEdit;
        public View layoutItem;

        public TemplateEditorViewHolder(View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_item);
            mediaContainer = itemView.findViewById(R.id.media_layout);
            textContainer = itemView.findViewById(R.id.text_layout);
            mediaCover = itemView.findViewById(R.id.media_cover);
            duration = itemView.findViewById(R.id.duration);
            mediaEdit = itemView.findViewById(R.id.media_edit);
            textCover = itemView.findViewById(R.id.text_cover);
            textEdit = itemView.findViewById(R.id.text_edit);
        }
    }

    public interface OnItemClickCallback {

        void onEdit(AliyunAETemplateAsset param, View view);

        void onSelect(AliyunAETemplateAsset param, View view);
    }
}
