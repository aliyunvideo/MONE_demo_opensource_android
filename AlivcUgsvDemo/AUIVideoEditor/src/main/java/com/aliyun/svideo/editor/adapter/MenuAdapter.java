package com.aliyun.svideo.editor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.svideo.editor.R;
import com.aliyun.svideo.editor.bean.Menu;

/**
 * 菜单适配器
 */
public class MenuAdapter extends BaseRecyclerAdapter<Menu, RecyclerView.ViewHolder> {

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ugsv_editor_layout_editor_bottom_menu_item, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, Menu item, int position) {
        MenuViewHolder viewHolder = (MenuViewHolder) holder;
        viewHolder.icon.setImageResource(item.icon);
        viewHolder.title.setText(item.text);
    }

    private static class MenuViewHolder extends RecyclerView.ViewHolder {

        private final ImageView icon;
        private final TextView title;

        public MenuViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iv_icon);
            title = itemView.findViewById(R.id.tv_title);
        }
    }
}
