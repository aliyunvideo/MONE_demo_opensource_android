package com.aliyun.svideo.editor.panel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.aliyun.svideo.editor.common.panel.OnItemClickListener;
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId;
import com.aliyun.svideo.editor.common.util.SpeedUtil;
import com.aliyun.svideo.editor.effect.VideoEffectClickListener;
import com.aliyun.svideo.editor.effect.VideoEffectEntryPanelView;
import com.aliyun.svideo.editor.effect.VideoEffectEntryViewModel;
import com.aliyun.svideo.editor.effect.VideoEffectManager;
import com.aliyun.svideo.editor.effect.VideoEffectSelectPanelView;
import com.aliyun.svideo.editor.effect.VideoEffectSelectViewModel;
import com.aliyun.svideo.editor.effect.VideoEffectTrack;
import com.aliyun.svideosdk.common.struct.effect.EffectFilter;
import com.aliyun.svideosdk.common.struct.effect.TrackEffectFilter;
import com.aliyun.svideosdk.common.struct.project.Source;
import com.aliyun.svideosdk.editor.AliyunIEditor;

import java.util.concurrent.TimeUnit;

public class EffectPanel extends BasePanel implements OnItemClickListener, VideoEffectClickListener {
    private VideoEffectEntryPanelView mVideoEffectEntryPanelView;
    private VideoEffectEntryViewModel mVideoEffectEntryViewModel;

    private VideoEffectSelectPanelView mVideoEffectSelectPanelView;
    private VideoEffectSelectViewModel mVideoEffectSelectViewModel;

    private VideoEffectManager mVideoEffectManager;
    private float mSpeed = 1.0f;

    public EffectPanel(@NonNull Context context) {
        this(context, null);
    }

    public EffectPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EffectPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInitView() {
        Context lContext = getContext();
        if(lContext instanceof ViewModelStoreOwner) {
            mVideoEffectEntryPanelView = new VideoEffectEntryPanelView(getContext());
            mVideoEffectSelectPanelView = new VideoEffectSelectPanelView(getContext());
            mVideoEffectSelectPanelView.setItemClickListener(this);
            addView(mVideoEffectEntryPanelView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        }

    }

    @Override
    public void onInitData(PanelManger panelManger,AliyunIEditor editor, int panelType) {
        super.onInitData(panelManger,editor, panelType);
        Context ctx = getContext();
        if(ctx instanceof LifecycleOwner) {
            LifecycleOwner owner = (LifecycleOwner) ctx;
            ViewModelProvider lViewModelProvider = new ViewModelProvider((ViewModelStoreOwner) ctx);
            mVideoEffectEntryViewModel = lViewModelProvider.get(VideoEffectEntryViewModel.class);
            mVideoEffectEntryViewModel.bind(owner, mAliyunIEditor, mVideoEffectManager);
            mVideoEffectSelectViewModel = new VideoEffectSelectViewModel();
            mVideoEffectEntryViewModel.setOnItemClickListener(this);
            mVideoEffectEntryViewModel.setVideoEffectClickListener(this);
            mVideoEffectEntryPanelView.setViewModel(mVideoEffectEntryViewModel);
            mVideoEffectSelectPanelView.setViewModel(mVideoEffectSelectViewModel);
            mSpeed = SpeedUtil.Companion.getSpeed(mAliyunIEditor);
            mVideoEffectEntryPanelView.updateSpeed(mSpeed);
        }
    }

    @Override
    public boolean onBackPressed() {
        return mVideoEffectSelectPanelView.onBackPress();
    }

    @Override
    protected void onRemove() {
        super.onRemove();
        mVideoEffectEntryViewModel.unBind();
    }

    @Override
    public void onPlayProgress(long currentPlayTime, long currentStreamPlayTime) {
        mVideoEffectEntryPanelView.updatePlayProgress(currentPlayTime);
    }

    @Override
    public void onItemClick(@Nullable View view, long id, @Nullable Object obj) {
        if (id == PanelItemId.ITEM_ID_ADD) {
            removeView(mVideoEffectSelectPanelView);
            addView(mVideoEffectSelectPanelView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    public void onEffectSelected(@NonNull String effectPath, String effectName) {

        long defaultDuration = 3000L;
        long  totalDuration = this.mAliyunIEditor.getPlayerController().getStreamDuration();
        long currentPlayPosition =
                this.mAliyunIEditor.getPlayerController().getCurrentStreamPosition();
        long rqDuration = currentPlayPosition + defaultDuration;
        //判断是否超出时间线
        long stickerDuration = 0L;
        if (rqDuration > totalDuration) {
            stickerDuration = totalDuration - currentPlayPosition;
        } else {
            stickerDuration = defaultDuration;
        }

        long time = mAliyunIEditor.getPlayerController().getCurrentStreamPosition();
        EffectFilter effectFilter = new EffectFilter(new Source(effectPath));
        TrackEffectFilter trackEffectFilter = new TrackEffectFilter.Builder()
            .source(new Source(effectPath))
            .effectConfig(effectFilter.getEffectConfig())
            .startTime(time, TimeUnit.MILLISECONDS).duration(stickerDuration, TimeUnit.MILLISECONDS)
                .build();
        mAliyunIEditor.addAnimationFilter(trackEffectFilter);
        VideoEffectTrack videoEffectTrack = new VideoEffectTrack(trackEffectFilter, effectName);
        mVideoEffectManager.addVideoEffect(videoEffectTrack);
        mVideoEffectEntryPanelView.onAddVideoEffect(videoEffectTrack,true);
        mVideoEffectSelectPanelView.onBackPress();
    }

    public void setVideoEffectManager(VideoEffectManager videoEffectManager) {
        mVideoEffectManager = videoEffectManager;
    }

    @Override
    public void onEffectDelete(@NonNull VideoEffectTrack track) {
        mAliyunIEditor.removeAnimationFilter(track.getMTrackEffectFilter());
        mVideoEffectManager.removeVideoEffect(track);
        mVideoEffectEntryPanelView.onRemoveVideoEffect(track);
    }
}
