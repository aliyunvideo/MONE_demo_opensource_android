package com.alivc.live.baselive_common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * 自动追加滚动消息视图适配器
 */
public class AutoScrollMessagesAdapter extends RecyclerView.Adapter<AutoScrollMessagesAdapter.RecyclerViewHolder> {
    private @NonNull
    ArrayList<String> msgList = new ArrayList<String>();

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_callback_message, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.applyTextMsg(msgList.get(position));
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public void applyDataMessage(String msg) {
        msgList.add(msg);
        notifyDataSetChanged();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.text_msg);
        }

        public void applyTextMsg(String msg) {
            if (mTextView != null) {
                mTextView.setText(msg);
            }
        }
    }
}
