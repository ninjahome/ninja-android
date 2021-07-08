package com.ninjahome.ninja.view.contacts

import com.ninjahome.ninja.R
import java.util.*

/**
 * @author amulya
 * @datetime 14 Oct 2014, 5:20 PM
 */
class ColorGenerator private constructor(private val mColors: List<Int>) {
    companion object {
        lateinit var DEFAULT: ColorGenerator
        lateinit var MATERIAL: ColorGenerator
        fun create(colorList: List<Int>): ColorGenerator {
            return ColorGenerator(colorList)
        }

        init {
            MATERIAL = create(Arrays.asList(R.color.color_eff2f1,R.color.color_F4CCE3,R.color.color_D6CCF4,
                    R.color.color_BACEF0,R.color.color_ABDDEE,R.color.color_CBEEA8,R.color.color_BAF1E6,
                    R.color.color_FAE5A6,R.color.color_F0B5B2,R.color.color_ACEFBA,R.color.color_BAD4EE,
                    R.color.color_EBEFAE,R.color.color_F2C2B4,R.color.color_D8D8D8
            ))
        }
    }

    private val mRandom: Random
    val randomColor: Int
        get() = mColors[mRandom.nextInt(mColors.size)]

    fun getColor(key: Any): Int {
        val index = Math.abs(key.hashCode()) % mColors.size
        println("-------------------------------${index}")
        return mColors[index]
    }

    init {
        mRandom = Random(System.currentTimeMillis())
    }
}