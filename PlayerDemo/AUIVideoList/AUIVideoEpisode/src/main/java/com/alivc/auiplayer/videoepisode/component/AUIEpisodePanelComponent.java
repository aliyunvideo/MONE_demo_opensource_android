package com.alivc.auiplayer.videoepisode.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.auiplayer.videoepisode.R;
import com.alivc.auiplayer.videoepisode.adapter.AUIEpisodePanelAdapter;
import com.alivc.auiplayer.videoepisode.data.AUIEpisodeData;
import com.alivc.auiplayer.videoepisode.data.AUIEpisodeDataEvent;
import com.alivc.auiplayer.videoepisode.listener.OnPanelEventListener;
import com.alivc.player.videolist.auivideolistcommon.view.CenterLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author keria
 * @date 2023/8/29
 * @brief 短剧选集列表组件
 */
public class AUIEpisodePanelComponent extends FrameLayout {
    private OnPanelEventListener mOnPanelEventListener;

    private final AUIEpisodePanelAdapter mAdapter = new AUIEpisodePanelAdapter();

    private TextView mTitleTextView;
    private ImageView mRetractImageView;
    private RecyclerView mRecyclerView;

    public AUIEpisodePanelComponent(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AUIEpisodePanelComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AUIEpisodePanelComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ilr_view_episode_panel, this);

        mTitleTextView = view.findViewById(R.id.tv_title);

        mRetractImageView = view.findViewById(R.id.iv_retract);
        mRetractImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPanelEventListener != null) {
                    mOnPanelEventListener.onClickRetract();
                }
            }
        });

        CenterLayoutManager layoutManager = new CenterLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemViewCacheSize(0);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.initListener(mOnPanelEventListener);
    }

    public void initListener(OnPanelEventListener listener) {
        mOnPanelEventListener = listener;
        mAdapter.initListener(mOnPanelEventListener);
    }

    public void initData(AUIEpisodeData episodeData) {
        if (episodeData == null || episodeData.list == null || episodeData.list.isEmpty()) {
            return;
        }
        mAdapter.initData(episodeData.list);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AUIEpisodeDataEvent event) {
        if (event == null) {
            return;
        }
        if (mRecyclerView != null) {
            mRecyclerView.scrollToPosition(event.curIndex);
        }
        if (mTitleTextView != null) {
            mTitleTextView.setText(event.episodeVideoInfo != null ? event.episodeVideoInfo.getTitle() : null);
        }
        mAdapter.notifyItemChanged(event.preIndex);
        mAdapter.notifyItemChanged(event.curIndex);
    }
}
