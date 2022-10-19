package com.aliyun.video.common.ui

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.aliyun.apsaravideo.AppHomeWatcher

open class BaseFragment : Fragment, AppHomeWatcher.AppStatusWatchListener {

    protected var mIsHidden = false
    protected var mIsPause = true
    protected open val mEnableInterceptBackSpace = false
    private val mAppHomeWatcher = AppHomeWatcher.InnerHolder.mInstance
    protected var mActivityPaused = false
    protected var mActivityResume = true

    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    protected fun isHostInvalid(): Boolean {
        return activity == null || requireActivity().isDestroyed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAppHomeWatcher.addWatchListener(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mIsHidden = hidden
    }

    override fun onResume() {
        super.onResume()
        mIsPause = false
    }

    override fun onPause() {
        super.onPause()
        mIsPause = true
    }


    protected fun finishSelf() {
        requireActivity().supportFragmentManager.popBackStack();
    }

    protected fun requestBaseActivity(): BaseActivity? {
        return requireActivity() as? BaseActivity
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAppHomeWatcher.removeListener(this)
    }

    override fun onAppStateChange(state: Int, activity: Activity?) {
        if (activity != getActivity() || activity == null)
            return
        when (state) {
            AppHomeWatcher.AppStatusWatchListener.STATE_RESUME -> {
                mActivityPaused = false
                mActivityResume = true
                onActivityResume()
            }

            AppHomeWatcher.AppStatusWatchListener.STATE_PAUSE -> {
                mActivityPaused = true
                mActivityResume = false
                onActivityPause()
            }
        }
    }

    open fun onActivityResume() {

    }

    open fun onActivityPause() {

    }

    inline fun <reified T : BaseFragment> showFragment(
        containerId: Int,
        bundle: Bundle?,
        tag: String
    ) {
        var fragment: BaseFragment? =
            requireActivity().supportFragmentManager.findFragmentByTag(tag) as BaseFragment?
        if (fragment == null) {
            val fragmentClazz = T::class.java
            val constructorList = fragmentClazz.constructors
            if (constructorList.isEmpty()) {
                fragment = BaseFragment()
            }
            fragment = constructorList[0].newInstance() as BaseFragment?
        }
        fragment?.arguments = bundle
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        if (!fragment!!.isAdded) {
            fragmentTransaction.add(containerId, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        } else if (fragment.isHidden) {
            fragmentTransaction.show(fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }
}