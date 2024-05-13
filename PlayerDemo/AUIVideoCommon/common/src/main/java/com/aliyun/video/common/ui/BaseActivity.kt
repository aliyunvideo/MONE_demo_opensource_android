package com.aliyun.video.common.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import com.aliyun.video.common.R
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ktx.immersionBar


/**
 *
 */
abstract class BaseActivity : FragmentActivity() {
    protected open val mStatusBarColor = R.color.status_bar_color
    protected open val mNavigationBarColor = R.color.black

    override fun onResume() {
        super.onResume()
        setStatusBarColor(mStatusBarColor, mNavigationBarColor)
    }

    fun setStatusBarColor(statusBarColor: Int, navigationBarColor: Int) {
        immersionBar {
            fitsSystemWindows(true)
            statusBarColor(statusBarColor)
            navigationBarColor(navigationBarColor)
            autoDarkModeEnable(true) //自动状态栏字体和导航栏图标变色，必须指定状态栏颜色和导航栏颜色才可以自动变色哦
            autoStatusBarDarkModeEnable(true, 0.2f) //
            init()
        }
    }

    fun hideStatusBar() {
        immersionBar {
            hideBar(BarHide.FLAG_HIDE_BAR)  //隐藏状态栏或导航栏或两者，不写默认不隐藏
            init()
        }
    }


    fun fullScreen(fullscreen: Boolean) {
        immersionBar {
            if (fullscreen) {
                hideStatusBar()
                fitsSystemWindows(false)
                transparentBar()
            } else {
                showStatusBar()
                fitsSystemWindows(true)
                statusBarColor(mStatusBarColor)
                navigationBarColor(mNavigationBarColor)
            }
            init()
        }
    }


    fun showStatusBar() {
        immersionBar {
            hideBar(BarHide.FLAG_SHOW_BAR)  //隐藏状态栏或导航栏或两者，不写默认不隐藏
            init()
        }
    }

    private fun setUpStatusBar() {
        val window: Window = getWindow()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.TRANSPARENT)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    protected open fun isStrangePhone(): Boolean {
        return ("mx5".equals(Build.DEVICE, ignoreCase = true)
                || "Redmi Note2".equals(Build.DEVICE, ignoreCase = true)
                || "Z00A_1".equals(Build.DEVICE, ignoreCase = true)
                || "hwH60-L02".equals(Build.DEVICE, ignoreCase = true)
                || "hermes".equals(Build.DEVICE, ignoreCase = true)
                || "V4".equals(
            Build.DEVICE,
            ignoreCase = true
        ) && "Meitu".equals(Build.MANUFACTURER, ignoreCase = true)
                || "m1metal".equals(
            Build.DEVICE,
            ignoreCase = true
        ) && "Meizu".equals(Build.MANUFACTURER, ignoreCase = true))
    }

    inline fun <reified T : BaseFragment> showFragment(
        containerId: Int,
        bundle: Bundle?,
        tag: String
    ) {
        var fragment: BaseFragment? =
            this.supportFragmentManager.findFragmentByTag(tag) as BaseFragment?
        if (fragment == null) {
            val fragmentClazz = T::class.java
            val constructorList = fragmentClazz.constructors
            if (constructorList.isEmpty()) {
                fragment = BaseFragment()
            }
            fragment = constructorList[0].newInstance() as BaseFragment?
        }
        fragment?.arguments = bundle
        val fragmentTransaction = this.supportFragmentManager.beginTransaction()
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


    override fun onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            if (!handleBackSpace()) {
                super.onBackPressed()
            }
        }
    }

    /**
     * @return 返回 true 表示要拦截
     */
    protected open fun handleBackSpace(): Boolean {
        return false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        KeyEventDispatcher.dispatchEvent(keyCode, event)
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!handleBackSpace()) {
                super.onKeyDown(keyCode, event)
            } else true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

}