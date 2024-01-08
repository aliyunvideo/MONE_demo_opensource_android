package com.alivc.live.barestream_interactive.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.live.barestream_interactive.R;
import com.alivc.live.barestream_interactive.bean.BareStreamBean;
import com.alivc.live.interactive_common.widget.InteractiveConnectView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractiveMultiBareRecyclerViewAdapter extends RecyclerView.Adapter<InteractiveMultiBareRecyclerViewAdapter.InteractiveMultiBareViewHolder> {

    private Map<Integer, Integer> mFLayoutWithPositionMap = new HashMap<>();
    private OnItemClickListener mListener;
    private List<BareStreamBean> mData;
    private OnItemViewChangedListener mOnPKItemViewChangedListener;

    public boolean reSetFrameLayout(int position, int frameLayoutHashCode) {
        if (mFLayoutWithPositionMap.containsKey(position)) {
            if (frameLayoutHashCode == mFLayoutWithPositionMap.get(position)) {
                return false;
            } else {
                mFLayoutWithPositionMap.put(position, frameLayoutHashCode);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onViewAttachedToWindow(InteractiveMultiBareViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (mOnPKItemViewChangedListener != null) {
            mOnPKItemViewChangedListener.onItemViewAttachedToWindow(holder.getAdapterPosition());
        }
    }

    @Override
    public void onViewDetachedFromWindow(InteractiveMultiBareViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (mOnPKItemViewChangedListener != null) {
            mOnPKItemViewChangedListener.onItemViewDetachedToWindow(holder.getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public InteractiveMultiBareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_multi_interactive_bare_item, parent, false);
        return new InteractiveMultiBareViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InteractiveMultiBareViewHolder holder, int position) {
        BareStreamBean bareStreamBean = mData.get(position);
        holder.isConnected(bareStreamBean.isConnected());
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void setOnConnectClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setOnItemViewChangedListener(OnItemViewChangedListener listener) {
        this.mOnPKItemViewChangedListener = listener;
    }

    public void setData(List<BareStreamBean> dataList) {
        this.mData = dataList;
        notifyDataSetChanged();
    }

    public class InteractiveMultiBareViewHolder extends RecyclerView.ViewHolder {

        private final InteractiveConnectView mConnectView;
        private final FrameLayout mRenderFrameLayout;

        public InteractiveMultiBareViewHolder(@NonNull View itemView) {
            super(itemView);
            mConnectView = itemView.findViewById(R.id.connect_view);
            mRenderFrameLayout = itemView.findViewById(R.id.render_frame);

            mConnectView.setDefaultText(mConnectView.getResources().getString(R.string.interactive_bare_stream_start_pull));
            mConnectView.setConnectClickListener(() -> {
                if (mListener != null) {
                    mListener.onConnectClick(getAdapterPosition());
                }
            });
        }

        public FrameLayout getRenderFrameLayout() {
            return mRenderFrameLayout;
        }

        public InteractiveConnectView getConnectView() {
            return mConnectView;
        }

        public void isConnected(boolean isConnected) {
            if (isConnected) {
                mConnectView.connected(mConnectView.getResources().getString(R.string.interactive_bare_stream_stop_pull));
            } else {
                mConnectView.unConnected(mConnectView.getResources().getString(R.string.interactive_bare_stream_start_pull));
            }
            mConnectView.getConnectFrameLayout().setVisibility(isConnected ? View.INVISIBLE : View.VISIBLE);
        }
    }

    public interface OnItemClickListener {
        void onConnectClick(int position);
    }

    public interface OnItemViewChangedListener {
        void onItemViewAttachedToWindow(int position);

        void onItemViewDetachedToWindow(int position);
    }
}
