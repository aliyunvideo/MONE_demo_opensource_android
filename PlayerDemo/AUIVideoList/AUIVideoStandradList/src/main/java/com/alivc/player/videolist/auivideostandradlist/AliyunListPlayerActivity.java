package com.alivc.player.videolist.auivideostandradlist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alivc.player.videolist.auivideostandradlist.R;

import java.lang.ref.WeakReference;

public class AliyunListPlayerActivity extends AppCompatActivity {

//    private AliyunListPlayerView mListPlayerView;
    private boolean mIsLoadMore = false;
    private int mLastVideoId = -1;
    private AUIVideoStandardListView mAUIVideoStandardListView;
    //    private ImageView mBackImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aliyun_list_player);

        initView();
//        initSts(true);
        initData();
        initListener();
    }

    private void initView() {
        mAUIVideoStandardListView = findViewById(R.id.aui_list_view);
//        mListPlayerView = findViewById(R.id.list_player_view);
//        mBackImageView = findViewById(R.id.iv_back);
    }

    private void refreshListDatas(){
//        initSts(false);
//        getDatas(mLastVideoId);
    }

    private void getDatas(){
    }

    private void initData() {
//        AUIVideoListLocalDataSource auiVideoListLocalDataSource = new AUIVideoListLocalDataSource(this);
//        LinkedList<ListVideoBean> videoList = auiVideoListLocalDataSource.getVideoList();
//        if (videoList != null) {
//            SparseArray<String> mSparseArray = new SparseArray<>();
//            //遍历资源,添加到列表播放器当中
//            for(int i = 0 ;i < videoList.size();i++){
//                String randomUUID = UUID.randomUUID().toString();
//                mListPlayerView.addUrl(videoList.get(i).getUrl(),randomUUID);
//                mSparseArray.put(i,randomUUID);
//            }
//            mListPlayerView.setData(videoList);
//            mListPlayerView.setCorrelationTable(mSparseArray);
//        }
//        AUIVideoListLocalDataSource auiVideoListLocalDataSource = new AUIVideoListLocalDataSource(this);
//        List<ListVideoBean> videoList = auiVideoListLocalDataSource.getVideoList();
//        mAUIVideoStandardListView.loadSources(videoList);

//        mAUIVideoStandardListView.setOnRefreshListener(new OnLoadDataListener() {
//            @Override
//            public void onRefresh() {
//                mAUIVideoStandardListView.loadSources(videoList);
//            }
//        });
    }

//    private void initSts(final boolean needLoadData) {
//        AlivcOkHttpClient.getInstance().get(ServiceCommon.GET_VIDEO_PLAY_STS, new AlivcOkHttpClient.HttpCallBack() {
//            @Override
//            public void onError(Request request, IOException e) {
//                ToastUtils.show(AliyunListPlayerActivity.this, e.getMessage());
//            }
//
//            @Override
//            public void onSuccess(Request request, String result) {
//                Gson gson = new Gson();
//                AliyunSts aliyunSts = gson.fromJson(result, AliyunSts.class);
//                if (aliyunSts != null && aliyunSts.getCode() == ServiceCommon.RESPONSE_SUCCESS) {
//                    if (mListPlayerView != null) {
//                        AliyunSts.StsBean data = aliyunSts.getData();
//                        StsInfo stsInfo = new StsInfo();
//                        stsInfo.setAccessKeyId(data.getAccessKeyId());
//                        stsInfo.setSecurityToken(data.getSecurityToken());
//                        stsInfo.setAccessKeySecret(data.getAccessKeySecret());
//                        mListPlayerView.setStsInfo(stsInfo);
//                        if(needLoadData){
//                            initData();
//                        }
//                    }
//
//                }
//            }
//        });
//    }

    private void initListener(){
//        mListPlayerView.setOnRefreshDataListener(new MyOnRefreshListener(this));
//        mBackImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if(mListPlayerView != null){
//            mListPlayerView.setOnBackground(false);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(mListPlayerView != null){
//            mListPlayerView.setOnBackground(true);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if(mListPlayerView != null){
//            mListPlayerView.setOnBackground(true);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(mListPlayerView != null){
//            mListPlayerView.destroy();
//        }
    }

    /**
     * 刷新数据监听
     */
    private static class MyOnRefreshListener implements AliyunListPlayerView.OnRefreshDataListener{

        private WeakReference<AliyunListPlayerActivity> weakReference;

        public MyOnRefreshListener(AliyunListPlayerActivity aliyunListPlayerActivity){
            weakReference = new WeakReference<>(aliyunListPlayerActivity);
        }

        @Override
        public void onRefresh() {
            AliyunListPlayerActivity aliyunListPlayerActivity = weakReference.get();
            if(aliyunListPlayerActivity != null){
                aliyunListPlayerActivity.onRefresh();
            }
        }

        @Override
        public void onLoadMore() {
            AliyunListPlayerActivity aliyunListPlayerActivity = weakReference.get();
            if(aliyunListPlayerActivity != null){
                aliyunListPlayerActivity.onLoadMore();
            }
        }
    }

    private void onRefresh(){
        mIsLoadMore = false;
        mLastVideoId = -1;
        refreshListDatas();
    }

    private void onLoadMore(){
        mIsLoadMore = true;
//        getDatas(mLastVideoId);
    }
}
