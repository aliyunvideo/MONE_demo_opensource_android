package com.aliyun.svideo.editor.caption

class CaptionConfig {
    companion object {

        //字幕动效
        const val EFFECT_NONE = 0
        const val EFFECT_UP = 1
        const val EFFECT_RIGHT = 4
        const val EFFECT_LEFT = 3
        const val EFFECT_DOWN = 2
        const val EFFECT_LINEARWIPE = 6
        const val EFFECT_FADE = 5
        const val EFFECT_SCALE = 7
        const val EFFECT_PRINT = 8
        const val EFFECT_ROTATE_BY = 9
        const val EFFECT_ROTATE_TO = 10
        const val EFFECT_SET1 = 11
        const val EFFECT_SET2 = 12
        const val EFFECT_WAVE = 13
        const val EFFECT_ROTATE_IN = 14
        const val EFFECT_HEAT = 15
        const val EFFECT_ROUNDSCAN = 16
        const val EFFECT_WAVE_JUMP = 17
        val POSITION_FONT_ANIM_ARRAY = intArrayOf(
            EFFECT_NONE, EFFECT_UP, EFFECT_DOWN, EFFECT_LEFT,EFFECT_RIGHT,
            EFFECT_LINEARWIPE, EFFECT_FADE, EFFECT_SCALE,
            EFFECT_PRINT, EFFECT_ROTATE_BY, EFFECT_ROTATE_TO, EFFECT_WAVE,
            EFFECT_ROTATE_IN, EFFECT_HEAT, EFFECT_ROUNDSCAN, EFFECT_WAVE_JUMP
        )

        val NAME_FONT_ANIM_ARRAY = intArrayOf(
            R.drawable.ic_editor_reset,R.drawable.caption_ic_xiangshang, R.drawable.caption_ic_xiangxia,R.drawable.caption_ic_xiangzuo, R.drawable.caption_ic_xiangyou,
            R.drawable.caption_ic_xianxingcadan, R.drawable.caption_ic_danrudanchu, R.drawable.caption_ic_suofang,
            R.drawable.caption_ic_daziji, R.drawable.caption_ic_zhongbai, R.drawable.caption_ic_yushua,  R.drawable.caption_ic_bolang,
            R.drawable.caption_ic_luoxuanshangsheng, R.drawable.caption_ic_xintiao, R.drawable.caption_ic_saomiao, R.drawable.caption_ic_bolangtanru
        )
        const val DEFAULT_DURATION: Long = 1500
        const val CAPTION_MIN_DURATION: Long = 500
    }
}