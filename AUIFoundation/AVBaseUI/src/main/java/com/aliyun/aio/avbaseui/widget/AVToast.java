package com.aliyun.aio.avbaseui.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.aio.avbaseui.R;

/**
 * 自定义样式Toast，具备以下特点：
 * 1. 最小宽度限制
 * 2. 最大宽度限制
 * 3. 字符串行数限制
 * 4. 显示样式自定义
 * */
public class AVToast {
    public static void show(Context ctx, boolean shortToast, int res){
        show(ctx, shortToast, ctx.getString(res));
    }

    public static void show(Context ctx, boolean shortToast, CharSequence sequence){
        Toast toast = new Toast(ctx);
        if(shortToast){
            toast.setDuration(Toast.LENGTH_SHORT);
        }else{
            toast.setDuration(Toast.LENGTH_LONG);
        }
        View rootView = LayoutInflater.from(ctx).inflate(R.layout.av_toast_view, null);
        ((TextView)rootView.findViewById(R.id.av_base_title)).setText(sequence);
        int yOffset = ctx.getResources().getDisplayMetrics().heightPixels / 6;
        toast.setGravity(Gravity.CENTER, 0, -yOffset);
        toast.setView(rootView);
        toast.show();
    }
}
