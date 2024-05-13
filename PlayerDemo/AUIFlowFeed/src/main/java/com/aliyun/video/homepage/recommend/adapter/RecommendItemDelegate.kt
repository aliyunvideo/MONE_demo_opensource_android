package com.aliyun.video.homepage.recommend.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aliyun.auiplayerserver.bean.VideoInfo
import com.aliyun.player.alivcplayerexpand.theme.Theme
import com.aliyun.player.alivcplayerexpand.util.PlayConfigManager
import com.aliyun.player.alivcplayerexpand.view.gesture.SimplyTapGestureListener
import com.aliyun.player.alivcplayerexpand.util.ImageLoader
import com.aliyun.video.R
import com.aliyun.video.common.ui.getItemView
import com.aliyun.video.databinding.LayoutRecommendListItemBinding
import com.drakeet.multitype.ItemViewDelegate

private const val TAG = "RecommendItemDelegate"

class RecommendItemDelegate(val clickFuc: OnRecommendItemClick) :
    ItemViewDelegate<VideoInfo, RecommendItemDelegate.ViewHolder>() {
    private var mViewAttach = false

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var mVideoFunctionViewShow = false
        private var mPlayIconShow = false
        private val mItemViewBinding = LayoutRecommendListItemBinding.bind(itemView)
        private val mVideoFunctionHideRunnable = Runnable {
            mItemViewBinding.apply {
                mVideoFunctionViewShow = false
                handlePlayFunctionView()
            }
        }
        private val mOnSeekChangeListener = object : SeekBar.OnSeekBarChangeListener {
            private var mFromUser = false
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mFromUser = fromUser
                if (fromUser) {
                    clickFuc.onSeeking(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (mFromUser) {
                    val progress = seekBar.progress
                    clickFuc.onSeekFinishVideo(progress)
                }
            }
        }

        fun rebind() {
            mItemViewBinding.apply {
                mVideoCover.visibility = View.VISIBLE
                mVideoPlayIcon.visibility = View.VISIBLE
                audioModeView.visibility = View.GONE
                tvSmallDuration.visibility = View.VISIBLE
                mVideoPlayIcon.setTag(R.id.item_playing, false)
                mVideoGestureView.setTag(R.id.item_audio_mode, false)
                updateVoiceCheckBox()
            }
        }

        fun onDetach() {
            mItemViewBinding.root.removeCallbacks(mVideoFunctionHideRunnable)
        }

        fun bind(item: VideoInfo, position: Int) {
            mItemViewBinding.apply {
                val coverUrl =
                    if (item.firstFrameUrl.isNullOrEmpty()) item.coverUrl else item.firstFrameUrl
                //封面
                mVideoCover.visibility = View.VISIBLE
                ImageLoader.loadImg(
                    coverUrl,
                    mVideoCover,
                    R.drawable.ic_material_placeholder
                )
                //时间-总时长
                tvSmallDuration.text = DateUtils.formatElapsedTime(item.duration.toLong())
                alivcInfoSmallDuration.text = DateUtils.formatElapsedTime(item.duration.toLong())
                //标题
                root.removeCallbacks(mVideoFunctionHideRunnable)
                mVideoTitle.text = item.title
                if (item.user != null) {
                    //头像
                    ImageLoader.loadCircleImg(
                        item.user?.avatarUrl,
                        mPortrait,
                        R.drawable.default_portrait_icon
                    )
                    //昵称
                    mAuthorName.text =
                        if (item.user?.userName?.isEmpty() == true) "默认昵称.尼古拉斯" else item.user?.userName
                }
                //默认显示 pause icon
                mPlayIconShow = false
                mVideoPlayIcon.apply {
                    this.visibility = View.VISIBLE
                    tvSmallDuration.visibility = View.VISIBLE
                    showPlayIcon(false)
                    setOnClickListener {
                        val playing = mVideoPlayIcon.getTag(R.id.item_playing) as Boolean
                        if (playing) {
                            //进入暂停
                            clickFuc.pauseVideo(position)
                            showPlayIcon(false)
                            root.removeCallbacks(mVideoFunctionHideRunnable)
                        } else {
                            //播放
                            clickFuc.playVideo(position)
                            showPlayIcon(true)
                            root.removeCallbacks(mVideoFunctionHideRunnable)
                            root.postDelayed(mVideoFunctionHideRunnable, 3000)
                        }
                    }
                }
                root.setOnClickListener {
                    //进入半屏播放页
                    clickFuc.jumpHalfScreenPage(position)
                }
                mCommentNumber.setOnClickListener {
                    clickFuc.jumpHalfScreenPage(position)
                }
                alivcScreenMode.setOnClickListener {
                    //进入全屏播放页
                    clickFuc.jumpFullScreenPage(position)
                }
                layoutContrastPlayTip.setOnClickListener {
                    it.visibility = View.GONE
                    clickFuc.onSeekFinishVideo(0)
                }
                alivcInfoSmallSeekbar.setOnSeekBarChangeListener(mOnSeekChangeListener)
                updateSeekBarTheme(Theme.Blue)
                alivcInfoSmallBar.visibility = View.GONE
                tvSmallDuration.visibility = View.VISIBLE
                mVideoFunctionViewShow = false
                mVideoGestureView.setOnGestureListener(object : SimplyTapGestureListener() {
                    override fun onSingleTapClick() {
                        val audioMode: Boolean =
                            mVideoGestureView.getTag(R.id.item_audio_mode) as Boolean
                        if (audioMode)
                            return

                        val playing = mVideoPlayIcon.getTag(R.id.item_playing) as Boolean
                        if (playing) {
                            mVideoFunctionViewShow = !mVideoFunctionViewShow
                            handlePlayFunctionView()
                            root.removeCallbacks(mVideoFunctionHideRunnable)
                            root.postDelayed(mVideoFunctionHideRunnable, 3000)
                        }
                    }

                    override fun onGestureEnd() {
                        super.onGestureEnd()
                        clickFuc.onLongPress(false)
                    }

                    override fun onLongPress() {
                        super.onLongPress()
                        clickFuc.onLongPress(true)
                    }
                })
                //选中是正常音量，没选中是静音
                updateVoiceCheckBox()
                checkBoxVoice.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (mViewAttach) {
                        clickFuc.onVoiceOpen(isChecked)
                    }
                }
            }
        }

        private fun showPlayIcon(playing: Boolean) {
            mItemViewBinding.mVideoPlayIcon.apply {
                setTag(R.id.item_playing, playing)
                val drawableId =
                    if (playing) R.drawable.feed_play_pause_icon else R.drawable.feed_play_play_icon
                setImageResource(drawableId)
            }
        }

        private fun updateVoiceCheckBox() {
            mItemViewBinding.apply {
                //checked 表示非静音
                if (checkBoxVoice.isChecked == PlayConfigManager.getPlayConfig().listPlayMute) {
                    checkBoxVoice.isChecked = !PlayConfigManager.getPlayConfig().listPlayMute
                }
            }
        }

        private fun handlePlayFunctionView() {
            mItemViewBinding.apply {
                updateVoiceCheckBox()

                val audioMode: Boolean = mVideoGestureView.getTag(R.id.item_audio_mode) as Boolean
                val isPlaying: Boolean = mVideoPlayIcon.getTag(R.id.item_playing) as Boolean

                alivcInfoSmallBar.visibility =
                    if (mVideoFunctionViewShow || audioMode) View.VISIBLE else View.GONE
                tvSmallDuration.visibility =
                    if (!mVideoFunctionViewShow) View.VISIBLE else View.GONE

                mVideoPlayIcon.visibility =
                    if (!isPlaying || mVideoFunctionViewShow) View.VISIBLE else View.GONE
                checkBoxVoice.visibility =
                    if (!mVideoFunctionViewShow && !audioMode) View.VISIBLE else View.GONE
            }
        }

        /**
         * 更新当前主题色
         * @param theme 设置的主题色
         */
        private fun updateSeekBarTheme(theme: Theme) {
            //获取不同主题的图片
            var progressDrawableResId =
                R.drawable.alivc_info_seekbar_bg_blue
            var thumbResId =
                R.drawable.alivc_info_seekbar_thumb_blue
            when (theme) {
                Theme.Blue -> {
                    progressDrawableResId = R.drawable.alivc_info_seekbar_bg_blue
                    thumbResId = R.drawable.shape_circle_video_seek_bar
                }
                Theme.Green -> {
                    progressDrawableResId = R.drawable.alivc_info_seekbar_bg_green
                    thumbResId = R.drawable.alivc_info_seekbar_thumb_green
                }
                Theme.Orange -> {
                    progressDrawableResId = R.drawable.alivc_info_seekbar_bg_orange
                    thumbResId = R.drawable.alivc_info_seekbar_thumb_orange
                }
                else -> {
                    progressDrawableResId = R.drawable.alivc_info_seekbar_bg_red
                    thumbResId = R.drawable.alivc_info_seekbar_thumb_red
                }
            }

            //这个很有意思。。哈哈。不同的seekbar不能用同一个drawable，不然会出问题。
            // https://stackoverflow.com/questions/12579910/seekbar-thumb-position-not-equals-progress
            mItemViewBinding.apply {
                //设置到对应控件中
                val smallProgressDrawable: Drawable? =
                    ContextCompat.getDrawable(itemView.context, progressDrawableResId)
                val smallThumb: Drawable? =
                    ContextCompat.getDrawable(itemView.context, thumbResId)
                alivcInfoSmallSeekbar.progressDrawable = smallProgressDrawable
                alivcInfoSmallSeekbar.thumb = smallThumb
            }

        }
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        item: VideoInfo
    ) {
        holder.bind(item, getPosition(holder))
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder =
        ViewHolder(parent.getItemView(R.layout.layout_recommend_list_item))

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
    }

    interface OnRecommendItemClick {
        fun jumpHalfScreenPage(position: Int)
        fun jumpFullScreenPage(position: Int)
        fun pauseVideo(position: Int)
        fun playVideo(position: Int)
        fun onSeekFinishVideo(progress: Int)
        fun onSeeking(progress: Int)
        fun onLongPress(begin: Boolean)
        fun onVoiceOpen(open: Boolean)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        mViewAttach = true
        holder.rebind()
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        mViewAttach = false
        holder.onDetach()

    }
}