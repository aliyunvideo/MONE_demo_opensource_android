package com.aliyun.video.homepage.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.aliyun.auiplayerserver.flowfeed.HomePageApiService
import com.aliyun.auiplayerserver.flowfeed.repository.ListDataFetcher
import github.leavesc.reactivehttp.base.BaseReactiveViewModel

private const val TAG = "BaseListViewModel"

/**
 *  列表请求的基础 ViewModel 类
 */
open class BaseListViewModel<T> : BaseReactiveViewModel() {
    protected val mListData = mutableListOf<T>()
    val mListLiveData = MutableLiveData<MutableList<T>>()
    private val mState = MutableLiveData<Int>()
    private var mPage = 1
    private val mRemoteDataSource by lazy {
        ListDataFetcher(this, HomePageApiService::class.java)
    }


    /**
     * 开始请求数据
     */
    open fun requestListData(isLoadMore: Boolean = false,context: Context, refresh: Boolean) {
        if (refresh) {
            mPage = 1
        } else {
            mPage++
        }
    }


}