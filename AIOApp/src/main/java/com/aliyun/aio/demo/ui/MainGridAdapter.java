package com.aliyun.aio.demo.ui;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.aio.demo.MainPageItem;
import com.aliyun.aio.demo.R;

import java.util.ArrayList;
import java.util.List;

public class MainGridAdapter extends BaseAdapter {

    private List<MainPageItem> mData = new ArrayList<>();
    private Context mContext;
    private GridItemClickListener mItemClickListener;

    public MainGridAdapter(Context context, GridItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    public void setData(List<MainPageItem> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public MainPageItem getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.aio_main_grid_item, null);
            holder = new ViewHolder(convertView);
            holder.title = convertView
                    .findViewById(R.id.aio_main_grid_title);
            holder.desc = convertView
                    .findViewById(R.id.aio_main_grid_dec);
            convertView.setTag(holder);
            holder.icon = convertView.findViewById(R.id.aio_main_grid_icon);
            holder.detailIcon = convertView.findViewById(R.id.aio_main_grid_detail);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 根据key值设置不同数据内容
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(mData.get(position));
            }
        });
        holder.title.setText(mData.get(position).title);
        holder.desc.setText(mData.get(position).dec);
        holder.icon.setImageResource(mData.get(position).iconId);
        holder.detailIcon.setImageResource(mData.get(position).detailIconId);

        return convertView;
    }

    class ViewHolder {
        View parent;
        TextView title;
        TextView desc;
        ImageView icon;
        ImageView detailIcon;
        public ViewHolder(View parent) {
            this.parent = parent;
        }
    }

    public interface GridItemClickListener {
        void onItemClick(MainPageItem item);
    }
}
