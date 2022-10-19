package com.aliyun.svideo.editor.panel;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.aliyun.svideo.editor.bean.Menu;
import com.aliyun.svideo.editor.effect.VideoEffectManager;
import com.aliyun.svideo.editor.filter.VideoFilterManager;
import com.aliyun.svideo.track.thumbnail.ThumbnailFetcherManger;
import com.aliyun.svideosdk.editor.AliyunIEditor;

import java.util.Stack;

/**
 * 面板管理器
 */
public class PanelManger {
    private Context mContext;
    private AliyunIEditor mAliyunIEditor;
    private ViewGroup mActionBar;
    private RelativeLayout mControlLayout;
    private FrameLayout mPanelLayout;
    private FrameLayout mContentLayout;
    private FrameLayout mTopLayout;
    private SurfaceView mSurfaceView;
    private int mActionBarHeight;
    /**
     * 默认面板高度
     */
    private int mMainPanelHeight;
    private int mSurfaceMaxWidth;
    private int mSurfaceMaxHeight;
    private float videoAspectRatio;
    /**
     * 当前面板
     */
    private BasePanel mCurPanel;
    private ClipPanel mClipPanel;
    private Stack<BasePanel> mPanelList = new Stack<>();

    private OnPanelChangeListener mOnPanelChangeListener;

    private VideoEffectManager mVideoEffectManager;
    private VideoFilterManager mVideoFilterManager;


