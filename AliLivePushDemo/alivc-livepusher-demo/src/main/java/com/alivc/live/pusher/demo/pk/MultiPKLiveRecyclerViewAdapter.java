package com.alivc.live.pusher.demo.pk;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.live.pusher.demo.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MultiPKLiveRecyclerViewAdapter extends RecyclerView.Adapter<MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder> {

    private OnPKConnectClickListener mListener;
    private List<Boolean> mData;

    @NonNull
    @NotNull
    @Override
    public MultiPKLiveViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_multi_pklive_item, parent, false);
        return new MultiPKLiveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MultiPKLiveViewHolder holder, int position) {
        Boolean isPKing = mData.get(position);
        holder.getRenderFrameLayout().setVisibility(isPKing ? View.VISIBLE : View.INVISIBLE);
        holder.getUnConnectFrameLayout().setVisibility(isPKing ? View.INVISIBLE : View.VISIBLE);
        if (isPKing) {
            holder.getConnectView().setText(holder.itemView.getContext().getResources().getString(R.string.pk_stop_connect));
            holder.getConnectView().setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.shape_interact_live_un_connect_btn_bg));
        } else {
            holder.getConnectView().setText(holder.itemView.getContext().getResources().getString(R.string.pk_start_connect));
            holder.getConnectView().setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.shape_pysh_btn_bg));
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void setOnPKConnectClickListener(OnPKConnectClickListener listener) {
        this.mListener = listener;
    }

    public void setData(List<Boolean> dataList) {
        this.mData = dataList;
    }

    public class MultiPKLiveViewHolder extends RecyclerView.ViewHolder {

        private final TextView mConnectTextView;
        private final FrameLayout mOtherFrameLayout;
        private final FrameLayout mUnConnectFrameLayout;

        public MultiPKLiveViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mConnectTextView = itemView.findViewById(R.id.tv_connect);
            mOtherFrameLayout = itemView.findViewById(R.id.frame_other);
            mUnConnectFrameLayout = itemView.findViewById(R.id.fl_un_connect);

            mConnectTextView.setOnClickListener(view -> {
                if (mListener != null) {
                    mListener.onPKConnectClick(getAdapterPosition());
                }
            });
        }

        public FrameLayout getRenderFrameLayout() {
            return mOtherFrameLayout;
        }

        public TextView getConnectView() {
            return mConnectTextView;
        }

        public FrameLayout getUnConnectFrameLayout() {
            return mUnConnectFrameLayout;
        }
    }

    public interface OnPKConnectClickListener {
        void onPKConnectClick(int position);
    }
}
