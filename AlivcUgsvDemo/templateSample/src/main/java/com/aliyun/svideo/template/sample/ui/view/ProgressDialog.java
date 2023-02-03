package com.aliyun.svideo.template.sample.ui.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.aliyun.svideo.template.sample.R;

public class ProgressDialog extends AppCompatDialogFragment {

    private TextView mProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.progress_dialog);
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.progress_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mProgress = view.findViewById(R.id.progress);
    }

    public void setProgress(int progress) {
        if (mProgress.getVisibility() != View.VISIBLE) {
            mProgress.setVisibility(View.VISIBLE);
        }
        mProgress.setText(progress + "%");
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mProgress.setVisibility(View.GONE);
        super.onDismiss(dialog);
    }
}
