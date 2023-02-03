package com.aliyun.svideo.template.sample.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.svideo.template.sample.AssetsUtils;
import com.aliyun.svideo.template.sample.R;
import com.aliyun.svideo.template.sample.util.Common;
import com.aliyun.svideo.template.sample.util.DateTimeUtils;
import com.aliyun.svideosdk.template.AliyunAEITemplate;
import com.aliyun.svideosdk.template.AliyunAETemplateCreator;
import com.aliyun.svideosdk.template.AliyunAETemplateIEditor;
import com.aliyun.svideosdk.template.AliyunAETemplateIPlayer;
import com.aliyun.svideosdk.template.AliyunTemplatePlayStateListener;
import com.aliyun.svideosdk.template.model.AliyunAETemplateAsset;
import com.aliyun.svideosdk.template.model.AliyunAETemplateAssetMedia;
import com.aliyun.svideosdk.template.view.AliyunAETemplatePlayView;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TemplatePreviewActivity extends AppCompatActivity {
    private TextView mTitle;
    private TextView mDuration;
    private TextView mAssets;
    private AliyunAETemplatePlayView mPlayView;
    private View mPlayViewContainer;
    private AliyunAETemplateIEditor mAliyunTemplateEditor;
    private AliyunAETemplateIPlayer mAliyunTemplatePlayer;
    private String mTemplateFolder;
    private String mTemplate;
    private AliyunAEITemplate mAliyunTemplate;
    private View mPlayIcon;
    private View mCustomTemplate;

    private AliyunTemplatePlayStateListener mListener = new AliyunTemplatePlayStateListener() {
        @Override
        public void onProgress(int frame) {
            mPlayView.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("czw", "onProgressChanged frame " + frame);
                }
            });
        }

        @Override
        public void onFinish() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mPlayBtn.setSelected(false);
                    Log.d("czw", "onFinish");
                    mAliyunTemplatePlayer.start();
                }
            });
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_preview);
        mTitle = findViewById(R.id.title);
        mDuration = findViewById(R.id.duration);
        mAssets = findViewById(R.id.assets);
        mPlayView = findViewById(R.id.preview);
        mPlayViewContainer = findViewById(R.id.playview_container);
        mPlayViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAliyunTemplatePlayer.isPlaying()){
                    mAliyunTemplatePlayer.pause();
                    mPlayIcon.setVisibility(View.VISIBLE);
                }else{
                    mPlayIcon.setVisibility(View.GONE);
                    mAliyunTemplatePlayer.start();
                }
            }
        });
        mPlayIcon = findViewById(R.id.play_indicator);
        mCustomTemplate = findViewById(R.id.custom_template);
        mCustomTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(TemplatePreviewActivity.this, TemplateEditActivity.class);
                startIntent.putExtra(Common.KEY_TEMPLATE, mTemplate);
                startIntent.putExtra(Common.KEY_FOLDER, mTemplateFolder);
                TemplatePreviewActivity.this.startActivity(startIntent);
            }
        });

        DisplayMetrics screenMetric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(screenMetric);
        int height = screenMetric.widthPixels * 16 / 9;
        ViewGroup.LayoutParams lp = mPlayViewContainer.getLayoutParams();
        lp.width = screenMetric.widthPixels;
        lp.height = height;
        mPlayViewContainer.setLayoutParams(lp);

        mAliyunTemplateEditor = AliyunAETemplateCreator.createTemplateEditor(this);

        mPlayView.setPlayCallback(mListener);

        mAliyunTemplateEditor.setPlayView(mPlayView);

        mTemplateFolder = getIntent().getStringExtra(Common.KEY_FOLDER);
        mTemplate = getIntent().getStringExtra(Common.KEY_TEMPLATE);
        String title = getIntent().getStringExtra(Common.KEY_TITLE);
        mTitle.setText(title);

        new LoadTemplateTask().execute(mTemplateFolder);
    }

    class LoadTemplateTask extends AsyncTask<String, Void, AliyunAEITemplate> {

        @Override
        protected AliyunAEITemplate doInBackground(String... strings) {
            if(!new File(mTemplateFolder).exists()){
                throw new RuntimeException("template folder not exists");
            }
            try {
                return AliyunAETemplateCreator.createTemplate(TemplatePreviewActivity.this, strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(AliyunAEITemplate tpl) {
            if (tpl != null) {
                mAliyunTemplateEditor.setTemplate(tpl);
                mAliyunTemplate = tpl;
                initWhenTemplateReady();
                preview();
            }
        }
    }

    private void initWhenTemplateReady(){
        if(mAliyunTemplate == null) throw new RuntimeException("no allow empty template");
        long duration = (long)(mAliyunTemplate.getDuration() * 1000);
        mDuration.setText(getString(R.string.total_duration).replace("{0}", DateTimeUtils.formatMs(duration)));
        List<AliyunAETemplateAsset> assets = mAliyunTemplate.getAssets();
        int mediaCount = 0;
        int textCount = 0;
        for(int i = 0; i < assets.size(); ++i){
            if(assets.get(i) instanceof AliyunAETemplateAssetMedia){
                ++mediaCount;
            }else{
                ++textCount;
            }
        }
        mAssets.setText(getString(R.string.tempalte_assets_statement).replace("{0}", mediaCount+"").replace("{1}", textCount+""));
    }

    private void preview(){
        mAliyunTemplateEditor.commit();
        mAliyunTemplatePlayer = mAliyunTemplateEditor.getPreviewPlayer();
        mAliyunTemplatePlayer.start();
    }
}
