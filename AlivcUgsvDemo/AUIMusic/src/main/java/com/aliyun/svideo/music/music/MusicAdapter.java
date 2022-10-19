package com.aliyun.svideo.music.music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.aio.avbaseui.widget.AVCircleProgressView;
import com.aliyun.svideo.music.R;
import com.aliyun.svideo.music.widget.MusicHorizontalScrollView;
import com.aliyun.svideo.music.widget.MusicWaveView;
import com.aliyun.svideosdk.common.struct.form.IMVForm;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter {
    private static final String TAG = MusicAdapter.class.getSimpleName();
    private List<EffectBody<MusicFileBean>> dataList = new ArrayList<>();
    private long mStreamDuration = 10 * 1000;
    private OnMusicSeek onMusicSeek;
    private int mSelectIndex = -1;
    private int[] mScrollX;
    private int mCachePosition;
    private int mCacheStartTime;


    private ArrayList<MusicFileBean> mLoadingMusic = new ArrayList<>();

    private static final int VIEW_TYPE_NO = 0;
    private static final int VIEW_TYPE_LOCAL = 1;
    private static final int VIEW_TYPE_REMOTE = 2;
    private static final int VIEW_TYPE_DOWNLOADING = 3;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicViewHolder(
                   LayoutInflater.from(parent.getContext()).inflate(R.layout.alivc_music_item_music, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        ((MusicViewHolder)holder).updateData(holder.itemView.getContext(), position, dataList.get(position));
        int viewType = getItemViewType(position);
        final MusicFileBean musicFileBean = dataList.get(position).getData();
        switch (viewType) {
        case VIEW_TYPE_LOCAL:

            ((MusicViewHolder)holder).downloadProgress.setVisibility(View.GONE);
            ((MusicViewHolder)holder).mDownloadView.setVisibility(View.GONE);
            ((MusicViewHolder)holder).mUseButton.setVisibility(View.VISIBLE);
            if (position == mSelectIndex) {
                ((MusicViewHolder)holder).mLocalIcon.setVisibility(View.VISIBLE);
                ((MusicViewHolder)holder).mUseButton.setVisibility(View.GONE);
                ((MusicViewHolder)holder).musicName.setSelected(true);
                ((MusicViewHolder)holder).musicSinger.setSelected(true);
                ((MusicViewHolder)holder).musicName.setText(musicFileBean.getTitle());
                if (musicFileBean.artist == null || musicFileBean.artist.isEmpty()) {
                    ((MusicViewHolder)holder).musicSinger.setVisibility(View.INVISIBLE);
                } else {
                    ((MusicViewHolder)holder).musicSinger.setVisibility(View.VISIBLE);
                    ((MusicViewHolder)holder).musicSinger.setText(musicFileBean.artist);
                }

                ((MusicViewHolder)holder).musicInfoLayout.setVisibility(View.VISIBLE);
                ((MusicViewHolder)holder).musicWave.setDisplayTime(mStreamDuration);
                ((MusicViewHolder)holder).musicWave.setTotalTime(musicFileBean.duration);
                ((MusicViewHolder)holder).musicWave.layout();
                ((MusicViewHolder)holder).musicWave.setVisibility(View.VISIBLE);
                ((MusicViewHolder)holder).scrollBar.setScrollViewListener(new MusicHorizontalScrollView.ScrollViewListener() {
                    @Override
                    public void onScrollChanged(HorizontalScrollView scrollView, int x, int y, int oldx, int oldy) {
                        if (position < mScrollX.length) { //添加判断，解决选择音乐片段的时候，切换模式引起的数组越界问题
                            mScrollX[position] = x;
                            setDurationTxt(((MusicViewHolder)holder), x, musicFileBean.duration);
                        }

                    }

                    @Override
                    public void onScrollStop() {

                        if (onMusicSeek != null && position < mScrollX.length) { //添加判断，解决选择音乐片段的时候，切换模式引起的数组越界问题
                            onMusicSeek.onSeekStop((int) ((float)mScrollX[position] / ((MusicViewHolder)holder).musicWave.getMusicLayoutWidth() * musicFileBean.duration));
                        }
                    }
                });
                Log.i(TAG, "position : " + position + " ,mScrollX : " + mScrollX[position]);
                if (position != 0 && mCachePosition == position && musicFileBean.duration != 0) {
                    //恢复选中的开始时间
                    mScrollX[position] = mCacheStartTime * ((MusicViewHolder)holder).musicWave.getMusicLayoutWidth() / musicFileBean.duration;
                    //这里需要一个延时scroll，不然首次无法正确定位
                    ((MusicViewHolder)holder).scrollBar.post(new Runnable() {
                        @Override
                        public void run() {
                            if (position < mScrollX.length) {
                                ((MusicViewHolder)holder).scrollBar.scrollTo(mScrollX[position], 0);
                            }
                        }
                    });
                } else if (mScrollX.length > position && mScrollX[position] != 0) {
                    //上下滑动的时候恢复选择的音乐片段
                    ((MusicViewHolder)holder).scrollBar.post(new Runnable() {
                        @Override
                        public void run() {
                            ((MusicViewHolder)holder).scrollBar.scrollTo(mScrollX[position], 0);
                            Log.i(TAG, "position : " + position + " ,scrollBar.scrollTo : " + mScrollX[position]);
                        }
                    });

                } else {
                    ((MusicViewHolder)holder).scrollBar.scrollTo(0, 0);
                }

            } else {
                ((MusicViewHolder)holder).mLocalIcon.setVisibility(View.GONE);
                ((MusicViewHolder)holder).musicInfoLayout.setVisibility(View.GONE);
                ((MusicViewHolder)holder).scrollBar.setScrollViewListener(null);
                ((MusicViewHolder)holder).musicName.setSelected(false);
                ((MusicViewHolder)holder).musicSinger.setSelected(false);
            }
            break;
        case VIEW_TYPE_REMOTE:
            ((MusicViewHolder)holder).mLocalIcon.setVisibility(View.GONE);
            ((MusicViewHolder)holder).downloadProgress.setVisibility(View.GONE);
            ((MusicViewHolder)holder).musicInfoLayout.setVisibility(View.GONE);
            ((MusicViewHolder)holder).mDownloadView.setVisibility(View.VISIBLE);
            ((MusicViewHolder)holder).mUseButton.setVisibility(View.GONE);
            ((MusicViewHolder)holder).scrollBar.setScrollViewListener(null);
            ((MusicViewHolder)holder).musicName.setSelected(false);
            ((MusicViewHolder)holder).musicSinger.setSelected(false);
            if (mSelectIndex == position) {
                ((MusicViewHolder)holder).musicName.setSelected(true);
                ((MusicViewHolder)holder).musicSinger.setSelected(true);
            } else {
                ((MusicViewHolder)holder).musicName.setSelected(false);
                ((MusicViewHolder)holder).musicSinger.setSelected(false);
            }
            break;
        case VIEW_TYPE_DOWNLOADING:
            ((MusicViewHolder)holder).mLocalIcon.setVisibility(View.GONE);
            ((MusicViewHolder)holder).downloadProgress.setVisibility(View.VISIBLE);
            ((MusicViewHolder)holder).musicInfoLayout.setVisibility(View.GONE);
            ((MusicViewHolder)holder).mDownloadView.setVisibility(View.GONE);
            ((MusicViewHolder)holder).mUseButton.setVisibility(View.GONE);
            ((MusicViewHolder)holder).scrollBar.setScrollViewListener(null);
            ((MusicViewHolder)holder).musicName.setSelected(false);
            ((MusicViewHolder)holder).musicSinger.setSelected(false);
            if (mSelectIndex == position) {
                ((MusicViewHolder)holder).musicName.setSelected(true);
                ((MusicViewHolder)holder).musicSinger.setSelected(true);
            } else {
                ((MusicViewHolder)holder).musicName.setSelected(false);
                ((MusicViewHolder)holder).musicSinger.setSelected(false);
            }
            break;
        default:
            break;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public void setData(ArrayList<EffectBody<MusicFileBean>> dataList, int selectIndex) {

        this.dataList.clear();
        this.dataList.addAll(dataList);
        mScrollX = new int[this.dataList.size()];
        //mSelectIndex = selectIndex;
        //if (onMusicSeek != null) {
        //    onMusicSeek.onSelectMusic(selectIndex, this.dataList.get(selectIndex));
        //}
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        int type = VIEW_TYPE_NO;

        if (position >= 0 && position < dataList.size()) {
            EffectBody<MusicFileBean> data = dataList.get(position);
            if (data.isLocal()) {
                return VIEW_TYPE_LOCAL;
            } else if (data.isLoading()) {
                return VIEW_TYPE_DOWNLOADING;
            } else {
                return VIEW_TYPE_REMOTE;
            }
        }
        return type;
    }
    /**
     * 下载开始
     * @param musicBody
     */
    public void notifyDownloadingStart(EffectBody<MusicFileBean> musicBody) {
        if (!mLoadingMusic.contains(musicBody.getData())) {
            mLoadingMusic.add(musicBody.getData());
            musicBody.setLoading(true);
        }
    }

    /**
     * 下载结束
     * @param viewHolder
     * @param mvBody
     * @param position
     */
    public synchronized void notifyDownloadingComplete(MusicViewHolder viewHolder, EffectBody<MusicFileBean> mvBody, int position) {
        mvBody.setLocal(true);
        mvBody.setLoading(false);
        mLoadingMusic.remove(mvBody.getData());
        notifyItemChanged(position);
        //下载完成后，重置downloadProgress控件的Progress
        if (viewHolder != null && viewHolder.downloadProgress != null) {
            viewHolder.downloadProgress.setProgress(0);
        }
    }

    public void updateProcess(MusicViewHolder viewHolder, int process, int position) {
        if (viewHolder != null && viewHolder.mPosition == position) {
            viewHolder.mLocalIcon.setVisibility(View.GONE);
            viewHolder.downloadProgress.setVisibility(View.VISIBLE);
            viewHolder.downloadProgress.setProgress(process);
        }
    }

    public void notifySelectPosition(int cacheStartTime, int cachePosition) {
        mCacheStartTime = cacheStartTime;
        mCachePosition = cachePosition;
        mSelectIndex = cachePosition;
        notifyDataSetChanged();
    }

    /**
     * 监听接口，外部实现
     */
    interface OnItemClickListener {
        /**
         * 外部实现下载事件
         * @param position 点击位置
         * @param data 数据
         */
        void onRemoteItemClick(int position, EffectBody<IMVForm> data);

        /**
         * 外部实现应用此mv
         * @param position 点击位置
         * @param data 该位置数据
         */
        void onLocalItemClick(int position, EffectBody<IMVForm> data);
    }
    class MusicViewHolder extends RecyclerView.ViewHolder {
        public TextView musicName;
        public TextView musicSinger;
        public MusicWaveView musicWave;
        public RelativeLayout musicInfoLayout;
        public RelativeLayout musicNameLayout;
        public MusicHorizontalScrollView scrollBar;
        public TextView musicStartTxt;
        public TextView musicEndTxt;
        public ImageView mLocalIcon;
        private AVCircleProgressView downloadProgress;
        private EffectBody<MusicFileBean> mData;
        private int mPosition;
        private ImageView mIconView;
        private ImageView mDownloadView;
        private TextView mUseButton;
        private TextView mDuration;
        public void updateData(Context context, int position, EffectBody<MusicFileBean> data) {
            this.mData = data;
            this.mPosition = position;
            MusicFileBean music = data.getData();
            musicName.setText(music.title);
            if (music.artist == null || music.artist.isEmpty()) {
                musicSinger.setVisibility(View.INVISIBLE);
            } else {
                musicSinger.setVisibility(View.VISIBLE);
                musicSinger.setText(music.artist);
            }
            String duration = formatDuration2Str(music.duration / 1000);
            mDuration.setText(duration);
            Glide.with(mIconView).load(music.image).placeholder(R.drawable.ic_ugsv_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .override(152, 152)
                .into(mIconView);
        }
        public MusicViewHolder(View itemView) {
            super(itemView);
            musicName = (TextView) itemView.findViewById(R.id.ugsv_music_name);
            musicSinger = (TextView) itemView.findViewById(R.id.ugsv_music_artist);
            musicWave = (MusicWaveView) itemView.findViewById(R.id.aliyun_wave_view);
            musicInfoLayout =  itemView.findViewById(R.id.ugsv_music_info_layout);
            musicNameLayout =  itemView.findViewById(R.id.ugsv_music_name_layout);
            scrollBar = (MusicHorizontalScrollView) itemView.findViewById(R.id.aliyun_scroll_bar);
            musicStartTxt = (TextView) itemView.findViewById(R.id.ugsv_music_start_txt);
            musicEndTxt = (TextView) itemView.findViewById(R.id.ugsv_music_end_txt);
            mLocalIcon = itemView.findViewById(R.id.ugsv_music_local_iv);
            mIconView = itemView.findViewById(R.id.ugsv_music_image);
            mDownloadView = itemView.findViewById(R.id.ugsv_music_download);
            mUseButton = itemView.findViewById(R.id.ugsv_music_use);
            mDuration = itemView.findViewById(R.id.ugsv_music_duration);
            downloadProgress = itemView.findViewById(R.id.ugsv_music_progress);
            setDurationTxt(this, 0, 0);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectIndex != mPosition) {
                        if (onMusicSeek != null) {
                            onMusicSeek.onSelectMusic(mPosition, mData);
                        }
                        mSelectIndex = mPosition;
                        if (mSelectIndex < mScrollX.length) {
                            //解决mSelectIndex引起的角标越界
                            mScrollX[mSelectIndex] = 0;
                            notifyDataSetChanged();

                        }
                    }
                }
            });
        }

    }
    private void setDurationTxt(MusicViewHolder holder, int x, int duration) {
        long leftTime = (int) ((float)x / holder.musicWave.getMusicLayoutWidth() * duration);
        long rightTime = leftTime + mStreamDuration;
        long time = leftTime / 1000;
        holder.musicStartTxt.setText(formatDuration2Str(time));
        time = rightTime / 1000;
        holder.musicEndTxt.setText(formatDuration2Str(time));
    }

    private String formatDuration2Str(long timeSec) {
        long min = timeSec / 60;
        long sec = timeSec % 60;
        return String.format("%1$02d:%2$02d", min, sec);
    }

    public interface OnMusicSeek {
        void onSeekStop(long start);
        void onSelectMusic(int position, EffectBody<MusicFileBean> effectBody);
    }

    public void setStreamDuration(long streamDuration) {
        this.mStreamDuration = streamDuration;
    }

    public void setOnMusicSeekListener(OnMusicSeek onMusicSeek) {
        this.onMusicSeek = onMusicSeek;
    }

    public int getSelectIndex() {
        return mSelectIndex;
    }
}
