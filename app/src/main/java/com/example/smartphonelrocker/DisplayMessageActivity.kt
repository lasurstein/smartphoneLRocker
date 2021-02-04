package com.example.smartphonelrocker

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.smartphonelrocker.sample.LayerService
import com.google.android.material.switchmaterial.SwitchMaterial


class DisplayMessageActivity : AppCompatActivity() {
    lateinit var mp0: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message)

        val message = intent.getStringExtra(EXTRA_MESSAGE)

        val textView = findViewById<TextView>(R.id.textView).apply {
            text = message
        }

        mp0= MediaPlayer.create(this, R.raw.bgm_maoudamashii_neorock80)
        mp0.isLooping=false

        mp0.start()


        val lockBtn = findViewById<ToggleButton>(R.id.lockBtn).apply {
            isChecked = LayerService.isActive
            setOnCheckedChangeListener { _, isChecked ->
                val intent = Intent(application, LayerService::class.java)
                if (isChecked) LayerService.start(this@DisplayMessageActivity)
                else LayerService.stop(this@DisplayMessageActivity)
            }
        }

    }

    override fun onDestroy() {
        mp0.release()
        super.onDestroy()
    }
}