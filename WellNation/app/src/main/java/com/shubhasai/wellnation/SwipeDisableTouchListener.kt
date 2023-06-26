package com.shubhasai.wellnation

import android.view.MotionEvent
import android.view.View

class SwipeDisableTouchListener : View.OnTouchListener {
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        return true // Consume touch events to disable swipe
    }
}