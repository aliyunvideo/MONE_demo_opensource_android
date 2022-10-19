package com.zhihu.matisse.ui;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.model.SelectedItemCollection;

public class AVMediaSelectedAdapter extends RecyclerView.Adapter<AVMediaSelectedAdapter.MediaSelectedViewHolder> {

    private SelectedItemCollection mSelectedItemCollection;

    private SelectedItemClickListener mOnItemClickListener;
    
    private boolean showDuration = true;

    public void setSelectedItemCollection(SelectedItemCollection selectedItemCollection) {
        mSelectedItemCollection = selectedItemCollection;
        notifyDataSetChanged();
    }

    public AVMediaSelectedAdapter(boolean showDuration) {
        this.showDuration = showDuration;
    }

    @NonNull
    @Override
    public MediaSelectedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View content = LayoutInflater.from(parent.getContext()).inflate(R.layout.av_matisse_layout_selected_item, parent, false);
        MediaSelectedViewHolder viewHolder = new MediaSelectedViewHolder(content);
        viewHolder.mImageView = content.findViewById(R.id.av_matisse_item_image);
        viewHolder.mDuration = content.findViewById(R.id.av_matisse_item_duration);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MediaSelectedViewHolder holder, int position) {
        Item item = mSelectedItemCollection.asList().get(position);
        int size = 150;
        SelectionSpec.getInstance().imageEngine.loadImage(holder.itemView.getContext(), size, size, holder.mImageView, item.getContentUri());
        if (showDuration) {
            String duration = DateUtils.formatElapsedTime(item.duration / 1000);
            holder.mDuration.setText(duration);
        } else {
            holder.mDuration.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.OnSelectedItemLick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSelectedItemCollection.count();
    }

    static class MediaSelectedViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;
        TextView mDuration;
        public MediaSelectedViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setOnItemClickListener(SelectedItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface SelectedItemClickListener {
        void OnSelectedItemLick(Item item);
    }
}
