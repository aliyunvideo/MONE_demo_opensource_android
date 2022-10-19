package com.aliyun.svideo.editor.clip.audioeffect

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.aliyun.svideo.editor.clip.R
import com.aliyun.svideo.editor.clip.base.BaseClipViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.ActionBarViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId
import com.aliyun.svideosdk.common.struct.project.AudioEffect
import com.aliyun.svideosdk.common.struct.project.VideoTrackClip
import com.aliyun.svideosdk.editor.AudioEffectType

class AudioEffectPanelViewModel(context: Context) : BaseClipViewModel() {

    var mIsCheckAll = false

    val actionBarViewModel = ActionBarViewModel(
        cancelVisible = true,
        confirmVisible = false,
        titleResId = R.string.ugsv_editor_audio_effect_title
    )

    val listViewModel = arrayListOf<AudioEffectViewModel>(
        AudioEffectViewModel(AudioEffectType.EFFECT_TYPE_DEFAULT, R.drawable.ic_editor_clip_reset, R.string.ugsv_editor_clip_none,true),
        AudioEffectViewModel(AudioEffectType.EFFECT_TYPE_LOLITA, R.drawable.ic_editor_ae_lolita, R.string.ugsv_editor_audio_effect_lolita),
        AudioEffectViewModel(AudioEffectType.EFFECT_TYPE_UNCLE, R.drawable.ic_editor_ae_uncle, R.string.ugsv_editor_audio_effect_uncle),
        AudioEffectViewModel(AudioEffectType.EFFECT_TYPE_ECHO, R.drawable.ic_editor_ae_echo, R.string.ugsv_editor_audio_effect_echo),
        AudioEffectViewModel(AudioEffectType.EFFECT_TYPE_REVERB, R.drawable.ic_editor_ae_ktv, R.string.ugsv_editor_audio_effect_reverb),
        AudioEffectViewModel(AudioEffectType.EFFECT_TYPE_MINIONS, R.drawable.ic_editor_ae_minions, R.string.ugsv_editor_audio_effect_minions),
        AudioEffectViewModel(AudioEffectType.EFFECT_TYPE_ROBOT, R.drawable.ic_editor_ae_robot, R.string.ugsv_editor_audio_effect_robot),
        AudioEffectViewModel(AudioEffectType.EFFECT_TYPE_BIG_DEVIL, R.drawable.ic_editor_ae_bigdevil, R.string.ugsv_editor_audio_effect_big_devil),
        AudioEffectViewModel(AudioEffectType.EFFECT_TYPE_DIALECT, R.drawable.ic_editor_ae_dialect, R.string.ugsv_editor_audio_effect_dialect)
    )

    fun onSpaceClick(view: View) {
        mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_CANCEL, null)
    }

    fun onCheckAllClick(view: View) {
        var imageView: ImageView = view as ImageView
        mIsCheckAll = !mIsCheckAll
        if (mIsCheckAll) {
            imageView.setImageResource(R.drawable.ugsv_editor_clip_check)
        } else {
            imageView.setImageResource(R.drawable.ugsv_editor_clip_uncheck)
        }
    }

    fun setSelectView(type: AudioEffectType) {
        for (model in listViewModel) {
            model.mSelect = model.mEffectType == type
        }
    }

    override fun onCurrentClipChanged(streamId: Int, trackClip: VideoTrackClip?) {
        if (trackClip == null) {
            return
        }
        var audioEffectType:AudioEffectType = AudioEffectType.EFFECT_TYPE_DEFAULT
        for (effect in trackClip.effects) {
            if (effect !is AudioEffect) {
                continue
            }
            var audioEffect = effect as AudioEffect
            audioEffectType = audioEffect.mEffectType
            break
        }
        for (model in listViewModel) {
            model.mSelect = model.mEffectType == audioEffectType
        }
    }

}