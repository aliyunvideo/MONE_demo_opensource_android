package com.aliyun.interactive_pk;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.aliyun.interactive_common.InteractAppInfoActivity;
import com.aliyun.interactive_common.InteractLiveAppInfoFragment;
import com.aliyun.interactive_common.utils.LivePushGlobalConfig;
import com.alivc.live.utils.TextFormatUtil;

import java.util.regex.Pattern;

/**
 * PK 互动输入信息界面
 */
@Route(path = "/interactivePK/pkInput")
public class PKLiveInputActivity extends AppCompatActivity {

    public static final String DATA_HOME_ID = "data_home_id";
    public static final String DATA_USER_ID = "data_user_id";

    private EditText mUserIdEditText;
    private EditText mRoomIdEditText;
    private TextView mConfirmTextView;
    private TextView mSettingTextView;
    private ImageView mBackImageView;
    private ImageView mUserIdClearImageView;
    private ImageView mRoomIdClearImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pklive_input);

        initView();
        initListener();
    }

    private void initView(){
        mUserIdEditText = findViewById(R.id.et_user_id);
        mRoomIdEditText = findViewById(R.id.et_room_id);
        mBackImageView = findViewById(R.id.iv_back);
        mConfirmTextView = findViewById(R.id.tv_confirm);
        mSettingTextView = findViewById(R.id.tv_setting);
        mUserIdClearImageView = findViewById(R.id.iv_user_id_clear);
        mRoomIdClearImageView = findViewById(R.id.iv_room_id_clear);
    }

    private void initListener(){
        InteractLiveAppInfoFragment interactLiveAppInfoFragment = (InteractLiveAppInfoFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_app_info);
        if(interactLiveAppInfoFragment != null){
            interactLiveAppInfoFragment.setOnEditClickListener(() -> {
                Intent intent = new Intent(PKLiveInputActivity.this, InteractAppInfoActivity.class);
                intent.putExtra(InteractAppInfoActivity.FROM_EDITOR,true);
                intent.putExtra(InteractAppInfoActivity.INTENT_FROM_PK,true);
                startActivity(intent);
            });
        }

        mBackImageView.setOnClickListener(view -> finish());

        mSettingTextView.setOnClickListener(view -> {
            Intent intent = new Intent(this, PKLiveSettingActivity.class);
            startActivity(intent);
        });

        mUserIdClearImageView.setOnClickListener(view -> mUserIdEditText.setText(""));
        mRoomIdClearImageView.setOnClickListener(view -> mRoomIdEditText.setText(""));

        mUserIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                changeConfirmTextView(checkEnable());
            }
        });

        mRoomIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                changeConfirmTextView(checkEnable());
            }
        });

        mConfirmTextView.setOnClickListener(view -> {
            if(checkEnable()){
                Intent intent;
                if (LivePushGlobalConfig.IS_MULTI_PK) {
                    intent = new Intent(PKLiveInputActivity.this, MultiPKLiveActivity.class);
                } else {
                    intent = new Intent(PKLiveInputActivity.this, PKLiveActivity.class);
                }

                intent.putExtra(DATA_HOME_ID,mRoomIdEditText.getText().toString());
                intent.putExtra(DATA_USER_ID,mUserIdEditText.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void changeConfirmTextView(boolean enable){
        if(enable){
            mConfirmTextView.setBackground(getResources().getDrawable(R.drawable.shape_pysh_btn_bg));
        }else{
            mConfirmTextView.setBackground(getResources().getDrawable(R.drawable.shape_rect_blue));
        }
    }

    private boolean checkEnable(){
        String userId = mUserIdEditText.getText().toString();
        String roomId = mRoomIdEditText.getText().toString();
        return !TextUtils.isEmpty(userId) && !TextUtils.isEmpty(roomId) && Pattern.matches(TextFormatUtil.REGULAR, userId) && Pattern.matches(TextFormatUtil.REGULAR, roomId);
    }
}