package com.aliyun.svideo.recorder.views.effects.paster;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.ugsv.common.global.AppInfo;
import com.aliyun.ugsv.common.utils.DensityUtils;
import com.aliyun.svideo.downloader.FileDownloaderCallback;
import com.aliyun.svideo.recorder.R;
import com.aliyun.svideo.recorder.views.dialog.AUIIPageTab;
import com.aliyun.svideo.recorder.views.dialog.AUIOnClearEffectListener;
import com.aliyun.svideo.recorder.views.effects.AUIEffectBody;
import com.aliyun.svideo.recorder.views.effects.manager.AUIEffectLoader;
import com.aliyun.svideosdk.common.struct.form.PreviewPasterForm;
import com.liulishuo.filedownloader.BaseDownloadTask;

import java.util.ArrayList;
import java.util.List;

/**
 * 人脸贴图
 */
public class AUIPasterChooseView extends Fragment implements AUIIPageTab, AUIPasterAdapter.OnItemClickListener, AUIOnClearEffectListener {
    //RecyclerView列数
    private static final int SPAN_COUNT = 5;
    private RecyclerView rvPaterChooser;
    private AUIEffectLoader mPaterLoader;
    private AUIPasterAdapter mAdapter;
    private boolean mNeedInitResourceId = true;
    private int mSelectedResourceId = -1;
    private PasterSelectListener mPasterSelectListener;
    private String mTabTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ugsv_recorder_chooser_paster, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPaterChooser = view.findViewById(R.id.rv_mv_chooser);
        mAdapter = new AUIPasterAdapter(getActivity());
        mAdapter.setOnItemClickListener(this);
        //设置选中位置
        if (mNeedInitResourceId) {
            mNeedInitResourceId = false;
            mSelectedResourceId = mAdapter.getDefaultResourceId();
        }
        mAdapter.setSelectedResourceId(mSelectedResourceId);
        rvPaterChooser.setAdapter(mAdapter);
        mPaterLoader = new AUIEffectLoader(getContext());
        rvPaterChooser.setLayoutManager(new GridLayoutManager(getActivity(), SPAN_COUNT));

        final int marginVertical = DensityUtils.dip2px(getContext(), 12);
        rvPaterChooser.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                int lastRow = (parent.getLayoutManager().getItemCount() - 1) / SPAN_COUNT;
                int rowIndex = position / SPAN_COUNT;
                outRect.top = marginVertical;
                if (rowIndex == lastRow) {
                    outRect.bottom = marginVertical;
                }
            }
        });
        loadPaterEffect();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 加载动图列表数据
     */
    private void loadPaterEffect() {
        mPaterLoader.loadAllPaster(AppInfo.getInstance().obtainAppSignature(getActivity().getApplicationContext()),
                new AUIEffectLoader.LoadCallback<PreviewPasterForm>() {
                    @Override
                    public void onLoadCompleted(List<PreviewPasterForm> localInfos, List<PreviewPasterForm> remoteInfos,
                                                Throwable e) {
                        List<AUIEffectBody<PreviewPasterForm>> remoteData = new ArrayList<>();
                        List<AUIEffectBody<PreviewPasterForm>> localData = new ArrayList<>();
                        if (localInfos != null) {
                            AUIEffectBody<PreviewPasterForm> body;
                            for (PreviewPasterForm form : localInfos) {
                                body = new AUIEffectBody<PreviewPasterForm>(form, true);
                                localData.add(body);
                            }
                        }
                        if (remoteInfos != null) {
                            AUIEffectBody<PreviewPasterForm> body;
                            for (PreviewPasterForm mv : remoteInfos) {
                                body = new AUIEffectBody<PreviewPasterForm>(mv, false);
                                remoteData.add(body);
                            }
                        }
                        remoteData.addAll(0, localData);
                        mAdapter.syncData(remoteData);
                    }
                });
    }

    @Override
    public void setTabTitle(String tabTitle) {
        this.mTabTitle = tabTitle;
    }

    @Override
    public String getTabTitle() {
        return mTabTitle;
    }

    @Override
    public int getTabIcon() {
        return 0;
    }

    @Override
    public void onRemoteItemClick(final int position, final AUIEffectBody<PreviewPasterForm> data) {
        //取消现在的应用的效果
        if (mPasterSelectListener != null) {
            mPasterSelectListener.onPasterSelected(mAdapter.getDataList().get(0).getData());
        }
        //下载
        final PreviewPasterForm pasterForm = data.getData();
        mSelectedResourceId = pasterForm.getId();
        mPaterLoader.downloadPaster(pasterForm, new FileDownloaderCallback() {
            @Override
            public void onStart(int downloadId, long soFarBytes, long totalBytes, int preProgress) {
                super.onStart(downloadId, soFarBytes, totalBytes, preProgress);
                mAdapter.updateProcess(
                        (AUIPasterAdapter.PasterViewHolder) rvPaterChooser.findViewHolderForAdapterPosition(position),
                        preProgress, position);
                mAdapter.notifyDownloadingStart(data, position);
            }

            @Override
            public void onProgress(int downloadId, long soFarBytes, long totalBytes, long speed, int progress) {
                mAdapter.updateProcess(
                        (AUIPasterAdapter.PasterViewHolder) rvPaterChooser.findViewHolderForAdapterPosition(position), progress, position);
            }

            @Override
            public void onFinish(int downloadId, String path) {
                if (mPasterSelectListener != null && mSelectedResourceId == pasterForm.getId()) {
                    mPasterSelectListener.onSelectPasterDownloadFinish(path);
                    if (!isDestroy) {
                        mPasterSelectListener.onPasterSelected(data.getData());
                    }
                }
                mAdapter.updateProcess(
                        (AUIPasterAdapter.PasterViewHolder) rvPaterChooser.findViewHolderForAdapterPosition(position),
                        100, position);
                mAdapter.notifyDownloadingComplete(data, position, false);
            }

            @Override
            public void onError(BaseDownloadTask task, Throwable e) {
                super.onError(task, e);
                mAdapter.notifyDownloadingComplete(data, position, true);
            }
        });
    }

    private boolean isDestroy = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDestroy = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroy = true;
    }

    @Override
    public void onLocalItemClick(int position, AUIEffectBody<PreviewPasterForm> data) {
        mSelectedResourceId = data.getData().getId();
        if (mPasterSelectListener != null) {
            mPasterSelectListener.onPasterSelected(data.getData());
        }
    }

    public void setPasterSelectListener(PasterSelectListener mPasterSelectListener) {
        this.mPasterSelectListener = mPasterSelectListener;
    }

    @Override
    public void onClearEffectClick() {
        mSelectedResourceId = mAdapter.getDefaultResourceId();
        mAdapter.setSelectedResourceId(mSelectedResourceId);
        mAdapter.notifyDataSetChanged();
        if (mPasterSelectListener != null) {
            mPasterSelectListener.onPasterSelected(mAdapter.getDataList().get(0).getData());
        }
    }
}
