package com.aliyun.player.alivcplayerexpand.playlist

interface OnListPlayCallback {
    /**
     * UI显示 loading
     */
    fun onPrepare()

    /**
     * loading 消失
     */
    fun onPlaying()

    /**
     * 显示暂停
     */
    fun onPause()

    fun onPlayComplete()

    /**
     * 播放进度更新 0- 1
     */
    fun onPlayProgress(
        playProgress: Float,
        currentPlayMillis: Int,
        durationMillis: Int
    )

    fun onPlayError(errorCode: Int, msg: String)

    /**
     * 如果是续播，则会回调此接口告诉上层
     */
    fun onContrastPlay(durationMillis: Int)
}