package com.alivc.live.pusher.widget;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import com.alivc.live.pusher.demo.R;

import java.util.List;

/**
 * 底部按钮
 */
public class ButtonListView extends FrameLayout {
    private RecyclerView mRecyclerView;
    private ButtonListAdapter mButtonListAdapter;
    private ButtonClickListener clickListener;

    public void setClickListener(ButtonClickListener clickListener) {
        this.clickListener = clickListener;
    }


    public ButtonListView(Context context) {
        this(context, null);
    }

    public ButtonListView(Context context,AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ButtonListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setData(List<String> data) {
        mButtonListAdapter.setData(data);
    }
    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.live_button_list, this, true);
        initRecyclerView(view);
    }

    private void initRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.live_button_recycle);
        mButtonListAdapter = new ButtonListAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ButtonListView.this.getContext()));
        mRecyclerView.setAdapter(mButtonListAdapter);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mButtonListAdapter.setClickListener(new ButtonClickListener() {
            @Override
            public void onButtonClick(String message, int position) {
                if(clickListener != null){
                    clickListener.onButtonClick(message,position);
                }
            }
        });
    }
}
