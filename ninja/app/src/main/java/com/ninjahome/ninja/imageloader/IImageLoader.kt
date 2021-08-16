package com.ninjahome.ninja.imageloader

import android.widget.ImageView
import androidx.annotation.DrawableRes

interface IImageLoader {
    fun loadImage(url: String, imageView: ImageView, @DrawableRes errImageId: Int = 0, @DrawableRes placeholderImageId: Int = 0,width:Int,height:Int)

}