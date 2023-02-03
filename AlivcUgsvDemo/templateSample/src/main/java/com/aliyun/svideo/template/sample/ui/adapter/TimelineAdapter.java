package com.aliyun.svideo.template.sample.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineHolder> {
    private int mWidth;
    private int mHeight;
    private int[] mTimePositions;
    private HashMap<Integer, Bitmap> mData;
    private Uri mUri;

    @NonNull
    @Override
    public TimelineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setBackgroundColor(Color.GRAY);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(mWidth, mHeight));
        return new TimelineHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineHolder holder, int position) {
        //Bitmap bm = mData.get(mTimePositions[position]);
        //ImageView itemView = (ImageView) holder.itemView;
        //if (bm != null) {
        //    itemView.setImageBitmap(bm);
        //} else {
        //    itemView.setImageBitmap(null);
        //}

        RequestOptions options = RequestOptions.frameOf(mTimePositions[position]);
        RequestOptions cacheOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE);
        Glide.with(holder.itemView.getContext())
                .load(mUri)
                .apply(options)
                .apply(cacheOptions)
                .into(((ImageView) holder.itemView));
    }

    @Override
    public int getItemCount() {
        return mTimePositions == null ? 0 : mTimePositions.length;
    }

    public void setData(int[] timePositions, HashMap<Integer, Bitmap> data) {
        mTimePositions = timePositions;
        this.mData = data;
        notifyDataSetChanged();
    }

    public void setBitmapSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void setVideoUri(Uri uri) {
        mUri = uri;
    }

    static class TimelineHolder extends RecyclerView.ViewHolder {
        public TimelineHolder(View itemView) {
            super(itemView);
        }
    }
}
