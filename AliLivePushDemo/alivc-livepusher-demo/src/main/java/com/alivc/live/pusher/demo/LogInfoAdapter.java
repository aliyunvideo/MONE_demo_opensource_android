package com.alivc.live.pusher.demo;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alivc.live.pusher.AlivcEncodeModeEnum;
import com.alivc.live.pusher.AlivcLivePushStatsInfo;

public class LogInfoAdapter extends RecyclerView.Adapter<LogInfoAdapter.LogViewHolder> {

    private String BITRATE_UNIT = "Kbps";
    private String FRAME_RATE_UNIT = "fps";
    private String PTS_UNIT = "ms";
    private String DURATION_UNIT = "ms";
    private String DELAY_UNIT = "ms";
    private Context mContext;

    public static class LogItem {
        public static final int CURRENT_CPU = 0;            //当前CPU
        public static final int CURRENT_MEMORY = 1;         //当前Memory
        public static final int VIDEO_CAPTURE_FPS = 2;       //视频采集帧率（实时帧率）
        public static final int AUDIO_ENCODE_BITRATE = 3;       //音频编码码率，单位kbps
        public static final int AUDIO_ENCODE_FPS = 4;           //音频编码帧率
        public static final int VIDEO_RENDER_FPS = 5;           //视频渲染帧率
        public static final int VIDEO_ENCODE_MODE = 6;          //视频编码模式
        public static final int VIDEO_ENCODE_BITRATE = 7;       //频编码码率,单位kbps
        public static final int VIDEO_ENCODE_FPS = 8;           //视频编码帧率（实时帧率）
        public static final int TOTALFRAME_ENCODED_VIDEO = 9;   //视频编码总帧数
        public static final int TOTALTIME_ENCODED_VODEO= 10;     //视频编码总耗时
        public static final int VIDEO_ENCODE_PARAM = 11;        //视频编码器设置参数
        public static final int AUDIO_UPLOAD_BITRATE = 12;      //音频输出码率，单位kbps
        public static final int VIDEO_UPLOAD_BITRATE = 13;      //视频输出码率，单位kbps
        public static final int AUDIO_PACKETS_INBUFFER = 14;    //缓存的音频帧总数
        public static final int VIDEO_PACKETS_INBUFFER = 15;    //缓存的视频帧总数
        public static final int VIDEO_UPLOAD_FPS = 16;          //视频上传帧率
        public static final int AUDIO_UPLOAD_FPS = 17;          //音频上传帧率
        public static final int CURRENTLY_UPLOADED_VIDEOFRAME_PTS = 18; //当前上传视频帧PTS
        public static final int CURRENTLY_UPLOADED_AUDIOFRAME_PTS = 19; //当前上传音频帧PTS
        public static final int PREVIOUS_VIDEO_KEYFRAME_PTS = 20;   //上一个视频关键帧PTS
        public static final int LASTVIDEO_PTS_INBUFFER = 21;        //缓冲区中最后一帧视频
        public static final int LASTAUDIO_PTS_INBUFFER = 22;        //缓冲区中最后一帧音频
        public static final int TOTALSIZE_UPLOADED_PACKETS = 23;    //数据上传总大小
        public static final int TOTALTIME_UPLOADING = 24;           //视频推流总耗时
        public static final int TOTALFRAMES_UPLOADED_VIDEO = 25;    //当前视频流已发送总帧数
        public static final int DROPDURATION_VIDEO_FRAMES = 26;     //视频丢帧总数
        public static final int TOTALTIMES_DROPING_VIDEOFRAMES = 27;//视频丢帧次数
        public static final int TOTALTIMES_DISCONNET = 28;  //总的断网次数
        public static final int TOTALTIMES_RECONNECT = 29;  //总的重连次数
        public static final int VIDEODURATION_FROMECAPTURE_TOUPLOAD = 30;   //视频延迟时长，当前last packet pts - cur capture pts，单位毫秒
        public static final int AUDIODURATION_FROMECAPTURE_TOUPLOAD = 31;   //音频延迟时长，当前last packet pts - cur capture pts，单位毫秒
        public static final int CURRENT_UPLOAD_PACKETSIZE = 32;//当前上传帧大小 单位byte
        public static final int MAXSIZE_VIDEOPACKETS_INBUFFER = 33; //缓冲队列中曾经最大的视频帧size
        public static final int MAXSIZE_AUDIOPACKETS_INBUFFER = 34; //缓冲队列中曾经最大的音频帧size
        public static final int LASTVIDEOFRAME_PTS_INQUEUE = 35;    //待发送队列中最后一个视频包的pts
        public static final int LASTAUDIOFRAME_PTS_INQUEUE = 36;    //带发送队列中最后一个音频包的pts
        public static final int AV_PTS_INTERVAL = 37;   //当前发送的音视频包pts diff
        public static final int AUDIOFRAMES_INENCODERBUFFER = 38; //音频编码器队列中帧缓存数
        public static final int VIDEOFRAMES_INENCODERBUFFER = 39; //视频编码器队列中帧缓存数
        public static final int VIDEOFRAMES_INRENDERBUFFER = 40;  //渲染队列中帧缓存数
        public static final int VIDEORENDER_TIMEPREFRAME = 41;    //每帧平均渲染时长，单位ms
//        public static final int AUDIO_CAPTURE_FPS = 42;       //音频采集帧率
//        public static final int AUDIO_VIDEO_PTSDIFF = 43;       //音视频pts差异
//        public static final int TOTALSENDED_PACKETSIZE_INTWOSECOND = 44;    //两秒内上传数据总大小


    }

