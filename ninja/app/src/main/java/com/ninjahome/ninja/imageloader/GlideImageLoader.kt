package com.ninjahome.ninja.imageloader

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import java.io.File

/**
 *Author:Mr'x
 *Time:2021/7/25
 *Description:
 */
class GlideImageLoader : IImageLoader {
    override fun loadImage(url: String, imageView: ImageView, @DrawableRes errImageId: Int,@DrawableRes placeholderImageId: Int) {
        Glide.with(imageView).load(url).placeholder(placeholderImageId).error(errImageId).error(errImageId).fitCenter().into(imageView)

    }
}