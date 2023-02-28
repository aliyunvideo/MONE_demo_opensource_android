package com.aliyun.svideo.template.sample.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.svideo.editor.common.widget.SquareProgressView;
import com.aliyun.svideo.template.sample.R;
import com.aliyun.svideo.template.sample.util.Common;
import com.aliyun.svideosdk.template.AliyunAETemplateCreator;
import com.aliyun.svideosdk.template.AliyunAETemplateIEditor;
import com.aliyun.svideosdk.template.AliyunTemplateRenderListener;

import org.json.JSONException;

import java.io.IOException;

public class TemplateExportActivity extends AppCompatActivity {
    private static final String TAG = TemplateExportActivity.class.getName();
    private static final String KEY_TEMPLATE = "KEY_TEMPLATE";
    private static final String KEY_OUTPUT_PATH = "KEY_OUTPUT_PATH";
    private static final String KEY_MUSIC_TYPE = "KEY_MUSIC_TYPE";
    private static final String KEY_MUSIC_PATH = "KEY_MUSIC_PATH";
    private ImageView mCover;
    private TextView mProgressDesc;
    private SquareProgressView mProgressView;
    private AliyunAETemplateIEditor mTemplateEditor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ugsv_template_export);
        mCover = findViewById(R.id.iv_thumbnail);
        mProgressDesc = findViewById(R.id.tv_progress);
        mProgressView = findViewById(R.id.progress);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitDialog();
            }
        });
        View coverContainer = findViewById(R.id.layout_progress);
        ViewGroup.LayoutParams lp = coverContainer.getLayoutParams();
        lp.height = lp.width * 16 / 9;
        coverContainer.setLayoutParams(lp);

        String template = getIntent().getStringExtra(KEY_TEMPLATE);
        String outputPath = getIntent().getStringExtra(KEY_OUTPUT_PATH);
        String folder = getIntent().getStringExtra(Common.KEY_FOLDER);
        String replaceAudio = getIntent().getStringExtra(KEY_MUSIC_PATH);
        int musicType = getIntent().getIntExtra(KEY_MUSIC_TYPE, TemplateEditActivity.PanelPresenter.MUSIC_ORIGIN);
        mTemplateEditor = AliyunAETemplateCreator.createTemplateEditor(this);
        try {
            mTemplateEditor.setTemplate(AliyunAETemplateCreator.createTemplate(this, folder, template));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(musicType == TemplateEditActivity.PanelPresenter.MUSIC_MUTE){
            mTemplateEditor.mute();
        }else if(musicType == TemplateEditActivity.PanelPresenter.MUSIC_ORIGIN){
            mTemplateEditor.restoreAudio();
        } else if(musicType == TemplateEditActivity.PanelPresenter.MUSIC_OTHER){
            mTemplateEditor.replaceAudio(replaceAudio);
        }
        mTemplateEditor.commit();
        mTemplateEditor.setOutputPath(outputPath);
        mTemplateEditor.startRender(new AliyunTemplateRenderListener() {
            @Override
            public void onStart() {
                Log.d(TAG, "onStart");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");
                finish();
            }

            @Override
            public void onProgress(int progress) {
                Log.d(TAG, "onProgress " + progress);
                mProgressDesc.setText(progress + "%");
                mProgressView.setProgress(progress);
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess");
                finish();
                VideoPlayActivity.start(TemplateExportActivity.this, outputPath);
            }

            @Override
            public void onError(String msg) {
                Log.d(TAG, "onError>>> " + msg);
                finish();
                Toast.makeText(TemplateExportActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void start(Context ctx, String folder, String templateConfig, int musicType, String replaceAudio, String outputPath){
        Intent startIntent = new Intent(ctx, TemplateExportActivity.class);
        startIntent.putExtra(KEY_TEMPLATE, templateConfig);
        startIntent.putExtra(KEY_OUTPUT_PATH, outputPath);
        startIntent.putExtra(KEY_MUSIC_TYPE, musicType);
        startIntent.putExtra(KEY_MUSIC_PATH, replaceAudio);
        startIntent.putExtra(Common.KEY_FOLDER, folder);
        ctx.startActivity(startIntent);
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    private void showExitDialog() {
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
                mTemplateEditor.cancelRender();
                finish();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTemplateEditor != null) mTemplateEditor.release();
    }
}
