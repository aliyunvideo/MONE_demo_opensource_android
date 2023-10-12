package com.alivc.auiplayer.videoepisode.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alivc.auiplayer.videoepisode.R;
import com.alivc.auiplayer.videoepisode.data.AUIEpisodeVideoInfo;
import com.alivc.auiplayer.videoepisode.listener.OnInteractiveEventListener;

/**
 * @author keria
 * @date 2023/8/29
 * @brief Feed流页面交互组件（点赞、评论、分享）
 */
public class AUIVideoInteractiveComponent extends FrameLayout {

    private LinearLayout mLikeLayout;
    private LinearLayout mCommentLayout;
    private LinearLayout mShareLayout;

    private ImageView mLikeImageView;

    private TextView mLikeTextview;
    private TextView mCommentTextview;
    private TextView mShareTextview;

    private AUIEpisodeVideoInfo mAUIEpisodeVideoInfo = null;
    private OnInteractiveEventListener mOnViewEventListener = null;

    public AUIVideoInteractiveComponent(@NonNull Context context) {
        super(context);
        initViews(context);
    }

    public AUIVideoInteractiveComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public AUIVideoInteractiveComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ilr_view_video_interactive, this);

        mLikeLayout = view.findViewById(R.id.layout_like);
        mCommentLayout = view.findViewById(R.id.layout_comment);
        mShareLayout = view.findViewById(R.id.layout_share);

        mLikeImageView = view.findViewById(R.id.iv_like);

        mLikeTextview = view.findViewById(R.id.tv_like);
        mCommentTextview = view.findViewById(R.id.tv_comment);
        mShareTextview = view.findViewById(R.id.tv_share);

        mLikeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean viewSelected = mLikeImageView.isSelected();
                if (mOnViewEventListener != null) {
                    mOnViewEventListener.onClickLike(mAUIEpisodeVideoInfo, !viewSelected);
                }
                updateView();
            }
        });

        mCommentLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewEventListener != null) {
                    mOnViewEventListener.onClickComment(mAUIEpisodeVideoInfo);
                }
            }
        });

        mShareLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewEventListener != null) {
                    mOnViewEventListener.onClickShare(mAUIEpisodeVideoInfo);
                }
            }
        });
    }

    public void initData(AUIEpisodeVideoInfo episodeVideoInfo) {
        if (episodeVideoInfo == null) {
            return;
        }
        mAUIEpisodeVideoInfo = episodeVideoInfo;
        updateView();
    }

    private void updateView() {
        if (mAUIEpisodeVideoInfo == null) {
            return;
        }
        mLikeTextview.setText(AUIEpisodeVideoInfo.formatNumber(mAUIEpisodeVideoInfo.likeCount));
        mCommentTextview.setText(AUIEpisodeVideoInfo.formatNumber(mAUIEpisodeVideoInfo.commentCount));
        mShareTextview.setText(AUIEpisodeVideoInfo.formatNumber(mAUIEpisodeVideoInfo.shareCount));
        mLikeImageView.setSelected(mAUIEpisodeVideoInfo.isLiked);
    }

    public void initListener(OnInteractiveEventListener listener) {
        mOnViewEventListener = listener;
    }
}
