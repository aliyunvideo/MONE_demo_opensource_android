package com.alivc.live.pusher.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.live.pusher.demo.Common;
import com.alivc.live.pusher.demo.MusicSelectAdapter;
import com.alivc.live.pusher.demo.R;
import com.alivc.live.pusher.demo.bean.MusicInfo;
import com.aliyun.aio.avbaseui.avdialog.AVBaseBottomSheetDialog;

public class PushMusicBottomSheet extends AVBaseBottomSheetDialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener ,MusicSelectAdapter.OnItemClick{
    private OnMusicSelectListener mOnMusicSelectListener;
    private MusicSelectAdapter mMusicAdapter;
    private Switch mEarsBackSwitch;
    private Switch mAudioDenoiseSwitch;
    private Switch mAudioIntelligentDenoiseSwitch;
    private View mIconVolumeView;
    private View mIconPlayView;
    private View mIconLoopView;
    private SeekBar mAccompanimentBar;
    private SeekBar mVoiceBar;
    private ProgressBar mMusicProgress;
    private TextView mMusicName;
    private TextView mCurrentTime;
    private TextView mDuration;

    public PushMusicBottomSheet(Context context) {
        super(context);
    }

    public PushMusicBottomSheet(Context context, int theme) {
        super(context, theme);
    }

