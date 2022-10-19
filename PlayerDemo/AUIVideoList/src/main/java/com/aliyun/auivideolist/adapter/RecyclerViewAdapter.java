package com.aliyun.auivideolist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.auivideolist.R;
import com.aliyun.auivideolist.bean.ListVideoBean;
import com.bumptech.glide.Glide;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private final Context mContext;
    private List<ListVideoBean> mData;
    public OnItemClickListener mListener;

    public RecyclerViewAdapter(Context context){
        this.mContext = context;
    }

    public void setData(List<ListVideoBean> data){
        this.mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_item, parent, false);
        return new RecyclerViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        ListVideoBean listVideoBean = mData.get(position);
        Glide.with(mContext).asBitmap()
//                .dontAnimate()
//                .priority(Priority.HIGH)
//                .format(DecodeFormat.PREFER_RGB_565)
                .load(listVideoBean.getCoverURL())
//                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.mIvCover);

        holder.mVideoTitleTextView.setText(listVideoBean.getTitle());
        holder.mAuthorTextView.setText("阿里云");
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        private final FrameLayout mRootFrameLayout;
        private final ImageView mIvCover;
        private final AppCompatSeekBar mSeekBar;
        private final TextView mVideoTitleTextView;
        private final TextView mAuthorTextView;
        private final ImageView mPlayImageView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mRootFrameLayout = itemView.findViewById(R.id.fm_root);
            mIvCover = itemView.findViewById(R.id.iv_cover);
            mSeekBar = itemView.findViewById(R.id.seekbar);
            mVideoTitleTextView = itemView.findViewById(R.id.tv_video_title);
            mAuthorTextView = itemView.findViewById(R.id.tv_author);
            mPlayImageView = itemView.findViewById(R.id.iv_play);

            mRootFrameLayout.setOnClickListener(view -> {
                if(mListener != null){
                    mListener.onItemClick(getAdapterPosition());
                }
            });
        }

        public ImageView getCoverView() {
            return mIvCover;
        }

        public void showPlayIcon(boolean isShown){
            if(isShown){
                mPlayImageView.setVisibility(View.VISIBLE);
            }else{
                mPlayImageView.setVisibility(View.GONE);
            }
        }

        public FrameLayout getFrameLayout(){
            return mRootFrameLayout;
        }

        public AppCompatSeekBar getSeekBar(){
            return mSeekBar;
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
