package com.example.smartphonelrocker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.smartphonelrocker.displayLock.PlayRockService
import com.example.smartphonelrocker.utilities.setAlarm

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Log.d("AlarmReceiver", "Received.")
        if (context != null) {
            val bundle: Bundle? = intent?.getBundleExtra("TIMER_DETAIL")
            if (bundle != null) {
                // 次回アラームの設定
                Log.d(
                    "AlarmReceiver", "Received alarm: id:%d, time:%s"
                        .format(bundle.getInt("id"), bundle.getString("time").toString())
                )
                setAlarm(
                    context,
                    bundle.getInt("id"),
                    bundle.getString("name").toString(),
                    bundle.getInt("hour"),
                    bundle.getInt("min"),
                    bundle.getString("time").toString()
                )
            } else {
                Log.d("warn", "Cannot set next alarm.")
            }

            // DisplayLockServiceの呼び出し
            val displayIntent = Intent(context, PlayRockService::class.java)
            displayIntent.putExtra("TIMER_DETAIL", bundle)
            displayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startService(displayIntent)
        }
    }
}