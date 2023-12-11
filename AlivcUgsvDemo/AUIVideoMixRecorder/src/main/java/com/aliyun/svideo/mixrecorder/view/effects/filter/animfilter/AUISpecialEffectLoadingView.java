package com.aliyun.svideo.mixrecorder.view.effects.filter.animfilter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.svideo.base.EffectParamsAdjustView;
import com.aliyun.svideo.record.R;
import com.aliyun.svideosdk.common.struct.effect.EffectConfig;
import com.aliyun.svideosdk.common.struct.effect.EffectFilter;
import com.aliyun.svideosdk.common.struct.effect.ValueTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 特效选择器UI
 */
public class AUISpecialEffectLoadingView extends FrameLayout {
    private AUISpecialEffectAdapter mAdapter;
    private List<String> mDataList = new ArrayList<>();
    private OnSpecialEffectItemClickListener mOnSpecialEffectItemClickListener;
    public static int ANIMATION_FILTER_REQUEST_CODE = 1006;
    public static final String SELECTED_ID = "selected_id";
    private EffectParamsAdjustView mParamsAdjustView;
    private static EffectFilter mCurrentEffect;

    public AUISpecialEffectLoadingView(@NonNull Context context) {
        this(context, null);
    }

    public AUISpecialEffectLoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AUISpecialEffectLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ugsv_mixrecorder_chooser_special_effect, this, true);
        mParamsAdjustView = view.findViewById(R.id.params_effect_view);
        mParamsAdjustView.setOnAdjustListener(new EffectParamsAdjustView.OnAdjustListener() {
            @Override
            public void onAdjust() {
                mOnSpecialEffectItemClickListener.onItemUpdate(mCurrentEffect);
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.ugsv_recorder_effect_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new AUISpecialEffectAdapter(getContext(), mDataList);

        recyclerView.setAdapter(mAdapter);
        // item点击事件
        mAdapter.setOnItemClickListener(new OnSpecialEffectItemClickListener() {
            @Override
            public void onItemClick(EffectFilter effectInfo, int index) {
                if (mOnSpecialEffectItemClickListener != null) {
                    showEffectParamsUI(effectInfo);
                    mCurrentEffect = effectInfo;
                    mOnSpecialEffectItemClickListener.onItemClick(effectInfo, index);
                }
            }

            @Override
            public void onItemUpdate(EffectFilter effectInfo) {
                mOnSpecialEffectItemClickListener.onItemUpdate(effectInfo);
            }
        });
    }

    /**
     * 显示参数调节ui，目前只提供{@link ValueTypeEnum#INT,ValueTypeEnum#FLOAT}两种类型
     *
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

    public void addData(List<String> data) {
        mDataList.clear();
        mDataList.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    public void setOnAnimFilterListItemClickListener(OnSpecialEffectItemClickListener listener) {
        this.mOnSpecialEffectItemClickListener = listener;
    }

    public void setSelectedPosition(int selectedPosition) {
        mAdapter.setSelectedPosition(selectedPosition);
    }

    public void setCurrResourceID(int id) {
        if (id != -1) {
        }
    }

    /**
     * 退出录制页面时清除当前的特效滤镜
     */
    public static void clearCacheEffectFilter() {
        mCurrentEffect = null;
    }
}
