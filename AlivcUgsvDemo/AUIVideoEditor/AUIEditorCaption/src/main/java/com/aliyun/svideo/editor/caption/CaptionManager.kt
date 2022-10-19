package com.aliyun.svideo.editor.caption

import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.util.Log
import com.aliyun.svideo.editor.caption.model.CaptionBorder
import com.aliyun.svideosdk.common.AliyunColor
import com.aliyun.svideosdk.common.struct.effect.EffectPaster
import com.aliyun.svideosdk.common.struct.project.Source
import com.aliyun.svideosdk.editor.AliyunIEditor
import com.aliyun.svideosdk.editor.AliyunIPasterController
import com.aliyun.svideosdk.editor.AliyunPasterManager
import com.aliyun.svideosdk.editor.impl.AliyunPasterControllerCompoundCaption
import java.util.concurrent.TimeUnit

class CaptionManager {


    companion object {
        private const val TAG = "CaptionManager"
        /**
         * 添加弹幕时边界判定
         *
         * @param mAliyunIEditor
         * @return 弹幕开始时间
         */
        fun captionDurationBoundJudge(mAliyunIEditor: AliyunIEditor?, duration: Long): Long {
            var captionDuration: Long = 0
            if (mAliyunIEditor != null) {
                val totalDuration: Long = mAliyunIEditor.playerController.streamDuration
                val currentPlayPosition: Long =
                    mAliyunIEditor.playerController.currentStreamPosition
                val rqDuration = currentPlayPosition + duration
                if (rqDuration > totalDuration) {
                    captionDuration = totalDuration - currentPlayPosition
                    if (captionDuration < CaptionConfig.CAPTION_MIN_DURATION) {
                        captionDuration = 0
                        Log.w(
                            TAG,
                            "captionDurationBoundJudge: captionDuration less CAPTION_MIN_DURATION"
                        )
                    }
                } else {
                    captionDuration = duration
                }
            }
            return captionDuration
        }

        fun addCaptionWithStartTime(
            context: Context,
            manager: AliyunPasterManager,
            content: String?,
            fontPath: String?,
            currentPosition: Long,
            duration: Long
        ): AliyunPasterControllerCompoundCaption? {
            var controller: AliyunPasterControllerCompoundCaption? = null
            if (duration > 0) {
                var captionContent = content;
                if (content.isNullOrEmpty()) {
                    captionContent = context.getString(R.string.ugsv_caption_effect_text_default)
                }
                controller = manager.addCaptionWithStartTime(
                    captionContent,
                    null,
                    Source(fontPath),
                    currentPosition,
                    duration,
                    TimeUnit.MILLISECONDS
                )
                controller.color = AliyunColor(Color.parseColor("#FFF9FAFB"))
                val ret = controller.apply()
                if (ret != 0) {
                    removeCaption(
                        manager,
                        controller
                    )
                    return null
                }
            }
            return controller
        }

        /**
         * 移除字幕
         */
        fun removeCaption(
            aliyunPasterManager: AliyunPasterManager,
            aliyunPasterControllerCompoundCaption: AliyunPasterControllerCompoundCaption
        ) {
            aliyunPasterManager.remove(aliyunPasterControllerCompoundCaption)
        }

        fun getAllCaption(aliyunPasterManager: AliyunPasterManager): MutableList<AliyunPasterControllerCompoundCaption> {
            val controllers: List<AliyunIPasterController> =
                aliyunPasterManager.findControllersByType(EffectPaster.PASTER_TYPE_CAPTION)
            return controllers.map {
                it as AliyunPasterControllerCompoundCaption
            }.reversed().toMutableList()

        }

        fun applyCaptionBorderChanged(
            controller: AliyunPasterControllerCompoundCaption,
            rotation: Float,
            scale: FloatArray?,
            pointF: PointF
        ) {
            controller.rotate = rotation
            if (scale != null && scale.isNotEmpty()) {
                controller.scale = scale[0]
            }
            controller.position = pointF
            controller.apply()
        }

        fun getCaptionSize(
            controller: AliyunPasterControllerCompoundCaption
        ): CaptionBorder {
            return CaptionBorder(controller.size, controller.scale, controller.rotate)
        }
    }

}