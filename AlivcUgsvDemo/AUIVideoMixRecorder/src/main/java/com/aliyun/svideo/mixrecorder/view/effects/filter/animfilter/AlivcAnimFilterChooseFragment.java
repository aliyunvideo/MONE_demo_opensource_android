package com.aliyun.svideo.mixrecorder.view.effects.filter.animfilter;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aliyun.svideo.record.R;
import com.aliyun.svideo.mixrecorder.util.RecordCommon;
import com.aliyun.svideo.mixrecorder.view.dialog.IPageTab;
import com.aliyun.svideosdk.common.struct.effect.EffectFilter;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 特效滤镜选择框页面
 *
 */
public class AlivcAnimFilterChooseFragment extends Fragment implements IPageTab {

    private OnAnimFilterItemClickListener mOnAnimFilterItemClickListener;
    private AnimFilterLoadingView mAnimFilterLoadingView;
    private AsyncTask<Void, String, List<String>> filterLoadTask;
    private List<String> filterData;
    private int filterPosition;
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
        return R.mipmap.alivc_svideo_record_effect;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mAnimFilterLoadingView = new AnimFilterLoadingView(getContext());
        mAnimFilterLoadingView.setFragment(this);
        return mAnimFilterLoadingView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (filterData == null || filterData.size() == 0) {
            filterLoadTask = new AnimFilterDataLoadingTask(this);
            filterLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mAnimFilterLoadingView.addData(filterData);
        }

        mAnimFilterLoadingView.setOnAnimFilterListItemClickListener(new OnAnimFilterItemClickListener() {
            @Override
            public void onItemClick(EffectFilter effectInfo, int index) {
                if (mOnAnimFilterItemClickListener != null) {
                    mOnAnimFilterItemClickListener.onItemClick(effectInfo, index);
                }
            }

            @Override
            public void onItemUpdate(EffectFilter effectInfo) {
                if (mOnAnimFilterItemClickListener != null) {
                    mOnAnimFilterItemClickListener.onItemUpdate(effectInfo);
                }
            }

        });
    }

    public void setOnAnimFilterItemClickListener(OnAnimFilterItemClickListener listener) {
        this.mOnAnimFilterItemClickListener = listener;
    }

    public void setFilterPosition(int filterPosition) {
        this.filterPosition = filterPosition;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAnimFilterLoadingView.setFilterPosition(filterPosition);
    }

    private static class AnimFilterDataLoadingTask extends AsyncTask<Void, String, List<String>> {

        private WeakReference<AlivcAnimFilterChooseFragment> contextWeakReference;

        AnimFilterDataLoadingTask(AlivcAnimFilterChooseFragment fragment) {
            contextWeakReference = new WeakReference<>(fragment);
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            return RecordCommon.getAnimationFilterList();
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            AlivcAnimFilterChooseFragment fragment = contextWeakReference.get();
            if (fragment != null) {
                fragment.notifyDataChanged(strings);
            }
        }
    }

    private void notifyDataChanged(List<String> strings) {
        // index 0的位置添加一条空数据, 让"无效果"显示出来
        this.filterData = strings;
        strings.add(0, "");
        mAnimFilterLoadingView.addData(filterData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (filterLoadTask != null) {
            filterLoadTask.cancel(true);
            filterLoadTask = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AnimFilterLoadingView.ANIMATION_FILTER_REQUEST_CODE) {
            if (mAnimFilterLoadingView == null) {
                return;
            }
            if (resultCode == Activity.RESULT_OK) {
                int id = data.getIntExtra(AnimFilterLoadingView.SELECTED_ID, 0);
                mAnimFilterLoadingView.setCurrResourceID(id);
            } else {
                mAnimFilterLoadingView.setCurrResourceID(-1);
            }
        }
    }

}
