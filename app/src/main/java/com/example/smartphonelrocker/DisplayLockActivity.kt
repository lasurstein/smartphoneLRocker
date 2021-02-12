package com.example.smartphonelrocker

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DisplayLockActivity : AppCompatActivity() {
    lateinit var mp0: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message)

        mp0 = MediaPlayer.create(this, R.raw.bgm_maoudamashii_neorock80)
        mp0.isLooping = false

        mp0.start()
    }

    override fun onDestroy() {
        mp0.release()
        super.onDestroy()
    }
}