    SparseArray<LogInfo> mLogInfos = new SparseArray<>();
    private String[] LABELS = null;

    public LogInfoAdapter(Context context) {
        this.mContext = context;
        loadLabels(context);
    }

    @Override
    public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_log_info, parent, false);
        LogViewHolder holder = new LogViewHolder(itemView);
        holder.tvLabel = (TextView) itemView.findViewById(R.id.tv_label);
        holder.tvValue = (TextView) itemView.findViewById(R.id.tv_value);
        return holder;
    }

    @Override
    public void onBindViewHolder(LogViewHolder holder, int position) {
        LogInfo logInfo = mLogInfos.get(position);
        if (logInfo != null) {
            holder.tvLabel.setText(logInfo.getLabel());
            holder.tvValue.setText(logInfo.getValue());
        }else {
            holder.tvLabel.setText(LABELS[position]);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 更新value
     * @param key
     * @param value
     */
    private void updateValue(int key, String value) {
        LogInfo logInfo = mLogInfos.get(key);
        if(logInfo == null) {
            logInfo = new LogInfo(LABELS[key], value);
            mLogInfos.put(key, logInfo);
        }else {
            logInfo.setValue(value);
        }
    }

    public void updateValue(AlivcLivePushStatsInfo alivcLivePushStatsInfo) {

        if(alivcLivePushStatsInfo == null) {
            return;
        }
        updateValue(LogItem.CURRENT_CPU, String.format("%.2f", alivcLivePushStatsInfo.getCpu()) + "%");
        updateValue(LogItem.CURRENT_MEMORY, String.format("%.2f", alivcLivePushStatsInfo.getMemory()) + "MB");
        updateValue(LogItem.VIDEO_CAPTURE_FPS, String.valueOf(alivcLivePushStatsInfo.getVideoCaptureFps()));
//        updateValue(LogItem.AUDIO_CAPTURE_FPS, String.valueOf(alivcLivePushStatsInfo.getAudioCaptureFps()));
        updateValue(LogItem.AUDIO_ENCODE_BITRATE, String.valueOf(alivcLivePushStatsInfo.getAudioEncodeBitrate()) + BITRATE_UNIT);
        updateValue(LogItem.AUDIO_ENCODE_FPS, String.valueOf(alivcLivePushStatsInfo.getAudioEncodeFps()));
        updateValue(LogItem.VIDEO_RENDER_FPS, String.valueOf(alivcLivePushStatsInfo.getVideoRenderFps()));
        updateValue(LogItem.VIDEO_ENCODE_MODE, alivcLivePushStatsInfo.getVideoEncodeMode() == AlivcEncodeModeEnum.Encode_MODE_HARD ? mContext.getString(R.string.hardware_encode) : mContext.getString(R.string.software_encode));
        updateValue(LogItem.VIDEO_ENCODE_BITRATE, String.valueOf(alivcLivePushStatsInfo.getVideoEncodeBitrate()) + BITRATE_UNIT);
        updateValue(LogItem.VIDEO_ENCODE_FPS, String.valueOf(alivcLivePushStatsInfo.getVideoEncodeFps()));
        updateValue(LogItem.TOTALFRAME_ENCODED_VIDEO, String.valueOf(alivcLivePushStatsInfo.getTotalFramesOfEncodedVideo()));
        updateValue(LogItem.TOTALTIME_ENCODED_VODEO, String.valueOf(alivcLivePushStatsInfo.getTotalTimeOfEncodedVideo()));
        updateValue(LogItem.VIDEO_ENCODE_PARAM, String.valueOf(alivcLivePushStatsInfo.getVideoEncodeParam()/1000) + BITRATE_UNIT);
        updateValue(LogItem.AUDIO_UPLOAD_BITRATE, String.valueOf(alivcLivePushStatsInfo.getAudioUploadBitrate()) + BITRATE_UNIT);
        updateValue(LogItem.VIDEO_UPLOAD_BITRATE, String.valueOf(alivcLivePushStatsInfo.getVideoUploadBitrate()) + BITRATE_UNIT);
        updateValue(LogItem.AUDIO_PACKETS_INBUFFER, String.valueOf(alivcLivePushStatsInfo.getAudioPacketsInUploadBuffer()));
        updateValue(LogItem.VIDEO_PACKETS_INBUFFER, String.valueOf(alivcLivePushStatsInfo.getVideoPacketsInUploadBuffer()));
        updateValue(LogItem.VIDEO_UPLOAD_FPS, String.valueOf(alivcLivePushStatsInfo.getVideoUploadeFps()));
        updateValue(LogItem.AUDIO_UPLOAD_FPS, String.valueOf(alivcLivePushStatsInfo.getAudioUploadFps()));
        updateValue(LogItem.CURRENTLY_UPLOADED_VIDEOFRAME_PTS, String.valueOf(alivcLivePushStatsInfo.getCurrentlyUploadedVideoFramePts()));
        updateValue(LogItem.CURRENTLY_UPLOADED_AUDIOFRAME_PTS, String.valueOf(alivcLivePushStatsInfo.getCurrentlyUploadedAudioFramePts()));
        updateValue(LogItem.PREVIOUS_VIDEO_KEYFRAME_PTS, String.valueOf(alivcLivePushStatsInfo.getPreviousVideoKeyFramePts()));
        updateValue(LogItem.LASTVIDEO_PTS_INBUFFER, String.valueOf(alivcLivePushStatsInfo.getLastVideoPtsInBuffer()));
        updateValue(LogItem.LASTAUDIO_PTS_INBUFFER, String.valueOf(alivcLivePushStatsInfo.getLastAudioPtsInBuffer()));
        updateValue(LogItem.TOTALSIZE_UPLOADED_PACKETS, String.valueOf(alivcLivePushStatsInfo.getTotalSizeOfUploadedPackets()));
        updateValue(LogItem.TOTALTIME_UPLOADING, String.valueOf(alivcLivePushStatsInfo.getTotalTimeOfUploading()));
        updateValue(LogItem.TOTALFRAMES_UPLOADED_VIDEO, String.valueOf(alivcLivePushStatsInfo.getTotalFramesOfUploadedVideo()));
        updateValue(LogItem.DROPDURATION_VIDEO_FRAMES, String.valueOf(alivcLivePushStatsInfo.getTotalDurationOfDropingVideoFrames()));
        updateValue(LogItem.TOTALTIMES_DROPING_VIDEOFRAMES, String.valueOf(alivcLivePushStatsInfo.getTotalTimesOfDropingVideoFrames()));
        updateValue(LogItem.TOTALTIMES_DISCONNET, String.valueOf(alivcLivePushStatsInfo.getTotalTimesOfDisconnect()));
        updateValue(LogItem.TOTALTIMES_RECONNECT, String.valueOf(alivcLivePushStatsInfo.getTotalTimesOfReconnect()));
        updateValue(LogItem.VIDEODURATION_FROMECAPTURE_TOUPLOAD, String.valueOf(alivcLivePushStatsInfo.getVideoDurationFromeCaptureToUpload()) + DURATION_UNIT);
        updateValue(LogItem.AUDIODURATION_FROMECAPTURE_TOUPLOAD, String.valueOf(alivcLivePushStatsInfo.getAudioDurationFromeCaptureToUpload()) + DURATION_UNIT);
        updateValue(LogItem.CURRENT_UPLOAD_PACKETSIZE, String.valueOf(alivcLivePushStatsInfo.getCurrentUploadPacketSize()));
//        updateValue(LogItem.AUDIO_VIDEO_PTSDIFF, String.valueOf(alivcLivePushStatsInfo.getAudioVideoPtsDiff()));
//        updateValue(LogItem.TOTALSENDED_PACKETSIZE_INTWOSECOND, String.valueOf(alivcLivePushStatsInfo.getTotalSendedPacketSizeInTwoSecond()));
        updateValue(LogItem.MAXSIZE_VIDEOPACKETS_INBUFFER, String.valueOf(alivcLivePushStatsInfo.getMaxSizeOfVideoPacketsInBuffer()));
        updateValue(LogItem.MAXSIZE_AUDIOPACKETS_INBUFFER, String.valueOf(alivcLivePushStatsInfo.getMaxSizeOfAudioPacketsInBuffer()));
        updateValue(LogItem.LASTVIDEOFRAME_PTS_INQUEUE, String.valueOf(alivcLivePushStatsInfo.getLastVideoFramePTSInQueue()));
        updateValue(LogItem.LASTAUDIOFRAME_PTS_INQUEUE, String.valueOf(alivcLivePushStatsInfo.getLastAudioFramePTSInQueue()));
        updateValue(LogItem.AV_PTS_INTERVAL, String.valueOf(alivcLivePushStatsInfo.getAvPTSInterval()));
        updateValue(LogItem.AUDIOFRAMES_INENCODERBUFFER, String.valueOf(alivcLivePushStatsInfo.getAudioFrameInEncodeBuffer()));
        updateValue(LogItem.VIDEOFRAMES_INENCODERBUFFER, String.valueOf(alivcLivePushStatsInfo.getVideoFramesInEncodeBuffer()));
        updateValue(LogItem.VIDEOFRAMES_INRENDERBUFFER, String.valueOf(alivcLivePushStatsInfo.getVideoFramesInRenderBuffer()));
        updateValue(LogItem.VIDEORENDER_TIMEPREFRAME, String.valueOf(alivcLivePushStatsInfo.getVideoRenderConsumingTimePerFrame()) + DURATION_UNIT);
        notifyDataSetChanged();
    }

    /**
     * 加载Labels
     * @param context
     */
    public void loadLabels(Context context) {
        LABELS = context.getResources().getStringArray(R.array.log_labels);
    }


    @Override
    public int getItemCount() {
        return LABELS.length;
    }

    class LogViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel;
        TextView tvValue;

        public LogViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class LogInfo {
        private String mLabel;
        private String mValue;

        public LogInfo(String label, String value) {
            mLabel = label;
            mValue = value;
        }

        public String getLabel() {
            return mLabel;
        }

        public void setLabel(String label) {
            mLabel = label;
        }

        public String getValue() {
            return mValue;
        }

        public void setValue(String value) {
            mValue = value;
        }
    }
}
