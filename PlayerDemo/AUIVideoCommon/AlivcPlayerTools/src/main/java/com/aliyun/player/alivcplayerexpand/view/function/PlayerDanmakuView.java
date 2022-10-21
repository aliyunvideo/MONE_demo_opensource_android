package com.aliyun.player.alivcplayerexpand.view.function;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;


import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.aliyunplayerbase.util.AliyunScreenMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * bilibili 弹幕
 *
 * @author hanyu
 */
public class PlayerDanmakuView extends DanmakuView {

    //弹幕最大显示行数
    private HashMap<Integer, Integer> mMaxLinesPair;
    //是否禁止重叠
    private HashMap<Integer, Boolean> mOverLappingEnablePair;
    //弹幕移动速度
    private float mSpeed = 1;
    //弹幕字体大小
    private float mTextSize = 6;
    //弹幕字体颜色,默认是白色
    private int mTextColor = Color.WHITE;
    //弹幕样式,默认从右到左
    private int mDanmaType = BaseDanmaku.TYPE_SCROLL_RL;
    //保存弹幕
    private Map<Integer, ArrayList<String>> mDanmakuList = new HashMap<>();

    private DanmakuContext mDanmakuContext;
    //当前屏幕模式
    private AliyunScreenMode mScreenMode = AliyunScreenMode.Small;
//    String[] danmkuArray = new String[]{
//            "东京奥运会，中国射击队，00后小姐姐，斩落奥运会首金",
//            "东京奥运会，中国射击队，00后小姐姐，斩落奥运会首金",
//            "东京奥运会，中国射击队，00后小姐姐，斩落奥运会首金",
//            "东京奥运会，中国射击队，00后小姐姐，斩落奥运会首金",
//            "东京奥运会，中国射击队，00后小姐姐，斩落奥运会首金1",
//            "阿里云视频云，做大做强",
//            "做厚中台，做深服务，做好基础",
//            "做厚中台，做深服务，做好基础1",
//            "做厚中台，做深服务，做好基础2",
//            "做厚中台，做深服务，做好基础3",
//            "做厚中台，做深服务，做好基础4",
//            "做厚中台，做深服务，做好基础5",
//            "做厚中台，做深服务，做好基础6",
//            "做厚中台，做深服务，做好基础7",
//            "做厚中台，做深服务，做好基础79",
//            "做厚中台，做深服务，做好基础119",
//            "上云就上阿里云，越战越勇",
//            "上云就上阿里云，越战越勇1",
//            "上云就上阿里云，越战越勇2",
//            "上云就上阿里云，越战越勇4",
//            "上云就上阿里云，越战越勇5",
//            "上云就上阿里云，越战越勇6",
//            "上云就上阿里云，越战越勇7",
//            "上云就上阿里云，越战越勇8",
//            "上云就上阿里云，越战越勇9",
//            "上云就上阿里云，越战越勇10",
//            "满屏的弹幕都在替赵政委伤心1",
//            "满屏的弹幕都在替赵政委伤心2",
//            "悲伤那么大",
//            "夫妻双双把家还",
//            "夫妻双双把家还",
//            "夫妻双双把家还",
//            "出来了！是个男孩",
//            "你要给我一个交代",
//    };
    String[] danmkuArray = new String[]{};


    public PlayerDanmakuView(Context context) {
        super(context);
        init();
    }

