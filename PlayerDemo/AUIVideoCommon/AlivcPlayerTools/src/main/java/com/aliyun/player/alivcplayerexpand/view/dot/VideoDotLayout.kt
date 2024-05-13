package com.aliyun.player.alivcplayerexpand.view.dot

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.aliyun.player.alivcplayerexpand.bean.DotBean
import com.aliyun.player.alivcplayerexpand.databinding.LayoutVideoDotLayoutBinding
import com.aliyun.player.alivcplayerexpand.util.DensityUtil
import com.aliyun.player.alivcplayerexpand.util.TimeFormater
import com.aliyun.player.alivcplayerexpand.util.ScreenUtils


private const val TAG = "VideoDotLayout"

class VideoDotLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    val mViewBinding = LayoutVideoDotLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    private var mCurrentDotBean: DotBean? = null
    var onVideoDotPlayClick: OnVideoDotPlayClick? = null
    private var mDurationSeconds = 60f

    private val mHideDotRunnable = Runnable {
        handleDotContentHide()
    }

    init {
        mViewBinding.apply {
            videoDotsView.setDotClickListener(object : VideoDotsView.OnDotClick {
                override fun onDotClick(index: Int, dotBean: DotBean) {
                    showDotContent(dotBean, true)
                }
            })
            videoDotContentLayout.setOnClickListener {
                if (mCurrentDotBean != null) {
                    handleDotContentHide()
                    onVideoDotPlayClick?.onDotPlayClick(mCurrentDotBean!!.time.toInt())
                }
            }
            videoDotContentLayout.visibility = View.GONE
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        Log.i(TAG, "onTouchEvent")
        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//        Log.i(TAG, "onInterceptTouchEvent")
        return super.onInterceptTouchEvent(ev)
    }

    private fun handleDotContentHide() {
        mViewBinding.apply {
            videoDotContentLayout.visibility = View.GONE
            videoDotsView.setSelected(-1)
        }
    }

    fun onShow() {
        mViewBinding.videoDotContentLayout.visibility = View.GONE
    }

    private fun showDotContent(dotBean: DotBean?, show: Boolean) {
        Log.i(TAG, "showDotContent dotBean:$dotBean show:$show")
        mViewBinding.apply {
            videoDotContentLayout.visibility = if (show) View.VISIBLE else View.GONE
            if (show && dotBean != null) {
                videoDotContentLayout.post {
                    val ratio = dotBean.time.toInt() / mDurationSeconds
                    val seekBarLeftMarginDp = 70f
                    val dotMargin = (ScreenUtils.getWidth(context) - DensityUtil.dip2px(
                        context,
                        seekBarLeftMarginDp * 2
                    )) * ratio
                    val contentWidth = videoDotContentLayout.width / 2
                    val maxLeftMargin = ScreenUtils.getWidth(context) - DensityUtil.dip2px(
                        context,
                        seekBarLeftMarginDp * 2
                    ) - videoDotContentLayout.width
                    var leftMargin =
                        if (dotMargin > contentWidth) dotMargin.toInt() - contentWidth else 0
                    Log.i(
                        TAG,
                        "showDotContent  ratio:$ratio dotMargin:$dotMargin contentWidth:$contentWidth leftMargin:$leftMargin maxLeftMargin:$maxLeftMargin"
                    )
                    if (leftMargin > maxLeftMargin) {
                        leftMargin = maxLeftMargin
                    }
                    val layoutParams: MarginLayoutParams =
                        videoDotContentLayout.layoutParams as MarginLayoutParams
                    layoutParams.leftMargin = leftMargin
                    videoDotContentLayout.layoutParams = layoutParams
                }
                mCurrentDotBean = dotBean
                videoDotTime.text = TimeFormater.formatMs((dotBean.time.toInt() * 1000).toLong())
                videoDotContent.text = dotBean.content
                removeCallbacks(mHideDotRunnable)
                postDelayed(mHideDotRunnable, 3000L)
            }
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(mHideDotRunnable)
    }

    fun setData(list: List<DotBean>, durationSeconds: Float) {
        mDurationSeconds = durationSeconds
        mViewBinding.videoDotsView.setData(list, durationSeconds)
    }

    interface OnVideoDotPlayClick {
        fun onDotPlayClick(seekSeconds: Int)
    }
}