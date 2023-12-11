package com.aliyun.svideo.mixrecorder.view.effects.filter.animfilter;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aliyun.svideo.mixrecorder.util.RecordCommon;
import com.aliyun.svideo.mixrecorder.view.dialog.AUIIPageTab;
import com.aliyun.svideosdk.common.struct.effect.EffectFilter;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 特效选择器面板
 */
public class AUISpecialEffectChooseFragment extends Fragment implements AUIIPageTab {

    private OnSpecialEffectItemClickListener mOnItemClickListener;
    private AUISpecialEffectLoadingView mLoadingView;
    private AsyncTask<Void, String, List<String>> mDataLoadTask;
    private List<String> mEffectDataList;
    private int mSelectedPosition;
    private String mTabTitle;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mLoadingView = new AUISpecialEffectLoadingView(getContext());
        return mLoadingView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mEffectDataList == null || mEffectDataList.size() == 0) {
            mDataLoadTask = new AnimFilterDataLoadingTask(this);
            mDataLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mLoadingView.addData(mEffectDataList);
        }

        mLoadingView.setOnAnimFilterListItemClickListener(new OnSpecialEffectItemClickListener() {
            @Override
            public void onItemClick(EffectFilter effectInfo, int index) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(effectInfo, index);
                }
            }

            @Override
            public void onItemUpdate(EffectFilter effectInfo) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemUpdate(effectInfo);
                }
            }

        });
    }

    public void setOnItemClickListener(OnSpecialEffectItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.mSelectedPosition = selectedPosition;
    }

    @Override
    public void onStart() {
        super.onStart();
        mLoadingView.setSelectedPosition(mSelectedPosition);
    }

    private static class AnimFilterDataLoadingTask extends AsyncTask<Void, String, List<String>> {

        private WeakReference<AUISpecialEffectChooseFragment> contextWeakReference;

        AnimFilterDataLoadingTask(AUISpecialEffectChooseFragment fragment) {
            contextWeakReference = new WeakReference<>(fragment);
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            return RecordCommon.getAnimationFilterList();
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            AUISpecialEffectChooseFragment fragment = contextWeakReference.get();
            if (fragment != null) {
                fragment.notifyDataChanged(strings);
            }
        }
    }

    private void notifyDataChanged(List<String> strings) {
        // index 0的位置添加一条空数据, 让"无效果"显示出来
        this.mEffectDataList = strings;
        strings.add(0, "");
        mLoadingView.addData(mEffectDataList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDataLoadTask != null) {
            mDataLoadTask.cancel(true);
            mDataLoadTask = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUISpecialEffectLoadingView.ANIMATION_FILTER_REQUEST_CODE) {
            if (mLoadingView == null) {
                return;
            }
            if (resultCode == Activity.RESULT_OK) {
                int id = data.getIntExtra(AUISpecialEffectLoadingView.SELECTED_ID, 0);
                mLoadingView.setCurrResourceID(id);
            } else {
                mLoadingView.setCurrResourceID(-1);
            }
        }
    }

}
