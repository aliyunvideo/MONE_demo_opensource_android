package com.aliyun.svideo.editor.sticker.widget

import android.graphics.Bitmap
import android.graphics.PointF
import android.text.Layout
import android.util.Log
import android.view.MotionEvent
import android.view.TextureView
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageView
import com.aliyun.svideo.editor.sticker.R
import com.aliyun.svideosdk.common.struct.effect.EffectPaster
import com.aliyun.svideosdk.common.struct.project.Source
import com.aliyun.svideosdk.editor.AliyunPasterBaseView
import com.aliyun.svideosdk.editor.AliyunPasterController
import com.aliyun.svideosdk.editor.pplayer.AnimPlayerView
import kotlin.math.abs
import kotlin.math.atan2

class StickerPasterPreview(
    private val pasterView: StickerBorderControllerView,
    var controller: AliyunPasterController,
    private val skipMoveCenter : Boolean
) : AliyunPasterBaseView {
    companion object {
        private const val DELTA_THREADHOLD = 10
    }
    private var isEditStarted = false
    private var animPlayerView: AnimPlayerView? = null
    private var mCenterX = 0f
    private var mCenterY = 0f
    private var originalCenter: FloatArray = FloatArray(2)

    private var isFirstTouch = true


    val rotationScaleBinding: OnTouchListener = object : OnTouchListener {
        private var mLastX = 0f
        private var mLastY = 0f
        private fun update(x: Float, y: Float) {
            val content: View = pasterView.contentView
            val x0 = (content.left + content.width / 2).toFloat()
            val y0 = (content.top + content.height / 2).toFloat()
            val dx = x - x0
            val dy = y - y0
            val dx0 = mLastX - x0
            val dy0 = mLastY - y0
            val scale = PointF.length(dx, dy) / Math.max(
                PointF.length(dx0, dy0),
                PointF.length((content.width / 2).toFloat(), (content.height / 2).toFloat())
            )
            val rot = (atan2(y - y0, x - x0) - atan2(
                mLastY - y0, mLastX - x0))
            if (java.lang.Float.isInfinite(scale) || java.lang.Float.isNaN(scale)
                || java.lang.Float.isInfinite(rot) || java.lang.Float.isNaN(rot)
            ) {
                return
            }
            mLastX = x
            mLastY = y
            pasterView.scaleContent(scale, scale)
            pasterView.rotateContent(rot, mCenterX, mCenterY)
        }

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (isFirstTouch) {
                        //原始中心点为view中心点
                        val center = FloatArray(2)
                        center[0] = pasterView.width / 2.0f
                        center[1] = pasterView.height / 2.0f
                        if (center[0] != 0.0f && center[1] != 0.0f) {
                            originalCenter = center
                            isFirstTouch = false
                        }
                    }
                    mLastX = v.left + event.x
                    mLastY = v.top + event.y
                    val center = pasterView.center!!
                    mCenterX = center[0] - originalCenter[0]
                    mCenterY = center[1] - originalCenter[1]
                }
                MotionEvent.ACTION_MOVE -> update(v.left + event.x, v.top + event.y)
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {}
                else -> {}
            }
            return true
        }
    }

    private val onTouchListener : OnTouchListener = object : OnTouchListener {
        private var mLastX : Float = 0f
        private var mLastY : Float = 0f

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (isFirstTouch) {
                        originalCenter = pasterView.center!!

                        isFirstTouch = false
                    }
                    if(mLastX == 0f) {
                        mLastX = v.left + event.x
                        mLastY = v.top + event.y
                    }
                }
                MotionEvent.ACTION_MOVE ->  {
                    val newX = v.left + event.x
                    val newY = v.top + event.y
                    val deltaX = newX - mLastX
                    val deltaY = newY - mLastY
                    if(abs(deltaX) > DELTA_THREADHOLD || abs(deltaY) > DELTA_THREADHOLD) {
                        pasterView.moveContent(deltaX, deltaY)
                        mLastX = newX
                        mLastY = newY
                    }
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    mLastX = 0f
                    mLastY = 0f
                }
                else -> {}
            }
            return true
        }
    }


    init {
        pasterView.contentWidth = controller.pasterWidth
        pasterView.contentHeight = controller.pasterHeight
        pasterView.isMirror = controller.isPasterMirrored
        pasterView.rotateContent(controller.pasterRotation)

        //sticker首次新增时，直接放置在中心点
        //已经增加过时，放置在上次的指定位置
        if(!skipMoveCenter) {
            val paster = controller.effect as EffectPaster
            val centerX = paster.x
            val centerY = paster.y

            val halfWidth = paster.displayWidth / 2
            val halfHeight = paster.displayHeight /2
            val dx = centerX - halfWidth
            val dy = centerY - halfHeight
            pasterView.moveContent(dx, dy)
        }

//        moveToCenter()

//        pasterView.moveContent(controller.pasterCenterX, controller.pasterCenterY)
//        //设置尺寸
//        val aimCx = (captionSize.rectF.left + captionSize.rectF.width() / 2).toInt()
//        val animCy = (captionSize.rectF.top + captionSize.rectF.height() / 2).toInt()
//        val baseWidth = width / 2
//        val baseHeight = height / 2
//        setContentSize(captionSize.rectF)
//        val dx = aimCx - baseWidth
//        val dy = animCy - baseHeight
//        if (width != 0) {
//            matrix.postTranslate(dx.toFloat(), dy.toFloat())
//        }

        pasterView.findViewById<ImageView>(R.id.stickerControllerTransform).setOnTouchListener(rotationScaleBinding)
        pasterView.setOnTouchListener(onTouchListener)
    }

    fun moveToCenter() {
//        mMoveDelay = true
        pasterView.post {
            val paster = controller.effect as EffectPaster
            val cx: Float = controller.pasterCenterX
            val cy: Float = controller.pasterCenterY
            pasterView.moveContent(cx - paster.displayWidth / 2, cy - paster.displayHeight / 2)
        }
    }

    override fun getTextMaxLines(): Int {
        return 0
    }

    override fun getTextAlign(): Layout.Alignment? {
        return null
    }

    override fun getTextPaddingX(): Int {
        return 0
    }

    override fun getTextPaddingY(): Int {
        return 0
    }

    override fun getTextFixSize(): Int {
        return 0
    }

    override fun getBackgroundBitmap(): Bitmap? {
        return null
    }

    override fun getText(): String? {
        return null
    }

    override fun getTextColor(): Int {
        return 0
    }

    override fun getTextStrokeColor(): Int {
        return 0
    }

    override fun isTextHasStroke(): Boolean {
        return false
    }

    override fun isTextHasLabel(): Boolean {
        return false
    }

    override fun getTextBgLabelColor(): Int {
        return 0
    }

    override fun getPasterTextOffsetX(): Int {
        return 0
    }

    override fun getPasterTextOffsetY(): Int {
        return 0
    }

    override fun getPasterTextWidth(): Int {
        return 0
    }

    override fun getPasterTextHeight(): Int {
        return 0
    }

    override fun getPasterTextRotation(): Float {
        return 0f
    }

    override fun getPasterTextFont(): String? {
        return null
    }

    override fun getPasterTextFontSource(): Source? {
        return null
    }

    override fun getPasterWidth(): Int {
        val scale = pasterView.scale
        val scaleX = scale[0]
        val width = pasterView.contentWidth
        return (width * scaleX).toInt()
    }

    override fun getPasterHeight(): Int {
        val scale = pasterView.scale
        val scaleY = scale[1]
        val height = pasterView.contentHeight
        return (height * scaleY).toInt()
    }

    override fun getPasterCenterY(): Int {
        return pasterView.center?.get(1)?.toInt() ?: 0
    }

    override fun getPasterCenterX(): Int {
        return pasterView.center?.get(0)?.toInt() ?: 0
    }

    override fun getPasterRotation(): Float {
        return pasterView.rotation
    }

    override fun transToImage(): Bitmap? {
        return null
    }

    override fun getPasterView(): View {
        return pasterView
    }

    override fun getTextView(): View? {
        return null
    }

    override fun isPasterMirrored(): Boolean {
        return pasterView.isMirror
    }

    fun editTimeStart() {
        if (isEditStarted) {
            return
        }
        isEditStarted = true
        pasterView.visibility = View.VISIBLE
        pasterView.bringToFront()
        playPasterEffect()
        controller.editStart()
    }

    fun editTimeCompleted() {
        if (!isEditStarted) {
            return
        }
        isEditStarted = false
        if (!controller.isRevert && !controller.isOnlyApplyUI && pasterView.width == 0 && pasterView.height == 0) {
            //1.isRevert为true时锁定了参数 - 动图撤销恢复
            //2.isOnlyApplyUI为true时 - 合成撤销恢复
            //初始化错误的时候，remove掉这个贴纸
            controller.removePaster()
            return
        }
        pasterView.visibility = View.INVISIBLE
        stopPasterEffect()
        controller.editCompleted()
    }

    private fun playPasterEffect() {
        val pv = TextureView(pasterView.context)
        animPlayerView = controller.createPasterPlayer(pv)
        val lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        val vg = pasterView.contentView as ViewGroup
        vg.addView(pv, lp)
    }

    private fun stopPasterEffect() {
        val vg = pasterView.contentView as ViewGroup
        vg.removeAllViews()
        animPlayerView = null
    }

    fun setPasterController(controller: AliyunPasterController) {
        this.controller = controller
        pasterView.contentWidth = controller.pasterWidth
        pasterView.contentHeight = controller.pasterHeight
        pasterView.isMirror = controller.isPasterMirrored
//        pasterView.rotateContent(controller.pasterRotation)
//        pasterView.post {
//            val aimCx = controller.pasterCenterX
//            val animCy = controller.pasterCenterY
//            val baseWidth = pasterView.width / 2
//            val baseHeight = pasterView.height / 2
//            val dx = aimCx - baseWidth
//            val dy = animCy - baseHeight
//            pasterView.moveContent(dx, dy)
//        }
//        pasterView.moveContent(controller.pasterCenterX, controller.pasterCenterY)
        if(isEditStarted) {
            stopPasterEffect()
            playPasterEffect()
        }
    }
}