package com.aliyun.svideo.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

/**
 * @author cross_ly
 * @date 2018/10/11
 * <p>描述:UiConfig相关操作的管理
 */
public class UIConfigManager {

    public static final String TAG = UIConfigManager.class.getSimpleName();
    /**
     * 获取uiStyleConfig里图片资源的drawable
     * @param context context
     * @param attrId style-attr对应的id
     * @param defaultResourceId 默认的图片id
     * @return Drawable
     */
    public static Drawable getDrawableResources(Context context, int attrId, int defaultResourceId) {

        TypedArray a = context.obtainStyledAttributes(new int[] {attrId});
        int resourceId = a.getResourceId(0, defaultResourceId);
        Drawable drawable = context.getResources().getDrawable(resourceId);
        a.recycle();
        return drawable;
    }

    /**
     * 设置图片资源，如果没有则使用默认的资源
     * @param view imageView
     * @param attrId style-attr对应的id
     * @param defaultResourceId 默认的图片id
     */
    public static void setImageResourceConfig(ImageView view, int attrId, int defaultResourceId) {

        TypedArray a = view.getContext().obtainStyledAttributes(new int[] {attrId});
        int resourceId = a.getResourceId(0, defaultResourceId);
        view.setImageResource(resourceId);
        a.recycle();
    }

    /**
     * 数组形式
     * 设置图片资源，如果没有则使用默认的资源
     * @param view imageView
     * @param attrId style-attr对应的id
     * @param defaultResourceId 默认的图片id
     */
    public static void setImageResourceConfig(ImageView[] view, int[] attrId, int[] defaultResourceId) {
        if (view.length <= 0) {
            return;
        }
        TypedArray a = view[0].getContext().obtainStyledAttributes(attrId);

        for (int i = 0; i < view.length; i++) {
            int resourceId = a.getResourceId(i, defaultResourceId[i]);
            view[i].setImageResource(resourceId);
        }
        a.recycle();
    }

    /**
     * 设置图片资源，如果没有则使用默认的资源
     * @param view TextView
     * @param index drawable的方向 0 = left ,1 = top ,2 = right ,3 = bottom
     * @param attrId style-attr对应的id
     * @param defaultResourceId 默认的图片id
     */
    public static void setImageResourceConfig(TextView view, int index, int attrId, int defaultResourceId) {
        int[] resourceId = new int[] {0, 0, 0, 0};

        TypedArray a = view.getContext().obtainStyledAttributes(new int[] {attrId});
        resourceId[index] = a.getResourceId(0, defaultResourceId);
        view.setCompoundDrawablesWithIntrinsicBounds(resourceId[0], resourceId[1], resourceId[2], resourceId[3]);
        a.recycle();
    }

    /**
     * 数组形式
     * 设置图片资源，如果没有则使用默认的资源
     * @param view TextView
     * @param index drawable的方向 0 = left ,1 = top ,2 = right ,3 = bottom
     * @param attrId style-attr对应的id
     * @param defaultResourceId 默认的图片id
     */
    public static void setImageResourceConfig(TextView[] view, int[] index, int[] attrId, int[] defaultResourceId) {

        if (view.length <= 0) {
            return;
        }
        TypedArray a = view[0].getContext().obtainStyledAttributes(attrId);

        for (int i = 0; i < view.length; i++) {
            int[] resourceId = new int[] {0, 0, 0, 0};
            resourceId[index[i]] = a.getResourceId(i, defaultResourceId[i]);
            view[i].setCompoundDrawablesWithIntrinsicBounds(resourceId[0], resourceId[1], resourceId[2], resourceId[3]);
            Log.d(TAG, "textView : " + view[i].getText() + " ,drawable : " + Arrays.toString(resourceId));
        }
        Log.i(TAG, "TypedArray.recycle , count : " + view.length);
        a.recycle();
    }

    /**
     * 设置背景图片资源，如果没有则使用默认的资源
     * @param view view
     * @param attrId style-attr对应的id
     * @param defaultResourceId 默认的图片id
     */
    public static void setImageBackgroundConfig(View view, int attrId, int defaultResourceId) {

        TypedArray a = view.getContext().obtainStyledAttributes(new int[] {attrId});
        int resourceId = a.getResourceId(0, defaultResourceId);
        view.setBackgroundResource(resourceId);
        a.recycle();
    }
}
