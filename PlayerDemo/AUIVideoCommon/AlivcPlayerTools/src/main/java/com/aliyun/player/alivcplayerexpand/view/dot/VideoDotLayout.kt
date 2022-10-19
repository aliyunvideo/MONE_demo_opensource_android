package com.aliyun.player.alivcplayerexpand.view.dot

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import com.aliyun.aio.utils.DensityUtil
import com.aliyun.player.alivcplayerexpand.bean.DotBean
import com.aliyun.player.alivcplayerexpand.databinding.LayoutVideoDotLayoutBinding
import com.aliyun.player.alivcplayerexpand.util.TimeFormater
import com.aliyun.player.aliyunplayerbase.util.ScreenUtils
import com.aliyun.player.aliyunplayerbase.util.setVisible
import com.aliyun.video.common.JJLog


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
            videoDotContentLayout.setVisible(false)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        JJLog.logi(TAG, "onTouchEvent")
        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//        JJLog.logi(TAG, "onInterceptTouchEvent")
        return super.onInterceptTouchEvent(ev)
    }

    private fun handleDotContentHide() {
        mViewBinding.apply {
            videoDotContentLayout.setVisible(false)
            videoDotsView.setSelected(-1)
        }
    }

    fun onShow() {
        mViewBinding.videoDotContentLayout.setVisible(false)
    }

    private fun showDotContent(dotBean: DotBean?, show: Boolean) {
        JJLog.logi(TAG, "showDotContent dotBean:$dotBean show:$show")
        mViewBinding.apply {
            videoDotContentLayout.setVisible(show)
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
                    JJLog.logi(
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