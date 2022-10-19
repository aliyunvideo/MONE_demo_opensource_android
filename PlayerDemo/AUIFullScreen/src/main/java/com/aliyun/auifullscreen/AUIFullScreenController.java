package com.aliyun.auifullscreen;

import com.aliyun.player.source.VidSts;

public class AUIFullScreenController {

    private AUIFullScreenActivity mActivity;
    private AUIFullScreenDao mDao;

    public AUIFullScreenController(AUIFullScreenActivity activity){
        this.mActivity = activity;
        mDao = new AUIFullScreenDao();
    }

    public void initData() {
        mActivity.showLoading(true);
        mDao.getStsDataSource(new AUIFullScreenDao.OnGetStsDataSourceListener() {
            @Override
            public void getStsDataSourceSuccess(VidSts vidSts) {
                mActivity.showLoading(false);
                mActivity.prepareDataSource(vidSts);
            }

            @Override
            public void getStsDataSourceFailure(String error) {
                mActivity.showLoading(false);
                mActivity.getDataSourceError(error);
            }
        });
    }
}
