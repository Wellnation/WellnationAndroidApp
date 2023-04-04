package com.shubhasai.wellnation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_UP
import android.view.MotionEvent.ACTION_UP
import androidx.core.content.ContextCompat.startActivity

class PowerbuttonBroadcastReceiver: BroadcastReceiver() {
    private var volumePressedCount = 0
    private var lastVolumePressTime = 0L

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action == Intent.ACTION_MEDIA_BUTTON) {
            val event = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
            if (event?.action == KeyEvent.ACTION_UP && event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                val currentTime = System.currentTimeMillis()
                if (lastVolumePressTime == 0L || currentTime - lastVolumePressTime > 1000) {
                    // Reset the count if it has been too long since the last press
                    volumePressedCount = 0
                }
                volumePressedCount++
                lastVolumePressTime = currentTime
                if (volumePressedCount == 2) {
                    // Send broadcast to trigger the receiver even if the app is closed
                    val toastIntent = Intent("com.shubhasai.wellnation.VOLUME_BUTTON_TOAST")
                    context?.sendBroadcast(toastIntent)
                    // Reset count and time for next press
                    volumePressedCount = 0
                    lastVolumePressTime = 0L
                }
            }
        }
    }

}