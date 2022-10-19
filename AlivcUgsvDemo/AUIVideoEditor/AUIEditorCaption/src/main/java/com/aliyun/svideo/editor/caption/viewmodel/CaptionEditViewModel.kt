package com.aliyun.svideo.editor.caption.viewmodel

import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.aliyun.common.utils.FileUtils
import com.aliyun.svideo.editor.caption.CaptionManager
import com.aliyun.svideo.editor.caption.model.CaptionBubble
import com.aliyun.svideo.editor.caption.model.CaptionFontStyleTemplate
import com.aliyun.svideo.editor.caption.model.CaptionResource
import com.aliyun.svideo.editor.caption.util.CaptionFrameAnimationUtil
import com.aliyun.svideo.editor.caption.util.CaptionResourceUtil
import com.aliyun.svideo.editor.common.panel.viewmodel.BaseViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId
import com.aliyun.svideo.editor.common.util.AssetUtil
import com.aliyun.svideo.editor.common.util.GsonHolder
import com.aliyun.svideosdk.common.AliyunColor
import com.aliyun.svideosdk.editor.AliyunIEditor
import com.aliyun.svideosdk.editor.impl.AliyunPasterControllerCompoundCaption
import java.io.File
import java.util.concurrent.TimeUnit


class CaptionEditViewModel() : BaseViewModel() {
    private val captionChangeListeners = mutableListOf<OnCaptionChangeListener>()
    val currentCaption = MutableLiveData<AliyunPasterControllerCompoundCaption?>()

    var currentFontStyleTemplateResource : CaptionResource? = null
    private lateinit var editor: AliyunIEditor

    fun init(editor: AliyunIEditor) {
        this.editor = editor
    }

    fun bind(selectCaption : AliyunPasterControllerCompoundCaption?) {
        this.currentCaption.value = selectCaption
        this.captionChangeListeners.forEach { it.onCurrentCaptionChange(selectCaption) }
    }

    fun unBind() {
        super.onCleared();
        this.currentCaption.value = null
        this.currentFontStyleTemplateResource = null
    }

