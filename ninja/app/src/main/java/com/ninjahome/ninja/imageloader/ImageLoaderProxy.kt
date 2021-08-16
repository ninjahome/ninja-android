package com.ninjahome.ninja.imageloader

import android.widget.ImageView
import androidx.annotation.DrawableRes

/**
 *Author:Mr'x
 *Time:2021/7/25
 *Description:
 */
object ImageLoaderProxy : IImageLoader {
    private lateinit var mImageLoader: IImageLoader

    fun initLoader(imageLoader: IImageLoader) {
        this.mImageLoader = imageLoader
    }

    override fun loadImage(url: String, imageView: ImageView, @DrawableRes errImageId: Int, @DrawableRes placeholderImageId: Int,width:Int,height:Int) {
        mImageLoader.loadImage(url, imageView, errImageId, placeholderImageId,width,height)
    }

}