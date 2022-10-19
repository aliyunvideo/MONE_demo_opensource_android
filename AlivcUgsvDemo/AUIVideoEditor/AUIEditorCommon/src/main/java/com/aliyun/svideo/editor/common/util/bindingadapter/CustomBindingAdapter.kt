package com.aliyun.svideo.editor.common.util.bindingadapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.aliyun.svideo.editor.common.util.image.ImageTools
import com.aliyun.svideo.editor.common.widget.DebouncedOnClickListener
import com.bumptech.glide.load.Transformation

@BindingAdapter("android:onClick")
fun View.debouncedListener(aOnClickListener: View.OnClickListener?) {
    aOnClickListener?.apply {
        setOnClickListener(DebouncedOnClickListener(this))
    }
}

@BindingAdapter("visibleOrGone")
fun View.setVisibleOrGone(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.GONE
}


@BindingAdapter(value = ["drawableLeft", "drawableSize"], requireAll = false)
fun setDrawableLeft(view: TextView, drawable: Drawable?, drawableSize: Float) {
    CustomBindingUtils.setIntrinsicBounds(drawable, drawableSize)
    val drawables = view.compoundDrawables
    view.setCompoundDrawables(drawable, drawables[1], drawables[2], drawables[3])
}

@BindingAdapter(value = ["drawableTop", "drawableSize"], requireAll = false)
fun setDrawableTop(view: TextView, drawable: Drawable?, drawableSize: Float) {
    CustomBindingUtils.setIntrinsicBounds(drawable, drawableSize)
    val drawables = view.compoundDrawables
    view.setCompoundDrawables(drawables[0], drawable, drawables[2], drawables[3])
}

@BindingAdapter(value = ["drawableRight", "drawableSize"], requireAll = false)
fun setDrawableRight(view: TextView, drawable: Drawable?, drawableSize: Float) {
    CustomBindingUtils.setIntrinsicBounds(drawable, drawableSize)
    val drawables = view.compoundDrawables
    view.setCompoundDrawables(drawables[0], drawables[1], drawable, drawables[3])
}

@BindingAdapter(
    value = ["imageUrl", "placeholder", "error", "imageResource", "transformation"],
    requireAll = false
)
fun ImageView.bindImageFromUrl(
    imageUrl: String?,
    placeHolder: Drawable?,
    error: Drawable?,
    imageResource: Int,
    transform: Transformation<Bitmap>?,
) {
    if (imageResource != 0) {
        setImageResource(imageResource)
    }
    if (imageUrl.isNullOrEmpty()) {
        if(imageResource != 0) {
            return
        }

        error?.let {
            this.setImageDrawable(it)
        }
        return
    }

    ImageTools.loadImage(
        this,
        imageUrl,
        placeHolder,
        error,
        transform
    )

}

@BindingAdapter("text")
fun setText(view: TextView, text: Int) {
    if(text != 0) {
        view.setText(text)
    }
}

class CustomBindingUtils {
    companion object {

        fun setIntrinsicBounds(drawable: Drawable?, drawableSize: Float) {
            if (drawable != null) {
                if (drawableSize <= 0) {
                    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                } else {
                    drawable.setBounds(0, 0, drawableSize.toInt(), drawableSize.toInt())
                }
            }

        }
    }


}