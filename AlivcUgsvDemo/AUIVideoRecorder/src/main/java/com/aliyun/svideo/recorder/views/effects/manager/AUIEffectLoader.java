/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.recorder.views.effects.manager;

import android.content.Context;
import android.database.Cursor;

import com.aliyun.common.utils.ToastUtil;
import com.aliyun.svideo.base.http.EffectService;
import com.aliyun.svideo.base.http.HttpCallback;
import com.aliyun.svideo.downloader.DownloaderManager;
import com.aliyun.svideo.downloader.FileDownloaderCallback;
import com.aliyun.svideo.downloader.FileDownloaderModel;
import com.aliyun.svideo.downloader.zipprocessor.DownloadFileUtils;
import com.aliyun.svideosdk.common.struct.form.AspectForm;
import com.aliyun.svideosdk.common.struct.form.IMVForm;
import com.aliyun.svideosdk.common.struct.form.PreviewPasterForm;
import com.liulishuo.filedownloader.BaseDownloadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AUIEffectLoader {

    public EffectService mService = new EffectService();
    private String mPackageName;
    private Context mContext;
    private ArrayList<PreviewPasterForm> mLoadingPaster;
    public interface LoadCallback<T> {
        void onLoadCompleted(List<T> localInfos, List<T> remoteInfos, Throwable e);
    }

    public AUIEffectLoader(Context mContext) {
        this.mContext = mContext;
        mPackageName = mContext.getApplicationInfo().packageName;
        mLoadingPaster = new ArrayList<>();
    }
    public List<FileDownloaderModel> loadLocalEffect(int effectType) {
        List<FileDownloaderModel> localPasters = new ArrayList<FileDownloaderModel>();
        List<String> selectedColumns = new ArrayList<String>();
        selectedColumns.add(FileDownloaderModel.ICON);
        selectedColumns.add(FileDownloaderModel.DESCRIPTION);
        selectedColumns.add(FileDownloaderModel.ID);
        selectedColumns.add(FileDownloaderModel.ISNEW);
        selectedColumns.add(FileDownloaderModel.LEVEL);
        selectedColumns.add(FileDownloaderModel.NAME);
        selectedColumns.add(FileDownloaderModel.PREVIEW);
        selectedColumns.add(FileDownloaderModel.SORT);

        selectedColumns.add(FileDownloaderModel.PREVIEWMP4);
        selectedColumns.add(FileDownloaderModel.PREVIEWPIC);
        selectedColumns.add(FileDownloaderModel.KEY);
        selectedColumns.add(FileDownloaderModel.DURATION);
        //selectedColumns.add(FileDownloaderModel.SUBTYPE);
        HashMap<String, String> conditionMap = new HashMap<String, String>();
        conditionMap.put(FileDownloaderModel.EFFECTTYPE, String.valueOf(effectType));
        Cursor cursor = DownloaderManager.getInstance()
                        .getDbController().getResourceColumns(conditionMap, selectedColumns);

        while (cursor.moveToNext()) {
            FileDownloaderModel paster = new FileDownloaderModel();
            paster.setIcon(cursor.getString(cursor.getColumnIndex(FileDownloaderModel.ICON)));
            paster.setDescription(cursor.getString(cursor.getColumnIndex(FileDownloaderModel.DESCRIPTION)));
            paster.setId(cursor.getInt(cursor.getColumnIndex(FileDownloaderModel.ID)));
            paster.setIsnew(cursor.getInt(cursor.getColumnIndex(FileDownloaderModel.ISNEW)));
            paster.setLevel(cursor.getInt(cursor.getColumnIndex(FileDownloaderModel.LEVEL)));
            paster.setName(cursor.getString(cursor.getColumnIndex(FileDownloaderModel.NAME)));
            paster.setPreview(cursor.getString(cursor.getColumnIndex(FileDownloaderModel.PREVIEW)));
            paster.setSort(cursor.getInt(cursor.getColumnIndex(FileDownloaderModel.SORT)));

            paster.setPreviewmp4(cursor.getString(cursor.getColumnIndex(FileDownloaderModel.PREVIEWMP4)));
            paster.setPreviewpic(cursor.getString(cursor.getColumnIndex(FileDownloaderModel.PREVIEWPIC)));
            paster.setKey(cursor.getString(cursor.getColumnIndex(FileDownloaderModel.KEY)));
            paster.setDuration(cursor.getLong(cursor.getColumnIndex(FileDownloaderModel.DURATION)));
            //paster.setSubtype(cursor.getInt(cursor.getColumnIndex(FileDownloaderModel.SUBTYPE)));

            localPasters.add(paster);
        }
        cursor.close();
        return localPasters;
    }

    public int loadAllPaster(String signature, final LoadCallback<PreviewPasterForm> callback) {

        mService.loadFrontEffectPaster(mContext.getPackageName(), new HttpCallback<List<PreviewPasterForm>>() {
            @Override
            public void onSuccess(List<PreviewPasterForm> result) {
                if (callback != null) {
                    List<PreviewPasterForm> localPasters = loadLocalPaster();
                    List<Integer> localIds = null;
                    if (localPasters != null && localPasters.size() > 0) {
                        localIds = new ArrayList<Integer>(localPasters.size());
                        for (PreviewPasterForm paster : localPasters) {
                            localIds.add(paster.getId());
                        }
                    }
                    if (localIds != null && localIds.size() > 0) {
                        for (int i = 0; i < result.size(); i++) {
                            if (localIds.contains(result.get(i).getId())) {
                                result.remove(i);
                                i--;
                            }
                        }
                    }

                    callback.onLoadCompleted(localPasters, result, null);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                List<PreviewPasterForm> localPasters = loadLocalPaster();
                if (callback != null) {
                    callback.onLoadCompleted(localPasters, null, e);
                }
            }
        });
        return 0;
    }

    public List<PreviewPasterForm> loadLocalPaster() {
        List<PreviewPasterForm> localPasters = new ArrayList<PreviewPasterForm>();
        List<String> selectedColumns = new ArrayList<String>();
        selectedColumns.add(FileDownloaderModel.ICON);
        selectedColumns.add(FileDownloaderModel.DESCRIPTION);
        selectedColumns.add(FileDownloaderModel.ID);
        selectedColumns.add(FileDownloaderModel.ISNEW);
        selectedColumns.add(FileDownloaderModel.LEVEL);
        selectedColumns.add(FileDownloaderModel.NAME);
        selectedColumns.add(FileDownloaderModel.PREVIEW);
        selectedColumns.add(FileDownloaderModel.PATH);
        selectedColumns.add(FileDownloaderModel.SORT);
        HashMap<String, String> conditionMap = new HashMap<String, String>();
        conditionMap.put(FileDownloaderModel.EFFECTTYPE, String.valueOf(EffectService.EFFECT_FACE_PASTER));
        Cursor cursor = DownloaderManager.getInstance()
                        .getDbController().getResourceColumns(conditionMap, selectedColumns);

        while (cursor.moveToNext()) {
            PreviewPasterForm paster = new PreviewPasterForm();
            paster.setIcon(cursor.getString(cursor.getColumnIndex(FileDownloaderModel.ICON)));
            paster.setId(cursor.getInt(cursor.getColumnIndex(FileDownloaderModel.ID)));
            paster.setLevel(cursor.getInt(cursor.getColumnIndex(FileDownloaderModel.LEVEL)));
            paster.setName(cursor.getString(cursor.getColumnIndex(FileDownloaderModel.NAME)));
            paster.setSort(cursor.getInt(cursor.getColumnIndex(FileDownloaderModel.SORT)));
            paster.setPath(cursor.getString(cursor.getColumnIndex(FileDownloaderModel.PATH)));

            //pasterid = 150 是本地asset打入的id号.目前需求认为本地打入的动图资源不应该显示再更多动图列表中
            //if (paster.getId() != 150){
            //    localPasters.add(paster);
            //}
            localPasters.add(paster);
        }
        cursor.close();
        return localPasters;
    }

    /**
     * 下载动图
     * @param pasterForm
     * @param callback
     */
    public void downloadPaster(final PreviewPasterForm pasterForm, final FileDownloaderCallback callback) {
        if (mLoadingPaster.contains(pasterForm)) {//如果已经在下载中了，则不能重复下载
            return;
        }
        mLoadingPaster.add(pasterForm);
        FileDownloaderModel fileDownloaderModel = new FileDownloaderModel();
        fileDownloaderModel.setUrl(pasterForm.getUrl());
        fileDownloaderModel.setEffectType(EffectService.EFFECT_FACE_PASTER);
        fileDownloaderModel.setPath(DownloadFileUtils.getAssetPackageDir(mContext,
                                    pasterForm.getName(), pasterForm.getId()).getAbsolutePath());
        fileDownloaderModel.setId(pasterForm.getId());
        fileDownloaderModel.setIsunzip(1);
        fileDownloaderModel.setName(pasterForm.getName());
        fileDownloaderModel.setIcon(pasterForm.getIcon());
        final FileDownloaderModel model = DownloaderManager.getInstance().addTask(fileDownloaderModel, fileDownloaderModel.getUrl());
        if (DownloaderManager.getInstance().isDownloading(model.getTaskId(), model.getPath())) {
            return;
        }
        DownloaderManager.getInstance().startTask(model.getTaskId(), new FileDownloaderCallback() {
            @Override
            public void onStart(int downloadId, long soFarBytes, long totalBytes, int preProgress) {
                if (callback != null) {
                    callback.onStart(downloadId, soFarBytes, totalBytes, preProgress);
                }

            }

            @Override
            public void onProgress(int downloadId, long soFarBytes, long totalBytes, long speed, int progress) {
                if (callback != null) {
                    callback.onProgress(downloadId, soFarBytes, totalBytes, speed, progress);
                }
            }

            @Override
            public void onFinish(int downloadId, String path) {
                mLoadingPaster.remove(pasterForm);
                if (callback != null) {
                    callback.onFinish(downloadId, path);
                }
            }

            @Override
            public void onError(BaseDownloadTask task, Throwable e) {
                mLoadingPaster.remove(pasterForm);
                super.onError(task, e);
                ToastUtil.showToast(mContext, e.getMessage());
                DownloaderManager.getInstance().deleteTaskByTaskId(model.getTaskId());
                DownloaderManager.getInstance().getDbController().deleteTaskById(pasterForm.getId());
                if (callback != null) {
                    callback.onError(task, e );
                }
            }
        });

    }
    /**
     * 加载已经下载的mv效果
     * @return
     */
    public List<IMVForm> loadLocalMV() {

        List<FileDownloaderModel> modelsTemp = DownloaderManager.getInstance().getDbController().getResourceByType(EffectService.EFFECT_MV);
        ArrayList<IMVForm> resourceForms = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();
        List<FileDownloaderModel> models = new ArrayList<>();
        if (modelsTemp != null && modelsTemp.size() > 0) {
            for (FileDownloaderModel model : modelsTemp) {
                if (new File(model.getPath()).exists()) {
                    models.add(model);
                }
            }
            for (FileDownloaderModel model : models) {
                IMVForm form = null;
                if (!ids.contains(model.getId())) {
                    ids.add(model.getId());
                    form = new IMVForm();
                    form.setId(model.getId());
                    form.setName(model.getName());
                    form.setKey(model.getKey());
                    form.setLevel(model.getLevel());
                    form.setTag(model.getTag());
                    form.setCat(model.getCat());
                    form.setIcon(model.getIcon());
                    form.setPreviewPic(model.getPreviewpic());
                    form.setPreviewMp4(model.getPreviewmp4());
                    form.setDuration(model.getDuration());
                    form.setType(model.getSubtype());
                    form.setAspectList(new ArrayList<AspectForm>());
                    resourceForms.add(form);
                } else {
                    for (IMVForm imvForm : resourceForms) {
                        if (imvForm.getId() == model.getId()) {
                            form = imvForm;
                        }
                    }
                }
                AspectForm pasterForm = addAspectForm(model);
                form.getAspectList().add(pasterForm);
            }
        }
        return resourceForms;
    }

    private AspectForm addAspectForm(FileDownloaderModel model) {
        AspectForm aspectForm = new AspectForm();
        aspectForm.setAspect(model.getAspect());
        aspectForm.setDownload(model.getDownload());
        aspectForm.setMd5(model.getMd5());
        aspectForm.setPath(model.getPath());
        return aspectForm;
    }

}
