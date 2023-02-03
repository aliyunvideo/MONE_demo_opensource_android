package com.aliyun.svideo.template.sample.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.aliyun.svideo.downloader.DownloaderManager;
import com.aliyun.svideo.downloader.FileDownloaderCallback;
import com.aliyun.svideo.downloader.FileDownloaderModel;
import com.aliyun.svideo.downloader.zipprocessor.ZipUtils;
import com.aliyun.svideo.template.sample.R;
import com.aliyun.svideo.template.sample.Template;
import com.aliyun.svideo.template.sample.util.Common;
import com.aliyun.svideo.template.sample.util.DateTimeUtils;
import com.aliyun.ugsv.common.utils.CommonUtil;
import com.aliyun.ugsv.common.utils.DensityUtil;
import com.aliyun.ugsv.common.utils.ToastUtil;
import com.bumptech.glide.Glide;
import com.liulishuo.filedownloader.BaseDownloadTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class TemplateListFragment extends Fragment {
    private RecyclerView mTemplateList;
    private TemplateListAdapter mAdapter;
    private GridLayoutManager mLayoutM;
    private List<Template> mDatas;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String templatestr = getArguments().getString(Common.KEY_TEMPLATE_LIST, null);
        if(TextUtils.isEmpty(templatestr)){
            throw new RuntimeException("input template list should not be null");
        }
        mDatas = Common.parseTemplateArrayJson(templatestr);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_template_list, container, false);
        mTemplateList = view.findViewById(R.id.template_list);
        init();
        return view;
    }

    private void init(){
        mLayoutM = new GridLayoutManager(this.getContext(), 2);
        mTemplateList.setLayoutManager(mLayoutM);
        mAdapter = new TemplateListAdapter(this.getContext());
        mTemplateList.setAdapter(mAdapter);
        mAdapter.setDataSource(mDatas);
    }

    public class TemplateViewHoder extends RecyclerView.ViewHolder{
        ImageView cover;
        TextView duration;
        TextView title;
        TextView desc;
        Context ctx;
        Template template;
        public TemplateViewHoder(Context ctx, @NonNull View itemView) {
            super(itemView);
            this.ctx = ctx;
            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity)ctx).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int itemW = (metrics.widthPixels - DensityUtil.dip2px(getContext(), 50)) / 2;
            int itemH = itemW * 16 / 9;
            ViewGroup.LayoutParams clp = itemView.getLayoutParams();
            clp.width = itemW;
            itemView.setLayoutParams(clp);
            cover = itemView.findViewById(R.id.cover);
            ViewGroup.LayoutParams coverlp = cover.getLayoutParams();
            coverlp.width = itemW;
            coverlp.height = itemH;
            cover.setLayoutParams(coverlp);
            duration = itemView.findViewById(R.id.duration);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkTemplateState(template);

                }
            });
        }

        public void bind(Template template){
            this.template = template;
            Glide.with(getContext()).load(template.cover).into(cover);
            duration.setText(getContext().getString(R.string.total_duration).replace("{0}", DateTimeUtils.formatMs(template.duration * 1000)));
            title.setText(template.name);
            desc.setText(template.description);
        }
    }
    public class TemplateListAdapter extends RecyclerView.Adapter<TemplateViewHoder>{
        private final Context mCtx;
        private List<Template> mTemplates;

        public TemplateListAdapter(Context ctx){
            this.mCtx = ctx;
        }

        public void setDataSource(List<Template> datas){
            mTemplates = datas;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public TemplateViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mCtx).inflate(R.layout.template_item, parent, false);
            return new TemplateViewHoder(mCtx, view);
        }

        @Override
        public void onBindViewHolder(@NonNull TemplateViewHoder holder, int position) {
            Template template = mTemplates.get(position);
            holder.bind(template);
        }

        @Override
        public int getItemCount() {
            return mTemplates != null ? mTemplates.size() : 0;
        }
    }

    private void gotoPreview(Template template){
        Intent startIntent = new Intent(getActivity(), TemplatePreviewActivity.class);
        startIntent.putExtra(Common.KEY_FOLDER, new File(getContext().getExternalFilesDir(Common.TEMPLATE_FOLDER), template.id + template.name).getPath());
        startIntent.putExtra(Common.KEY_TEMPLATE, template.name);
        startIntent.putExtra(Common.KEY_TITLE, template.name);
        getContext().startActivity(startIntent);
    }

    private void extractZip(Template template, String path){
        if(new File(getContext().getExternalFilesDir(Common.TEMPLATE_FOLDER), template.id + template.name).exists()) return;
        try {
            Common.unZip(path, new File(getContext().getExternalFilesDir(Common.TEMPLATE_FOLDER), template.id + template.name).getPath());
        } catch (Exception e) {
            Log.d(TemplateListFragment.class.getSimpleName(), "exception " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }

    private void checkTemplateState(Template template){
        if(new File(getContext().getExternalFilesDir(Common.TEMPLATE_FOLDER), template.id + template.name).exists()){
            gotoPreview(template);
            return;
        }
        String downloadPath = new File(getContext().getExternalFilesDir(Common.TEMPLATE_FOLDER), template.id + template.name + ".zip").getPath();
        if(new File(downloadPath).exists()){
            extractZip(template, downloadPath);
            gotoPreview(template);
            return;
        }
        if (!CommonUtil.hasNetwork(getContext())) {
            ToastUtil.showToast(getContext(), com.aliyun.svideo.music.R.string.alivc_music_network_not_connect);
            return;
        }
        if (CommonUtil.SDFreeSize() < 30 * 1024 * 1024) {
            Toast.makeText(getContext(), com.aliyun.svideo.music.R.string.alivc_music_no_free_memory, Toast.LENGTH_SHORT).show();
            return;
        }

        final FileDownloaderModel downloaderModel = new FileDownloaderModel();
        downloaderModel.setUrl(template.downloadUrl);
        downloaderModel.setDownload(template.id);
        downloaderModel.setName(template.name);
        downloaderModel.setIsunzip(0);
        downloaderModel.setPath(downloadPath);
        downloaderModel.setDescription(template.description);
        downloaderModel.setEffectType(FileDownloaderModel.EFFECT_TEMPLATE);
        if (downloaderModel == null) {
            Log.e(TemplateListFragment.class.getSimpleName(), "downloaderModel is null" );
        }
        final FileDownloaderModel model = DownloaderManager.getInstance().addTask(downloaderModel, template.downloadUrl);
        if (model == null) {
            Log.e(TemplateListFragment.class.getSimpleName(), "model is null" );
        }
        if (DownloaderManager.getInstance().isDownloading(model.getTaskId(), model.getPath())) {
            return;
        }
        DownloaderManager.getInstance().startTask(model.getTaskId(), new FileDownloaderCallback() {
            @Override
            public void onFinish(int downloadId, String path) {
                ToastUtil.showToast(getContext(), "下载完成");
                Log.d(TemplateListFragment.class.getSimpleName(), "onFinish " + downloadId + ", path: " + path);
                extractZip(template, path);
                gotoPreview(template);
            }

            @Override
            public void onStart(int downloadId, long soFarBytes, long totalBytes, int preProgress) {
                ToastUtil.showToast(getContext(), "开始下载");
                Log.d(TemplateListFragment.class.getSimpleName(), "onStart " + downloadId);
            }

            @Override
            public void onProgress(int downloadId, long soFarBytes, long totalBytes, long speed,
                                   int progress) {
                Log.d(TemplateListFragment.class.getSimpleName(), "onProgress " + progress);
            }

            @Override
            public void onError(BaseDownloadTask task, Throwable e) {
                Log.d(TemplateListFragment.class.getSimpleName(), "onError " + e.getMessage());
                ToastUtil.showToast(getContext(), e.getMessage());
                DownloaderManager.getInstance().deleteTaskByTaskId(model.getTaskId());
                DownloaderManager.getInstance().getDbController().deleteTask(model.getTaskId());
            }
        });
    }


}
