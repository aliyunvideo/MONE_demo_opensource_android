package com.aliyun.svideo.mixrecorder.view.effects.paster;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aliyun.svideo.record.R;
import com.aliyun.svideo.mixrecorder.view.effects.EffectBody;
import com.aliyun.svideo.base.widget.CircleProgressBar;
import com.aliyun.svideo.base.widget.CircularImageView;
import com.aliyun.svideosdk.common.struct.form.PreviewPasterForm;
import com.aliyun.ugsv.common.utils.image.AbstractImageLoaderTarget;
import com.aliyun.ugsv.common.utils.image.ImageLoaderImpl;
import com.aliyun.ugsv.common.utils.image.ImageLoaderOptions;

import java.util.ArrayList;
import java.util.List;

public class AlivcPasterAdapter extends RecyclerView.Adapter<AlivcPasterAdapter.PasterViewHolder> implements View.OnClickListener {

    private static final int VIEW_TYPE_NO = 0;
    private static final int VIEW_TYPE_LOCAL = 1;
    private static final int VIEW_TYPE_REMOTE = 2;
    private static final int VIEW_TYPE_DOWNLOADING = 3;
    private Context mContext;
    private List<EffectBody<PreviewPasterForm>> mDataList = new ArrayList<>();

    private ArrayList<PreviewPasterForm> mLoadingMV = new ArrayList<>();//正在下载的的mv
    private int mSelectedMVId = -1;
    public void setmItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    private OnItemClickListener mItemClickListener;
    public AlivcPasterAdapter(Context mContext) {
        this.mContext = mContext;
        PreviewPasterForm form = new PreviewPasterForm();
        EffectBody<PreviewPasterForm> effectBody = new EffectBody<PreviewPasterForm>(form, true);
        mDataList.add(0, effectBody);
    }

