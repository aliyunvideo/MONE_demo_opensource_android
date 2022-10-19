package com.aliyun.player.alivcplayerexpand.view.more;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.alivcplayerexpand.view.dlna.callback.OnDeviceItemClickListener;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.ClingDevice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 投屏设备选择adapter
 */
public class ScreenCostAdapter extends RecyclerView.Adapter<ScreenCostAdapter.ScreenCostViewHolder> {

    private Collection<ClingDevice> deviceList = new ArrayList<>();

    private OnDeviceItemClickListener mOnDlnaItemClickListener;

    @Override
    public ScreenCostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_screen_cost, parent, false);
        return new ScreenCostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ScreenCostViewHolder holder, final int position) {
        ClingDevice clingDevice = ((List<ClingDevice>) deviceList).get(position);
        if (clingDevice.isConnectedState()){
            holder.mConnectStateImageView.setVisibility(View.VISIBLE);
            holder.mConnectStateImageView.setImageResource(R.drawable.alivc_screen_cost_connected);
        }if (clingDevice.isConnectingState()){
            holder.mConnectStateImageView.setVisibility(View.VISIBLE);
            holder.mConnectStateImageView.setImageResource(R.drawable.loading_rotate);
        }else {
            holder.mConnectStateImageView.setVisibility(View.GONE);
        }
        holder.mDeviceNameTextView.setText(clingDevice.getDevice().getDetails().getFriendlyName());
        holder.mDlnaItemFramelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnDlnaItemClickListener != null) {
                    mOnDlnaItemClickListener.onItemClick(position);
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return deviceList == null ? 0 : deviceList.size();
    }

    public class ScreenCostViewHolder extends RecyclerView.ViewHolder {

        private TextView mDeviceNameTextView;
        private FrameLayout mDlnaItemFramelayout;
        private ImageView mConnectStateImageView;

        public ScreenCostViewHolder(View itemView) {
            super(itemView);
            mDeviceNameTextView = itemView.findViewById(R.id.tv_device_name);
            mDlnaItemFramelayout = itemView.findViewById(R.id.ll_dfm_item);
            mConnectStateImageView = itemView.findViewById(R.id.img_connect_state);
        }
    }

    /**
     * 清空
     */
    public void clear() {
        if (deviceList != null && deviceList.size() > 0) {
            deviceList.clear();
            notifyDataSetChanged();
        }
    }

    public void remove(ClingDevice device) {
        if (deviceList != null && deviceList.size() > 0 && deviceList.contains(device)) {
            int index = ((List) deviceList).indexOf(device);
            deviceList.remove(device);
            notifyItemRangeRemoved(index, 1);
        }
    }

    public void add(ClingDevice device) {
        if (deviceList != null) {
            deviceList.add(device);
            notifyItemInserted(deviceList.size());
        }
    }

    public ClingDevice getItem(int position) {
        if (deviceList == null || deviceList.size() - 1 < position) {
            return null;
        }
        return (ClingDevice) ((List)deviceList).get(position);
    }

    public void addAll(Collection<ClingDevice> deviceList) {
        this.deviceList = deviceList;
        notifyDataSetChanged();
    }

    public void setOnDeviceItemClickListener(OnDeviceItemClickListener listener) {
        this.mOnDlnaItemClickListener = listener;
    }
}
