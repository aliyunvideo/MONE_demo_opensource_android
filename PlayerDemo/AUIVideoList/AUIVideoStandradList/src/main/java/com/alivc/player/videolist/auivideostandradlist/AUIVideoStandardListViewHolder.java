package com.alivc.player.videolist.auivideostandradlist;

import android.view.View;

import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListViewHolder;
import com.aliyun.player.AliPlayer;

public class AUIVideoStandardListViewHolder extends AUIVideoListViewHolder {

    public AUIVideoStandardListViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindUrl(String url) {

    }

    @Override
    public AliPlayer getAliPlayer() {
        return null;
    }
}
