package com.aliyun.svideo.editor.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private List<T> items;
    private OnItemClickListener<T> itemClickListener;

    public void bindData(final List<T> items) {
        this.items = items;
        this.notifyDataSetChanged();
    }

    public final T getItem(final int position) {
        if (getItemCount() == 0) return null;
        return this.items.get(position);
    }

    public List<T> getItems() {
        return items;
    }

    public void addItem(T t) {
        checkItems();
        items.add(t);
        this.notifyItemInserted(getItemCount() - 1);
    }

    public void addItem(int position, T t) {
        checkItems();
        items.add(position, t);
        this.notifyItemInserted(position);
    }

    public void addItems(List<T> l) {
        checkItems();
        items.addAll(l);
        this.notifyItemRangeInserted(getItemCount() - l.size(), l.size());
    }

    public void remove(int position) {
        getItems().remove(position);
        notifyItemRemoved(position);
    }

    public void checkItems() {
        if (items == null)
            items = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return this.items != null ? this.items.size() : 0;
    }

    @Override
    public final void onBindViewHolder(final VH holder, final int position) {
        final T item = this.getItem(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(getItem(position), position);
                }
            }
        });
        this.onBindViewHolder(holder, item, position);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public OnItemClickListener<T> getItemClickListener() {
        return itemClickListener;
    }

    public abstract void onBindViewHolder(final VH holder, final T item, int position);

    public static abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void onBind(T item, int position);
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T t, int position);
    }
}