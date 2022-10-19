/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aliyun.svideo.editor.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.aliyun.svideo.editor.common.R

/**
 * 圆角控件，只限制画布为圆角，如果要支持gif，只能用这个。
 */
class RoundedRectangleImageView : AppCompatImageView {
    private val mRoundedRectPath = Path()
    private val mRectF = RectF()
    private var topLeftCornerRadius = 0f
    private var topRightCornerRadius = 0f
    private var bottomLeftCornerRadius = 0f
    private var bottomRightCornerRadius = 0f
    private var borderWidth = 0f
    private var mPaintBorder = Paint()

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.RoundedImage, 0, 0
        )
        //get the default value form the attrs
        val radius = typedArray.getDimension(R.styleable.RoundedImage_iv_cornerRadius, 0f)
        topLeftCornerRadius = typedArray.getDimension(R.styleable.RoundedImage_iv_topLeftCornerRadius, radius)
        topRightCornerRadius = typedArray.getDimension(R.styleable.RoundedImage_iv_topRightCornerRadius, radius)
        bottomLeftCornerRadius = typedArray.getDimension(R.styleable.RoundedImage_iv_bottomLeftCornerRadius, radius)
        bottomRightCornerRadius = typedArray.getDimension(R.styleable.RoundedImage_iv_bottomRightCornerRadius, radius)
        borderWidth = typedArray.getDimension(R.styleable.RoundedImage_iv_borderWidth, 0f)
        val borderColor = typedArray.getColor(R.styleable.RoundedImage_iv_borderColor, 0)
        typedArray.recycle()
        mPaintBorder.strokeWidth = borderWidth
        mPaintBorder.color = borderColor
        mPaintBorder.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val cornerDimensions = floatArrayOf(
            topLeftCornerRadius, topLeftCornerRadius,
            topRightCornerRadius, topRightCornerRadius,
            bottomRightCornerRadius, bottomRightCornerRadius,
            bottomLeftCornerRadius, bottomLeftCornerRadius
        )
        mRectF.set(0.0f, 0.0f, measuredWidth.toFloat(), measuredHeight.toFloat());
        mRoundedRectPath.addRoundRect(mRectF, cornerDimensions, Path.Direction.CW)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.clipPath(mRoundedRectPath)
        if (borderWidth > 0) {
            canvas.drawRoundRect(
                0f,
                0f,
                measuredWidth.toFloat(),
                measuredHeight.toFloat(),
                topLeftCornerRadius,
                bottomLeftCornerRadius,
                mPaintBorder
            )
        }
        super.onDraw(canvas)
    }
}