package com.example.smartphonelrocker.displayLock

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.smartphonelrocker.PlayRockView
import com.example.smartphonelrocker.R
import kotlinx.coroutines.*
import java.util.*
import java.util.Objects.isNull
import kotlin.concurrent.schedule

class PlayRockService() : Service(), SensorEventListener {

    private lateinit var playRockView: PlayRockView
    private lateinit var mp0: MediaPlayer

    private var accelX: Float = 0.0F
    private var accelY: Float = 0.0F
    private var accelZ: Float = 0.0F

    private var round: Int = 0
    private var count: Int = 0
    private var isUp: Boolean = false

    private var bundle: Bundle? = null

    val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        playRockView = PlayRockView.create(this)

        val musicRand = (1..8).random()
        val musicName = "rock_$musicRand"
        mp0 = MediaPlayer.create(this, resources.getIdentifier(musicName, "raw", packageName))
        mp0.apply {
            isLooping = true
            mp0.start()
        }
        Log.d("PlayRockService", "START: Play Rock.")

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            bundle = intent?.getBundleExtra("TIMER_DETAIL")
            if (!isNull(bundle)) {
                playRockView.findViewById<TextView>(R.id.playRockAlarmTime).text =
                    bundle?.getString("time").toString()
                val name = bundle?.getString("name").toString() + "の時間です!"
                playRockView.findViewById<TextView>(R.id.playRockAlarmName).text = name
                playRockView.findViewById<TextView>(R.id.playRockAlarmDisc).text = "あと10回スクワットしよう"
                playRockView.findViewById<ImageView>(R.id.imageViewSquat)
                    .setImageResource(if ((0..1).random() == 1) R.drawable.squat_man else R.drawable.squat_woman)
                playRockView.findViewById<ImageView>(R.id.imageViewArrow)
                    .setImageResource(R.drawable.down)
            }
            playRockView.show()

            val detectCallback: TimerTask.() -> Unit = {
                scope.launch {
                    detectMotion()
                }
            }
            Timer().schedule(200, 200, detectCallback)

            // TODO: 音量がだんだん大きくなるようにする
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun detectMotion() {
        // TODO: 検知できる運動を追加

        if (this.round >= 10) {
            Log.d("PlayRockService", "STOP: Exercise complete")
            scope.launch {
                val displayIntent = Intent(applicationContext, DisplayLockService::class.java)
                displayIntent.putExtra("TIMER_DETAIL", bundle)
                playRockView.hide()
                stopSelf()

                startService(displayIntent)
            }
            return
        }

        if (count > 5) {
            if (isUp) {
                round = round + 1
                scope.launch {
                    playRockView.findViewById<ImageView>(R.id.imageViewArrow)
                        .setImageResource(R.drawable.down)
                    val countText = "あと${10-round}回スクワットしよう"
                    playRockView.findViewById<TextView>(R.id.playRockAlarmDisc).text = countText
                }
            } else {
                scope.launch {
                    playRockView.findViewById<ImageView>(R.id.imageViewArrow)
                        .setImageResource(R.drawable.up)
                }
            }
            isUp = !isUp
            count = 0
            return
        }

        if (isUp) {
            if (accelY > 0.2) {
                count += 1
            } else if (accelY < -8.0) {
                this.count = 0
            }
        } else {
            if (accelY < -0.2) {
                count += 1
            } else if (accelY > 8.0) {
                count = 0
            }
        }

        Log.d(
            "PlayRockService:detectMotion",
            "isUp:%b, count:%d, round:%d, accelY: %f".format(isUp, count, round, accelY)
        )
    }

    override fun onSensorChanged(e: SensorEvent) {
        if (e.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            this.accelX = e.values[0]
            this.accelY = e.values[1]
            this.accelZ = e.values[2]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }

    /** Cleans up views just in case. */
    override fun onDestroy() {
        scope.cancel()
        playRockView.hide()
        mp0.release()
        super.onDestroy()
    }

    /** This service does not support binding. */
    override fun onBind(intent: Intent?) = null
}