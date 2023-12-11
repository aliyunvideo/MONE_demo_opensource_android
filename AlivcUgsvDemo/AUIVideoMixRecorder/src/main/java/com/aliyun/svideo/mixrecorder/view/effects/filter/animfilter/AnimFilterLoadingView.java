package com.aliyun.svideo.mixrecorder.view.effects.filter.animfilter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.aliyun.svideo.base.EffectParamsAdjustView;
import com.aliyun.svideo.base.Form.ResourceForm;
import com.aliyun.svideo.base.http.EffectService;
import com.aliyun.svideo.downloader.DownloaderManager;
import com.aliyun.svideo.downloader.FileDownloaderModel;
import com.aliyun.svideo.record.R;
import com.aliyun.svideo.mixrecorder.util.RecordCommon;
import com.aliyun.svideo.mixrecorder.view.CategoryAdapter;
import com.aliyun.svideo.mixrecorder.view.effects.filter.EffectInfo;
import com.aliyun.svideosdk.common.struct.effect.EffectConfig;
import com.aliyun.svideosdk.common.struct.effect.EffectFilter;
import com.aliyun.svideosdk.common.struct.effect.ValueTypeEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 特效滤镜view的封装
 */
public class AnimFilterLoadingView extends FrameLayout {

    private AnimFilterAdapter mAnimFilterAdapter;
    private List<String> mDataList = new ArrayList<>();
    private OnAnimFilterItemClickListener mOnAnimFilterItemClickListener;
    private ArrayList<ResourceForm> mFilterList4Category;
    private AsyncTask<Void, Void, List<FileDownloaderModel>> mLoadTask;
    private RecyclerView mRVCategory;
    private CategoryAdapter mCategoryAdapter;
    public static int ANIMATION_FILTER_REQUEST_CODE = 1006;
    public static final String SELECTED_ID = "selected_id";
    private Fragment mFragment;
    private EffectParamsAdjustView mParamsAdjustView;
    private static EffectFilter mCurrentEffect;
    private static int mCurrID = -1;

    public AnimFilterLoadingView(@NonNull Context context) {
        this(context, null);
    }

