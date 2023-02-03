package com.aliyun.svideo.editor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aliyun.aio.avtheme.AVBaseThemeActivity;
import com.aliyun.common.global.AliyunTag;
import com.aliyun.ugsv.common.qupaiokhttp.HttpRequest;
import com.aliyun.ugsv.common.qupaiokhttp.RequestParams;
import com.aliyun.ugsv.common.qupaiokhttp.StringHttpRequestCallback;
import com.aliyun.svideo.base.Constants;
import com.aliyun.svideo.base.utils.VideoInfoUtils;
import com.aliyun.svideo.editor.bean.ImageUploadCallbackBean;
import com.aliyun.svideo.editor.bean.RefreshVodVideoUploadAuth;
import com.aliyun.svideo.editor.bean.VodImageUploadAuth;
import com.aliyun.svideo.editor.bean.VodVideoUploadAuth;
import com.aliyun.svideo.editor.common.widget.SquareProgressView;
import com.aliyun.svideo.track.util.Util;
import com.aliyun.svideosdk.common.AliyunIThumbnailFetcher;
import com.aliyun.svideosdk.common.impl.AliyunThumbnailFetcherFactory;
import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;
import com.aliyun.svideosdk.common.struct.project.AliyunEditorProject;
import com.aliyun.svideosdk.common.struct.project.json.ProjectJSONSupportImpl;
import com.aliyun.svideosdk.editor.AliyunIComposeCallBack;
import com.aliyun.svideosdk.editor.AliyunIVodCompose;
import com.aliyun.svideosdk.editor.impl.AliyunComposeFactory;
import com.aliyun.svideosdk.editor.impl.AliyunVodCompose;
import com.aliyun.ugsv.common.utils.DateTimeUtils;
import com.aliyun.ugsv.common.utils.StringUtils;
import com.aliyun.ugsv.common.utils.ThreadUtils;
import com.aliyun.ugsv.common.utils.UriUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AUIVideoEditorComposeActivity extends AVBaseThemeActivity {
    private static final String TAG = AUIVideoEditorComposeActivity.class.getSimpleName();
    public static final String KEY_PARAM_CONFIG = "project_json_path";
    public static final String KEY_COVER_PATH = "cover_path";
    public static final String KEY_SHARE_TEXT = "share_text";

    private RelativeLayout mLayoutProgress;
    private SquareProgressView mProgressView;
    private ImageView mIvThumbnail;
    private TextView mTvProgress;

    private String config;
    private String mCoverPath;
    private String mOutputPath;
    private int videoWidth;
    private int videoHeight;

    private AliyunIVodCompose mCompose;
    private int mComposeProgress;
    private int mUploadProgress;

    private boolean mComposeCompleted;
    private boolean mIsUpload;
    /**
     * 视频文件大小
     */
    private long videoSize;
    /**
     * 图片文件大小
     */
    private long imageSize;
    private String videoId;
    private String mImageUrl;
    private String mShareText;

    private final AliyunIComposeCallBack mCallback = new AliyunIComposeCallBack() {
        @Override

        public void onComposeError(int errorCode) {
            Log.e(TAG, "onComposeError>" + errorCode);
            showMessage("Compose Error:" + errorCode, false);
        }

        @Override
        public void onComposeProgress(final int progress) {
            mProgressView.post(new Runnable() {
                @Override
                public void run() {
                    mComposeProgress = progress;
                    updateProgress(mComposeProgress, mUploadProgress);
                }
            });
        }

        @Override
        public void onComposeCompleted() {
            mComposeCompleted = true;
            mComposeProgress = 100;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //适配android Q
                ThreadUtils.runOnSubThread(new Runnable() {
                    @Override
                    public void run() {
                        UriUtils.saveVideoToMediaStore(AUIVideoEditorComposeActivity.this, mOutputPath);
                    }
                });
            } else {
                MediaScannerConnection.scanFile(getApplicationContext(),
                        new String[]{mOutputPath}, new String[]{"video/mp4"}, null);
            }
            if (EditorConfig.Companion.getInstance().getPublish()) {
                mProgressView.post(new Runnable() {
                    @Override
                    public void run() {
                        initUpload();
                    }
                });
            } else {
                showMessage(getString(R.string.ugsv_editor_compose_success), true);
            }

            VideoInfoUtils.printVideoInfo(mOutputPath);
        }
    };

    private final AliyunVodCompose.AliyunIVodUploadCallBack mUploadCallback = new AliyunVodCompose.AliyunIVodUploadCallBack() {

        @Override
        public void onUploadSucceed() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCompose != null && mCompose.getState() == AliyunIVodCompose.AliyunComposeState.STATE_IMAGE_UPLOADING) {
                        //如果是图片上传回调，继续视频上传
                        startVideoUpload();
                        return;
                    }
                    mUploadProgress = 100;
                    updateProgress(mComposeProgress, mUploadProgress);
                    showMessage(getString(R.string.ugsv_editor_upload_success), true);
                }
            });

        }

        @Override
        public void onUploadFailed(String code, String message) {
            Log.e(TAG, "onUploadFailed, errorCode:" + code + ", msg:" + message);
            showMessage(getString(R.string.ugsv_editor_upload_failed)+"\n "+message, false);
        }

        @Override
        public void onUploadProgress(final long uploadedSize, final long totalSize) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCompose == null) {
                        return;
                    }
                    int progress = 0;
                    if (mCompose.getState() == AliyunVodCompose.AliyunComposeState.STATE_IMAGE_UPLOADING) {
                        progress = (int)((uploadedSize * 100) / (totalSize  + videoSize));
                    } else if (mCompose.getState() == AliyunVodCompose.AliyunComposeState.STATE_VIDEO_UPLOADING) {
                        progress = (int)(((uploadedSize + imageSize) * 100) / (totalSize + imageSize));
                    }
                    mUploadProgress = progress;
                    updateProgress(mComposeProgress, mUploadProgress);
                }
            });

        }

        @Override
        public void onUploadRetry(String code, String message) {

        }

        @Override
        public void onUploadRetryResume() {

        }

        @Override
        public void onUploadTokenExpired() {
            if (mCompose == null) {
                return;
            }
            if (mCompose.getState() == AliyunIVodCompose.AliyunComposeState.STATE_IMAGE_UPLOADING) {
                startImageUpload();
            } else if (mCompose.getState() == AliyunIVodCompose.AliyunComposeState.STATE_VIDEO_UPLOADING) {
                refreshVideoUpload(videoId);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ugsv_editor_activity_compose);
        config = getIntent().getStringExtra(KEY_PARAM_CONFIG);
        mCoverPath = getIntent().getStringExtra(KEY_COVER_PATH);
        mShareText = getIntent().getStringExtra(KEY_SHARE_TEXT);
        initView();
    }

    private void initView() {
        mLayoutProgress = findViewById(R.id.layout_progress);
        mProgressView = findViewById(R.id.progress);
        mIvThumbnail = findViewById(R.id.iv_thumbnail);
        mTvProgress = findViewById(R.id.tv_progress);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mComposeCompleted) {
                    showExitDialog();
                }
            }
        });
        try {
            AliyunEditorProject project = new ProjectJSONSupportImpl().readValue(new File(config), AliyunEditorProject.class);
            videoWidth = project.getConfig().getOutputWidth();
            videoHeight = project.getConfig().getOutputHeight();
            mCoverPath = project.getCover().getPath();
            project.getConfig().getOutputWidth();
        } catch (Exception e) {
            e.printStackTrace();
        }
        float ratio = videoWidth * 1.0f / videoHeight;
        mLayoutProgress.post(new Runnable() {
            @Override
            public void run() {
                int width = mLayoutProgress.getWidth() - Util.dp2px(8);
                int height = (int) (width / ratio + Util.dp2px(8));
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mLayoutProgress.getLayoutParams();
                layoutParams.height = height;
                mLayoutProgress.setLayoutParams(layoutParams);
                if (!StringUtils.isEmpty(mCoverPath)) {
                    Glide.with(mIvThumbnail).load(mCoverPath).into(mIvThumbnail);
                } else {
                    loadThumbnailImage();
                }
                compose();
            }
        });
    }

    private void compose() {
        String time = DateTimeUtils.getDateTimeFromMillisecond(System.currentTimeMillis());
        mOutputPath = Constants.SDCardConstants.getDir(this) + time + Constants.SDCardConstants.COMPOSE_SUFFIX;
        mCompose = AliyunComposeFactory.createAliyunVodCompose();
        mCompose.init(this.getApplicationContext());
        int ret = mCompose.compose(config, mOutputPath, mCallback);
    }

    private void initUpload() {
        videoSize = new File(mOutputPath).length();
        imageSize = new File(mCoverPath).length();
        startImageUpload();
    }

    @SuppressLint("SetTextI18n")
    private void updateProgress(int composeProgress, int uploadProgress) {
        int progress = (composeProgress + uploadProgress) / (EditorConfig.Companion.getInstance().getPublish() ? 2 : 1);
        mProgressView.setProgress(progress);
        mTvProgress.setText(progress + "%");
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mCompose != null) {
            mCompose.resumeCompose();
            if (mIsUpload) {
                mCompose.resumeUpload();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCompose != null) {
            mCompose.pauseCompose();
            if (mIsUpload) {
                mCompose.pauseUpload();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompose != null) {
            mCompose.release();
            mCompose = null;
        }
    }

    private void loadThumbnailImage() {
        final AliyunIThumbnailFetcher fetcher = AliyunThumbnailFetcherFactory.createThumbnailFetcher();
        fetcher.fromConfigJson(config);
        fetcher.setFastMode(true);
        fetcher.setParameters(videoWidth, videoHeight,
                AliyunIThumbnailFetcher.CropMode.Mediate, VideoDisplayMode.SCALE, 1);
        fetcher.requestThumbnailImage(new long[]{0}, new AliyunIThumbnailFetcher.OnThumbnailCompletion() {

            @Override
            public void onThumbnailReady(Bitmap bitmap, long l, int index) {
                FileOutputStream fileOutputStream = null;
                String path = null;
                try {
                    File file = new File(config);
                    File thumbnailFile = new File(file.getParent(), "thumbnail.jpg");
                    path = thumbnailFile.getPath();
                    fileOutputStream = new FileOutputStream(thumbnailFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                            fileOutputStream = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                fetcher.release();
                showThumbnail(path);
            }

            @Override
            public void onError(int errorCode) {
                fetcher.release();
            }
        });
    }

    private void showThumbnail(String path) {
        Glide.with(mIvThumbnail).load(path).fitCenter().into(mIvThumbnail);
    }

    /**
     * 上传图片
     */
    private void startImageUpload() {
        Log.e(TAG, "startImageUpload");
        RequestParams params = new RequestParams();
        params.addFormDataPart("imageType", "default");
        HttpRequest.get("https://alivc-demo.aliyuncs.com/demo/getImageUploadAuth",
                params, new StringHttpRequestCallback() {
                    @Override
                    protected void onSuccess(String s) {
                        VodImageUploadAuth tokenInfo = VodImageUploadAuth.getImageTokenInfo(s);
                        if (tokenInfo != null && mCompose != null) {
                            int rv = mCompose.uploadImageWithVod(mCoverPath, tokenInfo.getUploadAddress(), tokenInfo.getUploadAuth(), mUploadCallback);
                            if (rv < 0) {
                                Log.d(TAG, "上传参数错误 video path : " + mOutputPath + " thumbnailk : " + mCoverPath);
                                showMessage(getResources().getString(R.string.ugsv_editor_upload_param_error), false);
                            } else {
                                mIsUpload = true;
                                ImageUploadCallbackBean imageUploadCallbackBean = new Gson().fromJson(s,
                                        ImageUploadCallbackBean.class);
                                if (imageUploadCallbackBean != null && "200".equals(imageUploadCallbackBean.getCode())) {
                                    mImageUrl = imageUploadCallbackBean.getData().getImageURL();
                                }
                            }

                        } else {
                            showMessage("Get image upload auth info failed", false);
                            Log.e(TAG, "Get image upload auth info failed");
                        }
                    }

                    @Override
                    public void onFailure(int errorCode, String msg) {
                        Log.e(TAG, "Get image upload auth info failed, errorCode:" + errorCode + ", msg:" + msg);
                        showMessage("Get image upload auth info failed", false);
                    }
                });
    }

    /**
     * 上传视频
     */
    private void startVideoUpload() {
        RequestParams params = new RequestParams();
        params.addFormDataPart("title", TextUtils.isEmpty(mShareText) ? "test video" : mShareText);
        params.addFormDataPart("fileName", mOutputPath);
        params.addFormDataPart("coverURL", mImageUrl == null ? "" : mImageUrl);
        HttpRequest.get("https://alivc-demo.aliyuncs.com/demo/getVideoUploadAuth?", params, new StringHttpRequestCallback() {
            @Override
            protected void onSuccess(String s) {
                VodVideoUploadAuth tokenInfo = VodVideoUploadAuth.getVideoTokenInfo(s);
                if (tokenInfo != null && mCompose != null) {
                    videoId = tokenInfo.getVideoId().toString();
                    int rv = mCompose.uploadVideoWithVod(mOutputPath, tokenInfo.getUploadAddress(), tokenInfo.getUploadAuth(), mUploadCallback);
                    if (rv < 0) {
                        Log.d(TAG, "上传参数错误 video path : " + mOutputPath + " thumbnailk : " + mCoverPath);
                        showMessage(getResources().getString(R.string.ugsv_editor_upload_param_error), false);
                    } else {
                        mIsUpload = true;
                    }

                } else {
                    showMessage("Get video upload auth failed", false);
                    Log.e(TAG, "Get video upload auth info failed");
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                Log.e(TAG, "Get video upload auth failed, errorCode:" + errorCode + ", msg:" + msg);
                showMessage("Get video upload auth failed", false);
            }
        });
    }

    /**
     * 刷新视频凭证
     * @param videoId
     */
    private void refreshVideoUpload(String videoId) {
        RequestParams params = new RequestParams();
        params.addFormDataPart("videoId", videoId);
        HttpRequest.get("https://alivc-demo.aliyuncs.com/demo/refreshVideoUploadAuth?", params, new StringHttpRequestCallback() {
            @Override
            protected void onSuccess(String s) {
                RefreshVodVideoUploadAuth tokenInfo = RefreshVodVideoUploadAuth.getReVideoTokenInfo(s);
                if (tokenInfo != null && mCompose != null) {
                    int rv = mCompose.refreshWithUploadAuth(tokenInfo.getUploadAuth());
                    if (rv < 0) {
                        Log.d(AliyunTag.TAG, "上传参数错误 video path : " + mOutputPath + " thumbnailk : " + mCoverPath);
                        showMessage(getResources().getString(R.string.ugsv_editor_upload_param_error), false);
                    } else {
                        mIsUpload = true;
                    }

                } else {
                    showMessage("Get video upload auth failed", false);
                    Log.e(AliyunTag.TAG, "Get video upload auth info failed");
                }

            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
            }
        });
    }

    private void showMessage(String msg, boolean isSucExit) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (AUIVideoEditorComposeActivity.this.isFinishing() || AUIVideoEditorComposeActivity.this.isDestroyed()) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(AUIVideoEditorComposeActivity.this);
                builder.setMessage(msg);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.alivc_common_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (isSucExit) {
                            setResult(isSucExit ? RESULT_OK : RESULT_CANCELED);
                        }
                        finish();
                    }
                });
                builder.create().show();
            }
        });
    }

    public void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.ugsv_editor_compose_confirm_tips);
        builder.setNegativeButton(R.string.aliyun_common_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.alivc_common_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        builder.create().show();
    }
}
