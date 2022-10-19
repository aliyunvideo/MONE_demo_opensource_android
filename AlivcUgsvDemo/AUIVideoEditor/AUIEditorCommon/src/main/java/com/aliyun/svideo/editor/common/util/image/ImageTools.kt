package com.aliyun.svideo.editor.common.util.image

//import com.makeramen.roundedimageview.RoundedImageView
//import de.hdodenhof.circleimageview.CircleImageView
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory

/*
 * Copyright (C) 2004 - 2019 UCWeb Inc. All Rights Reserved.
 * Description : ImageTools
 *
 * Created by zhihong.lanzh@alibaba-inc.com
 */
class ImageTools {
    companion object {

        //缩放规则
        const val OPTION_FORMAT = ";,90,webp;3,%s"
        //裁剪规则
        val OPTION_FORMAT_CROP = ";,90,webp;6,default,%s,1"

        fun loadImage(view: ImageView, imageUrl: String, placeHolder: Drawable?, error: Drawable?, transform: Transformation<Bitmap>?) {
            var imageUrlTmp = imageUrl

            if (!TextUtils.isEmpty(imageUrlTmp)) {

                //CircleImageView 和 Glide的cross动画有冲突，所以需要禁用动画
                //https://www.jianshu.com/p/f589d4ec4b15
//                val noAnimate = (view is CircleImageView) || (view is RoundedImageView)
                val noAnimate = false
                loadImage(
                    view,
                    imageUrlTmp,
                    placeHolder,
                    error,
                    DecodeFormat.DEFAULT,
                    transform,
                    noAnimate
                )
            }
        }

        @JvmStatic
        fun loadImage(view: ImageView, url: String, placeHolder: Int, errorHolder: Int, format: DecodeFormat) {

            val holder = if (placeHolder != 0) AppCompatResources.getDrawable(view.context, placeHolder) else null
            val error = if (errorHolder != 0) AppCompatResources.getDrawable(view.context, errorHolder) else null

            loadImage(view, url, holder, error, format)
        }

        fun loadImage(view: ImageView, url: String, placeHolder: Int, errorHolder: Int, format: DecodeFormat, target: Target<Drawable>) {
            val holder = if (placeHolder != 0) AppCompatResources.getDrawable(view.context, placeHolder) else null
            val error = if (errorHolder != 0) AppCompatResources.getDrawable(view.context, errorHolder) else null

            loadImage(
                view,
                url,
                holder,
                error,
                format,
                null,
                false,
                target
            )
        }

        @JvmStatic
        fun loadImage(view: ImageView, url: String, placeHolder: Drawable?, errorHolder: Drawable?, format: DecodeFormat) {
            loadImage(
                view,
                url,
                placeHolder,
                errorHolder,
                format,
                null
            )
        }

        @JvmStatic
        fun loadImage(view: ImageView, url: String, placeHolder: Drawable?, errorHolder: Drawable?, format: DecodeFormat, transform: Transformation<Bitmap>?) {
            loadImage(
                view,
                url,
                placeHolder,
                errorHolder,
                format,
                transform,
                false
            )
        }

        @JvmStatic
        fun loadImage(view: ImageView, url: String, placeHolder: Drawable?, errorHolder: Drawable?,
                      format: DecodeFormat?, transform: Transformation<Bitmap>?, noAnimate: Boolean) {
            loadImage(
                view,
                url,
                placeHolder,
                errorHolder,
                format,
                transform,
                noAnimate,
                null
            )
        }

        @JvmStatic
        fun loadImage(view: ImageView, url: String, placeHolder: Drawable?, errorHolder: Drawable?,
                      format: DecodeFormat?, transform: Transformation<Bitmap>?, noAnimate: Boolean, target: Target<Drawable>?) {

            if (url.isEmpty()) {
                if (placeHolder != null) {
                    view.setImageDrawable(placeHolder)
                } else if (errorHolder != null) {
                    view.setImageDrawable(errorHolder)
                }
                return
            }

            var loadBuilder: RequestBuilder<out Drawable>

            if (view.context is Activity) {
                if ((view.context as Activity).isDestroyed) {
                    return
                }
            }

            loadBuilder = if (url.contains(".gif")) {
                Glide.with(view.context)
                        .load(url)
                    .skipMemoryCache(true)
            } else {
                Glide.with(view.context)
                        .load(url)
                    .listener(GlideRequestListener())
            }

            if (placeHolder != null) {
                loadBuilder = loadBuilder.placeholder(placeHolder)
            }
            if (errorHolder != null) {
                loadBuilder = loadBuilder.error(errorHolder)
            }

            if (transform != null) {
                loadBuilder = loadBuilder.transform(transform)
            }

            if (format != null) {
                loadBuilder = loadBuilder.apply(RequestOptions.formatOf(format))
            }

            if (!noAnimate) {
                loadBuilder = loadBuilder.transition(DrawableTransitionOptions().crossFade(DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true)))
            }

            if (target != null) {
                loadBuilder.into(target)
            } else {
                loadBuilder.into(view)
            }

        }
    }
}

private class GlideRequestListener : RequestListener<Drawable> {

    private val TAG = "GlideRequestListener"

    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
        e?.logRootCauses(TAG)
        Log.d(TAG, "onLoadFailed() called with: e = [$e], model = [$model], target = [$target], isFirstResource = [$isFirstResource]")
        return false
    }

    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
        return false
    }


}