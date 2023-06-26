package com.shubhasai.wellnation.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object dateandtimeformat {
    fun formatSocialTime(date: Date): String {
        val now = Date()
        val diff = now.time - date.time

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = (hours / 24).toInt() // Convert days to Int

        return when {
            days > 1 -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
            days == 1 -> "Yesterday"
            hours > 0 -> "$hours hours ago"
            minutes > 0 -> "$minutes minutes ago"
            else -> "$seconds seconds ago"
        }
    }
    fun formatFirebaseDateTime(timestamp: Timestamp): String {
        val date: Date = timestamp.toDate()
        val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        return dateFormat.format(date)
    }
}