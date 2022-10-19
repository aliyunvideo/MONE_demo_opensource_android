package com.aliyun.svideo.editor.panel;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.aio.avbaseui.widget.AVPanelItemView;
import com.aliyun.svideo.editor.AUIVideoEditorActivity;
import com.aliyun.svideo.editor.R;
import com.aliyun.svideo.editor.clip.audioeffect.AudioEffectPanelController;
import com.aliyun.svideo.editor.clip.enhance.EnhancePanelController;
import com.aliyun.svideo.editor.clip.speed.SpeedEffectListener;
import com.aliyun.svideo.editor.clip.speed.SpeedPanelController;
import com.aliyun.svideo.editor.clip.trasition.OnTransitionChangedListener;
import com.aliyun.svideo.editor.clip.trasition.TransitionPanelController;
import com.aliyun.svideo.editor.clip.volume.VolumePanelController;
import com.aliyun.svideo.track.MainTrackContainer;
import com.aliyun.svideo.track.bean.BaseClipInfo;
import com.aliyun.svideo.track.inc.ClipTrackListener;
import com.aliyun.svideo.track.util.Util;
import com.aliyun.svideosdk.common.struct.effect.TransitionBase;
import com.aliyun.svideosdk.common.struct.project.TimeFilter;
import com.aliyun.svideosdk.common.struct.project.VideoTrackClip;
import com.aliyun.svideosdk.editor.AliyunIEditor;
import com.aliyun.svideosdk.editor.TimeEffectType;

import java.util.List;

/**
 * 剪辑面板
 */
public class ClipPanel extends BasePanel implements OnClickListener, OnTransitionChangedListener, SpeedEffectListener {

    private MainTrackContainer mClipTrackContainer;
    private ImageView mClose;
    private AVPanelItemView mItemSpeed;
    private AVPanelItemView mItemVolume;
    private AVPanelItemView mItemEnhance;
    private AVPanelItemView mItemTransition;
    private AVPanelItemView mItemAudioEffect;
    private SpeedPanelController mSpeedPanelController;
    private EnhancePanelController mEnhancePanelController;
    private AudioEffectPanelController mAudioEffectPanelController;
    private TransitionPanelController mTransitionPanelController;
    private VolumePanelController mVolumePanelController;
    private float mSpeed = 1.0f;

    public ClipPanel(@NonNull Context context) {
        this(context, null);
    }

    public ClipPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInitView() {
        LayoutInflater.from(getContext()).inflate(R.layout.ugsv_editor_layout_panel_clip, this, true);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mClose = findViewById(R.id.btn_close);
        mClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getContext() instanceof AUIVideoEditorActivity) {
                    ((AUIVideoEditorActivity) v.getContext()).onBackPressed();
                }
            }
        });
        mItemSpeed = findViewById(R.id.ugsv_editor_item_speed);
        mItemSpeed.setOnClickListener(this);
        mItemVolume = findViewById(R.id.ugsv_editor_item_volume);
        mItemVolume.setOnClickListener(this);
        mItemEnhance = findViewById(R.id.ugsv_editor_item_enhance);
        mItemEnhance.setOnClickListener(this);
        mItemTransition = findViewById(R.id.ugsv_editor_item_transition);
        mItemTransition.setOnClickListener(this);
        mItemAudioEffect = findViewById(R.id.ugsv_editor_item_audio_effect);
        mItemAudioEffect.setOnClickListener(this);

        mClipTrackContainer = new MainTrackContainer(getContext());
        mClipTrackContainer.setListener(new ClipTrackListener() {
            @Override
            public void onFocusChanged(BaseClipInfo clipInfo, boolean isFocus) {

            }

            @Override
            public void onTransitionClick(int index) {
                mTransitionPanelController.showPanel(index);
            }

            @Override
            public void onScrollChangedTime(long time) {
                time = time * 1000;
                mAliyunIEditor.seek(time);
                updateProgress();
            }
        });

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.topMargin = Util.dp2px(50);
        layoutParams.bottomMargin = Util.dp2px(60);
        addView(mClipTrackContainer, layoutParams);

    }


    @Override
    public boolean onBackPressed() {
        return mSpeedPanelController.onBackPress() || mEnhancePanelController.onBackPress()
            || mAudioEffectPanelController.onBackPress() || mTransitionPanelController.onBackPress() || mVolumePanelController.onBackPress();
    }

    @Override
    public void onInitData(PanelManger panelManger, AliyunIEditor editor, int panelType) {
        super.onInitData(panelManger, editor, panelType);
        List<VideoTrackClip> list = editor.getEditorProject().getTimeline().getPrimaryTrack().getVideoTrackClips();
        mClipTrackContainer.setVideoData(list);
        mSpeedPanelController = new SpeedPanelController(getPanelManger().getContentLayout(), editor);
        mEnhancePanelController = new EnhancePanelController(getPanelManger().getContentLayout(), editor);
        mAudioEffectPanelController = new AudioEffectPanelController(getPanelManger().getContentLayout(), editor);
        mTransitionPanelController = new TransitionPanelController(getPanelManger().getContentLayout(), editor);
        mVolumePanelController = new VolumePanelController(getPanelManger().getContentLayout(), editor);
        mTransitionPanelController.setOnTransitionChangedListener(this);
        mSpeedPanelController.setSpeedEffectListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPlayProgress(long currentPlayTime, long currentStreamPlayTime) {
        mClipTrackContainer.updatePlayProgress((long) (currentPlayTime / 1000 * mSpeed));
        updateProgress();
    }

    @Override
    public void onClick(View view) {
        if (view == mItemSpeed) {
            mSpeedPanelController.showPanel();
        } else if (view == mItemEnhance) {
            mEnhancePanelController.showPanel();
        } else if (view == mItemVolume) {
            mVolumePanelController.showPanel();
        } else if (view == mItemTransition) {

        } else if (view == mItemAudioEffect) {
            mAudioEffectPanelController.showPanel();
        }
    }

    public void updateProgress() {
        mSpeedPanelController.onUpdateProgress();
        mAudioEffectPanelController.onUpdateProgress();
        mEnhancePanelController.onUpdateProgress();
        mTransitionPanelController.onUpdateProgress();
        mVolumePanelController.onUpdateProgress();
    }

    @Override
    public void onUpdateTransition(int index, @Nullable TransitionBase transition) {
        long overlapDuration = transition == null ? 0 : transition.getOverlapDuration();
        mClipTrackContainer.updateTransition(index + 1, overlapDuration / 1000, transition != null);
        if (getContext() instanceof AUIVideoEditorActivity) {
            ((AUIVideoEditorActivity) getContext()).updateDuration(mAliyunIEditor.getDuration());
        }
    }

    @Override
    public void onSpeedChanged(float speed) {
        mAliyunIEditor.stop();
        for (TimeFilter item:mAliyunIEditor.getEditorProject().getAllTimeFilters()) {
            if (TimeEffectType.TIME_EFFECT_TYPE_RATE.ordinal() == item.getTimeFilterType()) {
                mAliyunIEditor.deleteTimeEffect(item.getId());
                break;
            }
        }
        mSpeed = speed;
        mAliyunIEditor.rate(speed, 0, mAliyunIEditor.getDuration(), false);
        mAliyunIEditor.play();
        if (getContext() instanceof AUIVideoEditorActivity) {
            ((AUIVideoEditorActivity) getContext()).updateDuration(mAliyunIEditor.getDuration());
        }
    }
}
