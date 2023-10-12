package com.alivc.live.interactive_common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alivc.live.interactive_common.R;

public class InteractiveQrEditView extends ConstraintLayout {

   private TextView titleView;
   private EditText etContentView;
   private ImageView qrImageView;
   private OnQrClickListener mQrClickListener;

   public InteractiveQrEditView(@NonNull Context context) {
      super(context);
      init(context,null);
   }

   public InteractiveQrEditView(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      init(context,attrs);
   }

   public InteractiveQrEditView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(context,attrs);
   }

   private void init(Context context,AttributeSet attrs) {
      View inflateView = LayoutInflater.from(context).inflate(R.layout.layout_interactive_qr_input_view,this,true);
      titleView = inflateView.findViewById(R.id.tv_interactive_pull_url);
      etContentView = inflateView.findViewById(R.id.et_interactive_pull_url);
      qrImageView = inflateView.findViewById(R.id.iv_qr_pull_url);

      if (attrs != null) {
         TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InteractiveQrEditView);
         String title = typedArray.getString(R.styleable.InteractiveQrEditView_title);
         typedArray.recycle();
         titleView.setText(title);
      }

      initListener();
   }

   private void initListener() {
      qrImageView.setOnClickListener(view -> {
         if (mQrClickListener != null) {
            mQrClickListener.onQrClick();
         }
      });
   }

   public void setTitle(String title) {
      titleView.setText(title);
   }

   public void setEditText(String text) {
      etContentView.setText(text);
   }

   public String getEditText() {
      return etContentView.getText().toString();
   }

   public void setOnQrClickListener(OnQrClickListener listener) {
      this.mQrClickListener = listener;
   }

   public interface OnQrClickListener {
      void onQrClick();
   }
}
