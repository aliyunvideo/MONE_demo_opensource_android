package com.alivc.live.pusher.widget;

import android.annotation.SuppressLint;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.alivc.live.pusher.demo.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ButtonListAdapter extends RecyclerView.Adapter {
    private ButtonClickListener clickListener;
    private final boolean isItemHide = false;

    public void setClickListener(ButtonClickListener clickListener) {
        this.clickListener = clickListener;
        mButtonEnableMap = new HashMap<>();
    }

    private final List<String> mListDatas = new ArrayList<>();
    private Map<String,Boolean> mButtonEnableMap;
    public void setData(List<String> data){

        if(data == null){
            return;
        }
        mListDatas.clear();
        mListDatas.addAll(data);
        notifyDataSetChanged();
    }

    public List<String> getData(){
        return mListDatas;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_recycle_button_item_view, parent, false);
        ButtonHolder buttonHolder = new ButtonHolder(view);
        return buttonHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
//        position 从 0开始
        ButtonHolder viewHolder= (ButtonHolder) holder;
        viewHolder.textView.setText(mListDatas.get(position));
        if(isItemHide && position < mListDatas.size()-1){
            viewHolder.textView.setClickable(false);
            viewHolder.textView.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.textView.setClickable(true);
            viewHolder.textView.setVisibility(View.VISIBLE);
        }
        if(TextUtils.isEmpty(mListDatas.get(position))){
            viewHolder.textView.setClickable(false);
            viewHolder.textView.setVisibility(View.INVISIBLE);
        }
        viewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickListener != null){
                    clickListener.onButtonClick(mListDatas.get(position),position);
                }
            }
        });
        boolean enable = true;
        if (mButtonEnableMap.containsKey(mListDatas.get(position))){
            enable = mButtonEnableMap.get(mListDatas.get(position));
        }
        viewHolder.textView.setEnabled(enable);
    }
    @Override
    public int getItemCount() {
        return mListDatas.size();
    }

    private class ButtonHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        public ButtonHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.vip_item_text);
        }
    }
}


