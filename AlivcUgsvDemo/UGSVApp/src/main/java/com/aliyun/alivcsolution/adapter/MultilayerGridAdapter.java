package com.aliyun.alivcsolution.adapter;

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.alivcsolution.R;
import com.aliyun.alivcsolution.model.ScenesModel;
import com.aliyun.common.global.Version;

/**
 * Created by Mulberry on 2018/4/11.
 */

public class MultilayerGridAdapter extends BaseAdapter {

    private List<ScenesModel> listData;
    private LayoutInflater inflater;
    private WeakReference<Context> context;
    private int mIndex;//页数下标，表示第几页，从0开始
    private int mPagerSize;//每页显示的最大数量

    public MultilayerGridAdapter(Context context, List<ScenesModel> listData, int mIndex, int mPagerSize) {
        this.context = new WeakReference<>(context);
        this.listData = listData;
        this.mIndex = mIndex;
        this.mPagerSize = mPagerSize;
        inflater = LayoutInflater.from(this.context.get());
    }

    /**
     * 先判断数据集的大小是否足够显示满本页？listData.size() > (mIndex + 1)*mPagerSize
     * 如果满足，则此页就显示最大数量mPagerSize的个数
     * 如果不够显示每页的最大数量，那么剩下几个就显示几个 (listData.size() - mIndex*mPagerSize)
     */
    @Override
    public int getCount() {
        return listData.size() > (mIndex + 1) * mPagerSize ? mPagerSize : (listData.size() - mIndex * mPagerSize);
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position + mIndex * mPagerSize);
    }

    @Override
    public long getItemId(int position) {
        return position + mIndex * mPagerSize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.alivc_home_item_layout, parent, false);
            holder = new ViewHolder();
            holder.proName = (TextView) convertView.findViewById(R.id.tv_item_title);
            holder.imgUrl = (ImageView) convertView.findViewById(R.id.iv_item_image);
            int width = (ScreenUtils.getWidth(context.get()) - 100) / 2;
            int height = width *  2 / 3 ;
            //item的layoutparams用GridView.LayoutParams或者  AbsListView.LayoutParams设置，不能用LinearLayout.LayoutParams
            //convertView.setLayoutParams(new    GridView.LayoutParams(width,height));
            convertView.setLayoutParams(new AbsListView.LayoutParams(width, height) );
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //重新确定position（因为拿到的是总的数据源，数据源是分页加载到每页的GridView上的，为了确保能正确的点对不同页上的item）
        final int pos = position + mIndex * mPagerSize; //假设mPagerSize=8，假如点击的是第二页（即mIndex=1）上的第二个位置item(position=1),那么这个item的实际位置就是pos=9
        ScenesModel scenesModel = listData.get(pos);
        holder.proName.setText(scenesModel.getName());
        holder.imgUrl.setImageResource(scenesModel.getImgUrl());

        if ("svideo_pro".equals(Version.MODULE) && convertView.getContext().getString(R.string.solution_recorder).equals(scenesModel.getName())) {
            //专业版录制添加race hot 提示
            convertView.findViewById(R.id.rl_item_hint).setVisibility(View.VISIBLE);
            ((ImageView)convertView.findViewById(R.id.iv_item_hint)).setImageResource(R.mipmap.icon_hot);
            ((TextView)convertView.findViewById(R.id.tv_item_hint)).setText(R.string.alivc_recorder_queen_hint);
        }

        return convertView;
    }

    class ViewHolder {
        private TextView proName;
        private ImageView imgUrl;
    }
}