    @Override
    public PasterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.alivc_recorder_item_effect_mv, parent, false);
        PasterViewHolder holder = new PasterViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PasterViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
        case VIEW_TYPE_NO:
            holder.ivDownloadIcon.setVisibility(View.GONE);
            holder.downloadProgress.setVisibility(View.GONE);
            holder.mPosition = position;
            holder.mData = mDataList.get(position);
            holder.resourceImageView.setImageResource(R.drawable.alivc_svideo_effect_none);
            break;
        case VIEW_TYPE_LOCAL:
            holder.ivDownloadIcon.setVisibility(View.GONE);
            holder.downloadProgress.setVisibility(View.GONE);
            holder.updateData(position, mDataList.get(position));
            break;
        case VIEW_TYPE_REMOTE:
            holder.ivDownloadIcon.setVisibility(View.VISIBLE);
            //下载失败替换下载图标
            if (mDataList.get(position).isLoadingError()) {
                holder.ivDownloadIcon.setImageResource(R.mipmap.alivc_svideo_icon_replay);
            } else {
                holder.ivDownloadIcon.setImageResource(R.mipmap.alivc_svideo_icon_download);
            }
            holder.downloadProgress.setVisibility(View.GONE);
            holder.updateData(position, mDataList.get(position));
            break;
        case VIEW_TYPE_DOWNLOADING:
            holder.ivDownloadIcon.setVisibility(View.GONE);
            holder.downloadProgress.setVisibility(View.VISIBLE);
            holder.ivDownloadIcon.setImageResource(R.mipmap.aliyun_svideo_icon_magic_light);
            holder.updateData(position, mDataList.get(position));
            break;
        default:
            break;
        }
        if (mSelectedMVId == mDataList.get(position).getData().getId()) {
            holder.resourceImageView.setSelected(true);
        } else {
            holder.resourceImageView.setSelected(false);
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
            EffectBody<PreviewPasterForm> data = mDataList.get(position);
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
     * @param mvBody
     */
    public void notifyDownloadingStart(EffectBody<PreviewPasterForm> mvBody) {
        if (!mLoadingMV.contains(mvBody.getData())) {
            mLoadingMV.add(mvBody.getData());
            mvBody.setLoading(true);
        }
    }

    /**
     * 下载结束
     * @param mvBody
     * @param position
     */
    public synchronized void notifyDownloadingComplete(EffectBody<PreviewPasterForm> mvBody, int position, boolean isError) {
        mvBody.setLocal(true);
        mvBody.setLoading(false);
        mLoadingMV.remove(mvBody.getData());
        if (isError) {
            mvBody.setLocal(false);
            mvBody.setLoading(false);
            mvBody.setLoadingError(true);
        } else {
            mvBody.setLocal(true);
            mvBody.setLoading(false);
        }
        notifyItemChanged(position);
    }
    public void updateProcess(PasterViewHolder viewHolder, int process, int position) {
        if (viewHolder != null && viewHolder.mPosition == position) {
            viewHolder.ivDownloadIcon.setVisibility(View.GONE);
            viewHolder.downloadProgress.setVisibility(View.VISIBLE);
            viewHolder.downloadProgress.setProgress(process);
        }
    }

    /**
     * 刷新
     * @param syncDataList
     */
    public synchronized void syncData(List<EffectBody<PreviewPasterForm>> syncDataList) {
        if (syncDataList == null) {
            return ;
        }
        ArrayList<EffectBody<PreviewPasterForm>> delList = new ArrayList<>();
        for (EffectBody<PreviewPasterForm> item : mDataList) {
            if (syncDataList.contains(item)) {
                delList.add(item);
            }
        }
        mDataList.removeAll(delList);
        for (EffectBody<PreviewPasterForm> item : syncDataList) {
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
         * @param position 点击位置
         * @param data 数据
         */
        void onRemoteItemClick(int position, EffectBody<PreviewPasterForm> data);

        /**
         * 外部实现应用此mv
         * @param position 点击位置
         * @param data 该位置数据
         */
        void onLocalItemClick(int position, EffectBody<PreviewPasterForm> data);
    }

    class PasterViewHolder extends RecyclerView.ViewHolder {
        private CircularImageView resourceImageView;
        private ImageView ivDownloadIcon;
        private EffectBody<PreviewPasterForm> mData;
        private int mPosition;
        private CircleProgressBar downloadProgress;
        public void updateData(int position, EffectBody<PreviewPasterForm> data) {
            this.mData = data;
            this.mPosition = position;
            PreviewPasterForm paster = data.getData();

            new ImageLoaderImpl().loadImage(resourceImageView.getContext(), paster.getIcon(),
                                            new ImageLoaderOptions.Builder().asBitmap().build()
            ).into(resourceImageView, new AbstractImageLoaderTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource) {
                    resourceImageView.setImageBitmap(resource);
                }
            });

        }
        public PasterViewHolder(View itemView) {
            super(itemView);
            resourceImageView = (CircularImageView) itemView.findViewById(R.id.resource_image_view);
            ivDownloadIcon = (ImageView) itemView.findViewById(R.id.iv_download_icon);
            downloadProgress = (CircleProgressBar) itemView.findViewById(R.id.download_progress);
            downloadProgress.isFilled(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        if (mData.isLocal()) {
                            mItemClickListener.onLocalItemClick(mPosition, mData );
                        } else {
                            mItemClickListener.onRemoteItemClick(mPosition, mData );
                        }

                        notifyItemChanged(getSelectedPostion());
                        mSelectedMVId = mData.getData().getId();
                        notifyItemChanged(getSelectedPostion());
                    }

                }
            });
        }

    }
    private int getSelectedPostion() {
        int size = mDataList.size();
        for (int i = 0; i < size; i++) {
            if (mDataList.get(i).getData().getId() == mSelectedMVId) {
                return i;
            }
        }
        return 0;
    }
    public void setSelectedMVId(int mSelectedMVId) {
        this.mSelectedMVId = mSelectedMVId;
    }

    public List<EffectBody<PreviewPasterForm>> getDataList() {
        return mDataList;
    }

}
