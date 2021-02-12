package com.example.smartphonelrocker.displaylock

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.smartphonelrocker.PlayRockView
import com.example.smartphonelrocker.R
import java.util.*
import java.util.Objects.isNull
import kotlin.concurrent.schedule

class PlayRockService() : Service() {

    private lateinit var playRockView: PlayRockView
    private lateinit var mp0: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        playRockView = PlayRockView.create(this)

        mp0 = MediaPlayer.create(this, R.raw.bgm_maoudamashii_neorock80)
        mp0.isLooping = true
        mp0.start()
        Log.d("PlayRockService", "START: Play Rock.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val bundle: Bundle? = intent?.getBundleExtra("TIMER_DETAIL")
            if (!isNull(bundle)) {
                playRockView.findViewById<TextView>(R.id.playRockAlarmTime).text =
                    bundle?.getString("time").toString()
                val name = bundle?.getString("name").toString() + "の時間です!"
                playRockView.findViewById<TextView>(R.id.playRockAlarmName).text = name
            }
            playRockView.show()

            val displayIntent = Intent(this, DisplayLockService::class.java)
            displayIntent.putExtra("TIMER_DETAIL", bundle)

            val timerCallback: TimerTask.() -> Unit = {
                Log.d("PlayRockService", "STOP: 30 sec passed.")
                this.cancel()
                playRockView.hide()
                stopSelf()
                startService(displayIntent)
            }
            Timer().schedule(60000, 1000, timerCallback)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /** Cleans up views just in case. */
    override fun onDestroy() {
        playRockView.hide()
        mp0.release()
        super.onDestroy()
    }

    /** This service does not support binding. */
    override fun onBind(intent: Intent?) = null
}