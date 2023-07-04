package com.alivc.live.interactive_common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alivc.live.commonutils.FastClickUtil;
import com.alivc.live.interactive_common.R;

/**
 * 用户连麦窗口显示'房间ID'和'用户ID'
 */
public class RoomAndUserInfoView extends ConstraintLayout {

    private TextView mUserInfoTextView;
    private ImageButton mInteractMuteImageButton;

    private OnClickEventListener mOnClickEventListener = null;
    private boolean mInteractMute = false;

    public RoomAndUserInfoView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public RoomAndUserInfoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoomAndUserInfoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View inflateView = LayoutInflater.from(context).inflate(R.layout.layout_room_user_info_view, this, true);

        mUserInfoTextView = inflateView.findViewById(R.id.tv_user_info);

        mInteractMuteImageButton = inflateView.findViewById(R.id.iv_interact_mute);
        mInteractMuteImageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick() || mOnClickEventListener == null) {
                    return;
                }
                mInteractMute = !mInteractMute;
                mOnClickEventListener.onClickInteractMute(mInteractMute);
                mInteractMuteImageButton.setBackgroundResource(mInteractMute ? R.drawable.ic_interact_mute : R.drawable.ic_interact_not_mute);
            }
        });
    }

    public void setUserInfo(String channelId, String userId) {
        mUserInfoTextView.setVisibility(View.VISIBLE);
        mUserInfoTextView.setText(String.format("cid:%s, uid:%s", channelId, userId));
    }

    public void enableMute(boolean mute) {
        mInteractMuteImageButton.setVisibility(mute ? View.VISIBLE : View.GONE);
    }

    public void initListener(OnClickEventListener listener) {
        mOnClickEventListener = listener;
    }

    public interface OnClickEventListener {
        void onClickInteractMute(boolean mute);
    }

}
