package com.example.smartphonelrocker.displaylock

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.smartphonelrocker.DisplayLockView
import com.example.smartphonelrocker.R
import java.util.*
import java.util.Objects.isNull
import kotlin.concurrent.schedule

class DisplayLockService : Service() {

    private lateinit var displayLockView: DisplayLockView

    override fun onCreate() {
        super.onCreate()
        displayLockView = DisplayLockView.create(this)
        Log.d("DisplayLockService", "START: Display locked.")

        displayLockView.findViewById<Button>(R.id.exitLockButton).setOnClickListener {
            Log.d("DisplayLockService", "STOP: press exit button.")
            stopSelf()
        }

        val timerCallback: TimerTask.() -> Unit = {
            Log.d("DisplayLockService", "STOP: 30 sec passed.")
            this.cancel()
            displayLockView.hide()
            stopSelf()
        }
        Timer().schedule(300000, 1000, timerCallback)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val bundle: Bundle? = intent?.getBundleExtra("TIMER_DETAIL")
            if (!isNull(bundle)) {
                displayLockView.findViewById<TextView>(R.id.displayLockAlarmTime).text =
                    bundle?.getString("time").toString()
                val name = bundle?.getString("name").toString() + "の時間です!"
                displayLockView.findViewById<TextView>(R.id.displayLockAlarmName).text = name

            }
            displayLockView.show()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /** Cleans up views just in case. */
    override fun onDestroy() {
        displayLockView.hide()
        super.onDestroy()
    }

    /** This service does not support binding. */
    override fun onBind(intent: Intent?) = null
}