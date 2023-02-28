package com.alivc.live.pusher.demo;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alivc.live.pusher.AlivcLivePushStatsInfo;

public class PushDiagramStatsFragment extends Fragment {

    public static final String TAG = "PushDiagramStatsFragment";

    private TextView mAudioEncodeText;
    private ProgressBar mAudioEncodeProgress;
    private TextView mAudioPushText;
    private ProgressBar mAudioPushProgress;
    private TextView mVideoCaptureText;
    private ProgressBar mVideoCaptureProgress;
    private TextView mVideoRenderText;
    private ProgressBar mVideoRenderProgress;
    private TextView mVideoEncodeText;
    private ProgressBar mVideoEncodeProgress;
    private TextView mVideoPushText;
    private ProgressBar mVideoPushProgress;
    private TextView mBitAudioEncodeText;
    private ProgressBar mBitAudioEncodeProgress;
    private TextView mBitVideoEncodeText;
    private ProgressBar mBitVideoEncodeProgress;
    private TextView mBitPushText;
    private ProgressBar mBitPushProgress;
    private TextView mVideoRenderBufferText;
    private ProgressBar mVideoRenderBufferProgress;
    private TextView mVideoEncodeBufferText;
    private ProgressBar mVideoEncodeBufferProgress;
    private TextView mVideoUploadBufferText;
    private ProgressBar mVideoUploadBufferProgress;
    private TextView mAudioEncodeBufferText;
    private ProgressBar mAudioEncodeBufferProgress;
    private TextView mAudioUploadBufferText;
    private ProgressBar mAudioUploadBufferProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.push_diagram_log, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAudioEncodeText = (TextView) view.findViewById(R.id.audio_encode_fps_text);
        mAudioEncodeProgress = (ProgressBar) view.findViewById(R.id.audio_encode_fps_bar);
        mAudioPushText = (TextView) view.findViewById(R.id.audio_push_fps_text);
        mAudioPushProgress = (ProgressBar) view.findViewById(R.id.audio_push_fps_bar);
        mVideoCaptureText = (TextView) view.findViewById(R.id.video_capture_fps_text);
        mVideoCaptureProgress = (ProgressBar) view.findViewById(R.id.video_capture_fps_bar);
        mVideoRenderText = (TextView) view.findViewById(R.id.video_render_fps_text);
        mVideoRenderProgress = (ProgressBar) view.findViewById(R.id.video_render_fps_bar);
        mVideoEncodeText = (TextView) view.findViewById(R.id.video_encode_fps_text);
        mVideoEncodeProgress = (ProgressBar) view.findViewById(R.id.video_encode_fps_bar);
        mVideoPushText = (TextView) view.findViewById(R.id.video_push_fps_text);
        mVideoPushProgress = (ProgressBar) view.findViewById(R.id.video_push_fps_bar);
        mBitAudioEncodeText = (TextView) view.findViewById(R.id.bit_audio_encode_text);
        mBitAudioEncodeProgress = (ProgressBar) view.findViewById(R.id.bit_audio_encode_bar);
        mBitVideoEncodeText = (TextView) view.findViewById(R.id.bit_video_encode_text);
        mBitVideoEncodeProgress = (ProgressBar) view.findViewById(R.id.bit_video_encode_bar);
        mBitPushText = (TextView) view.findViewById(R.id.bit_push_text);
        mBitPushProgress = (ProgressBar) view.findViewById(R.id.bit_push_bar);
        mVideoRenderBufferText = (TextView) view.findViewById(R.id.video_renderbuffer_text);
        mVideoRenderBufferProgress = (ProgressBar) view.findViewById(R.id.video_renderbuffer_bar);
        mVideoEncodeBufferText = (TextView) view.findViewById(R.id.video_encodebuffer_text);
        mVideoEncodeBufferProgress = (ProgressBar) view.findViewById(R.id.video_encodebuffer_bar);
        mVideoUploadBufferText = (TextView) view.findViewById(R.id.video_uploadbuffer_text);
        mVideoUploadBufferProgress = (ProgressBar) view.findViewById(R.id.video_uploadbuffer_bar);
        mAudioEncodeBufferText = (TextView) view.findViewById(R.id.audio_encodebuffer_text);
        mAudioEncodeBufferProgress = (ProgressBar) view.findViewById(R.id.audio_encodebuffer_bar);
        mAudioUploadBufferText = (TextView) view.findViewById(R.id.audio_uploadbuffer_text);
        mAudioUploadBufferProgress = (ProgressBar) view.findViewById(R.id.audio_uploadbuffer_bar);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void updateValue(AlivcLivePushStatsInfo alivcLivePushStatsInfo) {

        if(alivcLivePushStatsInfo != null) {
            int audioEncodeFps = alivcLivePushStatsInfo.getAudioEncodeFps();
            int audioPush = alivcLivePushStatsInfo.getAudioUploadFps();
            int videoCaptureFps = alivcLivePushStatsInfo.getVideoCaptureFps();
            int videoRender = alivcLivePushStatsInfo.getVideoRenderFps();
            int videoEncodeFps = alivcLivePushStatsInfo.getVideoEncodeFps();
            int videoPush = alivcLivePushStatsInfo.getVideoUploadeFps();
            int audioBit = alivcLivePushStatsInfo.getAudioEncodeBitrate();
            int videoBit = alivcLivePushStatsInfo.getVideoEncodeBitrate();
            int pushBit = alivcLivePushStatsInfo.getVideoUploadBitrate();
            int videoRenderBuffer = alivcLivePushStatsInfo.getVideoFramesInRenderBuffer();
            int videoEncodeBuffer = alivcLivePushStatsInfo.getVideoFramesInEncodeBuffer();
            int videoUploadBuffer = alivcLivePushStatsInfo.getVideoPacketsInUploadBuffer();
            int audioEncodeBuffer = alivcLivePushStatsInfo.getAudioFrameInEncodeBuffer();
            int audioUploadBuffer = alivcLivePushStatsInfo.getAudioPacketsInUploadBuffer();
            mAudioEncodeText.setText(String.valueOf(audioEncodeFps));
            mAudioEncodeProgress.setProgress(audioEncodeFps);
            mAudioPushText.setText(String.valueOf(audioPush));
            mAudioPushProgress.setProgress(audioPush);
            mVideoCaptureText.setText(String.valueOf(videoCaptureFps));
            mVideoCaptureProgress.setProgress(videoCaptureFps);
            mVideoRenderText.setText(String.valueOf(videoRender));
            mVideoRenderProgress.setProgress(videoRender);
            mVideoEncodeText.setText(String.valueOf(videoEncodeFps));
            mVideoEncodeProgress.setProgress(videoEncodeFps);
            mVideoPushText.setText(String.valueOf(videoPush));
            mVideoPushProgress.setProgress(videoPush);
            mBitAudioEncodeText.setText(String.valueOf(audioBit));
            mBitAudioEncodeProgress.setProgress(audioBit);
            mBitVideoEncodeText.setText(String.valueOf(videoBit));
            mBitVideoEncodeProgress.setProgress(videoBit);
            mBitPushText.setText(String.valueOf(pushBit));
            mBitPushProgress.setProgress(pushBit);
            mVideoRenderBufferText.setText(String.valueOf(videoRenderBuffer));
            mVideoRenderBufferProgress.setProgress(videoRenderBuffer);
            mVideoEncodeBufferText.setText(String.valueOf(videoEncodeBuffer));
            mVideoEncodeBufferProgress.setProgress(videoEncodeBuffer);
            mVideoUploadBufferText.setText(String.valueOf(videoUploadBuffer));
            mVideoUploadBufferProgress.setProgress(videoUploadBuffer);
            mAudioEncodeBufferText.setText(String.valueOf(audioEncodeBuffer));
            mAudioEncodeBufferProgress.setProgress(audioEncodeBuffer);
            mAudioUploadBufferText.setText(String.valueOf(audioUploadBuffer));
            mAudioUploadBufferProgress.setProgress(audioUploadBuffer);

        }

    }
}
