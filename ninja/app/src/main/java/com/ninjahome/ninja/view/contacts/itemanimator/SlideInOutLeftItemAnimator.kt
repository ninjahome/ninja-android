/*
 * ******************************************************************************
 *   Copyright (c) 2014-2015 Gabriele Mariotti.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  *****************************************************************************
 */
package com.ninjahome.ninja.view.contacts.itemanimator

import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * @see RecyclerView.setItemAnimator
 */
class SlideInOutLeftItemAnimator(recyclerView: RecyclerView?) : BaseItemAnimator(
    recyclerView!!
) {
    override fun animateRemoveImpl(holder: RecyclerView.ViewHolder?) {
        val view = holder!!.itemView
        val animation = ViewCompat.animate(view)
        mRemoveAnimations.add(holder)
        animation
            .setDuration(removeDuration)
            .alpha(0f)
            .translationX(-mRecyclerView.layoutManager!!.width.toFloat())
            .setListener(object : VpaListenerAdapter() {
                override fun onAnimationStart(view: View) {
                    dispatchRemoveStarting(holder)
                }

                override fun onAnimationEnd(view: View) {
                    animation.setListener(null)
                    ViewCompat.setAlpha(view, 1f)
                    ViewCompat.setTranslationX(view, -mRecyclerView.layoutManager!!.width.toFloat())
                    dispatchRemoveFinished(holder)
                    mRemoveAnimations.remove(holder)
                    dispatchFinishedWhenDone()
                }
            }).start()
    }

    override fun prepareAnimateAdd(holder: RecyclerView.ViewHolder?) {
        ViewCompat.setTranslationX(
            holder!!.itemView,
            -mRecyclerView.layoutManager!!.width.toFloat()
        )
    }

    override fun animateAddImpl(holder: RecyclerView.ViewHolder?) {
        val view = holder!!.itemView
        val animation = ViewCompat.animate(view)
        mAddAnimations.add(holder)
        animation.translationX(0f)
            .alpha(1f)
            .setDuration(addDuration)
            .setListener(object : VpaListenerAdapter() {
                override fun onAnimationStart(view: View) {
                    dispatchAddStarting(holder)
                }

                override fun onAnimationCancel(view: View) {
                    ViewCompat.setTranslationX(view, 0f)
                    ViewCompat.setAlpha(view, 1f)
                }

                override fun onAnimationEnd(view: View) {
                    animation.setListener(null)
                    ViewCompat.setTranslationX(view, 0f)
                    ViewCompat.setAlpha(view, 1f)
                    dispatchAddFinished(holder)
                    mAddAnimations.remove(holder)
                    dispatchFinishedWhenDone()
                }
            }).start()
    }
}