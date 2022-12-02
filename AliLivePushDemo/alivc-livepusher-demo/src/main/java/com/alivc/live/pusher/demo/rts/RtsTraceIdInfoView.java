package com.alivc.live.pusher.demo.rts;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import com.alivc.live.pusher.demo.R;
import com.aliyun.aio.avbaseui.widget.AVToast;
import com.aliyun.interactive_common.widget.AUILiveDialog;

public class RtsTraceIdInfoView extends ConstraintLayout {

    private final View inflate;
    private Group mGroup;
    private TextView mTipsTextView;
    private TextView mTraceIdTextView;
    private TextView mCopyTextView;
    private TextView mCancelTextView;
    private AUILiveDialog mAUILiveDialog;
    private final ClipboardManager mClipboardManager;
    private String mUrl;
    private String mTraceId;

    public RtsTraceIdInfoView(@NonNull Context context) {
        this(context, null);
    }

    public RtsTraceIdInfoView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public RtsTraceIdInfoView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate = LayoutInflater.from(context).inflate(R.layout.layout_rts_traceid_info, this, true);
        mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        initView();
        initListener();
    }

    private void initView() {
        mAUILiveDialog = new AUILiveDialog(getContext());
        mAUILiveDialog.setContentView(this);
        mGroup = inflate.findViewById(R.id.group);
        mTipsTextView = inflate.findViewById(R.id.tv_tips);
        mTraceIdTextView = inflate.findViewById(R.id.tv_trace_id);
        mCopyTextView = inflate.findViewById(R.id.tv_copy);
        mCancelTextView = inflate.findViewById(R.id.tv_cancel);
    }

    private void initListener() {
        mCancelTextView.setOnClickListener(view -> mAUILiveDialog.dismiss());
        mCopyTextView.setOnClickListener(view -> {
            mAUILiveDialog.dismiss();
            //复制
            ClipData mClipData = ClipData.newPlainText("Label", mTraceId + ";" + mUrl);
            mClipboardManager.setPrimaryClip(mClipData);

            AVToast.show(getContext(), true, R.string.pull_rts_trace_id_info_copy_success);
        });
    }

    public void setTraceId(String traceId) {
        if (TextUtils.isEmpty(traceId)) {
            mGroup.setVisibility(View.GONE);
            mTipsTextView.setText(R.string.pull_rts_trace_id_info_error);
            mCancelTextView.setText(R.string.pull_rts_trace_id_info_confirm);
            mTraceIdTextView.setText("");
        } else {
            mGroup.setVisibility(View.VISIBLE);
            mTipsTextView.setText(R.string.pull_rts_trace_id_info_success);
            mCancelTextView.setText(R.string.pull_rts_trace_id_info_confirm);
            mTraceIdTextView.setText(traceId);
        }
        this.mTraceId = traceId;
        mAUILiveDialog.show();
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }
}
