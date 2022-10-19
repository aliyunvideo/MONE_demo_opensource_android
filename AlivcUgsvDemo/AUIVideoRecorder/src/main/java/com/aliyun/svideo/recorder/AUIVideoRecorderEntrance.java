package com.aliyun.svideo.recorder;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.aliyun.aio.avbaseui.widget.AVLoadingDialog;
import com.aliyun.ugsv.auibeauty.api.constant.BeautySDKType;
import com.aliyun.svideo.recorder.bean.AUIRecorderInputParam;
import com.aliyunsdk.queen.menu.download.BeautyMenuMaterial;
import com.aliyunsdk.queen.menu.download.OnDownloadUICallback;

public class AUIVideoRecorderEntrance {

    private static AUIRecorderInputParam defaultInputParam() {
        AUIRecorderInputParam recordParam = new AUIRecorderInputParam.Builder()
                .setIsAutoClearTemp(false)
                .setVideoRenderingMode(BeautySDKType.QUEEN)
                .setWaterMark(true)
                .build();
        return recordParam;
    }

    public static void startRecord(Context context) {
        startRecord(context, defaultInputParam());
    }

    public static void startRecord(Context context, AUIRecorderInputParam recordInputParam) {
        checkModelAndRun(context, new Runnable() {
            @Override
            public void run() {
                startRecordImpl(context, recordInputParam);
            }
        });
    }

   private static void startRecordImpl(Context context, AUIRecorderInputParam recordInputParam) {
        Intent intent = new Intent(context, AUIVideoRecorderActivity.class);
        intent.putExtra(AUIRecorderInputParam.INTENT_KEY_RESOLUTION_MODE, recordInputParam.getResolutionMode());
        intent.putExtra(AUIRecorderInputParam.INTENT_KEY_MAX_DURATION, recordInputParam.getMaxDuration());
        intent.putExtra(AUIRecorderInputParam.INTENT_KEY_MIN_DURATION, recordInputParam.getMinDuration());
        intent.putExtra(AUIRecorderInputParam.INTENT_KEY_RATION_MODE, recordInputParam.getRatioMode());
        intent.putExtra(AUIRecorderInputParam.INTENT_KEY_GOP, recordInputParam.getGop());
        intent.putExtra(AUIRecorderInputParam.INTENT_KEY_FRAME, recordInputParam.getFrame());
        intent.putExtra(AUIRecorderInputParam.INTENT_KEY_QUALITY, recordInputParam.getVideoQuality());
        intent.putExtra(AUIRecorderInputParam.INTENT_KEY_CODEC, recordInputParam.getVideoCodec());
        intent.putExtra(AUIRecorderInputParam.INTENT_KEY_VIDEO_OUTPUT_PATH, recordInputParam.getVideoOutputPath());
        intent.putExtra(AUIRecorderInputParam.INTENT_KEY_VIDEO_RENDERING_MODE, recordInputParam.getmRenderingMode());
        intent.putExtra(AUIRecorderInputParam.INTENT_KEY_RECORD_FLIP, recordInputParam.isUseFlip());
        intent.putExtra(AUIRecorderInputParam.INTENT_KEY_IS_SVIDEO_QUEEN, recordInputParam.isSvideoRace());
        intent.putExtra(AUIRecorderInputParam.INTENT_KEY_IS_AUTO_CLEAR, recordInputParam.isAutoClearTemp());
        intent.putExtra(AUIRecorderInputParam.INTENT_KEY_WATER_MARK, recordInputParam.hasWaterMark());
        context.startActivity(intent);
    }

    private static void checkModelAndRun(Context activity, final Runnable runnable) {
        final WeakReference<Context> contextRef = new WeakReference<>(activity);
        BeautyMenuMaterial.getInstance().checkModelReady(activity, new OnDownloadUICallback() {
            @Nullable
            private AVLoadingDialog mLoadingDialog;

            @Override
            public void onDownloadStart(@StringRes int tipsResId) {
                showProgressDialog(tipsResId);
            }

            @Override
            public void onDownloadProgress(final float v) {
            }

            @Override
            public void onDownloadSuccess() {
                hideProgressDialog();
                runnable.run();
            }

            @Override
            public void onDownloadError(@StringRes int tipsResId) {
                hideProgressDialog();
                showErrorTips(tipsResId);
            }

            private void showProgressDialog(@StringRes int tipsResId) {
                Context context = contextRef.get();
                if (null == mLoadingDialog && context != null) {
                    mLoadingDialog = new AVLoadingDialog(context).tip(context.getString(tipsResId));
                    mLoadingDialog.show();
                }
            }

            private void hideProgressDialog() {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }
            }

            public void showErrorTips(@StringRes int tipsResId) {
                Context context = contextRef.get();
                if (context != null) {
                    Toast.makeText(context, tipsResId, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
