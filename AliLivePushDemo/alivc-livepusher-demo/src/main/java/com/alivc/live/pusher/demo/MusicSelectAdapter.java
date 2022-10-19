package com.alivc.live.pusher.demo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alivc.live.pusher.demo.bean.MusicInfo;

import java.util.ArrayList;

public class MusicSelectAdapter extends RecyclerView.Adapter<MusicSelectAdapter.MusicViewHolder> {


    private OnItemClick mOnItemClick = null;

    private ArrayList<MusicInfo> musicList = new ArrayList<>();

    private int mPosition = 0;


    public MusicSelectAdapter(Context context) {
        MusicInfo info = new MusicInfo(context.getResources().getString(R.string.no_music), "", "", "");
        musicList.add(info);
        ArrayList<MusicInfo> list = Common.getResource();
        musicList.addAll(list);
        MusicInfo info1 = new MusicInfo(context.getResources().getString(R.string.internet_music), "", "", "http://docs-aliyun.cn-hangzhou.oss.aliyun-inc.com/assets/attach/51991/cn_zh/1511776743437/JUST%202017.mp3");
        musicList.add(info1);
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_select_item_info, parent, false);
        MusicViewHolder holder = new MusicViewHolder(itemView);
        holder.tvMusicName = (TextView) itemView.findViewById(R.id.music_name);
        holder.tvMusicCheck = (ImageView) itemView.findViewById(R.id.music_check);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mOnItemClick != null) {
                    mOnItemClick.onItemClick(musicList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                }
                int lastPosition  = mPosition;
                mPosition  = holder.getAdapterPosition();
                notifyItemChanged(lastPosition);
                notifyItemChanged(mPosition);

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final MusicViewHolder holder, final int position) {
        MusicInfo musicInfo = musicList.get(position);

        if(mPosition == position) {
            holder.tvMusicCheck.setVisibility(View.VISIBLE);
        } else {
            holder.tvMusicCheck.setVisibility(View.GONE);
        }
        if (musicInfo != null) {
            holder.tvMusicName.setText(musicInfo.getMusicName());
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    class MusicViewHolder extends RecyclerView.ViewHolder{
        TextView tvMusicName;
        ImageView tvMusicCheck;

        public MusicViewHolder(View itemView) {
            super(itemView);
        }
    }


    public void setOnItemClick(OnItemClick onItemClick) {
        mOnItemClick = onItemClick;
    }





    public interface OnItemClick {
        void onItemClick(MusicInfo musicInfo, int position);
    }
}
