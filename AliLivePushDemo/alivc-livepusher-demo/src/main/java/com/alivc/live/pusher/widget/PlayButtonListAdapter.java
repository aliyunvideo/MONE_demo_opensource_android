package com.alivc.live.pusher.widget;

import android.annotation.SuppressLint;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alivc.live.pusher.demo.R;
import com.alivc.live.pusher.demo.bean.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayButtonListAdapter extends RecyclerView.Adapter {
    private ButtonClickListener clickListener;
    private boolean isItemHide = false;
    public void setClickListener(ButtonClickListener clickListener) {
        this.clickListener = clickListener;
        mButtonEnableMap = new HashMap<>();
    }

    private List<String> mListDatas = new ArrayList<>();
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
    public void hideItems(boolean isItemHide){
        this.isItemHide = isItemHide;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_button_item_view, parent, false);
        ButtonHolder buttonHolder = new ButtonHolder(view);
        return buttonHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
//        position 从 0开始
        ButtonHolder viewHolder= (ButtonHolder) holder;
        viewHolder.textView.setText(mListDatas.get(position));
        viewHolder.imageView.setImageResource(Constants.getLive_img_soure().get(mListDatas.get(position)));
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickListener!=null){
                    clickListener.onButtonClick(mListDatas.get(position),position);
                }
            }
        });
        boolean enable = true;
        if (mButtonEnableMap.containsKey(mListDatas.get(position))){
            enable = mButtonEnableMap.get(mListDatas.get(position));
        }
        if(!enable){
            if("开始推流".equalsIgnoreCase(mListDatas.get(position))){
                viewHolder.imageView.setImageResource(R.drawable.live_push_gray);
            }
            if("数据指标".equalsIgnoreCase(mListDatas.get(position))){
                viewHolder.imageView.setImageResource(R.drawable.live_data_gray);
            }
            if("麦克风".equalsIgnoreCase(mListDatas.get(position))){
                viewHolder.imageView.setImageResource(R.drawable.live_microphone_gray);
            }
            if("调节参数".equalsIgnoreCase(mListDatas.get(position))){
                viewHolder.imageView.setImageResource(R.drawable.live_adjust_gray);
            }
        }else {
            if("开始推流".equalsIgnoreCase(mListDatas.get(position))){
                viewHolder.imageView.setImageResource(R.drawable.live_push);
            }
            if("麦克风".equalsIgnoreCase(mListDatas.get(position))){
                viewHolder.imageView.setImageResource(R.drawable.live_microphone);
            }
            if("数据指标".equalsIgnoreCase(mListDatas.get(position))){
                viewHolder.imageView.setImageResource(R.drawable.live_data);
            }
        }
        viewHolder.imageView.setEnabled(enable);
        //针对数据指标做特殊处理
        if("数据指标".equalsIgnoreCase(mListDatas.get(position))){
            viewHolder.imageView.setEnabled(true);
        }
    }
    @Override
    public int getItemCount() {
        return mListDatas.size();
    }

    private class ButtonHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imageView;
        public ButtonHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_anchor_text);
            imageView=itemView.findViewById(R.id.iv_anchor);
        }
    }

    public void setButtonEnable(String buttonName,boolean enable){
        mButtonEnableMap.put(buttonName,enable);
        int position = -1;
        for (int i = 0; i < mListDatas.size(); i++) {
            if (mListDatas.get(i).equals(buttonName)){
                position = i;
            }
        }
        if (position>=0){
            notifyItemChanged(position);
        }
    }


}


