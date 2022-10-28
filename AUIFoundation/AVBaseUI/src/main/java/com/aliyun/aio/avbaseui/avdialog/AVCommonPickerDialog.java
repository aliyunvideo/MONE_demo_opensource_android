package com.aliyun.aio.avbaseui.avdialog;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.aliyun.aio.avbaseui.AVBaseDialogFragment;
import com.aliyun.aio.avbaseui.R;
import com.zyyoona7.wheel.WheelView;
import com.zyyoona7.wheel.adapter.ArrayWheelAdapter;

import java.util.ArrayList;
import java.util.List;

public class AVCommonPickerDialog extends AVBaseDialogFragment implements View.OnClickListener {
    private WheelView mWheelView;
    private List<PickerItem> mPickerItemList;
    private PickerAdapter mPickerAdapter;
    private TextView mCancel;
    private TextView mSubmit;
    private OnPickListener mPickListener;
    private TextView mTitle;
    private String mTextTitle;
    private int mSelectedIndex;
    private int mRequestCode;

    private AVCommonPickerDialog() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AV_Common_Dialog_Style);
        mPickerAdapter = new PickerAdapter();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.av_common_picker_dialog, null);
        mWheelView = view.findViewById(R.id.av_picker_view);
        mWheelView.setSelectedTextColor(Color.WHITE);
        mWheelView.setLineSpacing(1.5f);
        mWheelView.setShowCurtain(true);
        mWheelView.setCurtainColorRes(R.color.fg_strong);
        mWheelView.setTypeface(Typeface.DEFAULT, true);
        mWheelView.setAdapter(mPickerAdapter);
        mPickerAdapter.setData(mPickerItemList);

        mWheelView.setSelectedPosition(mSelectedIndex);

        mCancel = view.findViewById(R.id.cancel_button);
        mCancel.setOnClickListener(this);
        mSubmit = view.findViewById(R.id.ok_button);
        mSubmit.setOnClickListener(this);
        mTitle = view.findViewById(R.id.text_title);
        mTitle.setText(mTextTitle);
        return view;
    }


    public void setData(List<PickerItem> itemList) {
        if (mPickerItemList == null) {
            mPickerItemList = new ArrayList<>();
        }
        mPickerItemList.clear();
        mPickerItemList.addAll(itemList);
    }


    public void setSelectItemIndex(int index) {
        mWheelView.setSelectedPosition(index);
    }

    public void setPickListener(OnPickListener listener) {
        mPickListener = listener;
    }


    @Override
    public void onStart() {
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = (int)(getResources().getDisplayMetrics().density * 240);
        params.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if (v == mCancel) {
            dismiss();
        } else if (v == mSubmit) {
            if (mPickListener != null) {
                PickerItem item = mWheelView.getSelectedItem();
                mPickListener.onSubmit(mRequestCode, item);
                dismiss();
            }
        }
    }

    public static class PickerAdapter extends ArrayWheelAdapter<PickerItem> {

        @Override
        public String getItemText$wheelview_release(Object o) {
            if (o instanceof PickerItem) {
                return ((PickerItem) o).mItemName;
            }
            return "";
        }

        @Override
        public String getItemTextByIndex$wheelview_release(int index) {
            if (index < 0 || index >= getData().size()) {
                return "";
            }
            return getData().get(index).mItemName;
        }
    }

    public interface OnPickListener {
        void onSubmit(int requestCode, PickerItem pickerItem);
    }

    public static class PickerItem {
        public int index;
        public String mItemName;
        public Object mAttachValue;
        public PickerItem(String itemName, Object value) {
            mItemName = itemName;
            mAttachValue = value;
        }
    }

    public static class ArgsSelector {
        public int mRequestCode;
        public String mTitle;
        public int mSelectedIndex;
        public List<AVCommonPickerDialog.PickerItem> mItemList = new ArrayList<>();

        public int findSelectedIndexByText(Object value) {
            for(int i = 0; i < mItemList.size(); i++) {
                PickerItem pickerItem = mItemList.get(i);
                if (!value.getClass().equals(pickerItem.mAttachValue.getClass())) {
                    continue;
                }
                if(value instanceof Integer && (int)pickerItem.mAttachValue == (int) value) {
                    return i;
                } else if(value instanceof Float && (float)pickerItem.mAttachValue == (float) value) {
                    return i;
                } else if(value instanceof Long && (long)pickerItem.mAttachValue == (long) value) {
                    return i;
                } else if(value instanceof Double && (double)pickerItem.mAttachValue == (double) value) {
                    return i;
                }
            }
            return 0;
        }
    }

    public static class Builder {

        private AVCommonPickerDialog mCommonPickerDialog;

        public Builder(int requestCode, List<PickerItem> itemList) {
            mCommonPickerDialog = new AVCommonPickerDialog();
            mCommonPickerDialog.setData(itemList);
            mCommonPickerDialog.mRequestCode = requestCode;
        }

        public Builder(ArgsSelector selector) {
            mCommonPickerDialog = new AVCommonPickerDialog();
            mCommonPickerDialog.setData(selector.mItemList);
            mCommonPickerDialog.mRequestCode = selector.mRequestCode;
            mCommonPickerDialog.mTextTitle = selector.mTitle;
            mCommonPickerDialog.mSelectedIndex = selector.mSelectedIndex;
        }

        public Builder setTitle(String title) {
            mCommonPickerDialog.mTextTitle = title;
            return this;
        }

        public Builder setListener(OnPickListener listener) {
            mCommonPickerDialog.setPickListener(listener);
            return this;
        }

        public Builder setSelectedIndex(int index) {
            mCommonPickerDialog.mSelectedIndex = index;
            return this;
        }

        public AVCommonPickerDialog build() {
            return mCommonPickerDialog;
        }
    }
}
