package com.aliyun.player.alivcplayerexpand.view.more;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.alivcplayerexpand.util.ThreadUtils;
import com.aliyun.player.alivcplayerexpand.view.dlna.DeviceSearchListener;
import com.aliyun.player.alivcplayerexpand.view.dlna.callback.DeviceListChangedListener;
import com.aliyun.player.alivcplayerexpand.view.dlna.callback.OnDeviceItemClickListener;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.ClingDevice;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.ClingDeviceList;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.IDevice;
import com.aliyun.player.alivcplayerexpand.view.dlna.manager.ClingManager;
import com.aliyun.player.alivcplayerexpand.view.dlna.manager.DeviceManager;
import com.aliyun.player.alivcplayerexpand.view.dlna.service.ClingUpnpService;

import org.fourthline.cling.model.meta.Device;

/**
 * 投屏
 *
 * @author hanyu
 */
public class ScreenCostView extends LinearLayout {

    private OnDeviceItemClickListener mOutDLNAOptionListener;

    /**
     * DLNA start
     */
    //发现设备监听
    private DeviceSearchListener mDeviceSearchListener = new DeviceSearchListener();
    private ClingManager clingUpnpServiceManager;
    //连接监听
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ClingUpnpService.LocalBinder binder = (ClingUpnpService.LocalBinder) iBinder;
            ClingUpnpService beyondUpnpService = binder.getService();
            clingUpnpServiceManager = ClingManager.getInstance();
            clingUpnpServiceManager.setUpnpService(beyondUpnpService);
            clingUpnpServiceManager.setDeviceManager(new DeviceManager());

            clingUpnpServiceManager.getRegistry().addListener(mDeviceSearchListener);
            //Search on service created.
            clingUpnpServiceManager.searchDevices();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            ClingManager.getInstance().setUpnpService(null);
        }
    };
    /**
     * DLNA end
     */

    private View view;
    /**
     * 刷新
     */
    private ImageView mRefreshImageView;
    /**
     * 设备列表
     */
    private RecyclerView mRecyclerView;
    private ScreenCostAdapter mScreenCostAdapter;
    /**
     * 刷新投屏设备动画
     */
    private ObjectAnimator mRefreshValueAnimator;
    private View mSearchStateParentView;
    private ImageView mSearchStateImageView;
    private TextView mSearchStateTextView;

    public ScreenCostView(Context context) {
        super(context);
        init(context);
    }

    public ScreenCostView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScreenCostView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.alivc_dialog_screen_cost, this, true);

        initView();
        initRecyclerView();
        initListener();
        initService();
        initAnimation();
    }

    private void initAnimation() {
        mRefreshValueAnimator = ObjectAnimator.ofFloat(mRefreshImageView, "Rotation", 0f, 90f, 180f, 360f);
        mRefreshValueAnimator.setDuration(1000);
        mRefreshValueAnimator.setRepeatMode(ValueAnimator.RESTART);
    }

    private void initView() {
        mRefreshImageView = view.findViewById(R.id.iv_refresh);
        mRecyclerView = view.findViewById(R.id.recyclerview);
        mSearchStateParentView = view.findViewById(R.id.fm_search_state);
        mSearchStateImageView = view.findViewById(R.id.img_search_state);
        mSearchStateTextView = view.findViewById(R.id.tv_search_state_name);
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mScreenCostAdapter = new ScreenCostAdapter();
        mRecyclerView.setAdapter(mScreenCostAdapter);
    }

    private void initService() {
        Intent upnpServiceIntent = new Intent(getContext(), ClingUpnpService.class);
        getContext().bindService(upnpServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void initListener() {
        //刷新
        mRefreshImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mRefreshValueAnimator.start();
                Log.e("AliyunDLNA", "投屏设备搜索刷新 : ");
                clingUpnpServiceManager.searchDevices();
            }
        });

        mDeviceSearchListener.setOnDeviceListChangedListener(new DeviceListChangedListener() {
            @Override
            public void onDeviceAdded(final IDevice device) {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("AliyunDLNA", "投屏设备添加 : ");
                        if (clingUpnpServiceManager != null) {
                            clingUpnpServiceManager.onSearchSuccess();
                        }
                        mSearchStateParentView.setVisibility(GONE);
                        mScreenCostAdapter.add((ClingDevice) device);
                        mRefreshValueAnimator.cancel();

                    }
                });
            }

            @Override
            public void onDeviceRemoved(final IDevice device) {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("AliyunDLNA", "投屏设备移除 : ");
                        mScreenCostAdapter.remove((ClingDevice) device);
                        mRefreshValueAnimator.cancel();
                    }
                });
            }

            @Override
            public void onDeviceSearchTimeout() {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSearchStateTextView.setText(R.string.alivc_screen_cost_device_empty);
                        mSearchStateImageView.setImageResource(R.drawable.alivc_screen_cost_airplay);
                        mSearchStateParentView.setVisibility(VISIBLE);
                    }
                });

            }

            @Override
            public void onDeviceSearchStart() {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //清除数据
                        ClingDeviceList.getInstance().destroy();
                        mScreenCostAdapter.clear();
                        mSearchStateTextView.setText(R.string.alivc_screen_cost_scanning);
                        mSearchStateImageView.setImageResource(R.drawable.loading_rotate);
                        mSearchStateParentView.setVisibility(VISIBLE);
                    }
                });

            }

            @Override
            public void onDeviceConnected() {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mScreenCostAdapter != null) {
                            if (clingUpnpServiceManager != null) {
                                clingUpnpServiceManager.onDeviceConnected();
                            }
                            mScreenCostAdapter.notifyDataSetChanged();
                        }
                    }
                });

            }

            @Override
            public void onDeviceConnectTimeout() {
                if (mScreenCostAdapter != null) {
                    if (clingUpnpServiceManager != null) {
                        clingUpnpServiceManager.cleanSelectedDevice();
                    }
                    mScreenCostAdapter.notifyDataSetChanged();
                }
            }
        });

        //item点击事件
        mScreenCostAdapter.setOnDeviceItemClickListener(new OnDeviceItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // 选择连接设备
                ClingDevice item = mScreenCostAdapter.getItem(position);
                if (item == null) {
                    return;
                }

                ClingManager.getInstance().setSelectedDevice(item);

                Device device = item.getDevice();
                if (device == null) {
                    return;
                }
                if (clingUpnpServiceManager != null){
                    clingUpnpServiceManager.onStartConnectDevice();
                }
                Log.e("AliyunDLNA", "投屏设备链接 ");
                if (mOutDLNAOptionListener != null) {
                    mOutDLNAOptionListener.onItemClick(position);
                }

//                String selectedDeviceName = String.format(getResources().getString(R.string.selectedText), device.getDetails().getFriendlyName());
//                mTVSelected.setText(selectedDeviceName);
            }
        });
    }

    public void destroy() {
        getContext().unbindService(mServiceConnection);
        ClingManager.getInstance().destroy();
        ClingDeviceList.getInstance().destroy();
        if (mRefreshValueAnimator != null) {
            mRefreshValueAnimator.cancel();
            mRefreshValueAnimator.removeAllUpdateListeners();
        }
    }

    public void setOnDeviceItemClickListener(OnDeviceItemClickListener listener) {
        this.mOutDLNAOptionListener = listener;
    }

}