    protected PushMusicBottomSheet(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected View getContentView() {
        View rootView = getLayoutInflater().inflate(R.layout.push_music_sheet, null, false);

        mEarsBackSwitch = rootView.findViewById(R.id.ears_back);
        mAudioDenoiseSwitch = rootView.findViewById(R.id.audio_noise);
        mAudioIntelligentDenoiseSwitch = rootView.findViewById(R.id.audio_intelligent_denoise);
        mEarsBackSwitch.setOnCheckedChangeListener(this);
        mAudioDenoiseSwitch.setOnCheckedChangeListener(this);
        mAudioIntelligentDenoiseSwitch.setOnCheckedChangeListener(this);

        mIconVolumeView = rootView.findViewById(R.id.img_volume);
        mIconPlayView = rootView.findViewById(R.id.img_play);
        mIconLoopView = rootView.findViewById(R.id.img_loop);
        rootView.findViewById(R.id.cancel).setOnClickListener(this);
        rootView.findViewById(R.id.confirm_button).setOnClickListener(this);
        mIconVolumeView.setOnClickListener(this);
        mIconPlayView.setOnClickListener(this);
        mIconLoopView.setOnClickListener(this);

        mAccompanimentBar = rootView.findViewById(R.id.accompaniment_seekbar);
        mVoiceBar = rootView.findViewById(R.id.voice_seekbar);
        mAccompanimentBar.setOnSeekBarChangeListener(this);
        mVoiceBar.setOnSeekBarChangeListener(this);
        mMusicProgress = rootView.findViewById(R.id.resolution_seekbar);
        mMusicName =rootView.findViewById(R.id.music_name);
        mCurrentTime = rootView.findViewById(R.id.tv_current_time);
        mDuration =rootView.findViewById(R.id.tv_duration_time);
        updateButton(false);
        RecyclerView recyclerView = rootView.findViewById(R.id.music_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (mMusicAdapter == null) {
            mMusicAdapter = new MusicSelectAdapter(getContext());
            recyclerView.setAdapter(mMusicAdapter);
            mMusicAdapter.setOnItemClick(this::onItemClick);
        }

        return rootView;
    }

    @Override
    protected ViewGroup.LayoutParams getContentLayoutParams() {
        return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        try {
            if (id == R.id.img_play) {
                boolean pauseSelected = mIconPlayView.isSelected();
                mIconPlayView.setSelected(!pauseSelected);
                if (mOnMusicSelectListener != null) {
                    mOnMusicSelectListener.onBGPlay(!pauseSelected);
                }

            } else if (id == R.id.img_loop) {
                boolean loopSelected = mIconLoopView.isSelected();
                mIconLoopView.setSelected(!loopSelected);
                if (mOnMusicSelectListener != null) {
                    mOnMusicSelectListener.onBGLoop(!loopSelected);
                }

            } else if (id == R.id.img_volume) {
                boolean isSelect = mIconVolumeView.isSelected();
                mIconVolumeView.setSelected(!isSelect);
                if (mOnMusicSelectListener != null) {
                    mOnMusicSelectListener.onMute(!isSelect);
                }
                mAccompanimentBar.setEnabled(isSelect);
                mVoiceBar.setEnabled(isSelect);
            }else if (id == R.id.cancel || id== R.id.confirm_button){
                dismiss();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.ears_back) {
            if (mOnMusicSelectListener != null) {
                mOnMusicSelectListener.onBGMEarsBack(isChecked);
            }
        } else if (id == R.id.audio_noise) {
            if (mOnMusicSelectListener != null) {
                mOnMusicSelectListener.onAudioNoise(isChecked);
            }
            updateAudioDenoiseSwitchState();
        } else if (id == R.id.audio_intelligent_denoise) {
            if (mOnMusicSelectListener != null) {
                mOnMusicSelectListener.onAudioIntelligentNoise(isChecked);
            }
            updateAudioDenoiseSwitchState();
        }
    }

    // 使用智能降噪，需关闭普通降噪；两者功能互斥使用
    private void updateAudioDenoiseSwitchState() {
        if (mAudioDenoiseSwitch == null || mAudioIntelligentDenoiseSwitch == null) {
            return;
        }
        boolean useCustom = mAudioDenoiseSwitch.isChecked();
        boolean useIntelligent = mAudioIntelligentDenoiseSwitch.isChecked();
        if (!useCustom && !useIntelligent) {
            mAudioDenoiseSwitch.setEnabled(true);
            mAudioIntelligentDenoiseSwitch.setEnabled(true);
        } else if (useCustom && !useIntelligent) {
            mAudioIntelligentDenoiseSwitch.setEnabled(false);
        } else if (!useCustom && useIntelligent) {
            mAudioDenoiseSwitch.setEnabled(false);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        try {
            int seekBarId = seekBar.getId();

            if (mAccompanimentBar.getId() == seekBarId) {
                if (mOnMusicSelectListener != null) {
                    mOnMusicSelectListener.onBGMVolume(progress);
                }
            } else if (mVoiceBar.getId() == seekBarId) {
                if (mOnMusicSelectListener != null) {
                    mOnMusicSelectListener.onCaptureVolume(progress);
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void updateProgress(long progress, long totalTime) {
        mDuration.setText(Common.getTime(totalTime));
        mCurrentTime.setText(Common.getTime(progress));
        mMusicProgress.setProgress((int) progress);
        mMusicProgress.setMax((int) totalTime);
    }

    private void updateButtonState(boolean bool) {
        updateButton(bool);
        mIconPlayView.setSelected(bool);
    }

    private void updateButton(boolean bool) {
        mIconVolumeView.setEnabled(bool);
        mIconPlayView.setEnabled(bool);
        mIconLoopView.setEnabled(bool);

        if (!bool) {
            mAccompanimentBar.setEnabled(false);
            mVoiceBar.setEnabled(false);
        } else {
            mAccompanimentBar.setEnabled(!mIconVolumeView.isSelected());
            mVoiceBar.setEnabled(!mIconVolumeView.isSelected());
        }
    }

    public void setOnMusicSelectListener(OnMusicSelectListener onMusicSelectListener) {
        mOnMusicSelectListener = onMusicSelectListener;
    }

    @Override
    public void onItemClick(MusicInfo musicInfo, int position) {
        updateButtonState(position > 0);
        if (musicInfo != null) {
            setPlayMusicUiState(musicInfo);
            if (mOnMusicSelectListener != null) {
                mOnMusicSelectListener.onBgResource(musicInfo.getPath());
            }
        }
    }

    private void setPlayMusicUiState(@NonNull MusicInfo musicInfo) {
        mMusicName.setText(musicInfo.getMusicName());
        mMusicProgress.setProgress(0);
    }

    public interface OnMusicSelectListener {
        void onBGMEarsBack(boolean state);

        void onAudioNoise(boolean state);

        void onAudioIntelligentNoise(boolean state);

        void onBGPlay(boolean state);

        void onBgResource(String path);

        void onBGLoop(boolean state);

        void onMute(boolean state);

        void onCaptureVolume(int progress);

        void onBGMVolume(int progress);
    }
}
