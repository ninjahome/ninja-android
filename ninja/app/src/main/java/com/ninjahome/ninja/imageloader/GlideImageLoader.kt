package com.ninjahome.ninja.imageloader

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide

/**
 *Author:Mr'x
 *Time:2021/7/25
 *Description:
 */
class GlideImageLoader : IImageLoader {
    override fun loadImage(url: String, imageView: ImageView, @DrawableRes errImageId: Int, @DrawableRes placeholderImageId: Int,width:Int,height:Int) {

        Glide.with(imageView).load(url).placeholder(placeholderImageId).error(errImageId).override(width, height).fitCenter().into(imageView)

    }
}