    public AnimFilterLoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimFilterLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.alivc_recorder_view_anim_filter, this, true);

        mRVCategory = view.findViewById(R.id.alivc_filter_category);
        mParamsAdjustView = view.findViewById(R.id.params_effect_view);
        mParamsAdjustView.setOnAdjustListener(new EffectParamsAdjustView.OnAdjustListener() {
            @Override
            public void onAdjust() {
                mOnAnimFilterItemClickListener.onItemUpdate(mCurrentEffect);
            }
        });
        mCategoryAdapter = new CategoryAdapter(getContext());
        mRVCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mCategoryAdapter.setMoreClickListener(new CategoryAdapter.OnMoreClickListener() {
            @Override
            public void onMoreClick() {
                Intent moreIntent = new Intent();
                moreIntent.setClassName(getContext(), "com.aliyun.svideo.editor.effectmanager.MoreAnimationEffectActivity");
                mFragment.startActivityForResult(moreIntent, ANIMATION_FILTER_REQUEST_CODE);
            }
        });
        mCategoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(EffectInfo effectInfo, int index) {
                if (effectInfo.isCategory && mFilterList4Category.size() > index) {
                    ResourceForm resourceForm = mFilterList4Category.get(index);
                    mCurrID = resourceForm.getId();
                    changeCategoryDir(resourceForm);
                }
                return true;
            }
        });
        mRVCategory.setAdapter(mCategoryAdapter);
        RecyclerView recyclerView = view.findViewById(R.id.alivc_filter_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mAnimFilterAdapter = new AnimFilterAdapter(getContext(), mDataList);

        recyclerView.setAdapter(mAnimFilterAdapter);
        // item点击事件
        mAnimFilterAdapter.setOnItemClickListener(new OnAnimFilterItemClickListener() {
            @Override
            public void onItemClick(EffectFilter effectInfo, int index) {
                if (mOnAnimFilterItemClickListener != null) {
                    showEffectParamsUI(effectInfo);
                    mCurrentEffect = effectInfo;
                    mOnAnimFilterItemClickListener.onItemClick(effectInfo, index);
                }
            }

            @Override
            public void onItemUpdate(EffectFilter effectInfo) {
                mOnAnimFilterItemClickListener.onItemUpdate(effectInfo);
            }

        });


        loadLocalAnimationFilter();
    }

    /**
     * 显示参数调节ui，目前只提供{@link ValueTypeEnum#INT,ValueTypeEnum#FLOAT}两种类型
     * @param ef EffectFilter
     */
    private void showEffectParamsUI(final EffectFilter ef) {
        List<EffectConfig.NodeBean> nodeTree = ef.getNodeTree();
        List<EffectConfig.NodeBean.Params> paramsList = new ArrayList<>();
        if (nodeTree == null || nodeTree.size() == 0) {
            mParamsAdjustView.setVisibility(GONE);
            return;
        }
        for (EffectConfig.NodeBean nodeBean : nodeTree) {
            List<EffectConfig.NodeBean.Params> params = nodeBean.getParams();
            if (params == null || params.size() == 0) {
                continue;
            }
            for (EffectConfig.NodeBean.Params param : params) {
                ValueTypeEnum type = param.getType();
                if (type == ValueTypeEnum.INT || type == ValueTypeEnum.FLOAT) {
                    //当前只调节INT和FLOAT类型参数
                    paramsList.add(param);
                }
            }
        }
        if (paramsList.size() == 0) {
            mParamsAdjustView.setVisibility(GONE);
        } else {
            mParamsAdjustView.setVisibility(VISIBLE);
            mParamsAdjustView.setData(paramsList);
        }

    }

    public void addData(List<String> alivcFilterInfos) {
        mDataList.addAll(alivcFilterInfos);
        mAnimFilterAdapter.notifyDataSetChanged();
    }

    public void setOnAnimFilterListItemClickListener(OnAnimFilterItemClickListener listener) {
        this.mOnAnimFilterItemClickListener = listener;
    }


    public void setFilterPosition(int filterPosition) {
        mAnimFilterAdapter.setSelectedPos(filterPosition);
    }

    public void loadLocalAnimationFilter() {

        mLoadTask = new AnimFilterLoadingView.MyLoadAsyncTask();
        mLoadTask.execute();
    }

    public void setCurrResourceID(int id) {
        if (id != -1) {
            mCurrID = id;
        }
        loadLocalAnimationFilter();
    }

    public void initResourceLocalWithSelectId(int id, List<FileDownloaderModel> downloadmodels) {
        mFilterList4Category = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();

        //添加一个默认资源
        FileDownloaderModel extFile = new FileDownloaderModel();
        extFile.setPath(RecordCommon.QU_DIR + RecordCommon.QU_ANIMATION_FILTER);
        extFile.setNameEn("default");
        extFile.setName("默认");
        extFile.setId(0);
        downloadmodels.add(0,extFile);

        if (downloadmodels.size() > 0) {
            for (FileDownloaderModel model : downloadmodels) {
                if (new File(model.getPath()).exists()) {
                    if (!ids.contains(model.getId())) {
                        ids.add(model.getId());
                        ResourceForm form = new ResourceForm();
                        form.setPreviewUrl(model.getPreview());
                        form.setIcon(model.getIcon());
                        form.setLevel(model.getLevel());
                        form.setName(model.getName());
                        form.setNameEn(model.getNameEn());
                        form.setId(model.getId());
                        form.setDescription(model.getDescription());
                        form.setSort(model.getSort());
                        form.setIsNew(model.getIsnew());
                        form.setPath(model.getPath());
                        mFilterList4Category.add(form);
                    }
                }
            }
        }

        ResourceForm form = new ResourceForm();
        form.setMore(true);
        mFilterList4Category.add(form);
        mCategoryAdapter.setData(mFilterList4Category);
        if (ids.size() > 0 && (id == -1 || !ids.contains(id)) ) {
            id = ids.get(0);
        }
        int categoryIndex = 0;
        for (ResourceForm resourceForm : mFilterList4Category) {
            if (resourceForm.getId() == id) {
                changeCategoryDir(resourceForm);
                break;
            }
            categoryIndex++;
        }
        mRVCategory.smoothScrollToPosition(categoryIndex);
        mCategoryAdapter.selectPosition(categoryIndex);
        if (mCurrID == -1) {
            loadLocalAnimationFilter();
            mCurrID = 0;
        }
        Log.d("TAG", "categoryIndex :" + categoryIndex);
    }

    private void changeCategoryDir(ResourceForm resourceForm) {
        if (resourceForm != null && resourceForm.getPath() != null) {
            List<String> fileListByDir = RecordCommon.getFileListByDir(resourceForm.getPath());
            // index 0的位置添加一条空数据, 让"无效果"显示出来
            fileListByDir.add(0, "");
            mAnimFilterAdapter.setDataList(fileListByDir);
            mAnimFilterAdapter.setCurrentEffect(mCurrentEffect);
            if (mCurrentEffect == null || mCurrentEffect.getEffectConfig() == null){
                //当前选中的不是自定义资源
                mParamsAdjustView.setVisibility(GONE);
            }else if (!fileListByDir.contains(mCurrentEffect.getPath())){
                //切换tab时
                mParamsAdjustView.setVisibility(GONE);
            }else {
                showEffectParamsUI(mCurrentEffect);
            }
            mAnimFilterAdapter.notifyDataSetChanged();
        }
    }

    public void setFragment(Fragment fragemt) {
        mFragment = fragemt;
    }

    private class MyLoadAsyncTask extends AsyncTask<Void, Void, List<FileDownloaderModel>> {

        @Override
        protected List<FileDownloaderModel> doInBackground(Void... voids) {
            return DownloaderManager.getInstance().getDbController().getResourceByType(
                       EffectService.ANIMATION_FILTER);
        }

        @Override
        protected void onPostExecute(List<FileDownloaderModel> fileDownloaderModels) {
            super.onPostExecute(fileDownloaderModels);
            initResourceLocalWithSelectId(mCurrID, fileDownloaderModels);
        }
    }

    /**
     * 退出录制页面时清除当前的特效滤镜
     */
    public static void clearCacheEffectFilter(){
        mCurrentEffect = null;
        mCurrID = -1;
    }
}
