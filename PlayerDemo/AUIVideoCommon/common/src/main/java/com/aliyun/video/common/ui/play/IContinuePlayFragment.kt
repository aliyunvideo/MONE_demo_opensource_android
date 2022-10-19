package com.aliyun.video.common.ui.play

interface IContinuePlayFragment {
    /**
     * 其它 fragment 会盖在上面
     */
    fun onBeforeHidden()

    /**
     * fragment 恢复到前台
     */
    fun onReShow()
}