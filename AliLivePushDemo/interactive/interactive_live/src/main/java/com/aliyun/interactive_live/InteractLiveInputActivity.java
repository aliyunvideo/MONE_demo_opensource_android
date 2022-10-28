package com.aliyun.interactive_live;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.aliyun.interactive_common.InteractAppInfoActivity;
import com.aliyun.interactive_common.InteractLiveAppInfoFragment;
import com.aliyun.interactive_common.utils.LivePushGlobalConfig;
import com.aliyun.interactive_live.widget.InteractLiveRadioButton;
import com.alivc.live.utils.TextFormatUtil;

import java.util.regex.Pattern;

@Route(path = "/interactiveLive/liveInput")
public class InteractLiveInputActivity extends AppCompatActivity {

    private EditText mUserIdEditText;
    private EditText mRoomIdEditText;
    private InteractLiveRadioButton mAnchorRadioButton;
    private InteractLiveRadioButton mAudienceRadioButton;
    private TextView mConfirmTextView;
    private ImageView mBackImageView;
    private ImageView mUserIdClearImageView;
    private ImageView mRoomIdClearImageView;

    private boolean isAnchor = true;
    private TextView mSettingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_interact_live_input);

        initView();
        initListener();
    }

    private void initView() {
        mUserIdEditText = findViewById(R.id.et_user_id);
        mRoomIdEditText = findViewById(R.id.et_room_id);
        mBackImageView = findViewById(R.id.iv_back);
        mAnchorRadioButton = findViewById(R.id.radio_button_anchor);
        mAudienceRadioButton = findViewById(R.id.radio_button_audience);
        mConfirmTextView = findViewById(R.id.tv_confirm);
        mUserIdClearImageView = findViewById(R.id.iv_user_id_clear);
        mRoomIdClearImageView = findViewById(R.id.iv_room_id_clear);
        mSettingTextView = findViewById(R.id.tv_setting);

        mAnchorRadioButton.setChecked(true);
    }

    private void initListener() {
        InteractLiveAppInfoFragment interactLiveAppInfoFragment = (InteractLiveAppInfoFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_app_info);
        if(interactLiveAppInfoFragment != null){
            interactLiveAppInfoFragment.setOnEditClickListener(() -> {
                Intent intent = new Intent(InteractLiveInputActivity.this, InteractAppInfoActivity.class);
                intent.putExtra(InteractAppInfoActivity.FROM_EDITOR,true);
                intent.putExtra(InteractAppInfoActivity.INTENT_FROM_PK,false);
                startActivity(intent);
            });
        }


        mBackImageView.setOnClickListener(view -> finish());

        mSettingTextView.setOnClickListener(view -> {
            Intent intent = new Intent(this, InteractLiveSettingActivity.class);
            startActivity(intent);
        });

        mUserIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                changeConfirmTextView(checkEnable());
            }
        });

        mRoomIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                changeConfirmTextView(checkEnable());
            }
        });

        mAnchorRadioButton.setOnClickListener(view -> {
            isAnchor = true;
            mAnchorRadioButton.setChecked(true);
            mAudienceRadioButton.setChecked(false);
        });

        mAudienceRadioButton.setOnClickListener(view -> {
            isAnchor = false;
            mAnchorRadioButton.setChecked(false);
            mAudienceRadioButton.setChecked(true);
        });

        mUserIdClearImageView.setOnClickListener(view -> mUserIdEditText.setText(""));
        mRoomIdClearImageView.setOnClickListener(view -> mRoomIdEditText.setText(""));

        mConfirmTextView.setOnClickListener(view -> {
            if (checkEnable()) {
                Intent intent;
                if (LivePushGlobalConfig.IS_MULTI_INTERACT && isAnchor) {
                    intent = new Intent(InteractLiveInputActivity.this, MultiInteractLiveActivity.class);
                } else {
                    intent = new Intent(InteractLiveInputActivity.this, InteractLiveActivity.class);
                }

                intent.putExtra(InteractLiveActivity.DATA_IS_ANCHOR, isAnchor);
                intent.putExtra(InteractLiveActivity.DATA_HOME_ID, mRoomIdEditText.getText().toString());
                intent.putExtra(InteractLiveActivity.DATA_USER_ID, mUserIdEditText.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void changeConfirmTextView(boolean enable) {
        if (enable) {
            mConfirmTextView.setBackground(getResources().getDrawable(R.drawable.shape_pysh_btn_bg));
        } else {
            mConfirmTextView.setBackground(getResources().getDrawable(R.drawable.shape_rect_blue));
        }
    }

    private boolean checkEnable() {
        String userId = mUserIdEditText.getText().toString();
        String roomId = mRoomIdEditText.getText().toString();
        return !TextUtils.isEmpty(userId) && !TextUtils.isEmpty(roomId) && Pattern.matches(TextFormatUtil.REGULAR, userId) && Pattern.matches(TextFormatUtil.REGULAR, roomId);
    }

}