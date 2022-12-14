package com.aliyun.svideo.editor.caption

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.lifecycle.LifecycleOwner
import com.aliyun.svideo.editor.caption.adapter.CaptionEditPanelAdapter
import com.aliyun.svideo.editor.caption.databinding.CaptionEditPanelBinding
import com.aliyun.svideo.editor.caption.viewmodel.CaptionEditViewModel
import com.aliyun.svideo.editor.common.panel.BasePanelView
import com.aliyun.ugsv.common.utils.ScreenUtils
import com.aliyun.ugsv.common.utils.ThreadUtils
import com.aliyun.ugsv.common.utils.ToastUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

open class CaptionEditPanelView(context: Context) :
    BasePanelView<CaptionEditPanelBinding>(context) {
    private lateinit var captionEditVM: CaptionEditViewModel
    private lateinit var captionTabPagerAdapter: CaptionEditPanelAdapter
    private lateinit var captionTabList: List<ICaptionTabPage>
    private var realDisplayHeight = 0
    private lateinit var textWatcher :EditorTextWatcher
    private lateinit var lazyGetTextRunnable : Runnable

    override val layoutId: Int
        get() = R.layout.caption_edit_panel

    override fun initView(context: Context) {
        super.initView(context)

        captionTabList = mutableListOf(
            CaptionFlowerTabPage(context),
            CaptionBubbleTabPage(context),
            CaptionStyleTabPage(context),
            CaptionAnimationTabPage(context)
        )

        initViewPage()
        initTableView()
        initEditView()

        realDisplayHeight = ScreenUtils.getDisplayHeight(getContext() as Activity)
    }

    private fun initViewPage() {
        mDataBinding.captionEditTabsPages.offscreenPageLimit = 3
        captionTabPagerAdapter = CaptionEditPanelAdapter()
        mDataBinding.captionEditTabsPages.adapter = captionTabPagerAdapter
        captionTabPagerAdapter.setData(captionTabList)
        captionTabPagerAdapter.notifyDataSetChanged()
    }

    private fun initTableView() {
        mDataBinding.captionEditTabs.tabMode = TabLayout.MODE_SCROLLABLE

        for (i in captionTabList) {
            val tab = mDataBinding.captionEditTabs.newTab()
            mDataBinding.captionEditTabs.addTab(tab)
        }

        TabLayoutMediator(mDataBinding.captionEditTabs, mDataBinding.captionEditTabsPages) { tab, position ->
            tab.text = captionTabList[position].getTitle()
        }.attach()
    }

    private fun initEditView() {
        lazyGetTextRunnable = Runnable {
            val editable: Editable = mDataBinding.captionEditText.text!!
            var content = editable.toString()

            if (TextUtils.isEmpty(content)) {
                content = getContext().getString(R.string.ugsv_caption_effect_text_default)
            }
            var maxLength = 40
//        if (!isTextOnly()) {
//            maxLength = 10
//        }
            //            else {
//                //???????????????90??????
//                maxLength = 90;
//            }
            // ??????EditText????????????maxLength?????????
            if (maxLength != 0 && content.length > maxLength) {
                // ?????????????????????????????????????????????11?????????????????????????????????????????????11-1???11??????????????????????????????????????????10??????????????????
                ToastUtils.show(context, R.string.ugsv_caption_tip_text_limit)
                val deleteCount = content.length - maxLength
                val selIndex: Int = mDataBinding.captionEditText.selectionStart
                var startIndex = selIndex - deleteCount
                var endIndex = selIndex
                //???????????????????????????????????????
                if (startIndex < 0) {
                    startIndex = 0
                }
                if (endIndex > content.length) {
                    endIndex = content.length
                }
                if (startIndex > endIndex) {
                    startIndex = endIndex
                }
                editable.delete(startIndex, endIndex)
            }

            captionEditVM.onTextChanged(content)
        }
        textWatcher = EditorTextWatcher(lazyGetTextRunnable)
        mDataBinding.captionEditText.addTextChangedListener(textWatcher)
    }

    fun setViewModel(captionEditVM: CaptionEditViewModel) {

        this.captionEditVM = captionEditVM
        mDataBinding.viewModel = captionEditVM
        val lifeOwner = context as LifecycleOwner
        mDataBinding.lifecycleOwner = lifeOwner
        captionTabPagerAdapter.setViewModel(captionEditVM)
        captionEditVM.currentCaption.value?.let {
            mDataBinding.captionEditText.setText(it.text)
        }
    }


    private class EditorTextWatcher(val textChanged : Runnable?) : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {
            textChanged?.let {
                ThreadUtils.removeCallbacks(it)
                ThreadUtils.runOnUiThread(it, 100)
            }
        }
    }
}