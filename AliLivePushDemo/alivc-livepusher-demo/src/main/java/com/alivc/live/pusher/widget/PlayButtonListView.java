package com.alivc.live.pusher.widget;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
 * @author xlx
 */
public class PlayButtonListView extends FrameLayout {
    private RecyclerView mRecyclerView;
    private PlayButtonListAdapter mButtonListAdapter;
    private ButtonClickListener clickListener;
    private boolean isItemsHide = false;

    public void setClickListener(ButtonClickListener clickListener) {
        this.clickListener = clickListener;
    }


    public PlayButtonListView(@NonNull Context context) {
        this(context, null);
    }

    public PlayButtonListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayButtonListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setData(List<String> data) {
        mButtonListAdapter.setData(data);
    }

    public void hideItems(boolean isItemHide){
        this.isItemsHide = isItemHide;
        mButtonListAdapter.hideItems(isItemHide);
        if(isItemHide) {
            this.setVisibility(INVISIBLE);
        }else{
            this.setVisibility(VISIBLE);
        }
    }
    public boolean isItemsHide(){
        return  isItemsHide;
    }
    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.live_button_list, this, true);
        initRecyclerView(view);
    }

    private void initRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.live_button_recycle);
        mButtonListAdapter = new PlayButtonListAdapter();
        LinearLayoutManager manager=new LinearLayoutManager(PlayButtonListView.this.getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mButtonListAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecortation(DensityUtil.dip2px(getContext(),28),getContext()));
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
    public void setButtonEnable(String buttonName,boolean enable){
        if (mButtonListAdapter!=null){
           mButtonListAdapter.setButtonEnable(buttonName,enable);
        }
    }
    public void changeButtonName(String oldName,String newName){
        if (mButtonListAdapter!=null){
//            mButtonListAdapter.changeButtonName(oldName,newName);
        }
    }

}
