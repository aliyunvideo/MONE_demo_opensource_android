package com.aliyun.svideo.editor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.aio.avtheme.AVBaseThemeActivity;
import com.aliyun.common.utils.BitmapUtil;
import com.aliyun.common.utils.StringUtils;
import com.aliyun.common.utils.ThreadUtils;
import com.aliyun.svideo.editor.adapter.BaseRecyclerAdapter;
import com.aliyun.svideo.editor.adapter.MenuAdapter;
import com.aliyun.svideo.editor.bean.Menu;
import com.aliyun.svideo.editor.caption.CaptionManager;
import com.aliyun.svideo.editor.caption.databinding.CaptionEditControllerBinding;
import com.aliyun.svideo.editor.caption.model.CaptionBorderState;
import com.aliyun.svideo.editor.caption.util.CaptionResourceUtil;
import com.aliyun.svideo.editor.caption.viewmodel.CaptionBorderViewModel;
import com.aliyun.svideo.editor.caption.viewmodel.CaptionEditViewModel;
import com.aliyun.svideo.editor.caption.widget.CaptionBorderView;
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId;
import com.aliyun.svideo.editor.controller.MusicSelectController;
import com.aliyun.svideo.editor.effect.VideoEffectManager;
import com.aliyun.svideo.editor.filter.VideoFilterManager;
import com.aliyun.svideo.editor.panel.CaptionEditPanel;
import com.aliyun.svideo.editor.panel.PanelManger;
import com.aliyun.svideo.editor.panel.StickerEditPanel;
import com.aliyun.svideo.editor.panel.StickerPanel;
import com.aliyun.svideo.editor.publish.AUIPublishActivity;
import com.aliyun.svideo.editor.sticker.databinding.StickerEditBorderControllerBinding;
import com.aliyun.svideo.editor.sticker.model.StickerBorderState;
import com.aliyun.svideo.editor.sticker.util.StickerResourceUtil;
import com.aliyun.svideo.editor.sticker.viewmodel.StickerEditViewModel;
import com.aliyun.svideo.editor.sticker.widget.StickerPasterPreview;
import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;
import com.aliyun.svideosdk.common.struct.project.Source;
import com.aliyun.svideosdk.editor.AliyunIEditor;
import com.aliyun.svideosdk.editor.AliyunPasterManager;
import com.aliyun.svideosdk.editor.EditorCallBack;
import com.aliyun.svideosdk.editor.impl.AliyunEditorFactory;
import com.aliyun.svideosdk.editor.impl.AliyunPasterControllerCompoundCaption;
import com.aliyun.ugsv.common.utils.FastClickUtil;
import com.aliyun.ugsv.common.utils.ToastUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AUIVideoEditorActivity extends AVBaseThemeActivity implements BaseRecyclerAdapter.OnItemClickListener<Menu>, View.OnClickListener {
    private static final String TAG = AUIVideoEditorActivity.class.getName();
    private static final int PUBLISH_REQUEST_CODE = 2022;
    private ImageView mBackBtn;
    private FrameLayout mContentLayout;
    private FrameLayout mTopLayout;
    private SurfaceView mSurfaceView;

    private RelativeLayout mControlLayout;

    private RelativeLayout mNormalLayout;
    private TextView mTimeTv;
    private ImageView mPlayBtn;
    private ImageView mFullBtn;

    private FrameLayout mFullControlLayout;
    private TextView mFullTimeTv;
    private TextView mFullDurationTv;
    private SeekBar mProgress;
    private ImageView mFullPlayBtn;
    private ImageView mContractBtn;

    private FrameLayout mBottomLayout;
    private RecyclerView mMenuRecyclerView;
    private MenuAdapter mMenuAdapter;
    private FrameLayout mPanelLayout;
    private ViewGroup mActionBar;
    private TextView mNextView;
    /**
     * 面板管理器
     */
    private PanelManger mPanelManger;
    /**
     * 编辑核心接口类
     */
    private AliyunIEditor mAliyunIEditor;
    private long mDurationUs;
    private long mCurPlayTimeUs;
    private boolean mIsNeedResume = false;

    private MusicSelectController mMusicSelectController;
    private CaptionBorderView mCaptionBorderView;
    private CaptionBorderViewModel mCaptionBorderViewModel;

    private StickerPasterPreview mStickerPasterPreview;
    private StickerEditViewModel mStickerEditViewModel;

    private VideoEffectManager mVideoEffectManager;
    private VideoFilterManager mVideoFilterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.ugsv_editor_activity_video_editor);
        initView();
        initBottomMenu();
        String projectJsonPath = getIntent().getStringExtra("projectJsonPath");
        if(!StringUtils.isEmpty(projectJsonPath)){
            initEditor(Uri.parse(projectJsonPath));
        }
        mVideoEffectManager = new VideoEffectManager();
        mVideoFilterManager = new VideoFilterManager();
        initState();

    }

    private void initView() {
        mBackBtn = findViewById(R.id.btn_back);
        mContentLayout = findViewById(R.id.layout_content);
        mTopLayout = findViewById(R.id.activity_top_layout);
        mSurfaceView = findViewById(R.id.v_surface);
        mBottomLayout = findViewById(R.id.layout_bottom);
        mPanelLayout = findViewById(R.id.layout_panel_content);
        mControlLayout = findViewById(R.id.layout_control_play);

        mActionBar = findViewById(R.id.ugsv_editor_toolbar);
        mNextView = findViewById(R.id.ugsv_editor_toolbar_next);

        mNormalLayout = findViewById(R.id.layout_normal);
        mTimeTv = findViewById(R.id.tv_time);
        mPlayBtn = findViewById(R.id.btn_play);
        mFullBtn = findViewById(R.id.btn_full);

        mFullControlLayout = findViewById(R.id.layout_full);
        mFullTimeTv = findViewById(R.id.tv_cur_time);
        mFullDurationTv = findViewById(R.id.tv_duration);
        mProgress = findViewById(R.id.progress);
        mFullPlayBtn = findViewById(R.id.btn_full_play);
        mContractBtn = findViewById(R.id.btn_contract);

        mMenuRecyclerView = findViewById(R.id.recycler_bottom_menu);
        mProgress.setMax(100);
        mBackBtn.setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);
        mFullPlayBtn.setOnClickListener(this);
        mFullBtn.setOnClickListener(this);
        mContractBtn.setOnClickListener(this);
        mNextView.setOnClickListener(this);
        mContentLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNormalLayout.getVisibility() == View.GONE) {
                    return;
                }
                if (mPanelManger != null) {
                    mPanelManger.onBackPressed();
                }
            }
        });
        mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mAliyunIEditor.seek(progress, TimeUnit.MILLISECONDS);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mAliyunIEditor.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mAliyunIEditor.play();
            }
        });
        mNormalLayout.setVisibility(View.VISIBLE);
        mFullControlLayout.setVisibility(View.GONE);
    }

    VideoEffectManager getVideoEffectManager() {
        return mVideoEffectManager;
    }

    private EditorCallBack mEditorCallback = new EditorCallBack() {

        @Override
        public void onEnd(int state) {
            Log.e(TAG, "AliyunIEditor onEnd:" + state);
            if (mAliyunIEditor != null) {
                //循环播放
                mAliyunIEditor.replay();
            }
        }

        @Override
        public void onError(int errorCode) {
            Log.e(TAG, "AliyunIEditor error " + errorCode);
        }

        @Override
        public int onCustomRender(int srcTextureID, int width, int height, long pts) {
            return srcTextureID;
        }

        @Override
        public int onTextureRender(int srcTextureID, int width, int height, long pts) {
            return 0;
        }

        @Override
        public void onPlayProgress(long currentPlayTime, long currentStreamPlayTime) {
            mTimeTv.post(() -> {
                if (mPanelManger != null) {
                    mPanelManger.onPlayProgress(currentPlayTime, currentStreamPlayTime);
                }
                updatePlayTime(currentPlayTime);

                if(mCaptionBorderViewModel != null) {
                    mCaptionBorderViewModel.onPlayerTimeChange(currentStreamPlayTime);
                }
                if(mStickerEditViewModel != null) {
                    mStickerEditViewModel.onPlayerTimeChange(currentStreamPlayTime);
                }

            });

        }

        @Override
        public void onDataReady() {
            Log.e(TAG, "AliyunIEditor onDataReady");
        }

        @Override
        public void onPlayStateChanged(boolean isPlaying) {
            mPlayBtn.post(() -> {
                if (isPlaying) {
                    mPlayBtn.setImageResource(R.drawable.ic_btn_control_pause);
                    mFullPlayBtn.setImageResource(R.drawable.ic_btn_control_pause);
                } else {
                    mPlayBtn.setImageResource(R.drawable.ic_btn_control_play);
                    mFullPlayBtn.setImageResource(R.drawable.ic_btn_control_play);
                }
            });
        }
    };

    private void initEditor(Uri uri) {
        //设置onTextureRender能够回调
        mEditorCallback.mNeedRenderCallback = EditorCallBack.RENDER_CALLBACK_TEXTURE;
        mAliyunIEditor = AliyunEditorFactory.creatAliyunEditor(uri, mEditorCallback);
        mMusicSelectController = new MusicSelectController(this, mAliyunIEditor);
        mContentLayout.post(() -> {
            initSurfaceLayout();
            mSurfaceView.post(() -> {
                AliyunPasterManager mPasterManager = mAliyunIEditor.getPasterManager();
                mSurfaceView.getHolder().setFixedSize(mSurfaceView.getWidth(), mSurfaceView.getHeight());
                mPasterManager.setDisplaySize(mSurfaceView.getWidth(), mSurfaceView.getHeight());
                mAliyunIEditor.init(mSurfaceView, getApplicationContext());
                mAliyunIEditor.setDisplayMode(VideoDisplayMode.FILL);
                mAliyunIEditor.setVolume(50);
                mAliyunIEditor.setFillBackgroundColor(Color.BLACK);
                changePlayState();
                mPlayBtn.setImageResource(R.drawable.ic_btn_control_pause);
                updateDuration(mAliyunIEditor.getDuration());

                mPanelManger = new PanelManger(AUIVideoEditorActivity.this, mAliyunIEditor, mContentLayout,mActionBar,
                        mTopLayout, mControlLayout, mPanelLayout, mSurfaceView);
                mPanelManger.setVideoEffectManager(mVideoEffectManager);
                mPanelManger.setVideoFilterManager(mVideoFilterManager);
                mPanelManger.setOnPanelChangeListener((panel, visible) -> {
                    if(panel.getPanelType() == Menu.MENU_CAPTION) {
                        if(visible) {
                            initCaptionState();
                        } else {
                            unInitCaptionState();
                        }
                    } else if(panel.getPanelType() == Menu.MENU_STICKER) {
                        if(visible) {
                            initStickerState((StickerPanel) panel);
                        } else {
                            unInitStickerState();
                        }
                    }
                });
            });
        });
    }

    private void initSurfaceLayout() {
        int outputWidth = mAliyunIEditor.getEditorProject().getConfig().getOutputWidth();
        int outputHeight = mAliyunIEditor.getEditorProject().getConfig().getOutputHeight();
        float videoAspectRatio = outputWidth * 1.0f / outputHeight;
        int width = mContentLayout.getWidth();
        int height = mContentLayout.getHeight();
        float viewAspectRatio = (float) width / height;
        float aspectDeformation = videoAspectRatio / viewAspectRatio - 1;
        if (aspectDeformation > 0) {
            height = (int) (width / videoAspectRatio);
        } else {
            width = (int) (height * videoAspectRatio);
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mSurfaceView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        mSurfaceView.setLayoutParams(layoutParams);
    }

    private void initBottomMenu() {
        List<Menu> menuList = new ArrayList<>();
        menuList.add(new Menu(Menu.MENU_MUSIC, R.drawable.ic_editor_menu_music, R.string.ugsv_editor_music));
        menuList.add(new Menu(Menu.MENU_TRANSITION, R.drawable.ic_editor_menu_transition, R.string.ugsv_editor_transition));
        menuList.add(new Menu(Menu.MENU_CAPTION, R.drawable.ic_editor_menu_caption, R.string.ugsv_editor_caption));
        menuList.add(new Menu(Menu.MENU_STICKER, R.drawable.ic_editor_menu_sticker, R.string.ugsv_editor_sticker));
        menuList.add(new Menu(Menu.MENU_FILTER, R.drawable.ic_editor_menu_filter, R.string.ugsv_editor_filter));
        menuList.add(new Menu(Menu.MENU_EFFECT, R.drawable.ic_editor_menu_effect, R.string.ugsv_editor_effect));
        mMenuAdapter = new MenuAdapter();
        mMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mMenuRecyclerView.setAdapter(mMenuAdapter);
        mMenuAdapter.addItems(menuList);
        mMenuAdapter.setItemClickListener(this);
    }
    private void initState() {
        copyAssets();
    }

    private void initCaptionState() {
        ViewModelProvider lViewModelProvider = new ViewModelProvider(this);
        CaptionEditViewModel captionEditViewModel = lViewModelProvider.get(CaptionEditViewModel.class);

        mCaptionBorderViewModel = new CaptionBorderViewModel(captionEditViewModel, mAliyunIEditor);
        mCaptionBorderViewModel.bind();
        //选中或者删除某文字监听
        Transformations.distinctUntilChanged(mCaptionBorderViewModel.getCurrentCaptionBorderState()).observe(this, borderController -> toggleEditCaptionTips(captionEditViewModel.getCurrentCaption().getValue(),  borderController));
    }

    private void unInitCaptionState() {
        mCaptionBorderViewModel.unbind();
    }

    private void initStickerState(StickerPanel stickerPanel) {
        stickerPanel.setOnItemClickListener((view, id, obj) -> {
            if(id == PanelItemId.ITEM_ID_ADD) {
                StickerEditPanel lStickerEditPanel = new StickerEditPanel(AUIVideoEditorActivity.this);
                FrameLayout.LayoutParams lLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                lLayoutParams.gravity = Gravity.BOTTOM;
                lStickerEditPanel.setLayoutParams(lLayoutParams);
                mPanelManger.pushGlobalPanel(lStickerEditPanel);


            }
        });

        ViewModelProvider lViewModelProvider = new ViewModelProvider(this);
        mStickerEditViewModel = lViewModelProvider.get(StickerEditViewModel.class);
        mStickerEditViewModel.init(mAliyunIEditor);


        //选中或者删除某文字监听
        Transformations.distinctUntilChanged(mStickerEditViewModel.getCurrentStickerBorderState()).observe(this, this::toggleEditStickerControlView);
    }

    private void unInitStickerState() {
        mStickerEditViewModel.unBind();
    }

    private void copyAssets() {
        ThreadUtils.runOnSubThread(() -> {
            CaptionResourceUtil.Companion.copyResource(AUIVideoEditorActivity.this);
            StickerResourceUtil.Companion.copyResource(AUIVideoEditorActivity.this);
        });
    }

    private void toggleEditCaptionTips(@Nullable AliyunPasterControllerCompoundCaption caption, @Nullable CaptionBorderState captionBorderState) {

        if(captionBorderState != null && captionBorderState.getVisible()) {
            if(mCaptionBorderView == null) {
                CaptionEditControllerBinding lCaptionEditControllerBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.caption_edit_controller, null, false);
                mCaptionBorderView = (CaptionBorderView)(lCaptionEditControllerBinding.getRoot());

                lCaptionEditControllerBinding.setViewModel(mCaptionBorderViewModel);
                mCaptionBorderViewModel.setOnItemClickListener((view, id, obj) -> {
                    if(id == PanelItemId.ITEM_ID_EDIT) {

                        showCaptionEditView();

                    } else if(id == PanelItemId.ITEM_ID_DELETE) {
                        if(mCaptionBorderView != null) {
                            this.mContentLayout.removeView(mCaptionBorderView);
                            mCaptionBorderView = null;
                        }
                        //如果当前以及打开了文字编辑框，则弹掉
                        if(mPanelManger.getCurrentGlobalPanel() instanceof CaptionEditPanel) {
                            mPanelManger.popGlobalPanel();
                        }
                    }
                });


                this.mContentLayout.addView(mCaptionBorderView, mSurfaceView.getLayoutParams());
            }

            mCaptionBorderView.bind(Objects.requireNonNull(captionBorderState.getCaptionBorder()), (rotation, scale, left, top, right, bottom) -> {
                PointF pasterPositionPointF = new PointF();
                pasterPositionPointF.x = (left + right) >> 1;
                pasterPositionPointF.y = (top + bottom) >> 1;
                CaptionManager.Companion.applyCaptionBorderChanged(Objects.requireNonNull(caption), -rotation, scale, pasterPositionPointF);
            });


        } else {

            if(mCaptionBorderView != null) {
                this.mContentLayout.removeView(mCaptionBorderView);
                mCaptionBorderView = null;
            }
        }
    }

    private void showCaptionEditView() {
        if(mPanelManger.getCurrentGlobalPanel() instanceof  CaptionEditPanel) {
            return;
        }
        CaptionEditPanel panel = new CaptionEditPanel(this);
        FrameLayout.LayoutParams lLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lLayoutParams.gravity = Gravity.BOTTOM;
        panel.setLayoutParams(lLayoutParams);
        mPanelManger.pushGlobalPanel(panel);
    }

    private void toggleEditStickerControlView(@Nullable StickerBorderState stickerBorderState) {

        if(stickerBorderState != null && stickerBorderState.getVisible()) {
            if(mStickerPasterPreview == null) {
                StickerEditBorderControllerBinding lStickerEditBorderControllerBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.sticker_edit_border_controller, null, false);
                mStickerPasterPreview = new StickerPasterPreview(lStickerEditBorderControllerBinding.editOverlay, Objects.requireNonNull(stickerBorderState.getController()), stickerBorderState.getNewlyAdd());
                ViewModelProvider lViewModelProvider = new ViewModelProvider(this);
                StickerEditViewModel stickerEditViewModel = lViewModelProvider.get(StickerEditViewModel.class);
                lStickerEditBorderControllerBinding.setViewModel(stickerEditViewModel);
                stickerBorderState.getController().setPasterView(mStickerPasterPreview);
                //sticker首次添加或者切换
                this.mContentLayout.addView(mStickerPasterPreview.getPasterView(), mSurfaceView.getLayoutParams());
                mStickerPasterPreview.editTimeStart();
            } else {
                //仅用于添加sticker时的更新
                //这里不能调用complete
                mStickerPasterPreview.setPasterController(Objects.requireNonNull(stickerBorderState.getController()));
            }


        } else {
            if(stickerBorderState != null) {
                removeStickerPasterPreview(stickerBorderState.getUpdate());
            } else {
                removeStickerPasterPreview(true);
            }
        }
    }

    private void removeStickerPasterPreview(boolean applyChange) {
        if(mStickerPasterPreview != null) {
            if(applyChange) {
                mStickerPasterPreview.editTimeCompleted();
            }
            mStickerPasterPreview.getController().setPasterView(null);
            this.mContentLayout.removeView(mStickerPasterPreview.getPasterView());
            mStickerPasterPreview = null;
        }
    }

    @Override
    public void onItemClick(Menu menu, int position) {
        if (menu.id == Menu.MENU_MUSIC) {
            if (mMusicSelectController != null) {
                mAliyunIEditor.pause();
                mMusicSelectController.showMusicSelectPanel(getSupportFragmentManager());
            }
        } else {
            mPanelManger.showPanel(menu.id);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAliyunIEditor != null && mIsNeedResume) {
            if (mAliyunIEditor.isPaused()) {
                mAliyunIEditor.play();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAliyunIEditor != null) {
            mIsNeedResume = mAliyunIEditor.isPlaying();
            if (mIsNeedResume) {
                mAliyunIEditor.pause();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mNormalLayout.getVisibility() == View.GONE) {
            changeScreenState();
            return;
        }
        boolean isConsume = false;
        if (mPanelManger != null) {
            isConsume = mPanelManger.onBackPressed();
        }
        if (!isConsume) {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPanelManger != null) {
            mPanelManger.onDestroy();
        }
        if (mAliyunIEditor != null) {
            mAliyunIEditor.onDestroy();
        }
    }

    @Override
    public void onClick(View v) {
        if (mBackBtn == v) {
            finish();
        } else if (mPlayBtn == v || mFullPlayBtn == v) {
            if (mAliyunIEditor != null) {
                changePlayState();
            }
        } else if (mFullBtn == v || mContractBtn == v) {
            changeScreenState();
        } else if (mNextView == v) {
            if (FastClickUtil.isFastClick()) {
                return;
            }
            mAliyunIEditor.saveEffectToLocal();
            mAliyunIEditor.seek(0);
            ThreadUtils.runOnSubThread(() -> {
                String temPath = null;
                Bitmap bitmap = mAliyunIEditor.getCurrentFrame();
                if (bitmap != null) {
                    temPath = mAliyunIEditor.getEditorProject().getProjectDir().getAbsolutePath() + File.separator + "cover.jpeg";
                    BitmapUtil.writeBitmap(temPath, bitmap, Bitmap.CompressFormat.JPEG, 80);
                    bitmap.recycle();
                }
                final String path = temPath;
                ThreadUtils.runOnUiThread(() -> {
                    if (path == null) {
                        ToastUtils.show(AUIVideoEditorActivity.this, "封面图读取失败");
                        return;
                    }
                    mAliyunIEditor.updateCover(new Source(path));
                    AUIVideoEditor.startPublish(AUIVideoEditorActivity.this, mAliyunIEditor.getEditorProject().getProjectFile().getPath(), path,
                        PUBLISH_REQUEST_CODE);
                });
            });


        }
    }

    private void changeScreenState() {
        if (mNormalLayout.getVisibility() == View.VISIBLE) {
            mNormalLayout.setVisibility(View.GONE);
            mBottomLayout.setVisibility(View.GONE);
            mPanelLayout.setVisibility(View.GONE);
            mFullControlLayout.setVisibility(View.VISIBLE);
        } else {
            mNormalLayout.setVisibility(View.VISIBLE);
            mBottomLayout.setVisibility(View.VISIBLE);
            mPanelLayout.setVisibility(View.VISIBLE);
            mFullControlLayout.setVisibility(View.GONE);
        }
        boolean isFullScreen = mNormalLayout.getVisibility() == View.GONE;
        if (mCaptionBorderViewModel != null) {
            mCaptionBorderViewModel.onFullScreenChange(isFullScreen);
        }
        if (mStickerEditViewModel != null) {
            mStickerEditViewModel.onFullScreenChange(isFullScreen);
        }
        mPanelManger.onScreenStateChanged(isFullScreen);
    }

    private void changePlayState() {
        if (mAliyunIEditor.isPlaying()) {
            mAliyunIEditor.pause();
        } else {
            mAliyunIEditor.play();
        }
    }

    public void updateDuration(long duration) {
        this.mDurationUs = duration;
        mProgress.setMax((int) (this.mDurationUs/1000));
        if (mAliyunIEditor.getCurrentPlayPosition() != mCurPlayTimeUs) {
            updatePlayTime(mAliyunIEditor.getCurrentPlayPosition());
        }
    }

    @SuppressLint("SetTextI18n")
    private void updatePlayTime(long timeUs) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        mCurPlayTimeUs = timeUs;
        String time = format.format(new Date(mCurPlayTimeUs / 1000));
        String duration = format.format(new Date(mDurationUs / 1000));
        mTimeTv.setText(time + "/" + duration);
        mFullTimeTv.setText(time);
        mFullDurationTv.setText(duration);
        int progress = (int) (mCurPlayTimeUs * 1.0f / mDurationUs * 100);
        mProgress.setProgress((int) (mCurPlayTimeUs / 1000));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PUBLISH_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String path = data.getStringExtra(AUIPublishActivity.KEY_COVER_PATH);
                    if (!StringUtils.isEmpty(path)) {
                        mAliyunIEditor.updateCover(new Source(path));
                    }
                }
                finish();
            }
        }
    }
}