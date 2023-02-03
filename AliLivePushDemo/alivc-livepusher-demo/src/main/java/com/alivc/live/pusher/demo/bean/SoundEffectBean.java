package com.alivc.live.pusher.demo.bean;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import com.alivc.live.pusher.AlivcLivePushAudioEffectReverbMode;
import com.alivc.live.pusher.AlivcLivePushAudioEffectVoiceChangeMode;
import com.alivc.live.pusher.demo.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SoundEffectBean implements Serializable {

    //icon drawable resId
    private int iconId;
    //description string resId
    private int descriptionId;
    private String name;


    public SoundEffectBean() {
    }

    public SoundEffectBean(@StringRes int descriptionId, String name) {
        this.descriptionId = descriptionId;
        this.name = name;
    }


    public SoundEffectBean(@DrawableRes int iconId, @StringRes int descriptionId, String name) {
        this.iconId = iconId;
        this.descriptionId = descriptionId;
        this.name = name;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(@IdRes int iconId) {
        this.iconId = iconId;
    }

    public int getDescriptionId() {
        return descriptionId;
    }

    public void setDescriptionId(@StringRes int descriptionId) {
        this.descriptionId = descriptionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 音效
     */
    public static class SoundEffectChangeVoiceBean {
        private static final HashMap<Integer, SoundEffectBean> mLivePushSoundEffectChangeVoice = new HashMap<>();

        static {
            mLivePushSoundEffectChangeVoice.put(0, new SoundEffectBean(R.drawable.ic_live_push_sound_effect_off, R.string.sound_effect_changevoice_off, AlivcLivePushAudioEffectVoiceChangeMode.SOUND_EFFECT_OFF.name()));
            mLivePushSoundEffectChangeVoice.put(1, new SoundEffectBean(R.drawable.ic_live_push_sound_effect_oldman, R.string.sound_effect_changevoice_oldman, AlivcLivePushAudioEffectVoiceChangeMode.SOUND_EFFECT_OLD_MAN.name()));
            mLivePushSoundEffectChangeVoice.put(2, new SoundEffectBean(R.drawable.ic_live_push_sound_effect_babyboy, R.string.sound_effect_changevoice_babyboy, AlivcLivePushAudioEffectVoiceChangeMode.SOUND_EFFECT_BABYBOY.name()));
            mLivePushSoundEffectChangeVoice.put(3, new SoundEffectBean(R.drawable.ic_live_push_sound_effect_babygirl, R.string.sound_effect_changevoice_babygirl, AlivcLivePushAudioEffectVoiceChangeMode.SOUND_EFFECT_BABYGILR.name()));
            mLivePushSoundEffectChangeVoice.put(4, new SoundEffectBean(R.drawable.ic_live_push_sound_effect_robot, R.string.sound_effect_changevoice_robot, AlivcLivePushAudioEffectVoiceChangeMode.SOUND_EFFECT_ROBOT.name()));
            mLivePushSoundEffectChangeVoice.put(5, new SoundEffectBean(R.drawable.ic_live_push_sound_effect_daimo, R.string.sound_effect_changevoice_daimo, AlivcLivePushAudioEffectVoiceChangeMode.SOUND_EFFECT_DAIMO.name()));
            mLivePushSoundEffectChangeVoice.put(6, new SoundEffectBean(R.drawable.ic_live_push_sound_effect_ktv, R.string.sound_effect_changevoice_ktv, AlivcLivePushAudioEffectVoiceChangeMode.SOUND_EFFECT_KTV.name()));
            mLivePushSoundEffectChangeVoice.put(7, new SoundEffectBean(R.drawable.ic_live_push_sound_effect_echo, R.string.sound_effect_changevoice_echo, AlivcLivePushAudioEffectVoiceChangeMode.SOUND_EFFECT_ECHO.name()));
        }

        public static HashMap<Integer, SoundEffectBean> getLivePushSoundEffectChangeVoice() {
            return mLivePushSoundEffectChangeVoice;
        }
    }

    /**
     * 混响
     */
    public static class SoundEffectReverb {
        private static final HashMap<Integer, SoundEffectBean> mLivePushSoundEffectReverb = new HashMap<>();

        static {
            mLivePushSoundEffectReverb.put(0, new SoundEffectBean(R.string.sound_effect_reverb_off, AlivcLivePushAudioEffectReverbMode.REVERB_OFF.name()));
            mLivePushSoundEffectReverb.put(1, new SoundEffectBean(R.string.sound_effect_reverb_vocal1, AlivcLivePushAudioEffectReverbMode.REVERB_VOCAL_I.name()));
            mLivePushSoundEffectReverb.put(2, new SoundEffectBean(R.string.sound_effect_reverb_vocal2, AlivcLivePushAudioEffectReverbMode.REVERB_VOCAL_II.name()));
            mLivePushSoundEffectReverb.put(3, new SoundEffectBean(R.string.sound_effect_reverb_bathroom, AlivcLivePushAudioEffectReverbMode.REVERB_BATHROOM.name()));
            mLivePushSoundEffectReverb.put(4, new SoundEffectBean(R.string.sound_effect_reverb_smallroom_bright, AlivcLivePushAudioEffectReverbMode.REVERB_SMALL_ROOM_BRIGHT.name()));
            mLivePushSoundEffectReverb.put(5, new SoundEffectBean(R.string.sound_effect_reverb_smallroom_dark, AlivcLivePushAudioEffectReverbMode.REVERB_SMALL_ROOM_DARK.name()));
            mLivePushSoundEffectReverb.put(6, new SoundEffectBean(R.string.sound_effect_reverb_mediumroom, AlivcLivePushAudioEffectReverbMode.REVERB_MEDIUM_ROOM.name()));
            mLivePushSoundEffectReverb.put(7, new SoundEffectBean(R.string.sound_effect_reverb_largeroom, AlivcLivePushAudioEffectReverbMode.REVERB_LARGE_ROOM.name()));
            mLivePushSoundEffectReverb.put(8, new SoundEffectBean(R.string.sound_effect_reverb_churchhall, AlivcLivePushAudioEffectReverbMode.REVERB_CHURCH_HALL.name()));
        }

        public static HashMap<Integer, SoundEffectBean> getLivePushSoundEffectReverb() {
            return mLivePushSoundEffectReverb;
        }
    }
}
