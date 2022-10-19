package com.aliyun.video.common.ui

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen

/**
 * 支持框架式加载动画的 RecyclerView
 */
class SkeletonRecyclerView @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    RecyclerView(context, attrs, defStyleAttr) {
    var skeletonScreen: SkeletonScreen? = null
    private var mSkeletonShow = false

    fun showSkeleton(mItemLayoutId: Int, mAdapter: Adapter<out ViewHolder>, count: Int) {
        skeletonScreen = Skeleton.bind(this)
            .adapter(mAdapter)
            .shimmer(false)
            .angle(20)
            .frozen(false)
            .duration(1200)
            .count(count)
            .load(mItemLayoutId)
            .show()
        mSkeletonShow = true
    }

    fun hideSkeleton() {
        if (mSkeletonShow) {
            skeletonScreen?.hide()
            mSkeletonShow = false
        }
    }
}