    public PlayerDanmakuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerDanmakuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private BaseDanmakuParser mBaseDanmakuParser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };

    private void init() {
        mDanmakuContext = DanmakuContext.create();

        mMaxLinesPair = new HashMap<>();
        mOverLappingEnablePair = new HashMap<>();
        //设置弹幕从右到左,最多显示3行
        mMaxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 3);
        //设置可以重叠
        mOverLappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);

        initDanmakuContext();
        initCallback();
    }

    private void initDanmakuContext() {
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_DEFAULT, 3)
                .setDuplicateMergingEnabled(false)//合并重复弹幕
                .setScrollSpeedFactor(mSpeed)
                .setScaleTextSize(mTextSize)
                .setMaximumLines(mMaxLinesPair)
                .setMaximumVisibleSizeInScreen(0)//设置弹幕密度,1自适应,0无限制
                .preventOverlapping(mOverLappingEnablePair)
                .setDanmakuMargin(5);

        enableDanmakuDrawingCache(true);
        prepare(mBaseDanmakuParser, mDanmakuContext);
    }

    private void initCallback() {
        setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                start();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {
            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {
            }

            @Override
            public void drawingFinished() {
            }
        });
    }

    /**
     * 设置弹幕速率
     */
    public void setDanmakuSpeed(float speed) {
        if (mDanmakuContext != null) {
            if (speed <= 0.01) {
                speed = 0.01f;
            }
            mDanmakuContext.setScrollSpeedFactor(speed);
        }
    }

    /**
     * 设置弹幕显示区域
     */
    public void setDanmakuRegion(int progress) {
        if (mMaxLinesPair != null) {
            switch (progress) {
                case 0:
                    //1/4弹幕
                    if (mDanmakuContext != null) {
                        mMaxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 3);
                        mDanmakuContext.setMaximumLines(mMaxLinesPair);
                    }
                    break;
                case 1:
                    //一半弹幕
                    if (mDanmakuContext != null) {
                        mMaxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5);
                        mDanmakuContext.setMaximumLines(mMaxLinesPair);
                    }
                    break;
                case 2:
                    //3/4弹幕
                    if (mDanmakuContext != null) {
                        mMaxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 7);
                        mDanmakuContext.setMaximumLines(null);
                    }
                    break;
                case 3:
                    //无限制
                    if (mDanmakuContext != null) {
                        mDanmakuContext.setMaximumLines(null);
                    }
                    break;
                default:
                    if (mDanmakuContext != null) {
                        mMaxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 3);
                        mDanmakuContext.setMaximumLines(null);
                    }
                    break;
            }
        }
    }

    public void addTestDanmku() {
        long time = getCurrentTime();
        for (int i = 0; i < danmkuArray.length; i++) {
            addTestDataDanmaku(danmkuArray[i], time + (i % 5) * 1000);
        }
    }

    /**
     * 添加一条弹幕
     *
     * @param content 弹幕内容
     */
    public void addDanmaku(String content, long time) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        BaseDanmaku baseDanmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(mDanmaType);
        if (baseDanmaku == null)
            return;
        baseDanmaku.text = content;
        baseDanmaku.textSize = mTextSize;
        baseDanmaku.setTime(getCurrentTime());
        baseDanmaku.borderColor = Color.WHITE;
        baseDanmaku.textColor = mTextColor;
        addDanmaku(baseDanmaku);
        int key = (int) (time / 1000);
        ArrayList<String> stringsList = mDanmakuList.get(key);
        if (stringsList == null) {
            stringsList = new ArrayList<>();
        }
        stringsList.add(content);
        mDanmakuList.put(key, stringsList);
    }

    public void addTestDataDanmaku(String content, long time) {
        if (TextUtils.isEmpty(content) || mDanmakuContext == null || !isPrepared()) {
            return;
        }
        BaseDanmaku baseDanmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(mDanmaType);
        if (baseDanmaku == null)
            return;
        baseDanmaku.text = content;
        baseDanmaku.textSize = mTextSize;
        baseDanmaku.setTime(time);
        baseDanmaku.textColor = mTextColor;
        addDanmaku(baseDanmaku);
        int key = (int) (time / 1000);
        ArrayList<String> stringsList = mDanmakuList.get(key);
        if (stringsList == null) {
            stringsList = new ArrayList<>();
        }
        stringsList.add(content);
        mDanmakuList.put(key, stringsList);
    }

    /**
     * 添加一条弹幕
     *
     * @param content 弹幕内容
     */
    public void addDanmaku(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        BaseDanmaku baseDanmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(mDanmaType);
        if (baseDanmaku == null) {
            return;
        }
        baseDanmaku.text = content;
        baseDanmaku.textSize = mTextSize;
        baseDanmaku.textColor = mTextColor;
        baseDanmaku.setTime(getCurrentTime());
        addDanmaku(baseDanmaku);
    }

    /**
     * 切换弹幕显示状态
     */
    public void switchDanmaku(boolean show) {
        if (show) {
            resume();
            show();
        } else {
            pause();
            hide();
        }
    }

    /**
     * 返回弹幕是否开启
     */
    public boolean danmuIsShown() {
        return isShown();
    }

    public void setScreenMode(AliyunScreenMode screenMode) {
        this.mScreenMode = screenMode;
    }


    /**
     * 播放器的 currentPosition
     */
    public void setCurrentPosition(int mCurrentPosition) {
//        if (mScreenMode == AliyunScreenMode.Small || !isShown()) {
//            return;
//        }
//        int time = mCurrentPosition / 1000;
//        if (time == 2) {
//            addDanmaku(getResources().getString(R.string.alivc_danmaku_text_1));
//            addTestDanmku();
//        }
//        if (time == 3) {
//            addDanmaku(getResources().getString(R.string.alivc_danmaku_text_2));
//            addTestDanmku();
//        }
//        if ((time == 5)) {
//            addDanmaku(getResources().getString(R.string.alivc_danmaku_text_3));
//            addTestDanmku();
//        }

        /**
         * TODO 要和IOS统一，输入过的弹幕只展示一次
         */
//        ArrayList<String> stringsList = mDanmakuList.get(time);
//        if (stringsList == null || stringsList.size() == 0) {
//            return;
//        }
//        for (String str : stringsList) {
//            addDanmaku(str);
//        }
    }

    public void clearDanmaList() {
        if (mDanmakuList != null && mDanmakuList.size() > 0) {
            mDanmakuList.clear();
        }
    }
}
