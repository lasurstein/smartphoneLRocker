package com.example.smartphonelrocker

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.TextView

class DisplayLockActivity : AppCompatActivity() {
    lateinit var mp0: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message)

        mp0= MediaPlayer.create(this,R.raw.bgm_maoudamashii_neorock80)
        mp0.isLooping=false

        mp0.start()
    }

    override fun onDestroy() {
        mp0.release()
        super.onDestroy()
    }
}