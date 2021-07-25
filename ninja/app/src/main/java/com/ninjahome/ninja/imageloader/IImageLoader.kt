package com.ninjahome.ninja.imageloader

import android.widget.ImageView
import androidx.annotation.DrawableRes
import java.io.File

interface IImageLoader {
    fun loadImage(url: String, imageView: ImageView, @DrawableRes errImageId: Int=0, @DrawableRes placeholderImageId: Int=0)

}