package com.aliyun.svideo.mixrecorder.view.effects.paster;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.aio.avbaseui.widget.AVCircleProgressView;
import com.aliyun.svideo.mixrecorder.view.effects.AUIEffectBody;
import com.aliyun.svideo.record.R;
import com.aliyun.svideosdk.common.struct.form.PreviewPasterForm;
import com.aliyun.ugsv.common.utils.image.AbstractImageLoaderTarget;
import com.aliyun.ugsv.common.utils.image.ImageLoaderImpl;

import java.util.ArrayList;
import java.util.List;

public class AUIPasterAdapter extends RecyclerView.Adapter<AUIPasterAdapter.PasterViewHolder>
        implements View.OnClickListener {

    private static final int VIEW_TYPE_NO = 0;
    private static final int VIEW_TYPE_LOCAL = 1;
    private static final int VIEW_TYPE_REMOTE = 2;
    private static final int VIEW_TYPE_DOWNLOADING = 3;
    private Context mContext;
    private List<AUIEffectBody<PreviewPasterForm>> mDataList = new ArrayList<>();

    private ArrayList<PreviewPasterForm> mLoadingSource = new ArrayList<>();//正在下载的的资源
    private int mSelectedSourceId = -1;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    private OnItemClickListener mItemClickListener;

    public AUIPasterAdapter(Context mContext) {
        this.mContext = mContext;
        PreviewPasterForm form = new PreviewPasterForm();
        AUIEffectBody<PreviewPasterForm> effectBody = new AUIEffectBody<PreviewPasterForm>(form, true);
        mDataList.add(0, effectBody);
    }

    @Override
    public PasterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ugsv_mixrecorder_chooser_paster_item, parent, false);
        PasterViewHolder holder = new PasterViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PasterViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        holder.mPosition = position;
        holder.mData = mDataList.get(position);
        // icon
        if (viewType == VIEW_TYPE_NO) {
            holder.mIcon.setImageResource(R.mipmap.ic_ugsv_recorder_chooser_clear);
        } else {
            new ImageLoaderImpl().loadImage(holder.mIcon.getContext(), holder.mData.getData().getIcon())
                    .into(holder.mIcon, new AbstractImageLoaderTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource) {
                            holder.mIcon.setImageDrawable(resource);
                        }
                    });
        }
        // mask
        holder.mMask.setVisibility(viewType == VIEW_TYPE_NO || viewType == VIEW_TYPE_LOCAL ? View.GONE : View.VISIBLE);
        // download
        holder.mDownload.setVisibility(viewType == VIEW_TYPE_REMOTE ? View.VISIBLE : View.GONE);
        // progress
        holder.mProgress.setVisibility(viewType == VIEW_TYPE_DOWNLOADING ? View.VISIBLE : View.GONE);
        if (mSelectedSourceId == holder.mData.getData().getId()) {
            holder.mIcon.setSelected(true);
        } else {
            holder.mIcon.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public int getItemViewType(int position) {
        int type = VIEW_TYPE_NO;
        if (position == 0) {
            return VIEW_TYPE_NO;
        }
        if (position > 0 && position < mDataList.size()) {
            AUIEffectBody<PreviewPasterForm> data = mDataList.get(position);
            if (data.isLocal()) {
                return VIEW_TYPE_LOCAL;
            } else if (data.isLoading()) {
                return VIEW_TYPE_DOWNLOADING;
            } else {
                return VIEW_TYPE_REMOTE;
            }
        }
        return type;
    }

    /**
     * 下载开始
     *
     * @param sourceBody
     */
    public void notifyDownloadingStart(AUIEffectBody<PreviewPasterForm> sourceBody, int position) {
        if (!mLoadingSource.contains(sourceBody.getData())) {
            mLoadingSource.add(sourceBody.getData());
            sourceBody.setLoading(true);
            notifyItemChanged(position);
        }
    }

    /**
     * 下载结束
     *
     * @param sourceBody
     * @param position
     */
    public synchronized void notifyDownloadingComplete(AUIEffectBody<PreviewPasterForm> sourceBody, int position, boolean isError) {
        sourceBody.setLocal(true);
        sourceBody.setLoading(false);
        mLoadingSource.remove(sourceBody.getData());
        if (isError) {
            sourceBody.setLocal(false);
            sourceBody.setLoading(false);
            sourceBody.setLoadingError(true);
        } else {
            sourceBody.setLocal(true);
            sourceBody.setLoading(false);
        }
        notifyItemChanged(position);
    }

    public void updateProcess(PasterViewHolder viewHolder, int process, int position) {
        if (viewHolder != null && viewHolder.mPosition == position) {
            viewHolder.mProgress.setProgress(process);
        }
    }

    /**
     * 刷新
     *
     * @param syncDataList
     */
    public synchronized void syncData(List<AUIEffectBody<PreviewPasterForm>> syncDataList) {
        if (syncDataList == null) {
            return;
        }
        ArrayList<AUIEffectBody<PreviewPasterForm>> delList = new ArrayList<>();
        for (AUIEffectBody<PreviewPasterForm> item : mDataList) {
            if (syncDataList.contains(item)) {
                delList.add(item);
            }
        }
        mDataList.removeAll(delList);
        for (AUIEffectBody<PreviewPasterForm> item : syncDataList) {
            if (!mDataList.contains(item)) {
                //if (!item.isLocal()) {
                mDataList.add(item);
                //}
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 监听接口，外部实现
     */
    interface OnItemClickListener {
        /**
         * 外部实现下载事件
         *
         * @param position 点击位置
         * @param data     数据
         */
        void onRemoteItemClick(int position, AUIEffectBody<PreviewPasterForm> data);

        /**
         * 外部实现应用此mv
         *
         * @param position 点击位置
         * @param data     该位置数据
         */
        void onLocalItemClick(int position, AUIEffectBody<PreviewPasterForm> data);
    }

    class PasterViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIcon;
        private View mMask;
        private ImageView mDownload;
        private AVCircleProgressView mProgress;
        private AUIEffectBody<PreviewPasterForm> mData;
        private int mPosition;

        public PasterViewHolder(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.ugsv_recorder_chooser_paster_item_icon);
            mMask = itemView.findViewById(R.id.ugsv_recorder_chooser_paster_item_mask);
            mDownload = itemView.findViewById(R.id.ugsv_recorder_chooser_paster_item_download);
            mProgress = itemView.findViewById(R.id.ugsv_recorder_chooser_paster_item_progress);
            itemView.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    if (mData.isLocal()) {
                        mItemClickListener.onLocalItemClick(mPosition, mData);
                    } else {
                        mItemClickListener.onRemoteItemClick(mPosition, mData);
                    }
                    notifyItemChanged(getSelectedPosition());
                    mSelectedSourceId = mData.getData().getId();
                    notifyItemChanged(getSelectedPosition());
                }
            });
        }
    }

    private int getSelectedPosition() {
        int size = mDataList.size();
        for (int i = 0; i < size; i++) {
            if (mDataList.get(i).getData().getId() == mSelectedSourceId) {
                return i;
            }
        }
        return 0;
    }

    public int getDefaultResourceId() {
        return mDataList.isEmpty() ? -1 : mDataList.get(0).getData().getId();
    }

    public void setSelectedResourceId(int resourceId) {
        this.mSelectedSourceId = resourceId;
    }

    public List<AUIEffectBody<PreviewPasterForm>> getDataList() {
        return mDataList;
    }

}