    public PanelManger(Context context, AliyunIEditor editor, FrameLayout contentLayout, ViewGroup actionBar,
                       FrameLayout topLayout, RelativeLayout controlLayout, FrameLayout panelLayout, SurfaceView surfaceView) {
        this.mContext = context;
        this.mAliyunIEditor = editor;
        this.mActionBar = actionBar;
        this.mContentLayout = contentLayout;
        this.mTopLayout = topLayout;
        this.mControlLayout = controlLayout;
        this.mPanelLayout = panelLayout;
        this.mSurfaceView = surfaceView;
        ViewGroup.LayoutParams params = this.mControlLayout.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) params;
            this.mMainPanelHeight = marginParams.bottomMargin;
        }
        mActionBarHeight = this.mActionBar.getHeight();
        mSurfaceMaxHeight = mContentLayout.getHeight();
        mSurfaceMaxWidth = controlLayout.getWidth();
        videoAspectRatio = this.mSurfaceView.getWidth() * 1.0f / this.mSurfaceView.getHeight();
    }

    public boolean isShow() {
        return mCurPanel != null;
    }

    public BasePanel getCurPanel() {
        return mCurPanel;
    }

    public final FrameLayout getContentLayout() {
        return mTopLayout;
    }

    public void showPanel(int menuId) {
        BasePanel newPanel = null;
        switch (menuId) {
            case Menu.MENU_TRANSITION:
                if (mClipPanel == null) {
                    mClipPanel = new ClipPanel(mContext);
                    mClipPanel.onInitData(this, mAliyunIEditor, menuId);
                }
                newPanel = mClipPanel;
                break;
            case Menu.MENU_CAPTION:
                CaptionPanel captionPanel = new CaptionPanel(mContext);
                captionPanel.onInitData(this, mAliyunIEditor, menuId);
                newPanel = captionPanel;
                break;
            case Menu.MENU_STICKER:
                StickerPanel stickerPanel = new StickerPanel(mContext);
                stickerPanel.onInitData(this, mAliyunIEditor, menuId);
                newPanel = stickerPanel;
                break;

            case Menu.MENU_FILTER:
                FilterPanel filterPanel = new FilterPanel(mContext);
                filterPanel.setVideoFilterManager(mVideoFilterManager);
                filterPanel.onInitData(this, mAliyunIEditor, menuId);
                newPanel = filterPanel;
                break;

            case Menu.MENU_EFFECT:
                EffectPanel effectPanel = new EffectPanel(mContext);
                effectPanel.setVideoEffectManager(mVideoEffectManager);
                effectPanel.onInitData(this, mAliyunIEditor, menuId);
                newPanel = effectPanel;
                break;
        }
        if (mCurPanel == newPanel) {
            return;
        }

        mCurPanel = newPanel;
        if(mOnPanelChangeListener != null) {
            mOnPanelChangeListener.OnPanelVisibilityChange(mCurPanel, true);
        }

        mCurPanel.onPlayProgress(mAliyunIEditor.getCurrentPlayPosition(), mAliyunIEditor.getCurrentStreamPosition());

        if (mCurPanel.isSurfaceNeedZoom()) {
            startAnimator(this.mMainPanelHeight, mCurPanel.getCalculateHeight(), false);
        }

        this.mPanelLayout.addView(mCurPanel);
        startAppearAnimY(mCurPanel);
    }

    public void pushGlobalPanel(BasePanel panel) {
        panel.onInitData(this, mAliyunIEditor, 0);
        if(mOnPanelChangeListener != null) {
            mOnPanelChangeListener.OnPanelVisibilityChange(panel, true);
        }
        this.mPanelList.add(panel);
        this.mTopLayout.addView(panel);
    }

    public void popGlobalPanel() {
        if(this.mPanelList.empty()) {
            return;
        }

        BasePanel lastPanel = this.mPanelList.pop();
        lastPanel.removeOwn();
        if(mOnPanelChangeListener != null) {
            mOnPanelChangeListener.OnPanelVisibilityChange(lastPanel, false);
        }
    }

    public BasePanel getCurrentGlobalPanel() {
        if(this.mPanelList.empty()) {
            return null;
        }
        return this.mPanelList.peek();
    }

    public void removeGlobalPanel(BasePanel panel) {
        panel.removeOwn();
        this.mPanelList.remove(panel);
        if(mOnPanelChangeListener != null) {
            mOnPanelChangeListener.OnPanelVisibilityChange(panel, false);
        }
    }

    public void hidePanel() {
        if (mCurPanel == null || (mCurPanel.getAnimation() != null && !mCurPanel.getAnimation().hasEnded())) {
            return;
        }

        if(mOnPanelChangeListener != null) {
            mOnPanelChangeListener.OnPanelVisibilityChange(mCurPanel, false);
        }
        if (mCurPanel.isSurfaceNeedZoom()) {
            startAnimator(mCurPanel.getCalculateHeight(), this.mMainPanelHeight, true);
        }

        startDisappearAnimY(mCurPanel);
    }

    public void onPlayProgress(long currentPlayTime, long currentStreamPlayTime) {
        for(BasePanel lBasePanel : mPanelList) {
            lBasePanel.onPlayProgress(currentPlayTime, currentStreamPlayTime);
        }

        if (mCurPanel != null) {
            mCurPanel.onPlayProgress(currentPlayTime, currentStreamPlayTime);
        }
    }

    public boolean onBackPressed() {
        if (mCurPanel == null) {
            return false;
        }

        BasePanel topGlobalPanel = getCurrentGlobalPanel();
        if(topGlobalPanel != null) {
            boolean result = topGlobalPanel.onBackPressed();
            if (!result) {
                popGlobalPanel();
                return true;
            }
            return true;
        }

        boolean result = mCurPanel.onBackPressed();
        if (!result) {
            hidePanel();
            return true;
        }
        return true;
    }

    public void onDestroy() {
        for(BasePanel lBasePanel : mPanelList) {
            lBasePanel.onDestroy();
        }
        ThumbnailFetcherManger.getInstance().releaseAll();
    }

    public void setVideoEffectManager(VideoEffectManager videoEffectManager) {
        mVideoEffectManager = videoEffectManager;
    }

    public void setVideoFilterManager(VideoFilterManager videoFilterManager) {
        mVideoFilterManager = videoFilterManager;
    }

    public void onScreenStateChanged(boolean isFullScreen) {
        ViewGroup.LayoutParams params = mControlLayout.getLayoutParams();
        ViewGroup.MarginLayoutParams marginParams = null;
        if (params instanceof ViewGroup.MarginLayoutParams) {
            marginParams = (ViewGroup.MarginLayoutParams) params;
        } else {
            marginParams = new ViewGroup.MarginLayoutParams(params);
        }
        if (isFullScreen) {
            mControlLayout.setTag(marginParams.bottomMargin);
            marginParams.setMargins(0, 0, 0, 0);
            int newMaxSurfaceHeight = (int) (mSurfaceMaxHeight + mMainPanelHeight + Math.abs(mActionBar.getTranslationY()));
            setSurfaceSize(mSurfaceMaxWidth, newMaxSurfaceHeight);
            mTopLayout.setVisibility(View.GONE);
        } else {
            int bottom = (int) mControlLayout.getTag();
            marginParams.setMargins(0, 0, 0, bottom);
            int newMaxSurfaceHeight = (int) (mSurfaceMaxHeight + mMainPanelHeight - bottom + Math.abs(mActionBar.getTranslationY()));
            setSurfaceSize(mSurfaceMaxWidth, newMaxSurfaceHeight);
            mTopLayout.setVisibility(View.VISIBLE);
        }
        mControlLayout.setLayoutParams(marginParams);
    }

    private void setSurfaceSize(int width, int height) {
        float viewAspectRatio = (float) width / height;
        float aspectDeformation = videoAspectRatio / viewAspectRatio - 1;
        if (aspectDeformation > 0) {
            height = (int) (width / videoAspectRatio);
        } else {
            width = (int) (height * videoAspectRatio);
        }
        ViewGroup.LayoutParams params = mSurfaceView.getLayoutParams();
        if (params.width != width || params.height != height) {
            params.width = width;
            params.height = height;
            mSurfaceView.setLayoutParams(params);
        }
    }

    private void startAnimator(int startHeight, int endHeight, boolean isShowActionBar) {
        int dis = startHeight - endHeight;
        ValueAnimator anim = ValueAnimator.ofInt(startHeight, endHeight);
        anim.setDuration(250);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAliyunIEditor.getPasterManager().setDisplaySize(mSurfaceView.getWidth(), mSurfaceView.getHeight());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.addUpdateListener(animation -> {
            int currentValue = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = mControlLayout.getLayoutParams();
            ViewGroup.MarginLayoutParams marginParams = null;
            if (params instanceof ViewGroup.MarginLayoutParams) {
                marginParams = (ViewGroup.MarginLayoutParams) params;
            } else {
                marginParams = new ViewGroup.MarginLayoutParams(params);
            }

            ViewGroup.LayoutParams contentParams = mContentLayout.getLayoutParams();
            ViewGroup.MarginLayoutParams contentMarginParams = null;
            if (contentParams instanceof ViewGroup.MarginLayoutParams) {
                contentMarginParams = (ViewGroup.MarginLayoutParams) contentParams;
            } else {
                contentMarginParams = new ViewGroup.MarginLayoutParams(contentParams);
            }

            float percent = ((currentValue - endHeight) * 1.0f / dis);
            if (isShowActionBar) {
                contentMarginParams.topMargin = (int) (mActionBarHeight * (1.0f - percent));
            } else {
                contentMarginParams.topMargin = (int) (mActionBarHeight * percent);

            }
            mActionBar.setTranslationY(-(mActionBarHeight - contentMarginParams.topMargin));
            mContentLayout.setLayoutParams(contentParams);

            int newMaxSurfaceHeight = (int) (mSurfaceMaxHeight + mMainPanelHeight - currentValue + Math.abs(mActionBar.getTranslationY()));
            marginParams.setMargins(0, 0, 0, currentValue);
            mControlLayout.setLayoutParams(marginParams);
            setSurfaceSize(mSurfaceMaxWidth, newMaxSurfaceHeight);
        });
        anim.start();
    }

    private void startAppearAnimY(View view) {
        final TranslateAnimation showAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        showAnim.setDuration(250);
        view.startAnimation(showAnim);
    }

    private void startDisappearAnimY(View view) {
        final TranslateAnimation hideAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f);
        hideAnim.setDuration(250);
        hideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCurPanel.removeOwn();
                mCurPanel = null;
                mAliyunIEditor.getPasterManager().setDisplaySize(mSurfaceView.getWidth(), mSurfaceView.getHeight());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(hideAnim);
    }


    public void setOnPanelChangeListener(OnPanelChangeListener onPanelChangeListener) {
        mOnPanelChangeListener = onPanelChangeListener;
    }

    public interface OnPanelChangeListener {
        void OnPanelVisibilityChange(BasePanel panel, boolean visible);
    }

}
