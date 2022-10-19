package com.aliyun.svideo.editor.clip.trasition

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.aliyun.svideo.editor.clip.R
import com.aliyun.svideo.editor.clip.base.BaseClipViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.ActionBarViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId
import com.aliyun.svideosdk.common.struct.effect.*
import com.aliyun.svideosdk.common.struct.project.AudioEffect
import com.aliyun.svideosdk.common.struct.project.VideoTrackClip
import com.aliyun.svideosdk.editor.AudioEffectType

class TransitionPanelViewModel(context: Context) : BaseClipViewModel() {

    var mIsCheckAll = false

    val actionBarViewModel = ActionBarViewModel(
        cancelVisible = true,
        confirmVisible = false,
        titleResId = R.string.ugsv_editor_audio_effect_title
    )

    val listViewModel = arrayListOf<TransitionViewModel>(
        TransitionViewModel(null, R.drawable.ic_editor_clip_reset, R.string.ugsv_editor_clip_none,true),
        TransitionViewModel(TransitionTranslate().setDirection(TransitionTranslate.DIRECTION_UP), R.drawable.ic_clip_transition_translate_up, R.string.ugsv_editor_tansition_translate_up),
        TransitionViewModel(TransitionTranslate().setDirection(TransitionTranslate.DIRECTION_DOWN), R.drawable.ic_clip_transition_translate_down, R.string.ugsv_editor_tansition_translate_down),
        TransitionViewModel(TransitionTranslate().setDirection(TransitionTranslate.DIRECTION_LEFT), R.drawable.ic_clip_transition_translate_left, R.string.ugsv_editor_tansition_translate_left),
        TransitionViewModel(TransitionTranslate().setDirection(TransitionTranslate.DIRECTION_RIGHT), R.drawable.ic_clip_transition_translate_right, R.string.ugsv_editor_tansition_translate_right),
        TransitionViewModel(TransitionShutter().setOrientation(TransitionShutter.ORIENTATION_HORIZONTAL).setLineWidth(0.1f), R.drawable.ic_clip_transition_shutter, R.string.ugsv_editor_tansition_shutter),
        TransitionViewModel(TransitionFade(), R.drawable.ic_clip_transition_fade, R.string.ugsv_editor_tansition_fade),
        TransitionViewModel(TransitionCircle(), R.drawable.ic_clip_transition_circle, R.string.ugsv_editor_tansition_circle),
        TransitionViewModel(TransitionFiveStar(), R.drawable.ic_clip_transition_fivestar, R.string.ugsv_editor_tansition_fivestar),
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

    fun setSelectView(type: TransitionBase?) {
        for (model in listViewModel) {
            model.mSelect = model.mEffectType == type
        }
    }

    override fun onCurrentClipChanged(streamId: Int, trackClip: VideoTrackClip?) {
        if (trackClip == null) {
            return
        }
//        var audioEffectType:AudioEffectType = AudioEffectType.EFFECT_TYPE_DEFAULT
//        for (effect in trackClip.effects) {
//            if (effect !is AudioEffect) {
//                continue
//            }
//            var audioEffect = effect as AudioEffect
//            audioEffectType = audioEffect.mEffectType
//            break
//        }
//        for (model in listViewModel) {
//            model.mSelect = model.mEffectType == audioEffectType
//        }
    }

}