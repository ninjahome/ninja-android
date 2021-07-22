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
            MATERIAL = create(Arrays.asList(R.color.color_eff2f1,R.color.color_f4cce3,R.color.color_d6ccf4,
                    R.color.color_bacef0,R.color.color_abddee,R.color.color_cbeea8,R.color.color_baf1e6,
                    R.color.color_fae5a6,R.color.color_f0b5b2,R.color.color_acefba,R.color.color_bad4ee,
                    R.color.color_ebefae,R.color.color_f2c2b4,R.color.color_d8d8d8
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