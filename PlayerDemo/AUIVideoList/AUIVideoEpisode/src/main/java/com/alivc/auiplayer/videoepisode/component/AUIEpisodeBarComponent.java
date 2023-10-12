package com.alivc.auiplayer.videoepisode.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alivc.auiplayer.videoepisode.R;
import com.alivc.auiplayer.videoepisode.data.AUIEpisodeData;

/**
 * @author keria
 * @date 2023/8/30
 * @brief 短剧页面底部bar组件
 */
public class AUIEpisodeBarComponent extends FrameLayout {
    private TextView mTitleTextView;

    public AUIEpisodeBarComponent(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AUIEpisodeBarComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AUIEpisodeBarComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ilr_view_episode_bar, this);
        mTitleTextView = view.findViewById(R.id.tv_title);
    }

    public void initData(AUIEpisodeData episodeData) {
        mTitleTextView.setText(episodeData != null ? episodeData.title : "");
    }
}
