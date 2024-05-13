package com.aliyun.video.play.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.aliyun.player.alivcplayerexpand.util.ScreenUtils
import com.aliyun.video.R

class FunctionShadowView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val mPaint = Paint()
    private var mInitShader = false
    private var linearGradient: LinearGradient? = null
    private var mRectF = RectF()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!mInitShader) {
            initShader()
            mInitShader = true
        }

        mPaint.shader = linearGradient
        canvas?.drawRect(mRectF, mPaint)
    }

    private fun initShader() {
        val colorList = arrayOf(
            ContextCompat.getColor(context, R.color.transparent),
            ContextCompat.getColor(context, R.color.alivc_common_bg_black_alpha_10),
            ContextCompat.getColor(context, R.color.alivc_common_bg_black_alpha_35),
            ContextCompat.getColor(context, R.color.alivc_common_bg_black_alpha_53),
            ContextCompat.getColor(context, R.color.alivc_common_bg_black_alpha_90)
        ).toIntArray()
        val position = arrayOf(0f, 0.167f, 0.333f, 0.5f, 1f).toFloatArray()
        linearGradient = LinearGradient(
            0f,
            0f,
            ScreenUtils.getWidth(context).toFloat(),
            ScreenUtils.getHeight(context).toFloat(),
            colorList,
            position,
            Shader.TileMode.MIRROR
        )
        mRectF.apply {
            top = 0f
            bottom = ScreenUtils.getHeight(context).toFloat()
            left = 0f
            right = ScreenUtils.getWidth(context).toFloat()
        }
    }
}