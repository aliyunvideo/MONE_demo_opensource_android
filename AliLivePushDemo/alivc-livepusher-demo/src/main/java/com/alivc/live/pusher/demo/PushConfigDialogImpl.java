package com.alivc.live.pusher.demo;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alivc.live.pusher.widget.PushConfigBottomSheet;

import java.util.Arrays;
import java.util.List;

public class PushConfigDialogImpl {
    private PushConfigBottomSheet mDialog;


    @Nullable
    private List<String> getTips(Context context, int id) {
        if (id == R.id.quality_modes) {
            return Arrays.asList(context.getResources().getString(R.string.quality_resolution_first),
                    context.getResources().getString(R.string.quality_fluency_first),
                    context.getResources().getString(R.string.quality_custom));
        } else if (id == R.id.audio_channel) {
            return Arrays.asList(context.getResources().getString(R.string.single_track), context.getResources().getString(R.string.dual_track));
        } else if (id == R.id.audio_profiles) {
            return Arrays.asList(context.getResources().getString(R.string.setting_audio_aac_lc),
                    context.getResources().getString(R.string.setting_audio_aac_he),
                    context.getResources().getString(R.string.setting_audio_aac_hev2),
                    context.getResources().getString(R.string.setting_audio_aac_ld));
        } else if (id == R.id.video_encoder_type) {
            return Arrays.asList(context.getResources().getString(R.string.h264),
                    context.getResources().getString(R.string.h265));
        }else if (id == R.id.b_frame_num){
            return Arrays.asList("0","1","3");
        }else if (id == R.id.main_orientation){
            return Arrays.asList(context.getResources().getString(R.string.portrait),context.getResources().getString(R.string.homeLeft),context.getResources().getString(R.string.homeRight));
        }else if (id == R.id.setting_display_mode){
            return Arrays.asList(context.getResources().getString(R.string.display_mode_full),context.getResources().getString(R.string.display_mode_fit),context.getResources().getString(R.string.display_mode_cut));
        }else if (id == R.id.push_mode){
            return Arrays.asList(context.getResources().getString(R.string.video_push_streaming),context.getResources().getString(R.string.audio_only_push_streaming),context.getResources().getString(R.string.video_only_push_streaming));
        }
        return null;
    }


    public void showConfigDialog(View view, PushConfigBottomSheet.OnPushConfigSelectorListener configSelectorListener, int defaultPosition) {
        int selectPosition = defaultPosition;
        List<String> tips = getTips(view.getContext(), view.getId());
        if (tips == null || defaultPosition >= tips.size()) {
            return;
        }


        if (mDialog == null) {
            mDialog = new PushConfigBottomSheet(view.getContext(), R.style.AIO_BottomSheetDialog);
        }
        Object lastIndex = view.getTag();
        if (lastIndex instanceof Integer) {
            selectPosition = (int) lastIndex;
        }
        mDialog.setData(tips, selectPosition);
        mDialog.setOnPushConfigSelectorListener(new PushConfigBottomSheet.OnPushConfigSelectorListener() {
            @Override
            public void confirm(String data, int index) {
                view.setTag(index);
                if (view instanceof TextView) {
                    ((TextView) view).setText(data);
                }
                if (configSelectorListener != null) {
                    configSelectorListener.confirm(data, index);
                }
            }
        });
        mDialog.show();
    }


    public void destroy() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

}
