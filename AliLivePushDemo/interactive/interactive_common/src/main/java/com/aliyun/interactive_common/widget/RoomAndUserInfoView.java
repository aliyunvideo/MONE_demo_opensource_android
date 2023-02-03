package com.aliyun.interactive_common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.aliyun.interactive_common.R;

/**
 * 显示 RoomId 和 UserId
 */
public class RoomAndUserInfoView extends ConstraintLayout {

    private TextView mRoomIdTextView;
    private TextView mUserIdTextView;

    public RoomAndUserInfoView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public RoomAndUserInfoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoomAndUserInfoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View inflateView = LayoutInflater.from(context).inflate(R.layout.layout_room_user_info_view,this,true);
        mRoomIdTextView = inflateView.findViewById(R.id.tv_room_id);
        mUserIdTextView = inflateView.findViewById(R.id.tv_user_id);
    }

    public void setRoomId(String roomId){
        mRoomIdTextView.setText(String.format("RoomID:%s", roomId));
    }

    public void setUserId(String userId){
        mUserIdTextView.setText(String.format("UserID:%s", userId));
    }
}
