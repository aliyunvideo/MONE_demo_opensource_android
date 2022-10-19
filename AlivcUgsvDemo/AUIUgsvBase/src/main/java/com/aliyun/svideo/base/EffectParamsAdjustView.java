package com.aliyun.svideo.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.svideo.base.widget.beauty.listener.AbstractOnProgressFloatChangeListener;
import com.aliyun.svideo.base.widget.beauty.seekbar.BeautySeekBar;
import com.aliyun.svideosdk.common.struct.effect.EffectConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 特效生态化自定义参数调节View
 */
public class EffectParamsAdjustView extends FrameLayout {

    List<EffectConfig.NodeBean.Params> mData = new ArrayList<>();
    private Adapter mAdapter;
    private OnAdjustListener mOnAdjustListener;

    public EffectParamsAdjustView(Context context) {
        this(context, null);
    }

    public EffectParamsAdjustView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EffectParamsAdjustView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                      ViewGroup.LayoutParams.WRAP_CONTENT));
        mAdapter = new Adapter();
        recyclerView.setAdapter(mAdapter);
        this.addView(recyclerView);
    }

    public void setData(List<EffectConfig.NodeBean.Params>  params) {
        mData.clear();
        mData.addAll(params);
        mAdapter.notifyDataSetChanged();
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.aliyun_svideo_effect_params_item, EffectParamsAdjustView.this, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final EffectConfig.NodeBean.Params params = mData.get(position);
            holder.mName.setText(mData.get(position).getName());
            Object[] value = params.getValue().getValue();
            final Object[] maxValue = params.getMaxValue().getValue();
            final Object[] minValue = params.getMinValue().getValue();
            holder.mSeekBar.setProgressChangeListener(new AbstractOnProgressFloatChangeListener() {

                @Override
                public void onProgressFloatChange(int progress, float progressFloat) {
                    switch (params.getType()){
                        case INT:
                            params.getValue().updateINT(progress);
                            break;
                        case FLOAT:
                            params.getValue().updateFLOAT(progressFloat);
                            break;
                        default:
                            break;

                    }
                    if (mOnAdjustListener != null){
                        mOnAdjustListener.onAdjust();
                    }
                }
            });

            switch (params.getType()) {
                case INT:
                    holder.mSeekBar.setFloatProgress(false);
                    holder.mSeekBar.setMin((int)minValue[0]);
                    holder.mSeekBar.setMax((int)maxValue[0]);
                    int percentI = (int)(((int)value[0] - (int)minValue[0])/(float)((int)maxValue[0] - (int)minValue[0]) * 100);
                    holder.mSeekBar.setIndicatorAndProgress(percentI,(int)value[0]);
                    break;
                case FLOAT:
                    holder.mSeekBar.setFloatProgress(true);
                    holder.mSeekBar.setMin((float)minValue[0]);
                    holder.mSeekBar.setMax((float)maxValue[0]);
                    int percentF = (int)(((float)value[0] - (float)minValue[0])/((float)maxValue[0] - (float)minValue[0]) * 100);
                    holder.mSeekBar.setIndicatorAndProgress(percentF,(float)value[0]);

                    break;
                default:
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView mName;
            private BeautySeekBar mSeekBar;

            private MyViewHolder(View itemView) {
                super(itemView);
                mName = itemView.findViewById(R.id.tv_effect_params_name);
                mSeekBar = itemView.findViewById(R.id.seekbar_effect_params);
            }
        }
    }

    public void setOnAdjustListener(OnAdjustListener onAdjustListener){
        this.mOnAdjustListener = onAdjustListener;
    }

    public interface OnAdjustListener{
        void onAdjust();
    }
}
