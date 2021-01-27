package com.example.smartphonelrocker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (context != null) {
            val displayIntent = Intent(context, DisplayMessageActivity::class.java)
            displayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(displayIntent)
        }
    }
}