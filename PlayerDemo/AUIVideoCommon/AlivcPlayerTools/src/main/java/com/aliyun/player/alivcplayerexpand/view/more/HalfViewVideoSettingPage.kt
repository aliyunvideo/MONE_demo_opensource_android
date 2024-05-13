package com.aliyun.player.alivcplayerexpand.view.more

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliyun.player.alivcplayerexpand.databinding.LayoutHalfScreenVideoMoreSettingPageBinding
import com.aliyun.player.alivcplayerexpand.util.DensityUtil
import com.aliyun.video.common.ui.adapter.ItemClickDelegate
import com.aliyun.video.common.ui.adapter.MultiSelectedAdapter

class HalfViewVideoSettingPage @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val mViewBinding =
        LayoutHalfScreenVideoMoreSettingPageBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
    var mItemClick: OnItemClick? = null

    init {
        mViewBinding.apply {
            halfScreenVideoMoreBack.setOnClickListener {
                mItemClick?.onBack()
            }
            videoMoreCancelTv.setOnClickListener {
                mItemClick?.cancelAll()
            }
        }
    }

    fun setUpData(
        type: VideoSettingType,
        contentList: List<String>,
        title: String,
        selectedIndex: Int
    ) {
        if (contentList.isEmpty())
            return
        mViewBinding.apply {
            val multiSelectedAdapter = MultiSelectedAdapter()
            videoPageSettingRcv.adapter = multiSelectedAdapter
            val itemInfoList = mutableListOf<VideoSettingItemInfo>()
            for (content in contentList) {
                itemInfoList.add(VideoSettingItemInfo(type, content, itemInfoList.size))
            }
            multiSelectedAdapter.register(PageSettingItemDelegate(object :
                ItemClickDelegate.ItemClickListener<VideoSettingItemInfo> {
                override fun onItemClick(position: Int, entity: VideoSettingItemInfo) {
                    multiSelectedAdapter.notifySelected(position)
                    mItemClick?.onItemClick(type, position)
                }

            }))
            videoMoreSettingPageTitle.text = title
            videoPageSettingRcv.layoutManager =
                GridLayoutManager(videoPageSettingRcv.context, 4, RecyclerView.VERTICAL, false)
            videoPageSettingRcv.addItemDecoration(
                SpaceItemDecoration(
                    DensityUtil.dip2px(context, 8f),
                    DensityUtil.dip2px(context, 8f)
                )
            )
            multiSelectedAdapter.items = itemInfoList
            multiSelectedAdapter.notifyDataSetChanged()
            multiSelectedAdapter.notifySelected(selectedIndex)
        }
    }

    enum class VideoSettingType(val type: Int) {
        VIDEO_SPEED(0),
        VIDEO_DEFINITION(1),
        DAMKUN_LOCATION(2)
    }

    interface OnItemClick {
        fun onItemClick(type: VideoSettingType, position: Int)
        fun onBack()
        fun cancelAll()
    }

    class SpaceItemDecoration(private val bottomSpace: Int, private val rightSpace: Int) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            val isFirstRow = parent.getChildAdapterPosition(view) % 2 == 0
            outRect.bottom = bottomSpace
            if (isFirstRow) {
                outRect.right = rightSpace / 2
                outRect.left = 0
            } else {
                outRect.right = 0
                outRect.left = rightSpace / 2
            }
        }
    }
}