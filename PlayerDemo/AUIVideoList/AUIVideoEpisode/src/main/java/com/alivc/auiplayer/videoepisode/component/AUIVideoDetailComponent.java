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
import com.alivc.auiplayer.videoepisode.data.AUIEpisodeVideoInfo;
import com.alivc.auiplayer.videoepisode.listener.OnDetailEventListener;

/**
 * @author keria
 * @date 2023/8/29
 * @brief Feed流页面详情组件（用户名、视频详情）
 */
public class AUIVideoDetailComponent extends FrameLayout {

    private TextView mUserNickTextview;
    // TODO: Expanding and collapsing not supported currently
    private TextView mDescriptionTextview;

    private AUIEpisodeVideoInfo mAUIEpisodeVideoInfo = null;

    private OnDetailEventListener mOnDetailEventListener = null;

    public AUIVideoDetailComponent(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AUIVideoDetailComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AUIVideoDetailComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ilr_view_video_detail, this);
        mUserNickTextview = view.findViewById(R.id.tv_user_nick);
        mDescriptionTextview = view.findViewById(R.id.tv_description);
    }

    public void initData(AUIEpisodeVideoInfo episodeVideoInfo) {
        if (episodeVideoInfo == null) {
            return;
        }
        mAUIEpisodeVideoInfo = episodeVideoInfo;

        mUserNickTextview.setText(String.format("@%s", episodeVideoInfo.getAuthor()));
        mUserNickTextview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDetailEventListener != null) {
                    mOnDetailEventListener.onClickAuthor(episodeVideoInfo);
                }
            }
        });

        mDescriptionTextview.setText(episodeVideoInfo.getTitle());
    }

    public void initListener(OnDetailEventListener listener) {
        mOnDetailEventListener = listener;
    }
}
