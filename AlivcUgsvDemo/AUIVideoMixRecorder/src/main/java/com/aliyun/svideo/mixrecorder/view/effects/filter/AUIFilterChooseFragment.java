package com.aliyun.svideo.mixrecorder.view.effects.filter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aliyun.svideo.mixrecorder.util.RecordCommon;
import com.aliyun.svideo.mixrecorder.view.dialog.AUIIPageTab;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 滤镜选择器面板
 */
public class AUIFilterChooseFragment extends Fragment implements AUIIPageTab {

    private String mTabTitle;
    private AUIFilterLoadingView mLoadingView;
    private OnFilterItemClickListener mOnItemClickListener;

    private AsyncTask<Void, String, List<String>> mFilterLoadTask;
    private List<String> mFilterData;
    private int mFilterSelectedPosition;


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
        //title only
        return 0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mLoadingView = new AUIFilterLoadingView(getContext());
        return mLoadingView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mFilterData == null || mFilterData.size() == 0) {
            mFilterLoadTask = new FilterDataLoadingTask(this);
            mFilterLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mLoadingView.notifyDataChange(mFilterData);
        }
        mLoadingView.setOnFilterListItemClickListener((effectInfo, position) -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(effectInfo, position);
            }
        });
    }

    public void setOnItemClickListener(OnFilterItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setFilterSelectedPosition(int filterPosition) {
        this.mFilterSelectedPosition = filterPosition;
    }

    @Override
    public void onStart() {
        super.onStart();
        mLoadingView.setFilterSelectedPosition(mFilterSelectedPosition);
    }

    private static class FilterDataLoadingTask extends AsyncTask<Void, String, List<String>> {

        private WeakReference<AUIFilterChooseFragment> contextWeakReference;

        FilterDataLoadingTask(AUIFilterChooseFragment fragment) {
            contextWeakReference = new WeakReference<>(fragment);
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            return RecordCommon.getColorFilterList();
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            AUIFilterChooseFragment fragment = contextWeakReference.get();
            if (fragment != null) {
                fragment.notifyDataChanged(strings);
            }
        }
    }

    private void notifyDataChanged(List<String> strings) {
        // index 0的位置添加一条空数据, 让"无效果"显示出来
        this.mFilterData = strings;
        strings.add(0, "");
        mLoadingView.notifyDataChange(mFilterData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFilterLoadTask != null) {
            mFilterLoadTask.cancel(true);
            mFilterLoadTask = null;
        }
    }
}