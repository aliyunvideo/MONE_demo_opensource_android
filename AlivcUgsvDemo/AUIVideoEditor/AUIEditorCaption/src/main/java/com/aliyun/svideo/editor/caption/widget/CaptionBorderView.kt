/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */
package com.aliyun.svideo.editor.caption.widget

import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.aliyun.svideo.editor.caption.R
import com.aliyun.svideo.editor.caption.model.CaptionBorder
import com.aliyun.svideo.editor.common.panel.OnItemClickListener
import com.aliyun.svideo.editor.common.widget.BaseAliyunPasterView
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max

class CaptionBorderView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : BaseAliyunPasterView(context, attrs, defStyle) {
    companion object {
        private const val DELTA_THREADHOLD = 10
    }
    private var mCenterX = 0f
    private var mCenterY = 0f
    private var originalCenter: FloatArray = FloatArray(2)
    private var isFirstTouch = true
    private var mOnCaptionControllerChangeListener: OnCaptionControllerChangeListener? = null
    private var onItemClickListener : OnItemClickListener? = null
    override fun setEditCompleted(isEditCompleted: Boolean) {}
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        validateTransform()
        mMatrixUtil.decomposeTSR(mTransform)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        mMatrixUtil.decomposeTSR(mTransform)
    }

    private var mContentView: View? = null
    private var mContentWidth = 0
    private var mContentHeight = 0
    private fun setContentSize(contentSize: RectF?) {
        if (contentSize == null) {
            return
        }
        mContentWidth = contentSize.width().toInt()
        mContentHeight = contentSize.height().toInt()
        setContentSize(mContentWidth, mContentHeight)
    }

    private fun setContentSize(contentWidth: Int, contentHeight: Int) {
        if (mContentView != null) {
            val layoutParams = mContentView!!.layoutParams
            layoutParams.width = contentWidth
            layoutParams.height = contentHeight
            mContentView!!.layoutParams = layoutParams
        }
    }

    override fun setContentWidth(contentWidth: Int) {
        mContentWidth = contentWidth
    }

    override fun setContentHeight(contentHeight: Int) {
        mContentHeight = contentHeight
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mContentView = findViewById(R.id.captionEditControllerContent)
        val transform =
            findViewById<View>(R.id.captionEditControllerTransform)
        transform?.setOnTouchListener(mRotationScaleBinding)
        setOnTouchListener(onTouchListener)
        setContentSize(mContentWidth, mContentHeight)
        if (mContentView is CaptionBorderControllerView) {
            (mContentView as CaptionBorderControllerView).setOnLayoutChangeListener(
                mContentLayoutChange
            )
        }
    }

    override fun setShowTextLabel(isShow: Boolean) {}
    override fun getTextLabel(): View? {
        return null
    }

    override fun getContentWidth(): Int {
        return mContentView!!.measuredWidth
    }

    override fun getContentHeight(): Int {
        return mContentView!!.measuredHeight
    }

    override fun getContentView(): View {
        return mContentView!!
    }

    interface OnCaptionControllerChangeListener {
        fun onControllerChanged(
            rotation: Float,
            scale: FloatArray?,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int
        )
    }

    private val mContentLayoutChange: CaptionBorderControllerView.OnLayoutChangeListener =
        object : CaptionBorderControllerView.OnLayoutChangeListener {
            override fun onLayoutChanged(left: Int, top: Int, right: Int, bottom: Int) {
                mOnCaptionControllerChangeListener?.onControllerChanged(
                    rotation,
                    scale,
                    left,
                    top,
                    right,
                    bottom
                )
            }
        }

    fun bind(
        captionSize: CaptionBorder,
        onCaptionControllerChangeListener: OnCaptionControllerChangeListener?
    ) {
        mOnCaptionControllerChangeListener = null
        val width = width
        if (width == 0) {
            post { reSizeBorder(captionSize) }
        } else {
            reSizeBorder(captionSize)
        }
        post { mOnCaptionControllerChangeListener = onCaptionControllerChangeListener }
    }

    private fun reSizeBorder(captionSize: CaptionBorder) {
        //数据清除
        mMatrixUtil.clear()
        val matrix = transform
        matrix.reset()
        if (center != null) {
            originalCenter = center!!
            isFirstTouch = false
        }
        //设置尺寸
        val aimCx = (captionSize.rectF.left + captionSize.rectF.width() / 2).toInt()
        val animCy = (captionSize.rectF.top + captionSize.rectF.height() / 2).toInt()
        val baseWidth = width / 2
        val baseHeight = height / 2
        setContentSize(captionSize.rectF)
        val dx = aimCx - baseWidth
        val dy = animCy - baseHeight
        if (width != 0) {
            matrix.postTranslate(dx.toFloat(), dy.toFloat())
        }
        scaleContent(captionSize.scale, captionSize.scale)
        val center = center
        if (center != null) {
            mCenterX = center[0] - originalCenter[0]
            mCenterY = center[1] - originalCenter[1]
        }
        rotateContent(-captionSize.rotation, mCenterX, mCenterY)
        invalidateTransform()
        visibility = VISIBLE
        bringToFront()
        if (width == 0) {
            post { reSizeBorder(captionSize) }
        }
    }

    private val onTouchListener : OnTouchListener = object : OnTouchListener {
        private var mLastX : Float = 0f
        private var mLastY : Float = 0f

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (isFirstTouch) {
                        originalCenter = center!!

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
                        moveContent(deltaX, deltaY)
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

    private val mRotationScaleBinding: OnTouchListener = object : OnTouchListener {
        private var mLastX = 0f
        private var mLastY = 0f
        private fun update(x: Float, y: Float) {
            val content = contentView
            val x0 = (content.left + content.width / 2).toFloat()
            val y0 = (content.top + content.height / 2).toFloat()
            val dx = x - x0
            val dy = y - y0
            val dx0 = mLastX - x0
            val dy0 = mLastY - y0
            val scale = PointF.length(dx, dy) / max(
                PointF.length(dx0, dy0),
                PointF.length((content.width / 2).toFloat(), (content.height / 2).toFloat())
            )
            val rot = (atan2((y - y0).toDouble(), (x - x0).toDouble()) - atan2(
                (mLastY
                        - y0).toDouble(), (mLastX - x0).toDouble()
            )).toFloat()
            if (java.lang.Float.isInfinite(scale) || java.lang.Float.isNaN(scale)
                || java.lang.Float.isInfinite(rot) || java.lang.Float.isNaN(rot)
            ) {
                return
            }
            mLastX = x
            mLastY = y
            scaleContent(scale, scale)
            rotateContent(rot, mCenterX, mCenterY)
        }

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (isFirstTouch) {
                        originalCenter = center!!
                        isFirstTouch = false
                    }
                    mLastX = v.left + event.x
                    mLastY = v.top + event.y
                    val center = center
                    mCenterX = center!![0] - originalCenter[0]
                    mCenterY = center[1] - originalCenter[1]
                }
                MotionEvent.ACTION_MOVE -> update(v.left + event.x, v.top + event.y)
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {}
                else -> {}
            }
            return true
        }
    }



}