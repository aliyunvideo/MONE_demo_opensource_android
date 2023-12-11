package com.alivc.live.interactive_common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.alivc.live.commonutils.FastClickUtil;
import com.alivc.live.interactive_common.R;

public class InteractiveSettingView extends LinearLayout {

    private ImageView mCameraImageView;
    private ImageView mMuteImageView;
    private ImageView mSpeakerPhoneImageView;
    private ImageView mEnableAudioImageView;
    private ImageView mEnableVideoImageView;
    private OnInteractiveSettingListener mOnInteractiveSettingListener;

    private boolean mEnableAudioCapture = true;
    private boolean mEnableVideoCapture = true;

    public InteractiveSettingView(Context context) {
        super(context);
        init(context);
    }

    public InteractiveSettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InteractiveSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_interactive_setting_view, this, true);
        mCameraImageView = inflate.findViewById(R.id.iv_camera);
        mMuteImageView = inflate.findViewById(R.id.iv_mute);
        mSpeakerPhoneImageView = inflate.findViewById(R.id.iv_speaker_phone);
        mEnableAudioImageView = inflate.findViewById(R.id.iv_enable_audio);
        mEnableVideoImageView = inflate.findViewById(R.id.iv_enable_video);

        initListener();
    }

    private void initListener() {
        mCameraImageView.setOnClickListener(view -> {
            if (mOnInteractiveSettingListener != null) {
                if (!FastClickUtil.isFastClick()) {
                    mOnInteractiveSettingListener.onSwitchCameraClick();
                }

            }
        });

        mMuteImageView.setOnClickListener(view -> {
            if (mOnInteractiveSettingListener != null) {
                if (!FastClickUtil.isFastClick()) {
                    mOnInteractiveSettingListener.onMuteClick();
                }
            }
        });

        mSpeakerPhoneImageView.setOnClickListener(view -> {
            if (mOnInteractiveSettingListener != null) {
                if (!FastClickUtil.isFastClick()) {
                    Boolean tag = (Boolean) mSpeakerPhoneImageView.getTag();
                    if (tag == null || !tag) {
                        mSpeakerPhoneImageView.setColorFilter(R.color.text_blue);
                        mSpeakerPhoneImageView.setTag(true);
                    } else {
                        mSpeakerPhoneImageView.clearColorFilter();
                        mSpeakerPhoneImageView.setTag(false);
                    }
                    mOnInteractiveSettingListener.onSpeakerPhoneClick();
                }
            }
        });

        mEnableAudioImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                mEnableAudioCapture = !mEnableAudioCapture;
                mOnInteractiveSettingListener.onEnableAudioClick(mEnableAudioCapture);
            }
        });

        mEnableVideoImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                mEnableVideoCapture = !mEnableVideoCapture;
                mOnInteractiveSettingListener.onEnableVideoClick(mEnableVideoCapture);
            }
        });
    }

    public void changeMute(boolean isMute) {
        mMuteImageView.setImageResource(isMute ? R.drawable.ic_interact_volume_off : R.drawable.ic_interact_volume_on);
    }

    public void setOnInteractiveSettingListener(OnInteractiveSettingListener listener) {
        this.mOnInteractiveSettingListener = listener;
    }

    public interface OnInteractiveSettingListener {
        void onSwitchCameraClick();

        void onMuteClick();

        void onSpeakerPhoneClick();

        void onEnableAudioClick(boolean enable);

        void onEnableVideoClick(boolean enable);
    }
}
