package com.aliyun.svideo.editor.panel;

import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;
import com.aliyun.common.utils.StringUtils;
import com.aliyun.svideo.editor.common.panel.OnItemClickListener;
import com.aliyun.svideo.editor.filter.VideoFilterClickListener;
import com.aliyun.svideo.editor.filter.VideoFilterEntryPanelView;
import com.aliyun.svideo.editor.filter.VideoFilterEntryViewModel;
import com.aliyun.svideo.editor.filter.VideoFilterItemViewModel;
import com.aliyun.svideo.editor.filter.VideoFilterManager;
import com.aliyun.svideosdk.common.struct.effect.TrackEffectFilter;
import com.aliyun.svideosdk.common.struct.project.Source;
import com.aliyun.svideosdk.editor.AliyunIEditor;

/**
 * @author geekeraven
 */
public class FilterPanel extends BasePanel implements OnItemClickListener, VideoFilterClickListener {

    private VideoFilterEntryPanelView mVideoFilterEntryPanelView;
    private VideoFilterEntryViewModel mVideoFilterEntryViewModel;
    private VideoFilterManager mVideoFilterManager;

    public FilterPanel(@NonNull Context context) {
        this(context, null);
    }

    public FilterPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInitView() {
        Context lContext = getContext();
        if(lContext instanceof ViewModelStoreOwner) {
            mVideoFilterEntryPanelView = new VideoFilterEntryPanelView(lContext);
            addView(mVideoFilterEntryPanelView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    public void onInitData(PanelManger panelManger,AliyunIEditor editor, int panelType) {
        super.onInitData(panelManger,editor, panelType);
        Context ctx = getContext();
        if(ctx instanceof LifecycleOwner) {
            LifecycleOwner owner = (LifecycleOwner) ctx;
            mVideoFilterEntryViewModel = new VideoFilterEntryViewModel();
            mVideoFilterEntryPanelView.setViewModel(mVideoFilterEntryViewModel, mVideoFilterManager);
            mVideoFilterEntryPanelView.setVideoFilterClickListener(this);
        }
    }

    public void setVideoFilterManager(VideoFilterManager videoFilterManager) {
        mVideoFilterManager = videoFilterManager;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void onRemove() {
        super.onRemove();
        //mVideoEffectEntryViewModel.unBind();
    }

    @Override
    public void onPlayProgress(long currentPlayTime, long currentStreamPlayTime) {
        //mVideoEffectEntryPanelView.updatePlayProgress(currentPlayTime);
    }

    @Override
    public void onItemClick(@Nullable View view, long id, @Nullable Object obj) {

    }

    @Override
    public void onEffectSelected(@NonNull VideoFilterItemViewModel viewModel) {
        mVideoFilterManager.setVideoFilterItem(viewModel);

        if (StringUtils.isEmpty(viewModel.getSourcePath())) {
            mAliyunIEditor.removeFilter();
        } else {
            TrackEffectFilter filter = new TrackEffectFilter.Builder().source(new Source(viewModel.getSourcePath()))
                .startTime(0, TimeUnit.MILLISECONDS)
                .duration(Integer.MAX_VALUE, TimeUnit.MILLISECONDS).build();
            mAliyunIEditor.applyFilter(filter);
        }
    }
}