    fun onCancelClick(view: View?) {
        mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_CANCEL)
    }

    fun onDeleteCaption(caption : AliyunPasterControllerCompoundCaption) {
        this.captionChangeListeners.forEach { it.onCaptionDeleted(caption) }
        CaptionManager.removeCaption(this.editor.pasterManager, caption)
        bind(null)
        this.currentCaption.value = null
    }


    fun onTextChanged(newText : String) {
        this.currentCaption.value?.apply {
            text = newText
            captionChangeListeners.forEach { it.onTextChange(this, newText) }
            apply()
        }
    }

    fun onFlowerResourceSelect(resource : CaptionResource) {
        this.currentCaption.value?.apply {
            setFontEffectTemplate(resource.source)
            apply()
        }
    }

    fun onAnimationResourceSelect(context : Context, resource : CaptionResource) {
        val startTimeInUs = this.currentCaption.value?.getStartTime(TimeUnit.MICROSECONDS) ?: TimeUnit.MILLISECONDS.toMicros(editor.playerController.currentStreamPosition)
        val durationInUs = this.currentCaption.value?.getDuration(TimeUnit.MICROSECONDS) ?: TimeUnit.MILLISECONDS.toMicros(editor.playerController.duration)
        val action = CaptionFrameAnimationUtil.createAction(context, resource.id, startTimeInUs, durationInUs)
        this.currentCaption.value?.apply {
            setFrameAnimation(action)
            apply()
        }
    }

    fun onBubbleResourceSelect(resource : CaptionResource) {

        this.currentCaption.value?.apply {
            setBubbleEffectTemplate(resource.source)
            if(FileUtils.isFileExists(resource.source.path)) {
                val bubbleJson = FileUtils.readFileToString(File("${resource.source.path}${File.separator}/config.json"))

                val captionBubble = GsonHolder.gson.fromJson(
                    bubbleJson,
                    CaptionBubble::class.java
                )

                val bubbleFontList = CaptionResourceUtil.getCaptionBubbleFontFileList()
                val foundBubbleFont = bubbleFontList.firstOrNull {
                    it.id == captionBubble.fontId
                }

                if(foundBubbleFont != null) {
                    fontPath = foundBubbleFont.source
                }

            }
            apply()

            captionChangeListeners.forEach { it.onBubbleChange(this, resource) }
        }
    }

    fun onFontTypefaceSelect(resource : CaptionResource) {
        this.currentCaption.value?.apply {
            fontPath = resource.source
            apply()
        }
    }

    fun onFontColorSelect(resource : CaptionResource) {
        this.currentCaption.value?.apply {
            color = AliyunColor(resource.id)
            apply()
        }
    }

    fun onFontStyleTemplateSelect(context : Context, resource : CaptionResource) {
        this.currentFontStyleTemplateResource = resource
        if(!resource.source.path.isNullOrEmpty()) {
            val fontStyleTemplateContent = AssetUtil.readAssertResource(context, resource.source.path)
            val fontStyleTemplate = GsonHolder.gson.fromJson(fontStyleTemplateContent, CaptionFontStyleTemplate::class.java)
            this.currentCaption.value?.let {

                fontStyleTemplate?.color?.let { color ->
                    try {
                        val argb = Color.parseColor(color)
                        it.color = AliyunColor(argb)
                    } catch (ex: IllegalArgumentException) {

                    }
                }

                fontStyleTemplate?.outline?.let { outline ->
                    try {
                        val argb = Color.parseColor(outline.color)
                        it.outlineWidth = outline.width
                        it.outlineColor = AliyunColor(argb)
                    } catch (ex: IllegalArgumentException) {

                    }
                }

                val bgColor = if(fontStyleTemplate?.bgcolor != null) {
                    val argb = Color.parseColor(fontStyleTemplate.bgcolor)
                    AliyunColor(argb)
                } else {
                    AliyunColor(Color.TRANSPARENT)
                }

                it.backgroundColor = bgColor

                val shadowOffset = PointF(fontStyleTemplate?.shadow?.x ?:0f, fontStyleTemplate?.shadow?.y ?:0f)
                var shadowColor = AliyunColor(Color.TRANSPARENT)
                if(fontStyleTemplate.shadow?.color != null) {
                    val argb = Color.parseColor(fontStyleTemplate.shadow.color)
                    shadowColor = AliyunColor(argb)
                }

                it.shadowOffset = shadowOffset
                it.shadowColor = shadowColor

                it.apply()
            }
        } else {
            this.currentCaption.value?.apply{
                color = AliyunColor(Color.WHITE)
                outlineWidth = 0.0f
                outlineColor = AliyunColor(0, 0, 0, 0)
                shadowOffset = PointF(0.0f, 0.0f)
                shadowColor = AliyunColor(0, 0, 0, 0)
                backgroundColor = AliyunColor(0, 0, 0, 0)
                apply()
            }
        }
    }

    fun addOnCaptionChangeListener(listener: OnCaptionChangeListener) {
        this.captionChangeListeners.add(listener)
    }

    fun removeOnCaptionChangeListener(listener: OnCaptionChangeListener) {
        this.captionChangeListeners.remove(listener)
    }

    interface OnCaptionChangeListener {
        fun onCurrentCaptionChange(caption: AliyunPasterControllerCompoundCaption?)
        fun onCaptionAdded(caption: AliyunPasterControllerCompoundCaption)
        fun onCaptionDeleted(caption: AliyunPasterControllerCompoundCaption)
        fun onTextChange(caption : AliyunPasterControllerCompoundCaption, text : String)
        fun onBubbleChange(caption : AliyunPasterControllerCompoundCaption, resource: CaptionResource)
    }


}