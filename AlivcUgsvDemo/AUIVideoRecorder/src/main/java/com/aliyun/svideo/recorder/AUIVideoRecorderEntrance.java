package com.aliyun.svideo.recorder;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.aliyun.aio.avbaseui.widget.AVLoadingDialog;
import com.aliyun.ugsv.auibeauty.api.constant.BeautySDKType;
import com.aliyunsdk.queen.menu.download.BeautyMenuMaterial;
import com.aliyunsdk.queen.menu.download.OnDownloadUICallback;

public class AUIVideoRecorderEntrance {


    public static void startRecord(Context context) {
        checkModelAndRun(context, new Runnable() {
            @Override
            public void run() {
                startRecordImpl(context);
            }
        });
    }

   private static void startRecordImpl(Context context) {
        Intent intent = new Intent(context, AUIVideoRecorderActivity.class);
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
