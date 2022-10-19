package com.aliyun.svideo.editor.caption.util

import android.content.Context
import com.aliyun.svideo.editor.caption.CaptionConfig
import com.aliyun.svideo.editor.common.util.AssetUtil.Companion.readAssertResource
import com.aliyun.svideosdk.common.struct.effect.*
import com.aliyun.svideosdk.common.struct.effect.ActionBase.PartParam

class CaptionFrameAnimationUtil {
    companion object {
        private const val TAG = "CaptionFrameAnimationUt"
        fun createAction(
            context: Context,
            animIndex: Int,
            startTimeUs: Long,
            duration: Long
        ): ActionBase? {
            var actionBase: ActionBase? = null
            val sourceId = animIndex.toString()
            when (animIndex) {
                CaptionConfig.EFFECT_NONE -> actionBase = null
                CaptionConfig.EFFECT_UP -> {

                    //方式1: 整体动画
                    val upActionTranslate = ActionTranslate()
                    upActionTranslate.setTranslateType(ActionTranslate.TranslateType.TranslateBy)
                    upActionTranslate.toPointY = 1f

                    //方式2 : 文字动画
                    /**
                     * ActionTranslate upActionTranslate = new ActionTranslate();
                     * setActionFromPoint(displayWidth, displayHeight, pasterPostionX, pasterPostionY, upActionTranslate);
                     * upActionTranslate.setScope(ActionBase.Scope.Part);
                     * ActionBase.PartParam lPartParam = new ActionBase.PartParam();
                     * lPartParam.setMode(ActionBase.PartParam.Mode.Random);
                     * lPartParam.setOverlayRadio(0.5f);
                     * upActionTranslate.setPartParam(lPartParam);
                     * upActionTranslate.setTranslateType(ActionTranslate.TranslateType.TranslateBy);
                     * upActionTranslate.setFillBefore(true);
                     * upActionTranslate.setFillAfter(true);
                     * upActionTranslate.setToPointY(5f);
                     * upActionTranslate.setToPointX(upActionTranslate.getFromPointX());
                     */
//            List<Frame<Frame.FramePoint>> frameList = new ArrayList<>();
//            frameList.add(new Frame<>(0.0f, new Frame.FramePoint(0.8f, -0.8f)));
//            frameList.add(new Frame<>(0.25f, new Frame.FramePoint(0.8f, 0.0f)));
//            frameList.add(new Frame<>(0.5f, new Frame.FramePoint(0.0f, 0.0f)));
//            frameList.add(new Frame<>(0.75f, new Frame.FramePoint(0.0f, 0.8f)));
//            frameList.add(new Frame<>(1.0f, new Frame.FramePoint(0.8f, 0.8f)));
//
//            upActionTranslate.setFrameConfig(frameList);
                    actionBase = upActionTranslate
                }
                CaptionConfig.EFFECT_RIGHT -> {
                    val rightActionBase = ActionTranslate()
                    rightActionBase.setTranslateType(ActionTranslate.TranslateType.TranslateBy)
                    rightActionBase.toPointX = 1f
                    actionBase = rightActionBase
                }
                CaptionConfig.EFFECT_LEFT -> {
                    val leftActionTranslate = ActionTranslate()
                    leftActionTranslate.setTranslateType(ActionTranslate.TranslateType.TranslateBy)
                    leftActionTranslate.toPointX = -1f
                    actionBase = leftActionTranslate
                }
                CaptionConfig.EFFECT_DOWN -> {
                    val downActionTranslate = ActionTranslate()
                    downActionTranslate.setTranslateType(ActionTranslate.TranslateType.TranslateBy)
                    downActionTranslate.toPointY = -1f
                    actionBase = downActionTranslate
                }
                CaptionConfig.EFFECT_SCALE -> {
                    actionBase = ActionScale()
                    actionBase.fromScale = 1f
                    actionBase.toScale = 0.25f
                }
                CaptionConfig.EFFECT_LINEARWIPE -> {
                    actionBase = ActionWipe()
                    actionBase.wipeMode = ActionWipe.WIPE_MODE_DISAPPEAR
                    actionBase.direction = ActionWipe.DIRECTION_RIGHT
                }
                CaptionConfig.EFFECT_FADE -> {
                    actionBase = ActionFade()
                    //方式1
                    actionBase.fromAlpha = 1.0f
                    actionBase.toAlpha = 0.2f
                }
                CaptionConfig.EFFECT_PRINT -> {
                    val lActionFade = ActionFade()
                    //            lActionFade.setFromAlpha(0.1f);
//            lActionFade.setToAlpha(1.0f);
//            lActionFade.setAnimationConfig("0.0:0.0;0.7:1.0;");
                    val frameList: MutableList<Frame<Float>> = ArrayList()
                    frameList.add(Frame(0.0f, 0.0f))
                    frameList.add(Frame(0.7f, 1.0f))
                    lActionFade.setFrameConfig(frameList)
                    lActionFade.setScope(ActionBase.Scope.Part)
                    lActionFade.setFillBefore(true)
                    lActionFade.setFillAfter(true)
                    actionBase = lActionFade
                }
                CaptionConfig.EFFECT_ROTATE_BY -> {

//            ActionRotateBy lActionRotateBy = new ActionRotateBy();
//            lActionRotateBy.setFromDegree(0);
//            lActionRotateBy.setRotateDegree(Math.PI);
//            actionBase = lActionRotateBy;

                    //钟摆采用RotateBy的实现
                    val lActionSet = ActionSet()
                    lActionSet.setMode(ActionSet.AnimationMode.Independent)
                    //首选向左转 30度
                    val lActionRotateBy1 = ActionRotateBy()
                    lActionRotateBy1.setFromDegree(0f)
                    lActionRotateBy1.rotateDegree = (-Math.PI / 6).toFloat()
                    lActionRotateBy1.setCenterX(0.0f)
                    lActionRotateBy1.setCenterY(1.0f)
                    lActionRotateBy1.startTime = startTimeUs
                    lActionRotateBy1.duration = duration / 6

                    //再向右转60，并来回转动
                    val lActionRotateBy2 = ActionRotateBy()
                    lActionRotateBy2.setFromDegree((-Math.PI / 6).toFloat())
                    lActionRotateBy2.rotateDegree = (Math.PI * 2 / 6).toFloat()
                    lActionRotateBy2.setCenterX(0.0f)
                    lActionRotateBy2.setCenterY(1.0f)
                    lActionRotateBy2.setRepeatMode(ActionBase.RepeatMode.Reverse)
                    lActionRotateBy2.startTime = startTimeUs + duration / 6
                    lActionRotateBy2.duration = duration * 2 / 6
                    lActionSet.addAction(lActionRotateBy1)
                    lActionSet.addAction(lActionRotateBy2)
                    actionBase = lActionSet
                    actionBase.setResId(sourceId)
                    return actionBase
                }
                CaptionConfig.EFFECT_ROTATE_TO -> {

//            ActionRotateTo lActionRotateTo = new ActionRotateTo();
//            lActionRotateTo.setFromDegree(0);
//            lActionRotateTo.setRotateToDegree(Math.PI * 2);
//            actionBase = lActionRotateTo;
                    val lActionSet = ActionSet()
                    lActionSet.setMode(ActionSet.AnimationMode.Independent)

                    //雨刷使用RotateTo的实现
                    val lActionRotateTo1 = ActionRotateTo()
                    //首选向右转 30度
                    lActionRotateTo1.fromDegree = 0f
                    lActionRotateTo1.rotateToDegree = (Math.PI / 6.0f).toFloat()
                    lActionRotateTo1.setCenterX(0.0f)
                    lActionRotateTo1.setCenterY(-1.0f)
                    lActionRotateTo1.startTime = startTimeUs
                    lActionRotateTo1.duration = duration / 6
                    //再向左转60，并来回转动
                    val lActionRotateTo2 = ActionRotateTo()
                    lActionRotateTo2.fromDegree = (Math.PI / 6.0f).toFloat()
                    lActionRotateTo2.rotateToDegree = (-Math.PI / 6.0f).toFloat()
                    lActionRotateTo2.setCenterX(0.0f)
                    lActionRotateTo2.setCenterY(-1.0f)
                    lActionRotateTo2.setRepeatMode(ActionBase.RepeatMode.Reverse)
                    lActionRotateTo2.startTime = startTimeUs + duration / 6
                    lActionRotateTo2.duration = duration / 3
                    lActionSet.addAction(lActionRotateTo1)
                    lActionSet.addAction(lActionRotateTo2)
                    actionBase = lActionSet
                    actionBase.setResId(sourceId)
                    return actionBase
                }
                CaptionConfig.EFFECT_WAVE -> {
                    val lActionShader = ActionShader()
                    val vertexFunc = readAssertResource(
                        context, "ugsv_caption_shader/wave.vert"
                    )
                    val fragmentFunc = readAssertResource(
                        context, "ugsv_caption_shader/wave.frag"
                    )
                    lActionShader.setShader(vertexFunc, fragmentFunc)
                    actionBase = lActionShader
                }
                CaptionConfig.EFFECT_SET1 -> {

                    //整体动画- Dependent 模式
                    val lActionSet = ActionSet()
                    lActionSet.setMode(ActionSet.AnimationMode.Dependent)
                    val lActionFade1 = ActionFade()
                    lActionFade1.fromAlpha = 0.1f
                    lActionFade1.toAlpha = 1.0f
                    lActionFade1.duration = duration / 4
                    lActionFade1.setFillBefore(true)
                    lActionSet.addAction(lActionFade1)
                    val lActionFade2 = ActionFade()
                    lActionFade2.fromAlpha = 1f
                    lActionFade2.toAlpha = 0.1f
                    lActionFade2.setStartOffset(duration * 3 / 4)
                    lActionFade2.duration = duration / 4
                    lActionSet.addAction(lActionFade2)
                    val lActionRotateBy1 = ActionRotateBy()
                    lActionRotateBy1.setFromDegree(0f)
                    lActionRotateBy1.rotateDegree = (Math.PI * 2).toFloat()
                    lActionRotateBy1.duration = duration / 2
                    lActionRotateBy1.setFillBefore(true)
                    lActionRotateBy1.setFillAfter(true)
                    lActionSet.addAction(lActionRotateBy1)
                    val lActionScale1 = ActionScale()
                    lActionScale1.fromScale = 0.25f
                    lActionScale1.toScale = 1f
                    lActionScale1.duration = duration / 2
                    lActionScale1.setFillBefore(true)
                    lActionScale1.setFillAfter(true)
                    lActionSet.addAction(lActionScale1)
                    actionBase = lActionSet
                    actionBase.setResId(sourceId)
                }
                CaptionConfig.EFFECT_SET2 -> {

                    //整体动画 - Independent 模式
                    val lActionSet = ActionSet()
                    lActionSet.setMode(ActionSet.AnimationMode.Independent)
                    val lActionFade1 = ActionFade()
                    lActionFade1.fromAlpha = 0.1f
                    lActionFade1.toAlpha = 1.0f
                    lActionFade1.startTime = startTimeUs
                    lActionFade1.duration = duration / 3
                    lActionFade1.setFillAfter(true)
                    lActionSet.addAction(lActionFade1)
                    val lActionRotateBy1 = ActionRotateBy()
                    lActionRotateBy1.setFromDegree(0f)
                    lActionRotateBy1.rotateDegree = (Math.PI * 2).toFloat()
                    lActionRotateBy1.startTime = startTimeUs
                    lActionRotateBy1.duration = duration / 2
                    lActionRotateBy1.setFillAfter(true)
                    lActionSet.addAction(lActionRotateBy1)
                    val lActionScale1 = ActionScale()
                    lActionScale1.fromScale = 0.0f
                    lActionScale1.toScale = 1f
                    lActionScale1.startTime = startTimeUs
                    lActionScale1.duration = duration / 2
                    lActionScale1.setFillAfter(true)
                    lActionSet.addAction(lActionScale1)
                    actionBase = lActionSet
                    actionBase.setResId(sourceId)
                    return actionBase
                }
                CaptionConfig.EFFECT_ROTATE_IN -> {

                    //螺旋上升
                    val lActionSet = ActionSet()
                    lActionSet.setScope(ActionBase.Scope.Part)
                    lActionSet.setMode(ActionSet.AnimationMode.Dependent)
                    val lPartParam = PartParam()
                    lPartParam.setMode(PartParam.Mode.Sequence)
                    lPartParam.setOverlayRadio(0.7f)
                    lActionSet.setPartParam(lPartParam)
                    val lActionFade1 = ActionFade()
                    lActionFade1.fromAlpha = 0.1f
                    lActionFade1.toAlpha = 1.0f
                    lActionFade1.duration = duration / 4
                    lActionFade1.setFillBefore(true)
                    lActionFade1.setFillAfter(true)
                    lActionSet.addAction(lActionFade1)
                    val lActionRotateBy1 = ActionRotateBy()
                    lActionRotateBy1.setFromDegree(0f)
                    lActionRotateBy1.rotateDegree = (Math.PI * 2).toFloat()
                    lActionRotateBy1.duration = duration / 2
                    lActionRotateBy1.setFillBefore(true)
                    lActionRotateBy1.setRepeatMode(ActionBase.RepeatMode.Restart)
                    lActionRotateBy1.setFillAfter(true)
                    lActionSet.addAction(lActionRotateBy1)
                    val lActionTranslate = ActionTranslate()
                    lActionTranslate.setTranslateType(ActionTranslate.TranslateType.TranslateBy)
                    lActionTranslate.fromPointX = 0f
                    lActionTranslate.fromPointY = -5.0f
                    lActionTranslate.toPointX = 0f
                    lActionTranslate.toPointY = 0.0f
                    lActionTranslate.duration = duration
                    lActionSet.addAction(lActionTranslate)
                    lActionSet.setFillBefore(true)
                    lActionSet.duration = duration * 3 / 4
                    lActionSet.startTime = startTimeUs
                    actionBase = lActionSet
                    actionBase.setResId(sourceId)
                    return actionBase
                }
                CaptionConfig.EFFECT_HEAT -> {

                    //心跳动画
                    val actionScale = ActionScale()
                    //            actionBase.setAnimationConfig("0:1.0,1.0;0.06:0.92,0.92;0.12:1.0252,1.0252;0.18:1.1775,1.1775;0.24:1.3116,1.3116;0.3:1.4128,1.4128;0.36:1.4761,1.4761;0.42:1.5,1.5;0.48:1.5,1.5;0.54:1.4727,1.4727;0.6:1.4089,1.4089;0.66:1.3093,1.3093;0.72:1.1779,1.1779;0.78:1.0283,1.0283;0.9:0.92,0.92;1.0:1.0,1.0;");
                    val frameList: MutableList<Frame<Float>> = ArrayList()
                    frameList.add(Frame(0.0f, 1.0f))
                    frameList.add(Frame(0.06f, 0.92f))
                    frameList.add(Frame(0.12f, 1.0252f))
                    frameList.add(Frame(0.18f, 1.1775f))
                    frameList.add(Frame(0.24f, 1.3116f))
                    frameList.add(Frame(0.3f, 1.4128f))
                    frameList.add(Frame(0.36f, 1.4761f))
                    frameList.add(Frame(0.42f, 1.5f))
                    frameList.add(Frame(0.48f, 1.5f))
                    frameList.add(Frame(0.54f, 1.4727f))
                    frameList.add(Frame(0.6f, 1.4089f))
                    frameList.add(Frame(0.66f, 1.3093f))
                    frameList.add(Frame(0.72f, 1.1779f))
                    frameList.add(Frame(0.78f, 1.0283f))
                    frameList.add(Frame(0.9f, 0.92f))
                    frameList.add(Frame(1.0f, 1.0f))
                    actionScale.setFrameConfig(frameList)
                    actionScale.startTime = startTimeUs
                    actionScale.duration = duration / 2
                    actionScale.setRepeatCount(1)
                    actionScale.setRepeatMode(ActionBase.RepeatMode.Restart)
                    actionBase = actionScale
                    actionBase.setResId(sourceId)
                    return actionBase
                }
                CaptionConfig.EFFECT_ROUNDSCAN -> {
                    val lActionShader = ActionShader()
                    val vertexFunc = readAssertResource(
                        context, "ugsv_caption_shader/round_scan.vert"
                    )
                    val fragmentFunc = readAssertResource(
                        context, "ugsv_caption_shader/round_scan.frag"
                    )
                    lActionShader.setShader(vertexFunc, fragmentFunc)
                    actionBase = lActionShader
                    actionBase.setResId(sourceId)
                }
                CaptionConfig.EFFECT_WAVE_JUMP -> {

                    //波浪弹入
                    val lActionSet = ActionSet()
                    lActionSet.setMode(ActionSet.AnimationMode.Dependent)
                    lActionSet.setScope(ActionBase.Scope.Part)
                    val lPartParam = PartParam()
                    lPartParam.setMode(PartParam.Mode.Sequence)
                    lPartParam.setOverlayRadio(0.6f)
                    lActionSet.setPartParam(lPartParam)
                    val lActionTranslate = ActionTranslate()
                    lActionTranslate.setTranslateType(ActionTranslate.TranslateType.TranslateBy)
                    lActionTranslate.fromPointX = 0f
                    lActionTranslate.fromPointY = 0.0f
                    lActionTranslate.toPointX = 0f
                    lActionTranslate.toPointY = 1.0f
                    lActionTranslate.duration = duration / 2
                    lActionSet.addAction(lActionTranslate)
                    val lActionTranslate2 = ActionTranslate()
                    lActionTranslate2.setTranslateType(ActionTranslate.TranslateType.TranslateBy)
                    lActionTranslate2.fromPointX = 0.0f
                    lActionTranslate2.fromPointY = 1.0f
                    lActionTranslate2.toPointX = 0f
                    lActionTranslate2.toPointY = 0.0f
                    lActionTranslate2.setFillAfter(true)
                    lActionTranslate2.setStartOffset(duration / 2)
                    lActionTranslate2.duration = duration / 2
                    lActionSet.addAction(lActionTranslate2)
                    val lActionFade1 = ActionFade()
                    lActionFade1.fromAlpha = 0.0f
                    lActionFade1.toAlpha = 1.0f
                    lActionFade1.duration = duration / 4
                    lActionFade1.setFillBefore(true)
                    lActionSet.addAction(lActionFade1)
                    lActionSet.setFillBefore(true)
                    lActionSet.setFillAfter(true)
                    actionBase = lActionSet
                    actionBase.setResId(sourceId)
                }
                else -> {}
            }
            if (actionBase != null) {
                actionBase.duration = duration
                actionBase.startTime = startTimeUs
                actionBase.resId = sourceId
            }
            return actionBase
        }
    }
}