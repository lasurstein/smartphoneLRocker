package com.example.smartphonelrocker.displaylock

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.smartphonelrocker.DisplayLockView
import com.example.smartphonelrocker.R
import java.util.*
import kotlin.concurrent.schedule

class DisplayLockService : Service() {

    private lateinit var displayLockView: DisplayLockView
    private lateinit var mp0: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        displayLockView = DisplayLockView.create(this)

        displayLockView.findViewById<Button>(R.id.exitLockButton).setOnClickListener {
            Log.d("DisplayLockService", "STOP: press exit button.")
            stopSelf()
        }

        mp0= MediaPlayer.create(this, R.raw.bgm_maoudamashii_neorock80)
        mp0.isLooping=false

        mp0.start()

        val timerCallback: TimerTask.() -> Unit = {
            Log.d("DisplayLockService", "STOP: 1 min passed.")
            this.cancel()
            stopSelf()
        }
        Timer().schedule(0, 600000, timerCallback)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            displayLockView.show()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /** Cleans up views just in case. */
    override fun onDestroy() {
        displayLockView.hide()
        mp0.release()
        super.onDestroy()
    }

    /** This service does not support binding. */
    override fun onBind(intent: Intent?) = null
}