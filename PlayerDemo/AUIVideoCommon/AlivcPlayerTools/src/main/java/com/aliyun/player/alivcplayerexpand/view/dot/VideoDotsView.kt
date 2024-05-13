package com.aliyun.player.alivcplayerexpand.view.dot

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.aliyun.player.alivcplayerexpand.R
import com.aliyun.player.alivcplayerexpand.bean.DotBean
import com.aliyun.player.alivcplayerexpand.util.DensityUtil

private const val TAG = "VideoDotsView"

class VideoDotsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val mDotList = mutableListOf<DotBean>()
    private val mPaint = Paint()
    private var mDotRadius: Float = DensityUtil.dip2px(context, 2f).toFloat()
    private var mSelectedDotRadius: Float = DensityUtil.dip2px(context, 5f).toFloat()
    private var mDotColor = Color.WHITE
    private var mSelectedDotColor = Color.BLUE
    private var mSelectedIndex = 1
    private var mDurationSeconds = 60f
    private var mDotClick: OnDotClick? = null

    init {
        attrs?.apply {
            val typeArray = context.obtainStyledAttributes(this, R.styleable.VideoDotsView)
            mDotRadius = typeArray.getDimension(
                R.styleable.VideoDotsView_dot_radius,
                DensityUtil.dip2px(context, 3f).toFloat()
            )
            mSelectedDotRadius = typeArray.getDimension(
                R.styleable.VideoDotsView_selected_dot_radius,
                DensityUtil.dip2px(context, 6f).toFloat()
            )
            mDotColor = typeArray.getColor(R.styleable.VideoDotsView_dot_color, Color.WHITE)
            mSelectedDotColor =
                typeArray.getColor(R.styleable.VideoDotsView_selected_dot_color, Color.BLUE)
            typeArray.recycle()
        }
        mPaint.isAntiAlias = true
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.textAlign = Paint.Align.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val hMeasureSpec =
            MeasureSpec.makeMeasureSpec(mSelectedDotRadius.toInt() * 2, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, hMeasureSpec)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
//        Log.i(TAG, "onTouchEvent")
        var consume = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                consume = handleTouchEvent(x)
            }
        }
        return if (consume) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    private fun handleTouchEvent(x: Float): Boolean {
        for (index in mDotList.indices) {
            //该点在View 中的相对坐标
            val cx =
                (mDotList[index].time.toFloat()) / mDurationSeconds * (measuredWidth - paddingRight - paddingLeft)
            if (cx - mDotRadius * 3 < x && cx + mDotRadius * 3 > x) {
                setSelected(index)
//                Log.i(TAG, "handleTouchEvent select index:$index")
                mDotClick?.onDotClick(index, mDotList[index])
                return true
            }
        }
        return false
    }

    /**
     * 设置打点数据和时间
     */
    fun setData(list: List<DotBean>, durationSeconds: Float) {
        mDurationSeconds = durationSeconds
        mDotList.clear()
        mDotList.addAll(list)
        invalidate()
    }


    fun setSelected(index: Int) {
        if (index > mDotList.size)
            return
        mSelectedIndex = index
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mDotList.isEmpty() || canvas == null) {
            return
        }
        val width = measuredWidth - paddingLeft - paddingRight
        for (index in mDotList.indices) {
            if (index == mSelectedIndex) {
                val cx = (mDotList[index].time.toFloat()) / mDurationSeconds * width
                val cy = mSelectedDotRadius
                mPaint.color = mSelectedDotColor
                canvas.drawCircle(cx, cy, mSelectedDotRadius, mPaint)
            }
            val cx = (mDotList[index].time.toFloat()) / mDurationSeconds * width
            val cy = mSelectedDotRadius
            mPaint.color = mDotColor
            canvas.drawCircle(cx, cy, mDotRadius, mPaint)
        }
    }

    fun setDotClickListener(onDotClick: OnDotClick) {
        this.mDotClick = onDotClick
    }

    interface OnDotClick {
        fun onDotClick(index: Int, dotBean: DotBean)
    }
}