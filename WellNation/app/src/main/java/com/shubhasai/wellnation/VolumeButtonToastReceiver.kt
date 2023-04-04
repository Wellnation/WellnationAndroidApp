package com.shubhasai.wellnation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class VolumeButtonToastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Volume button pressed twice!", Toast.LENGTH_SHORT).show()
    }
}